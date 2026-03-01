package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.dto.MessageEditDTO;
import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.dto.MessageReplyDTO;
import cc.feitwnd.entity.Messages;
import cc.feitwnd.exception.ValidationException;
import cc.feitwnd.mapper.MessageMapper;
import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.AsyncEmailService;
import cc.feitwnd.service.MessageService;
import cc.feitwnd.service.UserAgentService;
import cc.feitwnd.utils.IpUtil;
import cc.feitwnd.utils.MarkdownUtil;
import cc.feitwnd.vo.MessageVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 留言服务实现
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private WebsiteProperties websiteProperties;

    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // QQ号正则 (5-11位数字)
    private static final Pattern QQ_PATTERN = Pattern.compile("^[1-9][0-9]{4,10}$");

    /**
     * 访客提交留言
     * @param messageDTO
     * @param request
     */
    public void submitMessage(MessageDTO messageDTO, HttpServletRequest request) {
        // 1. 校验邮箱或QQ号
        validateEmailOrQq(messageDTO.getEmailOrQq());

        // 2. 创建留言实体
        Messages messages = new Messages();
        BeanUtils.copyProperties(messageDTO, messages);

        // 3. 处理Markdown内容
        if (messageDTO.getIsMarkdown() != null && messageDTO.getIsMarkdown() == 1) {
            // 如果是Markdown，转换为HTML
            String html = MarkdownUtil.toHtml(messageDTO.getContent());
            messages.setContentHtml(html);
        } else {
            // 如果不是Markdown，直接使用原内容
            messages.setContentHtml(messageDTO.getContent());
        }

        // 4. 设置访客ID
        Long visitorId = messageDTO.getVisitorId();
        messages.setVisitorId(visitorId);

        // 5. 获取IP地址信息
        String clientIp = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
        // 拼接地址: 省份-城市
        String province = geoInfo.getOrDefault("province", "");
        String city = geoInfo.getOrDefault("city", "");
        String location = province.isEmpty() && city.isEmpty() ? null
                : province.equals(city) ? province
                : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
        if(location != null && !location.isEmpty()) {
            messages.setLocation(location);
        }

        // 6. 解析UserAgent
        String userAgent = request.getHeader("User-Agent");
        String osName = userAgentService.getOsName(userAgent);
        String browserName = userAgentService.getBrowserName(userAgent);
        messages.setUserAgentOs(osName);
        messages.setUserAgentBrowser(browserName);

        // 7. 设置默认值
        messages.setIsApproved(0); // 默认未审核
        messages.setIsEdited(0);   // 默认未编辑
        messages.setCreateTime(LocalDateTime.now());
        messages.setUpdateTime(LocalDateTime.now());

        // 8. 保存到数据库
        messageMapper.save(messages);

        // 9. 检查父留言是否开启邮箱通知
        if (messageDTO.getParentId() != null) {
            notifyParentIfNeeded(messageDTO.getParentId(),
                    messageDTO.getNickname(), messageDTO.getContent(), "message");
        }

        log.info("访客提交留言成功: {}", messages);
    }

    /**
     * 校验邮箱或QQ号
     * @param emailOrQq
     */
    private void validateEmailOrQq(String emailOrQq) {
        if (emailOrQq == null || emailOrQq.trim().isEmpty()) {
            throw new ValidationException(MessageConstant.EMAIL_OR_QQ_REQUIRED);
        }

        emailOrQq = emailOrQq.trim();

        // 先判断是否是QQ号
        if (QQ_PATTERN.matcher(emailOrQq).matches()) {
            return; // QQ号格式正确
        }

        // 再判断是否是邮箱
        if (EMAIL_PATTERN.matcher(emailOrQq).matches()) {
            return; // 邮箱格式正确
        }

        // 都不匹配，抛出异常
        // 判断更像QQ号还是邮箱
        if (emailOrQq.matches("^[0-9]+$")) {
            throw new ValidationException(MessageConstant.INVALID_QQ_FORMAT);
        } else {
            throw new ValidationException(MessageConstant.INVALID_EMAIL_FORMAT);
        }
    }

    /**
     * 分页查询留言
     * @param messagePageQueryDTO
     * @return
     */
    public PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        PageHelper.startPage(messagePageQueryDTO.getPage(), messagePageQueryDTO.getPageSize());
        Page<Messages> page = (Page<Messages>) messageMapper.pageQuery(messagePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量审核留言
     * @param ids
     */
    public void batchApprove(List<Long> ids) {
        messageMapper.batchApprove(ids);
    }

    /**
     * 批量删除留言
     * @param ids
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        // 如果是根留言，级联删除所有子留言
        for (Long id : ids) {
            Messages message = messageMapper.getById(id);
            if (message != null && (message.getRootId() == null || message.getRootId() == 0)) {
                Integer childCount = messageMapper.countByRootId(id);
                if (childCount != null && childCount > 0) {
                    messageMapper.deleteByRootId(id);
                }
            }
        }
        messageMapper.batchDelete(ids);
    }

    /**
     * 管理员回复留言
     * @param messageReplyDTO
     */
    public void adminReply(MessageReplyDTO messageReplyDTO, HttpServletRequest request) {
        // 1. 创建留言实体
        Messages messages = new Messages();
        BeanUtils.copyProperties(messageReplyDTO, messages);

        // 2. 处理Markdown内容
        if (messageReplyDTO.getIsMarkdown() != null && messageReplyDTO.getIsMarkdown() == 1) {
            String html = MarkdownUtil.toHtml(messageReplyDTO.getContent());
            messages.setContentHtml(html);
        } else {
            messages.setContentHtml(messageReplyDTO.getContent());
        }

        // 3. 设置管理员回复标识
        messages.setIsAdminReply(StatusConstant.ENABLE);
        messages.setIsApproved(StatusConstant.ENABLE); // 管理员回复自动审核通过
        messages.setIsEdited(StatusConstant.DISABLE);
        messages.setNickname(websiteProperties.getTitle());
        messages.setCreateTime(LocalDateTime.now());
        messages.setUpdateTime(LocalDateTime.now());

        // 4. 捕获 IP / 地理位置 / UserAgent
        if (request != null) {
            String clientIp = IpUtil.getClientIp(request);
            Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
            String province = geoInfo.getOrDefault("province", "");
            String city = geoInfo.getOrDefault("city", "");
            String location = province.isEmpty() && city.isEmpty() ? null
                    : province.equals(city) ? province
                    : String.format("%s-%s", province, city).replaceAll("^-|-$", "");
            if(location != null && !location.isEmpty()) {
                messages.setLocation(location);
            }
            String userAgent = request.getHeader("User-Agent");
            messages.setUserAgentOs(userAgentService.getOsName(userAgent));
            messages.setUserAgentBrowser(userAgentService.getBrowserName(userAgent));
        }

        // 5. 保存到数据库
        messageMapper.save(messages);

        // 6. 检查父留言是否开启邮箱通知
        notifyParentIfNeeded(messageReplyDTO.getParentId(), websiteProperties.getTitle(),
                messageReplyDTO.getContent(), "message");

        log.info("管理员回复留言成功: parentId={}, content={}", messageReplyDTO.getParentId(), messageReplyDTO.getContent());
    }

    // ===== 博客端方法 =====

    public List<MessageVO> getMessageTree(Long visitorId) {
        List<MessageVO> allMessages = messageMapper.getApprovedList(visitorId);
        // 构建树形结构：根留言（rootId为null或0）作为一级，其余挂到根留言下
        List<MessageVO> rootMessages = new ArrayList<>();
        Map<Long, MessageVO> messageMap = allMessages.stream()
                .collect(Collectors.toMap(MessageVO::getId, m -> m));

        for (MessageVO msg : allMessages) {
            if (msg.getRootId() == null || msg.getRootId() == 0) {
                msg.setChildren(new ArrayList<>());
                rootMessages.add(msg);
            } else {
                MessageVO rootMsg = messageMap.get(msg.getRootId());
                if (rootMsg != null) {
                    if (rootMsg.getChildren() == null) {
                        rootMsg.setChildren(new ArrayList<>());
                    }
                    rootMsg.getChildren().add(msg);
                }
            }
        }
        return rootMessages;
    }

    /**
     * 访客编辑留言
     */
    public void editMessage(MessageEditDTO editDTO) {
        Messages message = messageMapper.getById(editDTO.getId());
        if (message == null) {
            throw new ValidationException("留言不存在");
        }
        if (!message.getVisitorId().equals(editDTO.getVisitorId())) {
            throw new ValidationException("无权编辑此留言");
        }

        Messages updateMessage = new Messages();
        updateMessage.setId(editDTO.getId());
        updateMessage.setContent(editDTO.getContent());

        if (editDTO.getIsMarkdown() != null && editDTO.getIsMarkdown() == 1) {
            updateMessage.setContentHtml(MarkdownUtil.toHtml(editDTO.getContent()));
        } else {
            updateMessage.setContentHtml(editDTO.getContent());
        }

        messageMapper.updateContent(updateMessage);
        log.info("访客编辑留言成功: id={}, visitorId={}", editDTO.getId(), editDTO.getVisitorId());
    }

    /**
     * 访客删除留言
     */
    @Transactional
    public void visitorDeleteMessage(Long id, Long visitorId) {
        Messages message = messageMapper.getById(id);
        if (message == null) {
            throw new ValidationException("留言不存在");
        }
        if (!message.getVisitorId().equals(visitorId)) {
            throw new ValidationException("无权删除此留言");
        }

        // 如果是根留言，级联删除所有子留言
        if (message.getRootId() == null || message.getRootId() == 0) {
            Integer childCount = messageMapper.countByRootId(id);
            if (childCount != null && childCount > 0) {
                messageMapper.deleteByRootId(id);
            }
        }

        messageMapper.deleteById(id);
        log.info("访客删除留言成功: id={}, visitorId={}", id, visitorId);
    }

    /**
     * 检查父留言是否开启邮箱通知，如果是则发送通知邮件
     */
    private void notifyParentIfNeeded(Long parentId, String replyNickname, String replyContent, String type) {
        if (parentId == null) {
            return;
        }
        try {
            Messages parentMessage = messageMapper.getById(parentId);
            if (parentMessage != null
                    && parentMessage.getIsNotice() != null
                    && parentMessage.getIsNotice() == 1
                    && parentMessage.getEmailOrQq() != null) {
                String emailOrQq = parentMessage.getEmailOrQq().trim();
                String email;
                if (emailOrQq.contains("@")) {
                    // 本身就是邮箱，直接使用
                    email = emailOrQq;
                } else if (emailOrQq.matches("^[1-9]\\d{4,10}$")) {
                    // QQ 号，拼接 @qq.com 构造邮箱地址
                    email = emailOrQq + "@qq.com";
                } else {
                    return; // 格式不评别，跳过
                }
                asyncEmailService.sendReplyNotificationAsync(
                        email,
                        parentMessage.getNickname(),
                        parentMessage.getContent(),
                        replyNickname,
                        replyContent,
                        type
                );
            }
        } catch (Exception e) {
            log.error("发送留言回复通知邮件异常: parentId={}, ex={}", parentId, e.getMessage());
        }
    }
}

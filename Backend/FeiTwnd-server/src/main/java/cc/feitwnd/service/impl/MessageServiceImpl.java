package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.context.BaseContext;
import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.entity.Messages;
import cc.feitwnd.exception.ValidationException;
import cc.feitwnd.mapper.MessageMapper;
import cc.feitwnd.service.MessageService;
import cc.feitwnd.service.UserAgentService;
import cc.feitwnd.utils.IpUtil;
import cc.feitwnd.utils.MarkdownUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;

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

    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // QQ号正则 (5-11位数字)
    private static final Pattern QQ_PATTERN = Pattern.compile("^[1-9][0-9]{4,10}$");

    @Override
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

        // 4. 设置访客ID (优先使用前端传来的，如果没有则从BaseContext获取)
        Long visitorId = messageDTO.getVisitorId();
        if (visitorId == null) {
            visitorId = BaseContext.getCurrentId();
        }
        messages.setVisitorId(visitorId);

        // 5. 获取IP地址信息
        String clientIp = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(clientIp);
        // 拼接地址: 国家-省份-城市
        String location = String.format("%s-%s-%s", 
            geoInfo.getOrDefault("country", ""),
            geoInfo.getOrDefault("province", ""),
            geoInfo.getOrDefault("city", "")
        );
        messages.setLocation(location);

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
}

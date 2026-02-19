package cc.feitwnd.service;

import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.dto.MessageReplyDTO;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 留言服务
 */
public interface MessageService {

    /**
     * 访客提交留言
     * @param messageDTO
     * @param request
     */
    void submitMessage(MessageDTO messageDTO, HttpServletRequest request);

    /**
     * 分页条件查询留言
     * @param messagePageQueryDTO
     * @return
     */
    PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 批量审核通过留言
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 批量删除留言
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 管理员回复留言
     * @param messageReplyDTO
     */
    void adminReply(MessageReplyDTO messageReplyDTO, HttpServletRequest request);

    // ===== 博客端方法 =====

    /**
     * 获取已审核留言列表（树形结构）
     */
    List<MessageVO> getMessageTree();

    /**
     * 访客编辑留言
     */
    void editMessage(cc.feitwnd.dto.MessageEditDTO editDTO);

    /**
     * 访客删除留言
     */
    void visitorDeleteMessage(Long id, Long visitorId);
}


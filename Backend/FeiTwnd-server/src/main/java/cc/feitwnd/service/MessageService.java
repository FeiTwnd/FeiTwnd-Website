package cc.feitwnd.service;

import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.dto.MessageReplyDTO;
import cc.feitwnd.result.PageResult;
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
    void batchApprove(String ids);

    /**
     * 批量删除留言
     * @param ids
     */
    void batchDelete(String ids);

    /**
     * 管理员回复留言
     * @param messageReplyDTO
     */
    void adminReply(MessageReplyDTO messageReplyDTO);
}


package cc.feitwnd.service;

import cc.feitwnd.dto.MessageDTO;
import jakarta.servlet.http.HttpServletRequest;

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
}

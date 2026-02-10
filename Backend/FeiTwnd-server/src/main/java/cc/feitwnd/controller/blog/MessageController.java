package cc.feitwnd.controller.blog;

import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客端留言接口
 */
@RestController("blogMessageController")
@RequestMapping("/blog/message")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 访客提交留言
     * @param messageDTO
     * @param request
     * @return
     */
    @PostMapping
    public Result<String> submitMessage(@RequestBody MessageDTO messageDTO, HttpServletRequest request) {
        log.info("访客提交留言: {}", messageDTO);
        messageService.submitMessage(messageDTO, request);
        return Result.success();
    }
}

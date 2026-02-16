package cc.feitwnd.controller.blog;

import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.dto.MessageEditDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.MessageService;
import cc.feitwnd.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    /**
     * 获取已审核留言列表（树形结构）
     */
    @GetMapping
    public Result<List<MessageVO>> getMessageTree() {
        log.info("博客端获取留言树");
        List<MessageVO> messageTree = messageService.getMessageTree();
        return Result.success(messageTree);
    }

    /**
     * 访客编辑留言
     */
    @PutMapping("/edit")
    public Result<String> editMessage(@RequestBody MessageEditDTO editDTO) {
        log.info("访客编辑留言: {}", editDTO);
        messageService.editMessage(editDTO);
        return Result.success();
    }

    /**
     * 访客删除留言
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteMessage(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("访客删除留言: id={}, visitorId={}", id, visitorId);
        messageService.visitorDeleteMessage(id, visitorId);
        return Result.success();
    }
}

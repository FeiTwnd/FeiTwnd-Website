package cc.feitwnd.controller.blog;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.dto.MessageDTO;
import cc.feitwnd.dto.MessageEditDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.MessageService;
import cc.feitwnd.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "留言过于频繁，请稍后再试")
    public Result<String> submitMessage(@Valid @RequestBody MessageDTO messageDTO, HttpServletRequest request) {
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
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> editMessage(@Valid @RequestBody MessageEditDTO editDTO) {
        log.info("访客编辑留言: {}", editDTO);
        messageService.editMessage(editDTO);
        return Result.success();
    }

    /**
     * 访客删除留言
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> deleteMessage(@PathVariable Long id, @RequestParam Long visitorId) {
        log.info("访客删除留言: id={}, visitorId={}", id, visitorId);
        messageService.visitorDeleteMessage(id, visitorId);
        return Result.success();
    }
}

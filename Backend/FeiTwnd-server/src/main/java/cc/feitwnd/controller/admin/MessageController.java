package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.dto.MessageReplyDTO;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.MessageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端留言接口
 */
@Slf4j
@RestController("adminMessageController")
@RequestMapping("/admin/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页条件查询留言
     * @param messagePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        log.info("分页条件查询留言: {}", messagePageQueryDTO);
        PageResult pageResult = messageService.pageQuery(messagePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量审核通过留言
     * @param ids
     * @return
     */
    @PutMapping("/approve")
    @OperationLog(value = OperationType.UPDATE, target = "message", targetId = "#ids")
    public Result<String> batchApprove(@RequestParam List<Long> ids) {
        log.info("批量审核通过留言: {}", ids);
        messageService.batchApprove(ids);
        return Result.success();
    }

    /**
     * 批量删除留言
     * @param ids
     * @return
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "message", targetId = "#ids")
    public Result<String> batchDelete(@RequestParam List<Long> ids) {
        log.info("批量删除留言: {}", ids);
        messageService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 管理员回复留言
     * @param messageReplyDTO
     * @return
     */
    @PostMapping("/reply")
    @OperationLog(value = OperationType.INSERT, target = "message", targetId = "#messageReplyDTO.parentId")
    public Result<String> adminReply(@Valid @RequestBody MessageReplyDTO messageReplyDTO) {
        log.info("管理员回复留言: {}", messageReplyDTO);
        messageService.adminReply(messageReplyDTO);
        return Result.success();
    }
}

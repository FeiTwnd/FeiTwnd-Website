package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.entity.FriendLinks;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.FriendLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端友链接口
 */
@RestController("adminFriendLinkController")
@RequestMapping("/admin/friendLink")
@Slf4j
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取所有友情链接信息
     */
    @GetMapping
    public Result<List<FriendLinks>> getAllFriendLink() {
        List<FriendLinks> friendLinkList = friendLinkService.getAllFriendLink();
        return Result.success(friendLinkList);
    }

    /**
     * 添加友情链接信息
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "friendLink")
    public Result addFriendLink(@RequestBody FriendLinks friendLink) {
        log.info("添加友情链接信息:{}", friendLink);
        friendLinkService.addFriendLink(friendLink);
        return Result.success();
    }

    /**
     * 批量删除友情链接信息
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "friendLink", targetId = "#ids")
    public Result deleteFriendLink(@RequestParam List<Long> ids) {
        log.info("批量删除友情链接信息:{}", ids);
        friendLinkService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 修改友情链接信息
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "friendLink", targetId = "#friendLink.id")
    public Result updateFriendLink(@RequestBody FriendLinks friendLink) {
        log.info("修改友情链接信息:{}", friendLink);
        friendLinkService.updateFriendLink(friendLink);
        return Result.success();
    }
}

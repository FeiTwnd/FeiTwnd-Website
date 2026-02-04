package cc.feitwnd.controller.admin;

import cc.feitwnd.entity.FriendLinks;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端友链接口
 */
@RestController("adminFriendLinkController")
@RequestMapping("/admin/friendLink")
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
    public Result addFriendLink(@RequestBody FriendLinks friendLink) {
        friendLinkService.addFriendLink(friendLink);
        return Result.success();
    }

    /**
     * 删除友情链接信息
     */
    @DeleteMapping("/{id}")
    public Result deleteFriendLink(@PathVariable Long id) {
        friendLinkService.deleteFriendLink(id);
        return Result.success();
    }

    /**
     * 修改友情链接信息
     */
    @PutMapping
    public Result updateFriendLink(@RequestBody FriendLinks friendLink) {
        friendLinkService.updateFriendLink(friendLink);
        return Result.success();
    }
}

package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.FriendLinkService;
import cc.feitwnd.vo.FriendLinkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客端友链接口
 */
@RestController("blogFriendLinkController")
@RequestMapping("/blog/friendLink")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取可见友情链接
     */
    @GetMapping
    public Result<List<FriendLinkVO>> getVisibleFriendLink() {
        List<FriendLinkVO> friendLinkVOList = friendLinkService.getVisibleFriendLink();
        return Result.success(friendLinkVOList);
    }
}

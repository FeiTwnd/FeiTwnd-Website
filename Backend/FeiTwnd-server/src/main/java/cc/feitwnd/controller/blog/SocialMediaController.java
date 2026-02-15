package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.SocialMediaService;
import cc.feitwnd.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客端社交媒体接口
 */
@RestController("blogSocialMediaController")
@RequestMapping("/blog/socialMedia")
public class SocialMediaController {

    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * 获取可见社交媒体信息
     */
    @GetMapping
    public Result<List<SocialMediaVO>> getVisibleSocialMedia() {
        List<SocialMediaVO> socialMediaVOList = socialMediaService.getVisibleSocialMedia();
        return Result.success(socialMediaVOList);
    }
}

package cc.feitwnd.controller.admin;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.SocialMediaService;
import cc.feitwnd.vo.SocialMediaAdminVO;
import cc.feitwnd.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  管理端社交媒体接口
 */
@RestController("adminSocialMediaController")
@RequestMapping("/admin/socialMedia")
public class SocialMediaController {

    @Autowired
    private SocialMediaService socialMediaService;

    /**
     * 获取所有社交媒体信息
     */
    @GetMapping
    public Result<List<SocialMediaAdminVO>> getAllSocialMedia() {
        List<SocialMediaAdminVO> socialMediaAdminVOList = socialMediaService.getAllSocialMedia();
        return Result.success(socialMediaAdminVOList);
    }
}

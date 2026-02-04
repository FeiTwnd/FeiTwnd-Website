package cc.feitwnd.controller.admin;

import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result<List<SocialMedia>> getAllSocialMedia() {
        List<SocialMedia> socialMediaList = socialMediaService.getAllSocialMedia();
        return Result.success(socialMediaList);
    }

    /**
     * 添加社交媒体信息
     */
    @PostMapping
    public Result addSocialMedia(@RequestBody SocialMedia socialMedia) {
        socialMediaService.addSocialMedia(socialMedia);
        return Result.success();
    }
    /**
     * 删除社交媒体信息
     */
    @DeleteMapping("/{id}")
    public Result deleteSocialMedia(@PathVariable Long id) {
        socialMediaService.deleteSocialMedia(id);
        return Result.success();
    }

    /**
     * 修改社交媒体信息
     */
    @PutMapping
    public Result updateSocialMedia(@RequestBody SocialMedia socialMedia) {
        socialMediaService.updateSocialMedia(socialMedia);
        return Result.success();
    }
}

package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.SocialMediaDTO;
import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.SocialMediaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  管理端社交媒体接口
 */
@RestController("adminSocialMediaController")
@RequestMapping("/admin/socialMedia")
@Slf4j
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
    @OperationLog(value = OperationType.INSERT, target = "socialMedia")
    public Result addSocialMedia(@Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("添加社交媒体信息: {}", socialMediaDTO);
        socialMediaService.addSocialMedia(socialMediaDTO);
        return Result.success();
    }
    /**
     * 批量删除社交媒体信息
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "socialMedia", targetId = "#ids")
    public Result deleteSocialMedia(@RequestParam List<Long> ids) {
        log.info("批量删除社交媒体信息: {}", ids);
        socialMediaService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 修改社交媒体信息
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "socialMedia", targetId = "#socialMediaDTO.id")
    public Result updateSocialMedia(@Valid @RequestBody SocialMediaDTO socialMediaDTO) {
        log.info("修改社交媒体信息: {}", socialMediaDTO);
        socialMediaService.updateSocialMedia(socialMediaDTO);
        return Result.success();
    }
}

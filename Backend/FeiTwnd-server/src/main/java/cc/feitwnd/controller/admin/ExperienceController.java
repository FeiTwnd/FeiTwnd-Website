package cc.feitwnd.controller.admin;

import cc.feitwnd.entity.Experiences;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  管理端经历接口
 */
@RestController("adminExperienceController")
@RequestMapping("/admin/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    /**
     * 根据分类获取经历信息
     */
    @GetMapping
    public Result<List<Experiences>> getExperience(@RequestParam(required = false) Integer type) {
        List<Experiences> experienceList = experienceService.getExperience(type);
        return Result.success(experienceList);
    }

    /**
     * 添加经历信息
     */
    @PostMapping
    public Result addExperience(@RequestBody Experiences experiences) {
        experienceService.addExperience(experiences);
        return Result.success();
    }

    /**
     * 修改经历信息
     */
    @PutMapping
    public Result updateExperience(@RequestBody Experiences experiences) {
        experienceService.updateExperience(experiences);
        return Result.success();
    }

    /**
     * 删除经历信息
     */
    @DeleteMapping("/{id}")
    public Result deleteExperience(@PathVariable Long id) {
        experienceService.deleteExperience(id);
        return Result.success();
    }

}

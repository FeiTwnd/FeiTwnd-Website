package cc.feitwnd.controller.admin;

import cc.feitwnd.entity.Skills;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.SkillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端技能接口
 */
@RestController("adminSkillController")
@RequestMapping("/admin/skill")
@Slf4j
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * 获取所有技能信息
     */
    @GetMapping
    public Result<List<Skills>> getAllSkill() {
        return Result.success(skillService.getAllSkill());
    }

    /**
     * 添加技能信息
     */
    @PostMapping
    public Result addSkill(@RequestBody Skills skills) {
        log.info("添加技能信息,{}", skills);
        skillService.addSkill(skills);
        return Result.success();
    }

    /**
     * 删除技能信息
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteSkill(@PathVariable Long id) {
        log.info("删除技能信息,{}", id);
        skillService.deleteSkill(id);
        return Result.success();
    }

    /**
     * 修改技能信息
     */
    @PutMapping
    public Result updateSkill(@RequestBody Skills skills) {
        log.info("修改技能信息,{}", skills);
        skillService.updateSkill(skills);
        return Result.success();
    }

}

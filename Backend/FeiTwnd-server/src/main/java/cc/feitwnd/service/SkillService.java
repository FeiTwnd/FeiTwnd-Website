package cc.feitwnd.service;

import cc.feitwnd.entity.Skills;
import cc.feitwnd.vo.SkillVO;

import java.util.List;

public interface SkillService {
    /**
     * 获取所有技能信息
     */
    List<Skills> getAllSkill();

    /**
     * 添加技能
     */
    void addSkill(Skills skills);

    /**
     * 删除技能
     */
    void deleteSkill(Long id);

    /**
     * 修改技能
     */
    void updateSkill(Skills skills);

    /**
     * 简历端获取技能信息
     */
    List<SkillVO> getSkillVO();
}

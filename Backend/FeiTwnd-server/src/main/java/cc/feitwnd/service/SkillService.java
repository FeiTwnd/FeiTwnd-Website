package cc.feitwnd.service;

import cc.feitwnd.entity.Skills;
import cc.feitwnd.vo.SkillVO;

import java.util.List;

import java.util.List;

public interface SkillService {
    /**
     * 获取所有技能信息
     * @return
     */
    List<Skills> getAllSkill();

    /**
     * 添加技能
     * @param skills
     */
    void addSkill(Skills skills);

    /**
     * 批量删除技能
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 修改技能
     * @param skills
     */
    void updateSkill(Skills skills);

    /**
     * 简历端获取技能信息
     * @return
     */
    List<SkillVO> getSkillVO();
}

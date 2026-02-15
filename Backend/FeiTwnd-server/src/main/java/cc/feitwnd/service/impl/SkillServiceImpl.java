package cc.feitwnd.service.impl;

import cc.feitwnd.entity.Skills;
import cc.feitwnd.mapper.SkillMapper;
import cc.feitwnd.service.SkillService;
import cc.feitwnd.vo.SkillVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillMapper skillMapper;

    /**
     * 获取所有技能信息
     * @return
     */
    public List<Skills> getAllSkill() {
        List<Skills> skillList = skillMapper.getAllSkill();
        return skillList;
    }

    /**
     * 添加技能信息
     * @param skills
     */
    public void addSkill(Skills skills) {
        skillMapper.addSkill(skills);
    }

    /**
     * 批量删除技能信息
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        skillMapper.batchDelete(ids);
    }

    /**
     * 修改技能信息
     * @param skills
     */
    public void updateSkill(Skills skills) {
        skillMapper.updateSkill(skills);
    }

    /**
     * 简历端获取技能信息
     * @return
     */
    public List<SkillVO> getSkillVO() {
        List<Skills> skills = skillMapper.getVisibleSkill();
        if(skills!=null && !skills.isEmpty()){
            List<SkillVO> skillVOList = skills.stream().map(skill -> SkillVO.builder()
                    .id(skill.getId())
                    .name(skill.getName())
                    .description(skill.getDescription())
                    .icon(skill.getIcon())
                    .sort(skill.getSort())
                    .build()
            ).toList();
            return skillVOList;
        }
        return Collections.emptyList();
    }
}

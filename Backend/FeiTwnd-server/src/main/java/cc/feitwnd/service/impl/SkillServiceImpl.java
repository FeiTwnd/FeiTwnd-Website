package cc.feitwnd.service.impl;

import cc.feitwnd.entity.Skills;
import cc.feitwnd.mapper.SkillMapper;
import cc.feitwnd.service.SkillService;
import cc.feitwnd.vo.SkillVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillMapper skillMapper;

    /**
     * 获取所有技能信息
     */
    public List<Skills> getAllSkill() {
        List<Skills> skillList = skillMapper.getAllSkill();
        return skillList;
    }

    /**
     * 添加技能信息
     */
    public void addSkill(Skills skills) {
        skillMapper.addSkill(skills);
    }

    /**
     * 删除技能信息
     */
    public void deleteSkill(Long id) {
        skillMapper.deleteById(id);
    }

    /**
     * 修改技能信息
     */
    public void updateSkill(Skills skills) {
        skillMapper.updateSkill(skills);
    }

    /**
     * 简历端获取技能信息
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
        return null;
    }
}

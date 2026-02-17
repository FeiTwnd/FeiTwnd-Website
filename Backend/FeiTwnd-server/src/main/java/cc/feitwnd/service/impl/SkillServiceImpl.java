package cc.feitwnd.service.impl;

import cc.feitwnd.dto.SkillDTO;
import cc.feitwnd.entity.Skills;
import cc.feitwnd.mapper.SkillMapper;
import cc.feitwnd.service.SkillService;
import cc.feitwnd.vo.SkillVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "skills", key = "'all'")
    public List<Skills> getAllSkill() {
        List<Skills> skillList = skillMapper.getAllSkill();
        return skillList;
    }

    /**
     * 添加技能信息
     * @param skills
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void addSkill(SkillDTO skillDTO) {
        Skills skills = new Skills();
        BeanUtils.copyProperties(skillDTO, skills);
        skillMapper.addSkill(skills);
    }

    /**
     * 批量删除技能信息
     * @param ids
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void batchDelete(List<Long> ids) {
        skillMapper.batchDelete(ids);
    }

    /**
     * 修改技能信息
     * @param skills
     */
    @CacheEvict(value = "skills", allEntries = true)
    public void updateSkill(SkillDTO skillDTO) {
        Skills skills = new Skills();
        BeanUtils.copyProperties(skillDTO, skills);
        skillMapper.updateSkill(skills);
    }

    /**
     * 简历端获取技能信息
     * @return
     */
    @Cacheable(value = "skills", key = "'visible'")
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

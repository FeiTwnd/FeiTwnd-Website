package cc.feitwnd.service.impl;

import cc.feitwnd.entity.Experiences;
import cc.feitwnd.mapper.ExperienceMapper;
import cc.feitwnd.service.ExperienceService;
import cc.feitwnd.vo.ExperienceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceMapper experienceMapper;

    /**
     * 获取经历信息
     */
    public List<Experiences> getExperience(Integer type) {
        List<Experiences> experienceList = experienceMapper.getExperienceByType(type);
        return experienceList;
    }

    /**
     * 添加经历信息
     */
    public void addExperience(Experiences experiences) {
        experienceMapper.insert(experiences);
    }

    /**
     * 修改经历信息
     */
    public void updateExperience(Experiences experiences) {
       experienceMapper.update(experiences);
    }

    /**
     * 删除经历信息
     */
    public void deleteExperience(Long id) {
        experienceMapper.deleteById(id);
    }

    /**
     * cv端获取全部经历信息
     */
    public List<ExperienceVO> getAllExperience() {
        List<Experiences> experienceList = experienceMapper.getAllExperience();
        if(experienceList != null && !experienceList.isEmpty()) {
            // 转换为VO
            List<ExperienceVO> experienceVOList = experienceList.stream().map(experiences -> ExperienceVO.builder()
                    .id(experiences.getId())
                    .type(experiences.getType())
                    .title(experiences.getTitle())
                    .subtitle(experiences.getSubtitle())
                    .logoUrl(experiences.getLogoUrl())
                    .startDate(experiences.getStartDate())
                    .endDate(experiences.getEndDate())
                    .content(experiences.getContent())
                    .build()
            ).toList();
            return experienceVOList;
        }
        return null;
    }
}

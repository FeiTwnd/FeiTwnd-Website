package cc.feitwnd.service;

import cc.feitwnd.dto.ExperienceDTO;
import cc.feitwnd.entity.Experiences;
import cc.feitwnd.vo.ExperienceVO;

import java.util.List;

public interface ExperienceService {
    /**
     * 根据类型获取经历信息
     * @param type
     * @return
     */
    List<Experiences> getExperience(Integer type);

    /**
     * 添加经历信息
     * @param experienceDTO
     */
    void addExperience(ExperienceDTO experienceDTO);

    /**
     * 修改经历信息
     * @param experienceDTO
     */
    void updateExperience(ExperienceDTO experienceDTO);

    /**
     * 批量删除经历
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * cv端获取全部经历信息
     * @return
     */
    List<ExperienceVO> getAllExperience();
}

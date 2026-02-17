package cc.feitwnd.service;

import cc.feitwnd.dto.SocialMediaDTO;
import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.vo.SocialMediaVO;

import java.util.List;

public interface SocialMediaService {
    /**
     * 获取可见社交媒体信息
     * @return
     */
    List<SocialMediaVO> getVisibleSocialMedia();

    /**
     * 获取所有社交媒体信息
     * @return
     */
    List<SocialMedia> getAllSocialMedia();

    /**
     * 添加社交媒体信息
     * @param socialMediaDTO
     */
    void addSocialMedia(SocialMediaDTO socialMediaDTO);

    /**
     * 批量删除社交媒体
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 修改社交媒体信息
     * @param socialMediaDTO
     */
    void updateSocialMedia(SocialMediaDTO socialMediaDTO);
}

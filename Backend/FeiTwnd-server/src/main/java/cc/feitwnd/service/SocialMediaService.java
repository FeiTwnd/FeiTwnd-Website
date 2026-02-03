package cc.feitwnd.service;

import cc.feitwnd.vo.SocialMediaAdminVO;
import cc.feitwnd.vo.SocialMediaVO;

import java.util.List;

public interface SocialMediaService {
    /**
     * 获取可见社交媒体信息
     */
    List<SocialMediaVO> getVisibleSocialMedia();

    /**
     * 获取所有社交媒体信息
     */
    List<SocialMediaAdminVO> getAllSocialMedia();

    /**
     * 添加社交媒体信息
     */
    void addSocialMedia(SocialMediaAdminVO socialMediaAdminVO);

    /**
     * 删除社交媒体信息
     */
    void deleteSocialMedia(Long id);

    /**
     * 修改社交媒体信息
     */
    void updateSocialMedia(SocialMediaAdminVO socialMediaAdminVO);
}

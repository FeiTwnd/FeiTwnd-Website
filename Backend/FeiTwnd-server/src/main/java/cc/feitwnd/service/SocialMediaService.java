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
}

package cc.feitwnd.service.impl;

import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.mapper.SocialMediaMapper;
import cc.feitwnd.service.SocialMediaService;
import cc.feitwnd.vo.SocialMediaAdminVO;
import cc.feitwnd.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    @Autowired
    private SocialMediaMapper socialMediaMapper;

    /**
     * 获取可见社交媒体信息
     */
    public List<SocialMediaVO> getVisibleSocialMedia() {
        // 获取数据库数据
        List<SocialMedia> socialMediaList = socialMediaMapper.getVisibleSocialMedia();
        // 转换为VO
        if (socialMediaList != null && socialMediaList.size() > 0) {
            return socialMediaList.stream().map(socialMedia -> SocialMediaVO.builder()
                    .id(socialMedia.getId())
                    .name(socialMedia.getName())
                    .icon(socialMedia.getIcon())
                    .link(socialMedia.getLink())
                    .sort(socialMedia.getSort())
                    .build()).toList();
        }
        return null;
    }

    /**
     * 获取所有社交媒体信息
     */
    public List<SocialMediaAdminVO> getAllSocialMedia() {
        // 获取数据库数据
        List<SocialMedia> socialMediaList = socialMediaMapper.getAllSocialMedia();
        // 转换为VO
        if (socialMediaList != null && socialMediaList.size() > 0) {
            return socialMediaList.stream().map(socialMedia -> SocialMediaAdminVO.builder()
                    .id(socialMedia.getId())
                    .name(socialMedia.getName())
                    .icon(socialMedia.getIcon())
                    .link(socialMedia.getLink())
                    .sort(socialMedia.getSort())
                    .isVisible(socialMedia.getIsVisible())
                    .build()).toList();
        }
        return null;
    }
}

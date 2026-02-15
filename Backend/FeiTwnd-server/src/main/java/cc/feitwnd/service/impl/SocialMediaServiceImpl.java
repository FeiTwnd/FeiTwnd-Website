package cc.feitwnd.service.impl;

import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.mapper.SocialMediaMapper;
import cc.feitwnd.service.SocialMediaService;
import cc.feitwnd.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    @Autowired
    private SocialMediaMapper socialMediaMapper;

    /**
     * 获取可见社交媒体信息
     * @return
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
        return Collections.emptyList();
    }

    /**
     * 获取所有社交媒体信息
     * @return
     */
    public List<SocialMedia> getAllSocialMedia() {
        // 获取数据库数据
        List<SocialMedia> socialMediaList = socialMediaMapper.getAllSocialMedia();
        if (socialMediaList != null && socialMediaList.size() > 0) {
            return socialMediaList;
        }
        return Collections.emptyList();
    }

    /**
     * 添加社交媒体
     * @param socialMedia
     */
    public void addSocialMedia(SocialMedia socialMedia) {
        // 添加到数据库
        socialMediaMapper.insert(socialMedia);
    }

    /**
     * 批量删除社交媒体
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        socialMediaMapper.batchDelete(ids);
    }

    /**
     * 修改社交媒体
     * @param socialMedia
     */
    public void updateSocialMedia(SocialMedia socialMedia) {
        // 更新到数据库
        socialMediaMapper.updateById(socialMedia);
    }
}

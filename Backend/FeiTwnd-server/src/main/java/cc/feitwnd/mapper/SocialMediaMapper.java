package cc.feitwnd.mapper;

import cc.feitwnd.entity.SocialMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SocialMediaMapper {
    /**
     * 获取可见社交媒体信息
     */
    @Select("select * from social_media where is_visible = 1")
    List<SocialMedia> getVisibleSocialMedia();

    /**
     * 获取所有社交媒体信息
     */
    @Select("select * from social_media")
    List<SocialMedia> getAllSocialMedia();
}

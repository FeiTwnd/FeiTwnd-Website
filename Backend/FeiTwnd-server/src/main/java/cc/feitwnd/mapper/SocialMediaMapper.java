package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.SocialMedia;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 添加社交媒体
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into social_media (name, icon, link, sort, is_visible, create_time, update_time) values (#{name}, #{icon}, #{link}, #{sort}, #{isVisible}, #{createTime}, #{updateTime})")
    void insert(SocialMedia socialMedia);

    /**
     * 删除社交媒体
     */
    @Delete("delete from social_media where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除社交媒体
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 修改社交媒体信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(SocialMedia socialMedia);
}

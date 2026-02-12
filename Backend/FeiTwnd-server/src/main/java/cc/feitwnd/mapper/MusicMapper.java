package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.MusicPageQueryDTO;
import cc.feitwnd.entity.Music;
import cc.feitwnd.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper {
    /**
     * 插入音乐
     * @param music
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Music music);

    /**
     * 分页查询音乐
     * @param musicPageQueryDTO
     * @return
     */
    Page<Music> pageQuery(MusicPageQueryDTO musicPageQueryDTO);

    /**
     * 更新音乐
     * @param music
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Music music);

    /**
     * 删除音乐
     * @param id
     */
    @Delete("delete from music where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除音乐
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询音乐
     * @param id
     * @return
     */
    @Select("select * from music where id = #{id}")
    Music getById(Long id);

    /**
     * 获取所有可见的音乐
     * @return
     */
    @Select("select * from music where is_visible = 1 order by sort asc, id desc")
    List<Music> getAllVisibleMusic();
}

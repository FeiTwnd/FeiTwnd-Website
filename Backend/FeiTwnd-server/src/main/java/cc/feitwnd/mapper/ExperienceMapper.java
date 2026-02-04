package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.Experiences;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExperienceMapper {
    /**
     * 根据类型获取经历信息
     */
    List<Experiences> getExperienceByType(Integer type);

    /**
     * 添加经历信息
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Experiences experiences);

    /**
     * 修改经历信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Experiences experiences);

    /**
     * 删除经历信息
     */
    @Delete("delete from experiences where id = #{id}")
    void deleteById(Long id);

    /**
     * 获取全部经历信息
     */
    @Select("select * from experiences where is_visible = 1 order by start_date desc")
    List<Experiences> getAllExperience();
}

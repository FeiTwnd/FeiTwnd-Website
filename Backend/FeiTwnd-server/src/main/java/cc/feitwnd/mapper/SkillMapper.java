package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.Skills;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkillMapper {
    /**
     * 获取所有技能信息
     */
    @Select("select * from skills order by sort")
    List<Skills> getAllSkill();

    /**
     * 添加技能信息
     */
    @AutoFill(value = OperationType.INSERT)
    void addSkill(Skills skills);

    /**
     * 删除技能信息
     */
    @Delete("delete from skills where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除技能
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 修改技能信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateSkill(Skills skills);

    /**
     * 获取可见技能信息
     */
    @Select("select * from skills where is_visible = 1 order by sort")
    List<Skills> getVisibleSkill();
}

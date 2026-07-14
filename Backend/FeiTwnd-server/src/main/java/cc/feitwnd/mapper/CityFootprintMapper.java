package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.CityFootprintPageQueryDTO;
import cc.feitwnd.entity.CityFootprint;
import cc.feitwnd.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CityFootprintMapper {

    /**
     * 分页查询城市足迹
     */
    Page<CityFootprint> pageQuery(CityFootprintPageQueryDTO pageQueryDTO);

    /**
     * 获取可见城市足迹
     */
    @Select("select * from city_footprints where is_visible = 1 order by visit_time desc, id asc")
    List<CityFootprint> getVisible();

    /**
     * 添加城市足迹
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(CityFootprint cityFootprint);

    /**
     * 修改城市足迹
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(CityFootprint cityFootprint);

    /**
     * 批量删除城市足迹
     */
    void batchDelete(List<Long> ids);
}

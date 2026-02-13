package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.ViewPageQueryDTO;
import cc.feitwnd.entity.Views;
import cc.feitwnd.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ViewMapper {
    /**
     * 插入一条浏览记录
     * @param view
     */
    void insert(Views view);

    /**
     * 分页查询浏览记录
     * @param viewPageQueryDTO
     * @return
     */
    Page<Views> pageQuery(ViewPageQueryDTO viewPageQueryDTO);

    /**
     * 统计总浏览量
     */
    @Select("select count(*) from views")
    Integer countTotal();

    /**
     * 统计今日浏览量
     */
    @Select("select count(*) from views where date(view_time) = curdate()")
    Integer countToday();
}

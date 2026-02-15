package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.DailyViewCountDTO;
import cc.feitwnd.dto.ViewPageQueryDTO;
import cc.feitwnd.entity.Views;
import cc.feitwnd.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * 统计指定日期范围内每日浏览量
     */
    List<DailyViewCountDTO> getDailyViewStats(LocalDate begin, LocalDate end);

    /**
     * 批量删除浏览记录
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

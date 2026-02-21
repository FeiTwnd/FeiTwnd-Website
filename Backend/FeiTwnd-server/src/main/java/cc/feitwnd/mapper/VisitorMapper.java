package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.DailyVisitorCountDTO;
import cc.feitwnd.dto.ProvinceCountDTO;
import cc.feitwnd.dto.VisitorPageQueryDTO;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VisitorMapper {
    /**
     * 根据访客指纹查询访客信息
     * @param fingerprint
     * @return
     */
    @Select("select * from visitors where fingerprint = #{fingerprint}")
    Visitors findVisitorByFingerprint(String fingerprint);

    /**
     * 根据id查询访客信息
     * @param id
     * @return
     */
    @Select("select * from visitors where id = #{id}")
    Visitors findById(Long id);

    /**
     * 插入访客信息
     * @param visitor
     */
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertVisitor(Visitors visitor);

    /**
     * 根据id更新访客信息
     * @param visitor
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Visitors visitor);

    /**
     * 分页查询
     * @param visitorPageQueryDTO
     * @return
     */
    Page<Visitors> pageQuery(VisitorPageQueryDTO visitorPageQueryDTO);

    /**
     * 批量封禁访客
     * @param ids
     */
    void batchBlock(List<Long> ids);

    /**
     * 批量解封访客
     * @param ids
     */
    void batchUnblock(List<Long> ids);

    /**
     * 统计总访客数
     */
    @Select("select count(*) from visitors")
    Integer countTotal();

    /**
     * 统计今日新增访客数
     */
    @Select("select count(*) from visitors where date(create_time) = curdate()")
    Integer countToday();

    /**
     * 统计指定日期范围内每日新增访客数
     */
    List<DailyVisitorCountDTO> getDailyNewVisitorStats(LocalDate begin, LocalDate end);

    /**
     * 统计访客省份分布
     */
    List<ProvinceCountDTO> getProvinceDistribution();
}

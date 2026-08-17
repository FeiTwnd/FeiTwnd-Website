package cc.feitwnd.service.city;

import cc.feitwnd.dto.CityFootprintDTO;
import cc.feitwnd.dto.CityFootprintPageQueryDTO;
import cc.feitwnd.entity.CityFootprint;
import cc.feitwnd.result.PageResult;

import java.util.List;

public interface CityFootprintService {

    /**
     * 管理端分页查询城市足迹
     */
    PageResult pageQuery(CityFootprintPageQueryDTO pageQueryDTO);

    /**
     * 添加城市足迹
     */
    void add(CityFootprintDTO cityFootprintDTO);

    /**
     * 修改城市足迹
     */
    void update(CityFootprintDTO cityFootprintDTO);

    /**
     * 批量删除城市足迹（同时删除关联图片）
     */
    void batchDelete(List<Long> ids);

    /**
     * 博客端获取可见城市足迹
     */
    List<CityFootprint> getVisible();
}

package cc.feitwnd.service;

import cc.feitwnd.dto.ViewPageQueryDTO;
import cc.feitwnd.result.PageResult;

import java.util.List;

public interface ViewService {
    /**
     * 分页查询浏览记录
     * @param viewPageQueryDTO
     * @return
     */
    PageResult pageQuery(ViewPageQueryDTO viewPageQueryDTO);

    /**
     * 批量删除浏览记录
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

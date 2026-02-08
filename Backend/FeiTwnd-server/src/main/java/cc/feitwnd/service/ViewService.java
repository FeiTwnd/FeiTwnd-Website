package cc.feitwnd.service;

import cc.feitwnd.dto.ViewPageQueryDTO;
import cc.feitwnd.result.PageResult;

public interface ViewService {
    /**
     * 分页查询浏览记录
     * @param viewPageQueryDTO
     * @return
     */
    PageResult pageQuery(ViewPageQueryDTO viewPageQueryDTO);
}

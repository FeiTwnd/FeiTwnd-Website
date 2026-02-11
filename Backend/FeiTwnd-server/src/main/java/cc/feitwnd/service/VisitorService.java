package cc.feitwnd.service;

import cc.feitwnd.dto.VisitorPageQueryDTO;
import cc.feitwnd.dto.VisitorRecordDTO;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;

public interface VisitorService {
    /**
     * 记录访客访问信息
     * @param visitorRecordDTO
     * @param httpRequest
     * @return
     */
    VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest httpRequest);

    /**
     * 分页查询访客列表
     * @param visitorPageQueryDTO
     * @return
     */
    PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO);

    /**
     * 批量封禁访客
     * @param ids
     */
    void batchBlock(String ids);

    /**
     * 批量解封访客
     * @param ids
     */
    void batchUnblock(String ids);
}

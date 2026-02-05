package cc.feitwnd.service;

import cc.feitwnd.dto.VisitorRecordDTO;
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
}

package cc.feitwnd.controller.home;

import cc.feitwnd.dto.VisitorRecordDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.VisitorService;
import cc.feitwnd.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页端访客接口
 */
@RestController("homeVisitorController")
@RequestMapping("/home/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 记录访客访问信息
     * @param visitorRecordDTO
     * @param httpRequest
     * @return
     */
    @PostMapping("/record")
    public Result<VisitorRecordVO> recordVisitorViewInfo(@RequestBody VisitorRecordDTO visitorRecordDTO,
                                                         HttpServletRequest httpRequest) {
        VisitorRecordVO visitorRecordVO = visitorService.recordVisitorViewInfo(visitorRecordDTO, httpRequest);
        return Result.success(visitorRecordVO);
    }
}

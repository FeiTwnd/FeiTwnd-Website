package cc.feitwnd.controller.cv;

import cc.feitwnd.annotation.RateLimit;
import cc.feitwnd.dto.VisitorRecordDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.VisitorService;
import cc.feitwnd.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简历端访客接口
 */
@RestController("cvVisitorController")
@RequestMapping("/cv/visitor")
@Slf4j
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
    @RateLimit(type = RateLimit.Type.IP, tokens = 10, burstCapacity = 15,
            timeWindow = 60, message = "请求过于频繁，请稍后再试")
    public Result<VisitorRecordVO> recordVisitorViewInfo(@Valid @RequestBody VisitorRecordDTO visitorRecordDTO,
                                                         HttpServletRequest httpRequest) {
        log.info("记录访客访问信息：{}", visitorRecordDTO);
        VisitorRecordVO visitorRecordVO = visitorService.recordVisitorViewInfo(visitorRecordDTO, httpRequest);
        return Result.success(visitorRecordVO);
    }
}

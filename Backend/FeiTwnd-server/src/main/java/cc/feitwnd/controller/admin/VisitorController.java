package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.VisitorPageQueryDTO;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端访客接口
 */
@Slf4j
@RestController("adminVisitorController")
@RequestMapping("/admin/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 获取访客列表
     * @param visitorPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getVisitorList(VisitorPageQueryDTO visitorPageQueryDTO) {
        log.info("获取访客列表,{}", visitorPageQueryDTO);
        PageResult pageResult = visitorService.pageQuery(visitorPageQueryDTO);
        return Result.success(pageResult);
    }
}

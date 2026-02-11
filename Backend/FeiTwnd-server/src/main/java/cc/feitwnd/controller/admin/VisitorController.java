package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.VisitorPageQueryDTO;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 批量封禁访客
     * @param ids 逗号分隔的ID字符串
     * @return
     */
    @PutMapping("/block")
    @OperationLog(type = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result<String> batchBlock(@RequestParam String ids) {
        log.info("批量封禁访客: {}", ids);
        visitorService.batchBlock(ids);
        return Result.success();
    }

    /**
     * 批量解封访客
     * @param ids 逗号分隔的ID字符串
     * @return
     */
    @PutMapping("/unblock")
    @OperationLog(type = OperationType.UPDATE, target = "visitor", targetId = "#ids")
    public Result<String> batchUnblock(@RequestParam String ids) {
        log.info("批量解封访客: {}", ids);
        visitorService.batchUnblock(ids);
        return Result.success();
    }

}

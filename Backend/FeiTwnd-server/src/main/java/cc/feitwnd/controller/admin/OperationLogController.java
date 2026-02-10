package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.OperationLogPageQueryDTO;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.operationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端操作日志接口
 */
@Slf4j
@RestController("adminOperationLogController")
@RequestMapping("/admin/operationLog")
public class OperationLogController {

    @Autowired
    private operationLogService operationLogService;

    /**
     * 分页查询操作日志
     * @param operationLogPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        log.info("分页查询操作日志,{}", operationLogPageQueryDTO);
        PageResult pageResult = operationLogService.pageQuery(operationLogPageQueryDTO);
        return Result.success(pageResult);
    }
}

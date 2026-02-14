package cc.feitwnd.service;

import cc.feitwnd.dto.OperationLogPageQueryDTO;
import cc.feitwnd.entity.OperationLogs;
import cc.feitwnd.result.PageResult;

public interface OperationLogService {
    /**
     * 保存操作日志
     * @param operationLogs
     */
    void save(OperationLogs operationLogs);

    /**
     * 分页查询操作日志
     * @param operationLogPageQueryDTO
     * @return
     */
    PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO);
}

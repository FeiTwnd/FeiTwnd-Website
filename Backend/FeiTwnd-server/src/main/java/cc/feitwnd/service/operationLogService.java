package cc.feitwnd.service;

import cc.feitwnd.entity.OperationLogs;

public interface operationLogService {
    /**
     * 保存操作日志
     * @param operationLogs
     */
    void save(OperationLogs operationLogs);
}

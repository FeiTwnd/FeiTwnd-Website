package cc.feitwnd.service.impl;

import cc.feitwnd.entity.OperationLogs;
import cc.feitwnd.mapper.operationLogMapper;
import cc.feitwnd.service.operationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class operationLogServiceImpl implements operationLogService {

    @Autowired
    private operationLogMapper operationLogMapper;

    /**
     * 保存操作日志
     * @param operationLogs
     */
    public void save(OperationLogs operationLogs) {
        operationLogMapper.save(operationLogs);
    }
}

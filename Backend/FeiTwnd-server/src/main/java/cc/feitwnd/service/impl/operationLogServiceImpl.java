package cc.feitwnd.service.impl;

import cc.feitwnd.dto.OperationLogPageQueryDTO;
import cc.feitwnd.entity.OperationLogs;
import cc.feitwnd.mapper.operationLogMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.operationLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 分页查询操作日志
     * @param operationLogPageQueryDTO
     * @return
     */
    public PageResult pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO) {
        PageHelper.startPage(operationLogPageQueryDTO.getPage(), operationLogPageQueryDTO.getPageSize());
        Page<OperationLogs> page = operationLogMapper.pageQuery(operationLogPageQueryDTO);
        long total = page.getTotal();
        List<OperationLogs> records = page.getResult();
        return new PageResult(total, records);
    }
}

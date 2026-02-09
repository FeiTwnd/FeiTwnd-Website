package cc.feitwnd.mapper;

import cc.feitwnd.entity.OperationLogs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface operationLogMapper {
    /**
     * 保存操作日志
     * @param operationLogs
     */
    void save(OperationLogs operationLogs);
}

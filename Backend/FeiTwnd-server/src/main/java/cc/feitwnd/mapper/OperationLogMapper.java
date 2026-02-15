package cc.feitwnd.mapper;

import cc.feitwnd.dto.OperationLogPageQueryDTO;
import cc.feitwnd.entity.OperationLogs;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationLogMapper {
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
    Page<OperationLogs> pageQuery(OperationLogPageQueryDTO operationLogPageQueryDTO);

    /**
     * 批量删除操作日志
     * @param ids
     */
    void batchDelete(List<Long> ids);
}

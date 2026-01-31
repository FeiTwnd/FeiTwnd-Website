package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 管理员ID
    private Long adminId;

    // 操作类型
    private String operationType;

    // 操作目标
    private String operationTarget;

    // 目标ID
    private Integer targetId;

    // 操作数据
    private String opreateData;

    // 操作结果，0-失败，1-成功
    private Integer result;

    // 错误信息
    private String errorMessage;

    // 操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
}

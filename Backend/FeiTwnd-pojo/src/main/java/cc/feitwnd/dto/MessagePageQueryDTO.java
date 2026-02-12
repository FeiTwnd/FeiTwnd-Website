package cc.feitwnd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言分页查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageQueryDTO implements Serializable {

    private Integer page;

    private Integer pageSize;

    // 是否审核通过，0-否，1-是
    private Integer isApproved;

    // 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    // 结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

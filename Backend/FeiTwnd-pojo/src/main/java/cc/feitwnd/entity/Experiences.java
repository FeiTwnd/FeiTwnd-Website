package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经历
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experiences implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 类型，0-教育经历，1-实习及工作经历,2-项目经历
    private Integer type;

    // 是否可见
    private Integer isVisible;

    // 开始时间
    private LocalDate startDate;

    // 结束时间
    private LocalDate endDate;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

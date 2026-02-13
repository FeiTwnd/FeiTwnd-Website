package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日新增访客统计DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyVisitorCountDTO {

    // 日期
    private LocalDate date;

    // 当日新增访客数
    private Integer count;
}

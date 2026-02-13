package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每日浏览量统计DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyViewCountDTO {

    // 日期
    private LocalDate date;

    // 当日浏览量
    private Integer count;
}

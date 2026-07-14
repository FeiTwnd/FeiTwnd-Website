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
 * 城市足迹
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityFootprint implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 城市编码
    private String cityCode;

    // 城市名称
    private String cityName;

    // 访问时间
    private LocalDate visitTime;

    // 是否可见
    private Integer isVisible;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

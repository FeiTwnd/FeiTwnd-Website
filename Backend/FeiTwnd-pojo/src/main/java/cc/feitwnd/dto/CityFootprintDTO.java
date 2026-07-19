package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 城市足迹DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityFootprintDTO implements Serializable {

    private Long id;

    // 城市编码
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    // 城市名称
    @NotBlank(message = "城市名称不能为空")
    private String cityName;

    // 访问时间
    private LocalDate visitTime;

    // 是否可见
    private Integer isVisible;
}

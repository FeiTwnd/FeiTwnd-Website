package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 城市图片DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityImageDTO implements Serializable {

    private Long id;

    // 城市id
    @NotNull(message = "城市ID不能为空")
    private Long cityId;

    // 图片url
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;
}

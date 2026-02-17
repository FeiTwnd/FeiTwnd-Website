package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 社交媒体DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaDTO implements Serializable {

    private Long id;

    // 名称
    @NotBlank(message = "名称不能为空")
    @Size(max = 20, message = "名称不能超过20字")
    private String name;

    // 图标类名
    @Size(max = 50, message = "图标类名不能超过50字")
    private String icon;

    // 链接
    @Size(max = 100, message = "链接不能超过100字")
    private String link;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;
}

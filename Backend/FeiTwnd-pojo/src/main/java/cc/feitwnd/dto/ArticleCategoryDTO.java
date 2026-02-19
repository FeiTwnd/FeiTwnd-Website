package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章分类DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategoryDTO implements Serializable {

    private Long id;

    // 分类名称
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 20, message = "分类名称不能超过20字")
    private String name;

    // URL标识
    @NotBlank(message = "URL标识不能为空")
    @Size(max = 20, message = "URL标识不能超过20字")
    private String slug;

    // 分类描述
    @Size(max = 100, message = "分类描述不能超过100字")
    private String description;

    // 排序，越小越靠前
    private Integer sort;
}

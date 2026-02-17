package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章标签DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagDTO implements Serializable {

    private Long id;

    // 标签名称
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 20, message = "标签名称不能超过20字")
    private String name;

    // URL标识
    @NotBlank(message = "URL标识不能为空")
    @Size(max = 30, message = "URL标识不能超过30字")
    private String slug;
}

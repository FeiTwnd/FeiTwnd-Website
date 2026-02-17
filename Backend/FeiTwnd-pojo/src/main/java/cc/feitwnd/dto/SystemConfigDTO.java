package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统配置DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO implements Serializable {

    private Long id;

    // 配置键
    @NotBlank(message = "配置键不能为空")
    @Size(max = 50, message = "配置键不能超过50字")
    private String configKey;

    // 配置值
    private String configValue;

    // 配置类型
    @Size(max = 20, message = "配置类型不能超过20字")
    private String configType;

    // 配置描述
    @Size(max = 255, message = "配置描述不能超过255字")
    private String description;
}

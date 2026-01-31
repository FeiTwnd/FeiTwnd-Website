package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 配置键
    private String configKey;

    // 配置值
    private String configValue;

    // 配置类型
    private String configType;

    // 配置描述
    private String description;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

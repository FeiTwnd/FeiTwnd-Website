package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skills implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 技能名称
    private String name;

    // 技能描述
    private String description;

    // 图标url
    private String icon;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

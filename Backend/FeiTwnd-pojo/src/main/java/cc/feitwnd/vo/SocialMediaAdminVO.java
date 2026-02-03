package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaAdminVO {
    private Long id;

    // 名称
    private String name;

    // 图标类名
    private String icon;

    // 链接
    private String link;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;
}

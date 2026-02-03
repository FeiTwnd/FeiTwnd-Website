package cc.feitwnd.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialMediaVO {
    private Long id;

    // 名称
    private String name;

    // 图标类名
    private String icon;

    // 链接
    private String link;

    // 排序，越小越靠前
    private Integer sort;
}

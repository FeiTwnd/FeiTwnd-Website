package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendLinkVO implements Serializable {
    private Long id;

    // 网站名称
    private String name;

    // 网站地址
    private String url;

    // 头像url
    private String avatarUrl;

    // 网站描述
    private String description;

    // 排序
    private Integer sort;
}

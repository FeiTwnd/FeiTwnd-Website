package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 友情链接DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendLinkDTO implements Serializable {

    private Long id;

    // 网站名称
    @NotBlank(message = "网站名称不能为空")
    @Size(max = 20, message = "网站名称不能超过20字")
    private String name;

    // 网站地址
    @NotBlank(message = "网站地址不能为空")
    @Size(max = 100, message = "网站地址不能超过100字")
    private String url;

    // 头像url
    private String avatarUrl;

    // 网站描述
    @Size(max = 255, message = "网站描述不能超过255字")
    private String description;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;
}

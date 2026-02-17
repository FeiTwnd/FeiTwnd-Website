package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 个人信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO implements Serializable {

    private Long id;

    // 昵称
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称不能超过20字")
    private String nickname;

    // 标签
    @NotBlank(message = "标签不能为空")
    @Size(max = 30, message = "标签不能超过30字")
    private String tag;

    // 个人简介
    @Size(max = 50, message = "个人简介不能超过50字")
    private String description;

    // 头像url
    private String avatar;

    // 个人网站
    @Size(max = 100, message = "网站地址不能超过100字")
    private String website;

    // 电子邮箱
    @Size(max = 50, message = "邮箱不能超过50字")
    private String email;

    // GitHub
    @Size(max = 100, message = "GitHub地址不能超过100字")
    private String github;

    // 所在地
    @Size(max = 50, message = "所在地不能超过50字")
    private String location;
}

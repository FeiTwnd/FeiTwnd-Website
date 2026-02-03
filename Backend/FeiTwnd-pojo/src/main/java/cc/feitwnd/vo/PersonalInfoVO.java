package cc.feitwnd.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PersonalInfoVO implements Serializable {

    private Long id;

    // 昵称
    private String nickname;

    // 标签
    private String tag;

    // 个人简介
    private String description;

    // 头像url
    private String avatar;

    // 个人网站
    private String website;

    // 电子邮箱
    private String email;

    // GitHub
    private String github;

    // 所在地
    private String location;
}

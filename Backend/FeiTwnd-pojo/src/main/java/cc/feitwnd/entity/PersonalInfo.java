package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 个人信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

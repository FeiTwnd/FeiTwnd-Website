package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 用户名
    private String username;

    // 加密后的密码
    private String password;

    // 电子邮箱
    private String email;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

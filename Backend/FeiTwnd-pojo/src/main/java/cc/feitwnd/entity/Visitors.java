package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访客
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visitors implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 访客指纹,用于唯一标识访客
    private String fingerprint;

    // 会话ID(当前浏览器会话)
    private String sessionId;

    // IP地址
    private String ip;

    // 用户代理
    private String userAgent;

    // 国家
    private String country;

    // 省份
    private String province;

    // 城市
    private String city;

    // 经度
    private String longitude;

    // 纬度
    private String latitude;

    // 首次访问时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstVisitTime;

    // 最后访问时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastVisitTime;

    // 访问次数
    private Long totalViews;

    // 是否被封禁,0-否，1-是
    private Integer isBlocked;

    // 封禁结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

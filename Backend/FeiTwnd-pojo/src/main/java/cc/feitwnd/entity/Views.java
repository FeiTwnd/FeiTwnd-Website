package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 浏览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Views implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 访客ID
    private Long visitorId;

    // 页面路径
    private String pagePath;

    // 来源URL
    private String referer;

    // IP地址
    private String ipAddress;

    // 用户代理
    private String userAgent;

    // 访问时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewTime;
}

package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Messages implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 评论内容
    private String content;

    // 转换后的HTML内容
    private String contentHtml;

    // 根留言ID,null是一级留言
    private Long rootId;

    // 父留言ID,null是一级留言
    private Long parentId;

    // 昵称
    private String nickname;

    // 邮箱或qq
    private String emailOrQq;

    // IP地址
    private String ipAddress;

    // 地址
    private String location;

    // 用户代理
    private String userAgent;

    // 操作系统名称
    private String userAgentOs;

    // 浏览器名称
    private String userAgentBrowser;

    // 用户指纹
    private String userFingerprint;

    // 是否审核通过，0-否，1-是
    private Integer isApproved;

    // 是否使用markdown，0-否，1-是
    private Integer isMarkdown;

    // 是否匿名，0-否，1-是
    private Integer isSecret;

    // 有回复是否通知，0-否，1-是
    private Integer isNotice;

    // 是否编辑过，0-否，1-是
    private Integer isEdited;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

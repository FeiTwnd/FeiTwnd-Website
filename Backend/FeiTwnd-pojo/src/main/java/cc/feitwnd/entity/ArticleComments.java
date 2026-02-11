package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章评论
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComments implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 文章ID
    private Long articleId;

    // 根评论ID,null是一级评论
    private Long rootId;

    // 父评论ID,null是一级评论
    private Long parentId;

    // 父评论昵称
    private String parentNickname;

    // 评论内容
    private String content;

    // 转换后的HTML内容
    private String contentHtml;

    // 访客ID
    private Long visitorId;

    // 昵称
    private String nickname;

    // 邮箱或qq
    private String emailOrQq;

    // 地址
    private String location;

    // 操作系统名称
    private String userAgentOs;

    // 浏览器名称
    private String userAgentBrowser;

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

    // 是否为管理员回复，0-否，1-是
    private Integer isAdminReply;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

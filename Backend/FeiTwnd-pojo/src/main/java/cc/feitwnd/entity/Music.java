package cc.feitwnd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 音乐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 音乐标题
    private String title;

    // 作者
    private String artist;

    // 时长，单位：秒
    private Integer duration;

    // 封面图片url
    private String coverImage;

    // 音频文件url
    private String musicUrl;

    // 歌词文件url
    private String lyricUrl;

    // 是否有歌词，0-否，1-是
    private Integer hasLyric;

    // 歌词类型,lrc,json,txt
    private String lyricType;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

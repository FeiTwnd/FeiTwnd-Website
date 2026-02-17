package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 音乐DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO implements Serializable {

    private Long id;

    // 音乐标题
    @NotBlank(message = "音乐标题不能为空")
    @Size(max = 50, message = "音乐标题不能超过50字")
    private String title;

    // 作者
    @Size(max = 50, message = "作者名称不能超过50字")
    private String artist;

    // 时长，单位：秒
    private Integer duration;

    // 封面图片url
    private String coverImage;

    // 音频文件url
    @NotBlank(message = "音频文件不能为空")
    private String musicUrl;

    // 歌词文件url
    private String lyricUrl;

    // 是否有歌词，0-否，1-是
    private Integer hasLyric;

    // 歌词类型,lrc,json,txt
    @Size(max = 10, message = "歌词类型不能超过10字")
    private String lyricType;

    // 排序，越小越靠前
    private Integer sort;

    // 是否可见
    private Integer isVisible;
}

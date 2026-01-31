package cc.feitwnd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 项目经历
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperiencesProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 经历ID
    private Long experienceId;

    // 项目名称
    private String projectName;

    // 项目内容
    private String content;
}

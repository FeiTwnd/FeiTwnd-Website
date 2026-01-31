package cc.feitwnd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教育经历
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperiencesEducation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 经历ID
    private Long experienceId;

    // 校徽url
    private String badge;

    // 学校名称
    private String school;

    // 专业名称
    private String major;
}

package cc.feitwnd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实习及工作经历
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperiencesWork implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 经历ID
    private Long experienceId;

    // 公司logo
    private String logo;

    // 公司名称
    private String company;

    // 职位名称
    private String position;

    // 工作内容
    private String content;
}

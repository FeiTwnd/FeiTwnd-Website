package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 经历DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO implements Serializable {

    private Long id;

    // 类型，0-教育经历，1-实习及工作经历,2-项目经历
    @NotNull(message = "经历类型不能为空")
    private Integer type;

    // 标题,公司名/学校名/项目名
    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题不能超过50字")
    private String title;

    // 副标题,职位/专业/项目角色
    @Size(max = 100, message = "副标题不能超过100字")
    private String subtitle;

    // logo
    private String logoUrl;

    // 内容
    @NotBlank(message = "内容不能为空")
    private String content;

    // 开始时间
    @NotNull(message = "开始时间不能为空")
    private LocalDate startDate;

    // 结束时间
    private LocalDate endDate;

    // 是否可见
    private Integer isVisible;
}

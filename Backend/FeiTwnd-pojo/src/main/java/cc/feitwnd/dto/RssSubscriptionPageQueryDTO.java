package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RssSubscriptionPageQueryDTO {

    // 页码
    private int page;

    // 每页显示数量
    private int pageSize;

    // 邮箱
    private String email;

    // 是否激活，0-否，1-是
    private Integer isActive;
}

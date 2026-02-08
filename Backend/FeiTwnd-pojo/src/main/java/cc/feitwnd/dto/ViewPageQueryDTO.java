package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewPageQueryDTO {

    // 页码
    private int page;

    // 每页显示数量
    private int pageSize;

    // 页面路径
    private String pagePath;

    // 来源URL
    private String referer;

    // 访客ID
    private Long visitorId;
}

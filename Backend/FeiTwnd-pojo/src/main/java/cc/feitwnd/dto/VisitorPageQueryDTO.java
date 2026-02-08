package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitorPageQueryDTO {

    // 页码
    private int page;

    // 每页显示数量
    private int pageSize;

    // 国家
    private String country;

    // 省份
    private String province;

    // 城市
    private String city;

    // 状态,是否被封禁 0正常 1封禁
    private Integer status;
}

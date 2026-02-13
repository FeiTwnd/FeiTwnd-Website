package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访客省份分布统计VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceVisitorVO {

    // 省份，以逗号分隔，例如：广东,北京,浙江
    private String provinceList;

    // 对应省份的访客数，以逗号分隔，例如：120,85,60
    private String countList;
}

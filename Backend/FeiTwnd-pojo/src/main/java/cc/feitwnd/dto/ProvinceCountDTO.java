package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省份访客数量DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceCountDTO {

    // 省份
    private String province;

    // 访客数量
    private Integer count;
}

package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 城市足迹分页查询DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityFootprintPageQueryDTO {

    private int page;

    private int pageSize;

    private String cityName;
}

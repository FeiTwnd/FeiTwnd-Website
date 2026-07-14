package cc.feitwnd.service;

import cc.feitwnd.dto.CityImageDTO;
import cc.feitwnd.entity.CityImage;

import java.util.List;

public interface CityImageService {

    /**
     * 获取某城市的所有图片
     */
    List<CityImage> getByCityId(Long cityId);

    /**
     * 添加城市图片
     */
    void add(CityImageDTO cityImageDTO);

    /**
     * 修改城市图片
     */
    void update(CityImageDTO cityImageDTO);

    /**
     * 删除单张图片
     */
    void delete(Long id);
}

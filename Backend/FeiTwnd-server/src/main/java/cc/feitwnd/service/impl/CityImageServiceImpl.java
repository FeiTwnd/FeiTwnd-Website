package cc.feitwnd.service.impl;

import cc.feitwnd.dto.CityImageDTO;
import cc.feitwnd.entity.CityImage;
import cc.feitwnd.mapper.CityImageMapper;
import cc.feitwnd.service.CityImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CityImageServiceImpl implements CityImageService {

    @Autowired
    private CityImageMapper cityImageMapper;

    @Override
    public List<CityImage> getByCityId(Long cityId) {
        List<CityImage> list = cityImageMapper.getByCityId(cityId);
        return list != null && !list.isEmpty() ? list : Collections.emptyList();
    }

    @Override
    public void add(CityImageDTO cityImageDTO) {
        CityImage cityImage = new CityImage();
        BeanUtils.copyProperties(cityImageDTO, cityImage);
        cityImageMapper.insert(cityImage);
    }

    @Override
    public void update(CityImageDTO cityImageDTO) {
        CityImage cityImage = new CityImage();
        BeanUtils.copyProperties(cityImageDTO, cityImage);
        cityImageMapper.update(cityImage);
    }

    @Override
    public void delete(Long id) {
        cityImageMapper.delete(id);
    }
}

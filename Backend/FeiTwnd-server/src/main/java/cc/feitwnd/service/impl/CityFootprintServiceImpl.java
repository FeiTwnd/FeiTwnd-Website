package cc.feitwnd.service.impl;

import cc.feitwnd.dto.CityFootprintDTO;
import cc.feitwnd.dto.CityFootprintPageQueryDTO;
import cc.feitwnd.entity.CityFootprint;
import cc.feitwnd.mapper.CityFootprintMapper;
import cc.feitwnd.mapper.CityImageMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.CityFootprintService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CityFootprintServiceImpl implements CityFootprintService {

    @Autowired
    private CityFootprintMapper cityFootprintMapper;

    @Autowired
    private CityImageMapper cityImageMapper;

    @Override
    public PageResult pageQuery(CityFootprintPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<CityFootprint> page = cityFootprintMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(CityFootprintDTO cityFootprintDTO) {
        CityFootprint cityFootprint = new CityFootprint();
        BeanUtils.copyProperties(cityFootprintDTO, cityFootprint);
        cityFootprintMapper.insert(cityFootprint);
    }

    @Override
    public void update(CityFootprintDTO cityFootprintDTO) {
        CityFootprint cityFootprint = new CityFootprint();
        BeanUtils.copyProperties(cityFootprintDTO, cityFootprint);
        cityFootprintMapper.update(cityFootprint);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            cityImageMapper.batchDeleteByCityId(id);
        }
        cityFootprintMapper.batchDelete(ids);
    }
}

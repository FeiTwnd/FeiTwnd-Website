package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.CityFootprintDTO;
import cc.feitwnd.dto.CityFootprintPageQueryDTO;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.city.CityFootprintService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端城市足迹接口
 */
@RestController("adminCityFootprintController")
@RequestMapping("/admin/footprint")
@Slf4j
public class CityFootprintController {

    @Autowired
    private CityFootprintService cityFootprintService;

    /**
     * 分页查询城市足迹
     */
    @GetMapping
    public Result<PageResult> pageQuery(CityFootprintPageQueryDTO pageQueryDTO) {
        PageResult pageResult = cityFootprintService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 添加城市足迹
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "cityFootprint")
    public Result add(@Valid @RequestBody CityFootprintDTO cityFootprintDTO) {
        log.info("添加城市足迹:{}", cityFootprintDTO);
        cityFootprintService.add(cityFootprintDTO);
        return Result.success();
    }

    /**
     * 修改城市足迹
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "cityFootprint", targetId = "#cityFootprintDTO.id")
    public Result update(@Valid @RequestBody CityFootprintDTO cityFootprintDTO) {
        log.info("修改城市足迹:{}", cityFootprintDTO);
        cityFootprintService.update(cityFootprintDTO);
        return Result.success();
    }

    /**
     * 批量删除城市足迹
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "cityFootprint", targetId = "#ids")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除城市足迹:{}", ids);
        cityFootprintService.batchDelete(ids);
        return Result.success();
    }
}

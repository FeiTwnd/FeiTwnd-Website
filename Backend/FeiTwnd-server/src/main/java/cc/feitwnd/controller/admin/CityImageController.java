package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.dto.CityImageDTO;
import cc.feitwnd.entity.CityImage;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.city.CityImageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端城市图片接口
 */
@RestController("adminCityImageController")
@RequestMapping("/admin/footprint/image")
@Slf4j
public class CityImageController {

    @Autowired
    private CityImageService cityImageService;

    /**
     * 获取某城市的所有图片
     */
    @GetMapping
    public Result<List<CityImage>> getByCityId(@RequestParam Long cityId) {
        List<CityImage> list = cityImageService.getByCityId(cityId);
        return Result.success(list);
    }

    /**
     * 添加城市图片
     */
    @PostMapping
    @OperationLog(value = OperationType.INSERT, target = "cityImage")
    public Result add(@Valid @RequestBody CityImageDTO cityImageDTO) {
        log.info("添加城市图片:{}", cityImageDTO);
        cityImageService.add(cityImageDTO);
        return Result.success();
    }

    /**
     * 修改城市图片
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "cityImage", targetId = "#cityImageDTO.id")
    public Result update(@Valid @RequestBody CityImageDTO cityImageDTO) {
        log.info("修改城市图片:{}", cityImageDTO);
        cityImageService.update(cityImageDTO);
        return Result.success();
    }

    /**
     * 删除单张图片
     */
    @DeleteMapping
    @OperationLog(value = OperationType.DELETE, target = "cityImage", targetId = "#id")
    public Result delete(@RequestParam Long id) {
        log.info("删除城市图片:{}", id);
        cityImageService.delete(id);
        return Result.success();
    }
}

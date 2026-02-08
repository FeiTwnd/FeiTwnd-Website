package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.ViewPageQueryDTO;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端浏览记录接口
 */
@Slf4j
@RestController("adminViewController")
@RequestMapping("/admin/view")
public class ViewController {

    @Autowired
    private ViewService viewService;

    /**
     * 获取浏览记录列表
     * @param viewPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> getViewList(ViewPageQueryDTO viewPageQueryDTO) {
        log.info("获取浏览记录列表,{}", viewPageQueryDTO);
        PageResult pageResult = viewService.pageQuery(viewPageQueryDTO);
        return Result.success(pageResult);
    }

}

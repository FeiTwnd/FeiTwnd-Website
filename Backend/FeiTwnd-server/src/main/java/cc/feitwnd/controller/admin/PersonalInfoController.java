package cc.feitwnd.controller.admin;

import cc.feitwnd.dto.PersonalInfoDTO;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.vo.PersonalInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端个人信息接口
 */
@RestController
@RequestMapping("/admin/info")
public class PersonalInfoController {

    @Autowired
    private PersonalInfoService personalInfoService;

    /**
     * 获取个人信息
     */
    @GetMapping
    public Result<PersonalInfoVO> getPersonalInfo() {
        PersonalInfoVO personalInfoVO = personalInfoService.getPersonalInfo();
        return Result.success(personalInfoVO);
    }

    /**
     * 更新个人信息
     */
    @PutMapping
    public Result updatePersonalInfo(@RequestBody PersonalInfoDTO personalInfoDTO) {
        personalInfoService.updatePersonalInfo(personalInfoDTO);
        return Result.success();
    }
}

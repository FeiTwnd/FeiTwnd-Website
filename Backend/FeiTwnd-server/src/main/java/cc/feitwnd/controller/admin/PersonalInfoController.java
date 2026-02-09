package cc.feitwnd.controller.admin;

import cc.feitwnd.annotation.OperationLog;
import cc.feitwnd.entity.PersonalInfo;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.result.Result;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.vo.PersonalInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端个人信息接口
 */
@RestController("adminPersonalInfoController")
@RequestMapping("/admin/personalInfo")
@Slf4j
public class PersonalInfoController {

    @Autowired
    private PersonalInfoService personalInfoService;

    /**
     * 获取个人信息
     */
    @GetMapping
    public Result<PersonalInfo> getPersonalInfo() {
        PersonalInfo personalInfo = personalInfoService.getAllPersonalInfo();
        return Result.success(personalInfo);
    }

    /**
     * 更新个人信息
     */
    @PutMapping
    @OperationLog(value = OperationType.UPDATE, target = "personalInfo", targetId = "#personalInfo.id")
    public Result updatePersonalInfo(@RequestBody PersonalInfo personalInfo) {
        log.info("更新个人信息: {}", personalInfo);
        personalInfoService.updatePersonalInfo(personalInfo);
        return Result.success();
    }
}

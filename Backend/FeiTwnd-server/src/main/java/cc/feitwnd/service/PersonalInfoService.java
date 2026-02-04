package cc.feitwnd.service;

import cc.feitwnd.entity.PersonalInfo;
import cc.feitwnd.vo.PersonalInfoVO;

public interface PersonalInfoService {
    /**
     * 获取个人信息
     */
    PersonalInfo getAllPersonalInfo();

    /**
     * 更新个人信息
     */
    void updatePersonalInfo(PersonalInfo personalInfo);

    /**
     * 其他端获取个人信息
     */
    PersonalInfoVO getPersonalInfo();
}

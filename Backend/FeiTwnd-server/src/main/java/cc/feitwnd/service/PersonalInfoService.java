package cc.feitwnd.service;

import cc.feitwnd.entity.PersonalInfo;
import cc.feitwnd.vo.PersonalInfoVO;

public interface PersonalInfoService {
    /**
     * 获取个人信息
     * @return
     */
    PersonalInfo getAllPersonalInfo();

    /**
     * 更新个人信息
     * @param personalInfo
     */
    void updatePersonalInfo(PersonalInfo personalInfo);

    /**
     * 其他端获取个人信息
     * @return
     */
    PersonalInfoVO getPersonalInfo();
}

package cc.feitwnd.service;

import cc.feitwnd.vo.PersonalInfoVO;

public interface PersonalInfoService {
    /**
     * 获取个人信息
     */
    PersonalInfoVO getPersonalInfo();

    /**
     * 更新个人信息
     */
    void updatePersonalInfo(PersonalInfoVO personalInfoVO);
}

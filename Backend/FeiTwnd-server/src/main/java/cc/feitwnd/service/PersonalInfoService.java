package cc.feitwnd.service;

import cc.feitwnd.dto.PersonalInfoDTO;
import cc.feitwnd.vo.PersonalInfoVO;

public interface PersonalInfoService {
    /**
     * 获取个人信息
     */
    PersonalInfoVO getPersonalInfo();

    /**
     * 更新个人信息
     */
    void updatePersonalInfo(PersonalInfoDTO personalInfoDTO);
}

package cc.feitwnd.service.impl;


import cc.feitwnd.entity.PersonalInfo;
import cc.feitwnd.mapper.PersonalInfoMapper;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.vo.PersonalInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Autowired
    private PersonalInfoMapper personalInfoMapper;

    /**
     * 管理端获取所有个人信息
     * @return
     */
    @Cacheable(value = "personalInfo", key = "'all'")
    public PersonalInfo getAllPersonalInfo() {
        PersonalInfo personalInfo = personalInfoMapper.getPersonalInfo();
        return personalInfo;
    }

    /**
     * 管理端更新个人信息
     * @param personalInfo
     */
    @CacheEvict(value = "personalInfo", allEntries = true)
    public void updatePersonalInfo(PersonalInfo personalInfo) {
        // 更新个人信息
        personalInfoMapper.updateById(personalInfo);
    }

    /**
     * 其他端获取个人信息
     * @return
     */
    @Cacheable(value = "personalInfo", key = "'vo'")
    public PersonalInfoVO getPersonalInfo() {
        PersonalInfo personalInfo = personalInfoMapper.getPersonalInfo();
        PersonalInfoVO personalInfoVO = PersonalInfoVO.builder()
                .id(personalInfo.getId())
                .nickname(personalInfo.getNickname())
                .tag(personalInfo.getTag())
                .description(personalInfo.getDescription())
                .avatar(personalInfo.getAvatar())
                .website(personalInfo.getWebsite())
                .email(personalInfo.getEmail())
                .github(personalInfo.getGithub())
                .location(personalInfo.getLocation())
                .build();
        return personalInfoVO;
    }
}

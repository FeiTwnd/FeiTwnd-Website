package cc.feitwnd.service.impl;


import cc.feitwnd.context.BaseContext;
import cc.feitwnd.dto.PersonalInfoDTO;
import cc.feitwnd.entity.PersonalInfo;
import cc.feitwnd.mapper.PersonalInfoMapper;
import cc.feitwnd.service.PersonalInfoService;
import cc.feitwnd.vo.PersonalInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Autowired
    private PersonalInfoMapper personalInfoMapper;

    /**
     * 获取个人信息
     */
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

    /**
     * 更新个人信息
     */
    public void updatePersonalInfo(PersonalInfoDTO personalInfoDTO) {
        // 获取管理员的个人信息
        PersonalInfo personalInfo = personalInfoMapper.getPersonalInfo();
        // 更新个人信息
        personalInfo.setId(personalInfo.getId());
        personalInfo.setNickname(personalInfoDTO.getNickname());
        personalInfo.setTag(personalInfoDTO.getTag());
        personalInfo.setDescription(personalInfoDTO.getDescription());
        personalInfo.setAvatar(personalInfoDTO.getAvatar());
        personalInfo.setWebsite(personalInfoDTO.getWebsite());
        personalInfo.setEmail(personalInfoDTO.getEmail());
        personalInfo.setGithub(personalInfoDTO.getGithub());
        personalInfo.setLocation(personalInfoDTO.getLocation());
        personalInfoMapper.updateById(personalInfo);
    }
}

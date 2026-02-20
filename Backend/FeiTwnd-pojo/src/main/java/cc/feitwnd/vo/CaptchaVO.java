package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 验证码VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 验证码ID */
    private String captchaId;

    /** 算术题目，如 "3 + 5 = ?" */
    private String question;

    /** 正确答案 */
    private int result;
}

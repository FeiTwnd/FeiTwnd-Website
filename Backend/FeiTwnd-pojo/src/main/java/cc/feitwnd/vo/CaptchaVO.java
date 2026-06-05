package cc.feitwnd.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 验证码VO（仅返回题目，答案服务端存储）
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
}

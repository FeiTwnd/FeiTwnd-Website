package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.vo.CaptchaVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 博客端公共接口
 */
@RestController
@RequestMapping("/blog/common")
public class CommonController {

    private final Random random = new Random();

    /**
     * 生成算术验证码
     */
    @GetMapping("/captcha/generate")
    public Result<CaptchaVO> generateCaptcha() {
        int num1 = random.nextInt(9) + 1;
        int num2 = random.nextInt(9) + 1;

        String[] operators = {"+", "-", "×"};
        String operator = operators[random.nextInt(operators.length)];

        int result;
        switch (operator) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "×": result = num1 * num2; break;
            default: result = num1 + num2;
        }

        String question = num1 + " " + operator + " " + num2 + " = ?";
        String captchaId = "captcha_" + System.currentTimeMillis() + "_" + random.nextInt(1000);

        CaptchaVO captchaVO = CaptchaVO.builder()
                .captchaId(captchaId)
                .question(question)
                .result(result)
                .build();

        return Result.success(captchaVO);
    }
}

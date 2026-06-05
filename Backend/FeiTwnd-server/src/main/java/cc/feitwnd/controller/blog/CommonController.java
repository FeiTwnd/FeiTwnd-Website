package cc.feitwnd.controller.blog;

import cc.feitwnd.result.Result;
import cc.feitwnd.service.CaptchaService;
import cc.feitwnd.vo.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 博客端公共接口
 */
@RestController("blogCommonController")
@RequestMapping("/blog/common")
public class CommonController {

    private final Random random = new Random();

    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成算术验证码（答案存储服务端，不返回给客户端）
     */
    @GetMapping("/captcha/generate")
    public Result<CaptchaVO> generateCaptcha() {
        int num1 = random.nextInt(9) + 1;
        int num2 = random.nextInt(9) + 1;

        String[] operators = {"+", "-", "×"};
        String operator = operators[random.nextInt(operators.length)];

        // 确保减法不会产生负数
        if ("-".equals(operator) && num1 < num2) {
            int temp = num1; num1 = num2; num2 = temp;
        }

        int result;
        switch (operator) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "×": result = num1 * num2; break;
            default: result = num1 + num2;
        }

        String question = num1 + " " + operator + " " + num2 + " = ?";
        String captchaId = "captcha_" + System.currentTimeMillis() + "_" + random.nextInt(1000);

        // 答案存储服务端
        captchaService.put(captchaId, result);

        CaptchaVO captchaVO = CaptchaVO.builder()
                .captchaId(captchaId)
                .question(question)
                .build();

        return Result.success(captchaVO);
    }
}

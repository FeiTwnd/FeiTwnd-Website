package cc.feitwnd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feitwnd.email")
@Data
public class EmailProperties {
    /**
     * 邮箱服务器邮箱
     */
    private String personal;

    private String from;
}

package cc.feitwnd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feitwnd.visitor")
@Data
public class VisitorProperties {
    private String verifyCode;
}

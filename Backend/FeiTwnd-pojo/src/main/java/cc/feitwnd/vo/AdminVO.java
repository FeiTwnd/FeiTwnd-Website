package cc.feitwnd.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AdminVO implements Serializable {
    private Long id;
    private String nickName;
    private String email;
}

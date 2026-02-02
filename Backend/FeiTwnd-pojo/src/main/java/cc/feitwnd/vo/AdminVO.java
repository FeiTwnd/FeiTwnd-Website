package cc.feitwnd.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminVO {
    private Long id;
    private String nickName;
    private String email;
}

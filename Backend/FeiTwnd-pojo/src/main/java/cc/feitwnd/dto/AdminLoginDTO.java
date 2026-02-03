package cc.feitwnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginDTO implements Serializable {

    private String username;

    private String password;

    private String code;
}

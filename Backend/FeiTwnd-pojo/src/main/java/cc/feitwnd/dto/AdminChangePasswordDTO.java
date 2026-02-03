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
public class AdminChangePasswordDTO implements Serializable {

    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;
}

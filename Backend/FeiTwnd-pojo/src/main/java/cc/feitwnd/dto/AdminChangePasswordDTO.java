package cc.feitwnd.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminChangePasswordDTO implements Serializable {

    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;
}

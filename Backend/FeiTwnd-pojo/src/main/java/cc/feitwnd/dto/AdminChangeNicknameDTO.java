package cc.feitwnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangeNicknameDTO {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称不能超过30字")
    private String nickname;
}

package com.indra.userservice.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @NotNull(message = "아이디 값은 입력이 필요합니다.")
    @Size(min = 3, message = "최소 3자 이상 입력하세요.")
    private String accountId;

    @NotNull(message = "비밀번호 값은 입력이 필요합니다.")
    @Size(min = 8, message = "최소 8자 이상 입력하세요.")
    private String password;

}

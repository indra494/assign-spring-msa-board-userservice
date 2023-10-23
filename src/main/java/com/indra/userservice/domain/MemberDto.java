package com.indra.userservice.domain;

import com.indra.userservice.type.RoleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberDto {

    private long id;

    @NotNull(message = "아이디 입력은 필수 입니다.")
    @Size(min = 3, max=30, message = "아이디는 최소 3글자 이상, 최대 30자 이하로 입력해야합니다.")
    private String accountId;

    @NotNull(message = "비밀번호 입력은 필수 입니다.")
    @Size(min=8, max=255, message = "비밀번호는 최소 8글자 이상 입력해야합니다.")
    private String password;

    @NotNull(message = "이름 입력은 필수 입니다.")
    @Size(min=1, max=30, message = "이름은 최소 1글자 이상, 최대 30글자 이하로 입력해야합니다.")
    private String name;

    @NotNull(message = "전화번호 입력은 필수 입니다.")
    @Size(min=13, max=13, message = "전화번호는 13글자로 입력해야합니다.")
    private String contact;

    @NotNull(message = "주소 입력은 필수 입니다.")
    @Size(min=1, max=255, message = "주소는 최소 1글자 이상, 255글자 이하로 입력해야합니다.")
    private String address;

    private long companyId;

    @NotNull(message = "권한 입력은 필수 입니다.")
    private RoleType role;

}

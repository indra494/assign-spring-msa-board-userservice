package com.indra.userservice.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyDto {
    private long id;

    @NotNull(message = "회사명 입력은 필수 입니다.")
    @Size(min=1, max=30, message = "회사명은 최소 1글자 이상, 최대 30글자 이하로 입력해야합니다.")
    private String name;

    @NotNull(message = "회사설명 입력은 필수 입니다.")
    @Size(min=1, max=255, message = "회사설명은 최소 1글자 이상, 최대 255글자 이하로 입력해야합니다.")
    private String description;

}

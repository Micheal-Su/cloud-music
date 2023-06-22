package com.tacitn.all.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Password {
    private Integer consumerId;
    private String oldPassword;
    private String newPassword;
}

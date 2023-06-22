package com.tacitn.feign.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Admin {
    private Long id;

    private String name;

    private String password;

}

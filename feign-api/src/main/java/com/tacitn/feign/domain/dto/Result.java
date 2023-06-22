package com.tacitn.feign.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;
    private Integer code;
    private String type;
    private Object data;

    public static Result ok() {
        return new Result(true, null,1,"success", null);
    }

    public static Result ok(Object data) {
        return new Result(true, null,1,"success", data);
    }

    public static Result ok(List<?> data) {
        return new Result(true, null,1,"success", data);
    }

    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg,0,"error", null);
    }

    public static Result warning(String warningMsg) {
        return new Result(false, warningMsg,2,"warning", null);
    }
}

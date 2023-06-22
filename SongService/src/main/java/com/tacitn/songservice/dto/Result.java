package com.tacitn.songservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String msg;
    private Integer code;
    private String type;
    private Object data;
    private long total;

    public static Result ok() {
        return new Result(true, null,1,"success", null,1);
    }

    public static Result ok(Object data) {
        return new Result(true, null,1,"success", data,1);
    }

    public static Result ok(List<?> data) {
        return new Result(true, null,1,"success", data,data.size());
    }

    public static Result ok(Map<?,?> data) {
        return new Result(true, null,1,"success", data,data.size());
    }

    public static Result pageOk(List<?> data, long total) {
        return new Result(true, null,1,"success", data,total);
    }

    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg,0,"error", null,0);
    }

    public static Result warning(String warningMsg) {
        return new Result(false, warningMsg,2,"warning", null,0);
    }
}

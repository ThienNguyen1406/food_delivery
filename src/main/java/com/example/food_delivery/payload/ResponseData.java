package com.example.food_delivery.payload;

/**
 * status code
 * desc
 * data{} ??
 */

public class ResponseData {
    private int  code;
    private String description;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}


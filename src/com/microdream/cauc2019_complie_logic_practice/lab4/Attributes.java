package com.microdream.complie_logic.lab4;

public class Attributes{
    private String place;
    private String code;
    public Attributes() {}
    public Attributes(String name, String code) {
        this.place = name;
        this.code = code;
    }
    public void setName(String name) {
        this.place = name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return this.place;
    }
    public String getCode() {
        return this.code;
    }
    public String toString() {
        return "[" + this.place + "," + this.code + "]";
    }
}
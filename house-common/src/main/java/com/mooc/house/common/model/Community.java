package com.mooc.house.common.model;

public class Community {
    private Integer id;
    private String cityNode;
    private String cityName;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityNode() {
        return cityNode;
    }

    public void setCityNode(String cityNode) {
        this.cityNode = cityNode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

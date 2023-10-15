package de.florianisme.wakeonlan.models;


import com.google.gson.annotations.SerializedName;

public class DeviceDto {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public DeviceDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public DeviceDto() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

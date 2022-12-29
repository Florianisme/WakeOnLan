package de.florianisme.wakeonlan.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceDto {

    @JsonProperty("id")
    private final int id;

    @JsonProperty("name")
    private final String name;

    public DeviceDto(@JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

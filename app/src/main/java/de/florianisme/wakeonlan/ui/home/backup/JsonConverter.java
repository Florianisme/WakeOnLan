package de.florianisme.wakeonlan.ui.home.backup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class JsonConverter {

    public static byte[] toJson(List<Device> devices) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(devices);
    }

    public static Device[] toModel(byte[] content) throws IOException {
        return new ObjectMapper().readValue(content, Device[].class);
    }
}

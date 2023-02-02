package de.florianisme.wakeonlan.persistence.mapper;

import de.florianisme.wakeonlan.persistence.entities.DeviceEntity;
import de.florianisme.wakeonlan.persistence.models.Device;

public class DeviceEntityMapper implements EntityMapper<Device, DeviceEntity> {

    @Override
    public Device entityToModel(DeviceEntity entity) {
        if (entity == null) {
            return new Device();
        }
        return new Device(entity.id, entity.name, entity.macAddress, entity.broadcastAddress, entity.port, entity.statusIp, entity.secureOnPassword);
    }

    @Override
    public DeviceEntity modelToEntity(Device model) {
        if (model == null) {
            return new DeviceEntity();
        }
        return new DeviceEntity(model.id, model.name, model.macAddress, model.broadcastAddress, model.port, model.statusIp, model.secureOnPassword);
    }
}

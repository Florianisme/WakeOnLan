package de.florianisme.wakeonlan.quicksettings;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.util.List;
import java.util.Optional;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.shutdown.ShutdownExecutor;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;
import de.florianisme.wakeonlan.ui.list.status.pool.PingStatusTesterPool;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestType;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTesterPool;
import de.florianisme.wakeonlan.wol.WolSender;

public abstract class DeviceTileService extends TileService implements DeviceStatusListener {

    private final StatusTesterPool statusTesterPool = PingStatusTesterPool.getInstance();

    private DeviceRepository deviceRepository;
    private Device device;

    private DeviceStatus lastDeviceStatus = DeviceStatus.UNKNOWN;


    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTileState();
    }

    private void updateTileState() {
        deviceRepository = DeviceRepository.getInstance(this);
        Optional<Device> optionalMachine = getMachineAtIndex(machineAtIndex());

        Tile tile = getQsTile();

        if (optionalMachine.isPresent()) {
            Device device = optionalMachine.get();
            this.device = device;
            statusTesterPool.scheduleStatusTest(device, this, StatusTestType.TILE);

            tile.setLabel(device.name);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle(getString(R.string.tile_subtitle));
            }
        } else {
            tile.setLabel(getString(R.string.tile_no_device_found));
            tile.setState(Tile.STATE_UNAVAILABLE);
        }
        tile.updateTile();
    }

    private Optional<Device> getMachineAtIndex(int index) {
        List<Device> machineList = deviceRepository.getAll();
        if (machineList.size() <= index) {
            return Optional.empty();
        }
        return Optional.of(machineList.get(index));
    }

    @Override
    public void onStatusAvailable(DeviceStatus deviceStatus) {
        Tile tile = getQsTile();
        tile.setState(deviceStatus == DeviceStatus.ONLINE ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();

        lastDeviceStatus = deviceStatus;
    }

    @Override
    public void onClick() {
        if (lastDeviceStatus == DeviceStatus.ONLINE) {
            if (device.remoteShutdownEnabled) {
                ShutdownExecutor.shutdownDevice(device);
                Toast.makeText(this, getString(R.string.remote_shutdown_send_command, device.name), Toast.LENGTH_LONG).show();
            }
        } else {
            WolSender.sendWolPacket(device);
            Toast.makeText(this, getString(R.string.wol_toast_sending_packet, device.name), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        statusTesterPool.stopStatusTest(device, StatusTestType.TILE);
    }

    abstract int machineAtIndex();
}

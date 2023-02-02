package de.florianisme.wakeonlan.quicksettings;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Optional;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusTester;
import de.florianisme.wakeonlan.ui.list.status.PingDeviceStatusTester;
import de.florianisme.wakeonlan.wol.WolSender;

public abstract class DeviceTileService extends TileService implements DeviceStatusListener {

    private AppDatabase appDatabase;
    private Device device;
    private DeviceStatusTester deviceStatusTester;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTileState();
    }

    private void updateTileState() {
        appDatabase = DatabaseInstanceManager.getInstance(this);
        Optional<Device> optionalMachine = getMachineAtIndex(machineAtIndex());

        Tile tile = getQsTile();

        if (optionalMachine.isPresent()) {
            Device device = optionalMachine.get();
            this.device = device;
            deviceStatusTester.scheduleDeviceStatusPings(device, this);

            tile.setLabel(device.name);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle(getString(R.string.tile_subtitle));
            }
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setLabel(getString(R.string.tile_no_device_found));
            tile.setState(Tile.STATE_UNAVAILABLE);
        }
        tile.updateTile();
    }

    private Optional<Device> getMachineAtIndex(int index) {
        List<Device> machineList = appDatabase.deviceDao().getAll();
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
    }

    @Override
    public void onClick() {
        try {
            WolSender.sendWolPacket(device);
            Toast.makeText(this, getString(R.string.wol_toast_sending_packet) + device.name, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error while sending WOL Packet", e);
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        deviceStatusTester = new PingDeviceStatusTester();
        updateTileState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        deviceStatusTester.stopDeviceStatusPings();
    }

    abstract int machineAtIndex();
}

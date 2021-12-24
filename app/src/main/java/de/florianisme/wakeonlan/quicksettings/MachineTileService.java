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
import de.florianisme.wakeonlan.persistence.Machine;
import de.florianisme.wakeonlan.wol.WolSender;

public abstract class MachineTileService extends TileService {

    protected AppDatabase appDatabase;
    protected Machine machine;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTileState();
    }

    private void updateTileState() {
        appDatabase = DatabaseInstanceManager.init(this.getApplicationContext());
        Optional<Machine> optionalMachine = getMachineAtIndex(machineAtIndex());

        if (optionalMachine.isPresent()) {
            Machine machine = optionalMachine.get();
            this.machine = machine;

            super.getQsTile().setLabel(machine.name);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                super.getQsTile().setSubtitle(getString(R.string.tile_subtitle));
            }
            super.getQsTile().setState(Tile.STATE_ACTIVE);
        } else {
            super.getQsTile().setLabel(getString(R.string.tile_no_machine_found));
            super.getQsTile().setState(Tile.STATE_UNAVAILABLE);
        }
        super.getQsTile().updateTile();
    }

    private Optional<Machine> getMachineAtIndex(int index) {
        List<Machine> machineList = appDatabase.machineDao().getAll();
        if (machineList.size() <= index) {
            return Optional.empty();
        }
        return Optional.of(machineList.get(index));
    }

    @Override
    public void onClick() {
        try {
            WolSender.sendWolPacket(machine);
            Toast.makeText(this, getString(R.string.wol_toast_sending_packet) + machine.name, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error while sending WOL Packet", e);
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState();
    }

    abstract int machineAtIndex();
}

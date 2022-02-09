package de.florianisme.wakeonlan.ui.home.backup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentBackupBinding;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;

public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;

    private DataExporter dataExporter;
    private DataImporter dataImporter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonExport.setOnClickListener(v -> {
            List<Device> devices = DatabaseInstanceManager.getInstance(getContext()).deviceDao().getAll();
            dataExporter = new DataExporter(devices);
            dataExporter.exportDevices(BackupFragment.this);
        });

        binding.buttonImport.setOnClickListener(v -> dataImporter = new DataImporter(devices -> {
            DeviceDao deviceDao = DatabaseInstanceManager.getInstance(getContext()).deviceDao();
            deviceDao.deleteAll();
            deviceDao.insertAll(devices.toArray(new Device[0]));
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            dataExporter.onActivityResult(requestCode, resultCode, data, getContext());
            dataImporter.onActivityResult(requestCode, resultCode, data, getContext());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to export devices to file", Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "Error while persisting backup", e);
        }
    }
}

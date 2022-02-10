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

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.FragmentBackupBinding;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;

public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;

    private DataExporter dataExporter;
    private DataImporter dataImporter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);

        dataExporter = new DataExporter();

        dataImporter = new DataImporter(devices -> {
            DeviceDao deviceDao = DatabaseInstanceManager.getInstance(BackupFragment.this.getContext()).deviceDao();
            deviceDao.replaceAllDevices(devices);
            Toast.makeText(getContext(), getString(R.string.backup_message_import_success, devices.length), Toast.LENGTH_LONG).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonExport.setOnClickListener(v -> dataExporter.exportDevices(this));
        binding.buttonImport.setOnClickListener(v -> dataImporter.importDevices(this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            dataExporter.onActivityResult(requestCode, resultCode, data, getContext());
            dataImporter.onActivityResult(requestCode, resultCode, data, getContext());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Unable to export/import devices", e);
        }
    }
}

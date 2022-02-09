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
import de.florianisme.wakeonlan.persistence.entities.Device;

public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;
    private DataExporter dataExporter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);

        dataExporter = new DataExporter();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataExporter.exportDevices(BackupFragment.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            List<Device> devices = DatabaseInstanceManager.getInstance(getContext()).deviceDao().getAll();

            dataExporter.onActivityResult(requestCode, resultCode, data, getContext(), devices);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to export devices to file", Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "Error while persisting backup", e);
        }
    }
}

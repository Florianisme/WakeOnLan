package de.florianisme.wakeonlan.ui.home.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.florianisme.wakeonlan.databinding.FragmentBackupBinding;

public class BackupFragment extends Fragment {

    private DataExporter dataExporter;
    private DataImporter dataImporter;

    private FragmentBackupBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataExporter = new DataExporter(this);
        dataImporter = new DataImporter(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonExport.setOnClickListener(v -> dataExporter.exportDevices());
        binding.buttonImport.setOnClickListener(v -> dataImporter.importDevices());
    }

}

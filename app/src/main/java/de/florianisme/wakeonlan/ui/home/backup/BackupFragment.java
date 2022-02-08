package de.florianisme.wakeonlan.ui.home.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import de.florianisme.wakeonlan.databinding.FragmentBackupBinding;

public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataExporter().exportDevices(new ArrayList<>(), getContext());
            }
        });
    }
}

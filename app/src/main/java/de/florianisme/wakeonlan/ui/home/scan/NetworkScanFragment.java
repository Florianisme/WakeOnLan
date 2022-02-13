package de.florianisme.wakeonlan.ui.home.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.florianisme.wakeonlan.databinding.FragmentNetworkScanBinding;
import de.florianisme.wakeonlan.ui.home.list.LinearLayoutManagerWrapper;

public class NetworkScanFragment extends Fragment {

    private FragmentNetworkScanBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNetworkScanBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.networkList;

        recyclerView.setAdapter(new NetworkScanAdapter(new ArrayList<>()));
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
    }
}

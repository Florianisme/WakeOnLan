package de.florianisme.wakeonlan.ui.home.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.databinding.FragmentNetworkScanBinding;
import de.florianisme.wakeonlan.ui.home.list.LinearLayoutManagerWrapper;
import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;

public class NetworkScanFragment extends Fragment {

    private FragmentNetworkScanBinding binding;
    private NetworkScanAdapter networkScanAdapter;

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
        startNetworkScan();
    }

    private void startNetworkScan() {
        networkScanAdapter.clearDataset();
        NetworkSniffTask networkSniffTask = new NetworkSniffTask(getContext(), getScanCallback());
        networkSniffTask.execute();
    }

    private ScanCallback getScanCallback() {
        return new ScanCallback() {
            @Override
            public void onError(int errorStringReference) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), errorStringReference, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDeviceFound(String ip) {
                NetworkScanDevice networkScanDevice = new NetworkScanDevice();
                networkScanDevice.setIpAddress(ip);

                networkScanAdapter.addItem(networkScanDevice);
            }
        };
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.networkList;

        networkScanAdapter = new NetworkScanAdapter();
        recyclerView.setAdapter(networkScanAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
    }
}

package de.florianisme.wakeonlan.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentNetworkScanBinding;
import de.florianisme.wakeonlan.ui.list.layoutmanager.LinearLayoutManagerWrapper;
import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback;
import de.florianisme.wakeonlan.ui.scan.model.NetworkScanDevice;

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
        setupSwipeToRefresh();

        startNetworkScan();
    }

    private void startNetworkScan() {
        binding.swipeRefresh.setRefreshing(true);
        networkScanAdapter.clearDataset();

        new NetworkScanTask(getScanCallback()).startScan(getContext());
    }

    private void setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::startNetworkScan);
    }

    private ScanCallback getScanCallback() {
        final List<NetworkScanDevice> resultList = new ArrayList<>(20);

        return new ScanCallback() {
            @Override
            public void onError(int errorStringReference) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), errorStringReference, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDeviceFound(String ip, String hostName) {
                NetworkScanDevice networkScanDevice = new NetworkScanDevice();
                networkScanDevice.setIpAddress(ip);
                if (hostName != null && !ip.equals(hostName) && !hostName.isEmpty()) {
                    networkScanDevice.setName(hostName);
                }

                synchronized (resultList) {
                    resultList.add(networkScanDevice);
                    getActivity().runOnUiThread(() -> networkScanAdapter.updateList(resultList));
                }
            }

            @Override
            public void onTaskEnd() {
                binding.swipeRefresh.setRefreshing(false);
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

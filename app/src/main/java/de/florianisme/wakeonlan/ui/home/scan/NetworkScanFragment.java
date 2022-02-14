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

import java.util.ArrayList;
import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentNetworkScanBinding;
import de.florianisme.wakeonlan.ui.home.list.LinearLayoutManagerWrapper;
import de.florianisme.wakeonlan.ui.home.scan.callbacks.ScanCallback;
import de.florianisme.wakeonlan.ui.home.scan.model.NetworkScanDevice;

public class NetworkScanFragment extends Fragment {

    private FragmentNetworkScanBinding binding;
    private NetworkScanAdapter networkScanAdapter;
    private NetworkScanTask networkScanTask;

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

        networkScanTask = new NetworkScanTask(getContext(), getScanCallback());
        startNetworkScan();
    }

    private void startNetworkScan() {
        binding.swipeRefresh.setRefreshing(true);

        networkScanTask.cancel(true);
        networkScanAdapter.clearDataset();
        networkScanTask = new NetworkScanTask(getContext(), getScanCallback());
        networkScanTask.execute();
    }

    private void setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::startNetworkScan);
    }

    private ScanCallback getScanCallback() {
        List<NetworkScanDevice> resultList = new ArrayList<>(15);

        return new ScanCallback() {
            @Override
            public void onError(int errorStringReference) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), errorStringReference, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDeviceFound(String ip, String hostName) {
                NetworkScanDevice networkScanDevice = new NetworkScanDevice();
                networkScanDevice.setIpAddress(ip);
                if (!ip.equals(hostName)) {
                    networkScanDevice.setName(hostName);
                }

                resultList.add(networkScanDevice);
                networkScanAdapter.updateList(resultList);
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

    @Override
    public void onResume() {
        super.onResume();
        startNetworkScan();
    }
}

package de.florianisme.wakeonlan.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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

        return new ScanCallback() {
            @Override
            public void onError(int errorStringReference) {
                runOnUiThread(() ->
                        Toast.makeText(getContext(), errorStringReference, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDeviceFound(String ip, String hostName) {
                NetworkScanDevice networkScanDevice = new NetworkScanDevice();
                networkScanDevice.setIpAddress(ip);
                if (hostName != null && !ip.equals(hostName) && !hostName.isEmpty()) {
                    networkScanDevice.setName(hostName);
                }

                runOnUiThread(() -> networkScanAdapter.addDevice(networkScanDevice));
            }

            private void runOnUiThread(Runnable runnable) {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    // Activity has already finished, Threads were still running. Do nothing
                    return;
                }
                activity.runOnUiThread(runnable);
            }

            @Override
            public void onTaskEnd() {
                runOnUiThread(() -> binding.swipeRefresh.setRefreshing(false));
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

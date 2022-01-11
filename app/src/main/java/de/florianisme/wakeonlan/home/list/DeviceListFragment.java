package de.florianisme.wakeonlan.home.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.florianisme.wakeonlan.databinding.FragmentListDevicesBinding;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.wear.WearClient;


public class DeviceListFragment extends Fragment {

    private FragmentListDevicesBinding binding;
    private AppDatabase databaseInstance;
    private DeviceListAdapter deviceListAdapter;

    private WearClient wearClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseInstance = DatabaseInstanceManager.getDatabaseInstance();
        binding = FragmentListDevicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wearClient = new WearClient().init(view.getContext());
        instantiateRecyclerView();
        registerLiveDataObserver();
    }

    private void registerLiveDataObserver() {
        databaseInstance.deviceDao().getAllAsObservable().observe(getViewLifecycleOwner(), devices -> {
            wearClient.onDeviceListUpdated(devices);

            deviceListAdapter.updateDataset(devices);
            deviceListAdapter.notifyDataSetChanged();
        });
    }

    private void instantiateRecyclerView() {
        deviceListAdapter = new DeviceListAdapter();
        RecyclerView machinesRecyclerView = binding.machineList;

        machinesRecyclerView.setAdapter(deviceListAdapter);
        machinesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wearClient.destroy();
        binding = null;
    }

}
package de.florianisme.wakeonlan.home.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentListDevicesBinding;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.Device;


public class DeviceListFragment extends Fragment {

    private FragmentListDevicesBinding binding;
    private AppDatabase databaseInstance;
    private DeviceListAdapter deviceListAdapter;

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

        populateRecyclerView();
    }

    private void populateRecyclerView() {
        List<Device> devices = loadMachines();
        deviceListAdapter = new DeviceListAdapter(devices);
        RecyclerView machinesRecyclerView = binding.machineList;

        machinesRecyclerView.setAdapter(deviceListAdapter);
        machinesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<Device> loadMachines() {
        return databaseInstance.machineDao().getAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceListAdapter.updateDataset(loadMachines());
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
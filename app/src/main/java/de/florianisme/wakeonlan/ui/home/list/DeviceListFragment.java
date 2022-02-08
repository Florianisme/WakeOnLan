package de.florianisme.wakeonlan.ui.home.list;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.FragmentListDevicesBinding;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.wear.WearClient;


public class DeviceListFragment extends Fragment {

    private FragmentListDevicesBinding binding;
    private DeviceListAdapter deviceListAdapter;

    private WearClient wearClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListDevicesBinding.inflate(inflater, container, false);
        binding.addDeviceFab.setOnClickListener(view -> Navigation.findNavController(container).navigate(R.id.MainActivity_to_AddMachineActivity));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wearClient = new WearClient().init(getContext());
        instantiateRecyclerView();
        registerLiveDataObserver();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void registerLiveDataObserver() {
        DatabaseInstanceManager.getInstance(getContext()).deviceDao()
                .getAllAsObservable()
                .observe(getViewLifecycleOwner(), devices -> {
                    wearClient.onDeviceListUpdated(devices);

                    deviceListAdapter.updateDataset(devices);
                });
    }

    private void instantiateRecyclerView() {
        List<Device> initialDataset = DatabaseInstanceManager.getInstance(getContext()).deviceDao().getAll();
        deviceListAdapter = new DeviceListAdapter(initialDataset, buildDeviceClickedCallback());
        deviceListAdapter.setHasStableIds(true);
        RecyclerView devicesRecyclerView = binding.machineList;

        devicesRecyclerView.setAdapter(deviceListAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
    }

    @NonNull
    private DeviceClickedCallback buildDeviceClickedCallback() {
        return deviceName -> {
            String snackbarText = getContext().getString(R.string.wol_toast_sending_packet, deviceName);
            View coordinatorView = getActivity().findViewById(R.id.device_list_coordinator_layout);

            Snackbar.make(coordinatorView, snackbarText, Snackbar.LENGTH_SHORT).show();
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wearClient.destroy();
        binding = null;
    }

}
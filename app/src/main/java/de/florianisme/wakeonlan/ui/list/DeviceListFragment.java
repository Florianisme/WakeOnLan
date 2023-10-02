package de.florianisme.wakeonlan.ui.list;

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
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.ui.list.layoutmanager.GridLayoutManagerWrapper;
import de.florianisme.wakeonlan.ui.list.layoutmanager.LinearLayoutManagerWrapper;
import de.florianisme.wakeonlan.ui.list.status.pool.PingStatusTesterPool;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTesterPool;


public class DeviceListFragment extends Fragment {

    private DeviceRepository deviceRepository;
    private FragmentListDevicesBinding binding;
    private DeviceListAdapter deviceListAdapter;

    private static final StatusTesterPool STATUS_TESTER_POOL = PingStatusTesterPool.getInstance();

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

        deviceRepository = DeviceRepository.getInstance(getContext());

        instantiateRecyclerView();
        registerLiveDataObserver();
    }

    private void registerLiveDataObserver() {
        deviceRepository.getAllAsObservable()
                .observe(getViewLifecycleOwner(), devices -> deviceListAdapter.updateDataset(devices));
    }

    private void instantiateRecyclerView() {
        List<Device> initialDataset = DeviceRepository.getInstance(getContext()).getAll();
        deviceListAdapter = new DeviceListAdapter(initialDataset, buildDeviceClickedCallback(), STATUS_TESTER_POOL);
        deviceListAdapter.setHasStableIds(true);
        RecyclerView devicesRecyclerView = binding.machineList;

        devicesRecyclerView.setAdapter(deviceListAdapter);
        devicesRecyclerView.setLayoutManager(getLayoutManager());
    }

    @NonNull
    private RecyclerView.LayoutManager getLayoutManager() {
        if (getResources().getBoolean(R.bool.isTablet)) {
            return new GridLayoutManagerWrapper(getContext(), 2);
        } else {
            return new LinearLayoutManagerWrapper(getContext());
        }
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
        binding = null;
    }

}
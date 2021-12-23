package de.florianisme.wakeonlan.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentListMachinesBinding;
import de.florianisme.wakeonlan.persistence.Machine;


public class MachineListFragment extends Fragment {

    private FragmentListMachinesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListMachinesBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine("Machine 1", "b1:bd:50:27:48:1b", "", 9));
        machines.add(new Machine("Machine 2", "51:c4:de:6c:06:33", "", 9));
        machines.add(new Machine("Machine 3", "d9:ad:41:ce:2f:0f", "", 9));
        MachineListAdapter machineListAdapter = new MachineListAdapter(machines);
        RecyclerView machinesRecyclerView = binding.machineList;

        machinesRecyclerView.setAdapter(machineListAdapter);
        machinesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
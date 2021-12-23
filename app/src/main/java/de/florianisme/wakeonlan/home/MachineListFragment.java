package de.florianisme.wakeonlan.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.florianisme.wakeonlan.databinding.FragmentListMachinesBinding;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.Machine;


public class MachineListFragment extends Fragment {

    private FragmentListMachinesBinding binding;
    private AppDatabase databaseInstance;
    private MachineListAdapter machineListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseInstance = DatabaseInstanceManager.getDatabaseInstance();
        binding = FragmentListMachinesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateRecyclerView();
    }

    private void populateRecyclerView() {
        List<Machine> machines = loadMachines();
        machineListAdapter = new MachineListAdapter(machines);
        RecyclerView machinesRecyclerView = binding.machineList;

        machinesRecyclerView.setAdapter(machineListAdapter);
        machinesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<Machine> loadMachines() {
        return databaseInstance.userDao().getAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        machineListAdapter.updateDataset(loadMachines());
        machineListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
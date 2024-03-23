package de.florianisme.wakeonlan.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.common.collect.Sets;

import java.util.Set;

import de.florianisme.wakeonlan.BuildConfig;
import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.databinding.ActivityMainBinding;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.shortcuts.DynamicShortcutManager;
import de.florianisme.wakeonlan.wear.WearClient;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private WearClient wearClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVersionInformation();

        setSupportActionBar(binding.toolbar);

        initializeNavController();
        initializeWearClient();
        initializeShortcuts();
    }

    private void setVersionInformation() {
        View headerView = binding.navigationView.getHeaderView(0);

        TextView versionView = headerView.findViewById(R.id.navigation_header_version);
        TextView headerTitleView = headerView.findViewById(R.id.navigation_header_title);

        headerTitleView.setOnApplyWindowInsetsListener((v, insets) -> {
            int calculatedTopPadding = Math.round(insets.getSystemWindowInsetTop() +
                    getResources().getDimension(R.dimen.navigation_header_top_padding));

            headerTitleView.setPadding(versionView.getPaddingStart(), calculatedTopPadding,
                    versionView.getPaddingEnd(), 0);
            return insets;
        });

        versionView.setText(getString(R.string.drawer_menu_header_version, BuildConfig.VERSION_NAME));
    }

    private void initializeWearClient() {
        wearClient = new WearClient(this);
        DeviceRepository.getInstance(this)
                .getAllAsObservable()
                .observe(this, devices -> wearClient.onDeviceListUpdated(devices));
    }

    private void initializeNavController() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(getMenuIds()).setOpenableLayout(binding.drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        setGithubShortcut();
    }

    private void setGithubShortcut() {
        binding.navigationView.getMenu().findItem(R.id.githubShortcut).setOnMenuItemClickListener(item -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Florianisme/WakeOnLan"));
            startActivity(browserIntent);

            return false;
        });
    }

    private void initializeShortcuts() {
        DynamicShortcutManager dynamicShortcutManager = new DynamicShortcutManager();
        DeviceRepository.getInstance(this)
                .getAllAsObservable()
                .observe(this, devices -> dynamicShortcutManager.updateShortcuts(this, devices));
    }

    private Set<Integer> getMenuIds() {
        return Sets.newHashSet(R.id.deviceListFragment, R.id.backupFragment, R.id.networkScanFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
package de.florianisme.wakeonlan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository
import de.florianisme.wakeonlan.shortcuts.DynamicShortcutManager
import de.florianisme.wakeonlan.ui.screens.BackupScreen
import de.florianisme.wakeonlan.ui.screens.DeviceListScreen
import de.florianisme.wakeonlan.ui.screens.NetworkScanScreen
import de.florianisme.wakeonlan.ui.theme.WakeOnLanTheme
import de.florianisme.wakeonlan.wear.WearClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeWearClient()
        initializeShortcuts()

        setContent {
            WakeOnLanTheme {
                MainScreen()
            }
        }
    }

    private fun initializeWearClient() {
        val wearClient = WearClient(this)
        DeviceRepository.getInstance(this).allAsObservable
            .observe(this) { devices -> wearClient.onDeviceListUpdated(devices) }
    }

    private fun initializeShortcuts() {
        val dynamicShortcutManager = DynamicShortcutManager()
        DeviceRepository.getInstance(this).allAsObservable
            .observe(this) { devices -> dynamicShortcutManager.updateShortcuts(this, devices) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = Destination.fromRoute(backStackEntry?.destination?.route)

    MainScreenScaffold(
        drawerState = drawerState,
        currentDestination = currentDestination,
        isSelected = { destination ->
            backStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
        },
        onDestinationSelected = { destination, selected ->
            scope.launch { drawerState.close() }
            if (!selected) {
                navController.navigate(destination.route) {
                    popUpTo(Destination.START.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        onMenuClick = { scope.launch { drawerState.open() } },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.START.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Destination.DEVICE_LIST.route) { DeviceListScreen() }
            composable(Destination.NETWORK_SCAN.route) { NetworkScanScreen() }
            composable(Destination.BACKUP.route) { BackupScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenScaffold(
    drawerState: DrawerState,
    currentDestination: Destination,
    isSelected: (Destination) -> Boolean,
    onDestinationSelected: (Destination, Boolean) -> Unit,
    onMenuClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Destination.entries.forEach { destination ->
                    val selected = isSelected(destination)
                    NavigationDrawerItem(
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.titleRes)) },
                        selected = selected,
                        onClick = { onDestinationSelected(destination, selected) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                GithubDrawerItem()
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(currentDestination.titleRes)) },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            },
            content = content,
        )
    }
}

@Composable
private fun DrawerHeader() {
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: ""
    }
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.drawer_menu_header_version, versionName),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun GithubDrawerItem() {
    val context = LocalContext.current
    NavigationDrawerItem(
        icon = { Icon(Icons.Filled.BugReport, contentDescription = null) },
        label = { Text(stringResource(R.string.drawer_menu_item_github)) },
        selected = false,
        onClick = {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Florianisme/WakeOnLan"),
            )
            context.startActivity(browserIntent)
        },
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Main screen", showBackground = true)
@Composable
private fun MainScreenPreview() {
    WakeOnLanTheme {
        MainScreenScaffold(
            drawerState = rememberDrawerState(DrawerValue.Closed),
            currentDestination = Destination.DEVICE_LIST,
            isSelected = { it == Destination.DEVICE_LIST },
            onDestinationSelected = { _, _ -> },
            onMenuClick = {},
        ) { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)) {
                Text(
                    text = stringResource(R.string.title_fragment_device_list),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Navigation drawer", showBackground = true)
@Composable
private fun MainScreenDrawerPreview() {
    WakeOnLanTheme {
        MainScreenScaffold(
            drawerState = rememberDrawerState(DrawerValue.Open),
            currentDestination = Destination.DEVICE_LIST,
            isSelected = { it == Destination.DEVICE_LIST },
            onDestinationSelected = { _, _ -> },
            onMenuClick = {},
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {}
        }
    }
}


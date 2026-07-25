package de.florianisme.wakeonlan.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.ui.graphics.vector.ImageVector
import de.florianisme.wakeonlan.R

/**
 * Drawer destinations that replace the old Fragment nav_graph.
 */
enum class Destination(
    val route: String,
    @param:StringRes val titleRes: Int,
    val icon: ImageVector,
) {
    DEVICE_LIST("device_list", R.string.title_fragment_device_list, Icons.AutoMirrored.Filled.List),
    NETWORK_SCAN("network_scan", R.string.title_fragment_network_scan, Icons.Filled.WifiFind),
    BACKUP("backup", R.string.title_fragment_backup, Icons.Filled.ImportExport);

    companion object {
        val START: Destination = DEVICE_LIST

        fun fromRoute(route: String?): Destination =
            entries.firstOrNull { it.route == route } ?: START
    }
}


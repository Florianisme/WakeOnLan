package de.florianisme.wakeonlan.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.ui.backup.BackupIo
import de.florianisme.wakeonlan.ui.backup.contracts.ChooseImportFileDestinationContract
import de.florianisme.wakeonlan.ui.backup.contracts.ChooseSaveFileDestinationContract

@Composable
fun BackupScreen() {
    val context = LocalContext.current

    val exportLauncher =
        rememberLauncherForActivityResult(ChooseSaveFileDestinationContract()) { uri: Uri? ->
            if (uri != null) BackupIo.exportDevices(context, uri)
        }
    val importLauncher =
        rememberLauncherForActivityResult(ChooseImportFileDestinationContract()) { uri: Uri? ->
            if (uri != null) BackupIo.importDevices(context, uri)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.backup_description),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.backup_note),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.backup_note_content),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { exportLauncher.launch(Any()) }) {
                Text(stringResource(R.string.backup_button_export))
            }
            OutlinedButton(onClick = { importLauncher.launch(Any()) }) {
                Text(stringResource(R.string.backup_button_import))
            }
        }
    }
}


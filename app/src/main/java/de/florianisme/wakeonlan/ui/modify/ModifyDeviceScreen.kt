package de.florianisme.wakeonlan.ui.modify

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.models.Device

/** Runs the broadcast interface lookup and fills the broadcast field, mirroring the old autofill button. */
fun fillBroadcastAddress(state: DeviceFormState) {
    BroadcastHelper().broadcastAddress.ifPresent { inetAddress ->
        state.broadcast = inetAddress.hostAddress.orEmpty()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyDeviceScreen(
    titleRes: Int,
    state: DeviceFormState,
    showDelete: Boolean,
    onPersist: (Device) -> Unit,
    onDelete: () -> Unit,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current

    var showTestDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    fun attemptSave() {
        state.showErrors = true
        if (state.isValid) {
            onPersist(state.toDevice())
            onFinish()
        } else {
            Toast.makeText(context, R.string.add_device_error_save_clicked, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun attemptClose() {
        if (state.isUnchanged()) onFinish() else showUnsavedDialog = true
    }

    BackHandler { attemptClose() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titleRes)) },
                navigationIcon = {
                    IconButton(onClick = { attemptClose() }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                },
                actions = {
                    if (showDelete) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.add_device_menu_delete)
                            )
                        }
                    }
                    IconButton(onClick = { attemptSave() }) {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = stringResource(R.string.add_device_menu_save)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            SectionTitle(R.string.device_title_general)
            LabeledField(
                value = state.name,
                onValueChange = { state.name = it },
                labelRes = R.string.add_device_name,
                errorRes = if (state.showErrors) state.nameError else null,
            )

            SectionTitle(R.string.device_title_connection)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacField(
                    state = state,
                    modifier = Modifier.weight(1f),
                )
                LabeledField(
                    value = state.port,
                    onValueChange = { state.port = it },
                    labelRes = R.string.add_device_port,
                    errorRes = if (state.showErrors) state.portError else null,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.width(120.dp),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LabeledField(
                    value = state.broadcast,
                    onValueChange = { state.broadcast = it },
                    labelRes = R.string.add_device_broadcast_address,
                    placeholder = "192.168.0.255",
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { fillBroadcastAddress(state) }) {
                    Icon(
                        Icons.Filled.WifiTethering,
                        contentDescription = stringResource(R.string.add_device_autofill_broadcast),
                    )
                }
            }

            SectionTitle(R.string.device_title_status)
            LabeledField(
                value = state.statusIp,
                onValueChange = { state.statusIp = it },
                labelRes = R.string.add_device_status_ip,
                placeholder = "192.168.0.100",
                supportingRes = R.string.add_device_status_ip_helper,
            )

            SectionTitle(R.string.add_device_shutdown)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.remoteShutdownEnabled,
                    onCheckedChange = { state.remoteShutdownEnabled = it },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_device_shutdown_enable))
            }
            Text(
                text = stringResource(R.string.add_device_shutdown_explanation),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (state.remoteShutdownEnabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LabeledField(
                        value = state.sshAddress,
                        onValueChange = { state.sshAddress = it },
                        labelRes = R.string.add_device_shutdown_ip,
                        errorRes = if (state.showErrors) state.sshAddressError else null,
                        modifier = Modifier.weight(1f),
                    )
                    LabeledField(
                        value = state.sshPort,
                        onValueChange = { state.sshPort = it },
                        labelRes = R.string.add_device_shutdown_port,
                        placeholder = "22",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.width(120.dp),
                    )
                }
                LabeledField(
                    value = state.sshUsername,
                    onValueChange = { state.sshUsername = it },
                    labelRes = R.string.add_device_shutdown_username,
                    errorRes = if (state.showErrors) state.sshUsernameError else null,
                )
                LabeledField(
                    value = state.sshPassword,
                    onValueChange = { state.sshPassword = it },
                    labelRes = R.string.add_device_shutdown_password,
                    errorRes = if (state.showErrors) state.sshPasswordError else null,
                    isPassword = true,
                )
                LabeledField(
                    value = state.sshCommand,
                    onValueChange = { state.sshCommand = it },
                    labelRes = R.string.add_device_shutdown_command,
                    errorRes = if (state.showErrors) state.sshCommandError else null,
                    placeholder = "sudo /usr/sbin/shutdown -h now",
                    supportingRes = R.string.add_device_shutdown_command_helper,
                )
                OutlinedButton(
                    onClick = {
                        state.showErrors = true
                        if (state.isValid) showTestDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text(stringResource(R.string.modify_device_test_shutdown))
                }
            }

            SectionTitle(R.string.device_title_security)
            LabeledField(
                value = state.secureOn,
                onValueChange = { state.secureOn = it },
                labelRes = R.string.add_device_secure_on,
                errorRes = if (state.showErrors) state.secureOnError else null,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showTestDialog) {
        ShutdownTestDialog(device = state.toDevice(), onDismiss = { showTestDialog = false })
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.edit_device_delete_title)) },
            text = { Text(stringResource(R.string.edit_device_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                    onFinish()
                }) { Text(stringResource(android.R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) { Text(stringResource(android.R.string.no)) }
            },
        )
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text(stringResource(R.string.modify_device_unsaved_changes_title)) },
            text = { Text(stringResource(R.string.modify_device_unsaved_changes_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    attemptSave()
                }) { Text(stringResource(R.string.modify_device_unsaved_changes_positive)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    onFinish()
                }) { Text(stringResource(R.string.modify_device_unsaved_changes_negative)) }
            },
        )
    }
}

@Composable
private fun MacField(state: DeviceFormState, modifier: Modifier = Modifier) {
    var fieldValue by remember {
        mutableStateOf(TextFieldValue(state.mac, TextRange(state.mac.length)))
    }
    val showError = (state.showErrors || state.mac.isNotEmpty()) && state.macError != null

    OutlinedTextField(
        value = fieldValue,
        onValueChange = { newValue ->
            val formatted = DeviceFormValidation.autoFormatMac(state.mac, newValue.text)
            state.mac = formatted
            // If we inserted characters (colons), keep the caret at the end; otherwise honour the user's caret.
            val caret = if (formatted != newValue.text) formatted.length else newValue.selection.end
            fieldValue = TextFieldValue(formatted, TextRange(caret.coerceIn(0, formatted.length)))
        },
        label = { Text(stringResource(R.string.add_device_mac_address)) },
        placeholder = { Text("AB:12:CD:34:EF:56") },
        isError = showError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        supportingText = if (showError) {
            { Text(stringResource(state.macError!!)) }
        } else {
            null
        },
        modifier = modifier.padding(top = 4.dp),
    )
}

@Composable
private fun SectionTitle(titleRes: Int) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun LabeledField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    errorRes: Int? = null,
    supportingRes: Int? = null,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        placeholder = placeholder?.let { { Text(it) } },
        isError = errorRes != null,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = when {
            errorRes != null -> {
                { Text(stringResource(errorRes)) }
            }

            supportingRes != null -> {
                { Text(stringResource(supportingRes)) }
            }

            else -> null
        },
        modifier = modifier.padding(top = 4.dp),
    )
}



package de.florianisme.wakeonlan.ui.modify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.common.base.Throwables
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.models.Device
import de.florianisme.wakeonlan.shutdown.ShutdownModel
import de.florianisme.wakeonlan.shutdown.exception.CommandExecuteException
import de.florianisme.wakeonlan.shutdown.listener.ShutdownExecutorListener
import de.florianisme.wakeonlan.shutdown.test.ShutdownCommandTester
import net.schmizz.sshj.connection.ConnectionException
import net.schmizz.sshj.userauth.UserAuthException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

private val SuccessGreen = Color(0xFF479C44)

@Composable
fun ShutdownTestDialog(device: Device, onDismiss: () -> Unit) {
    val context = LocalContext.current

    var destinationReached by remember { mutableStateOf(false) }
    var authorized by remember { mutableStateOf(false) }
    var sessionCreated by remember { mutableStateOf(false) }
    var commandExecuted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val listener = object : ShutdownExecutorListener {
            override fun onTargetHostReached() {
                destinationReached = true
            }

            override fun onLoginSuccessful() {
                authorized = true
            }

            override fun onSessionStartSuccessful() {
                sessionCreated = true
            }

            override fun onCommandExecuteSuccessful() {
                commandExecuted = true
            }

            override fun onSudoPromptTriggered(shutdownModel: ShutdownModel) {
                errorMessage = context.getString(
                    R.string.test_shutdown_error_execution_sudo_prompt,
                    shutdownModel.command
                )
            }

            override fun onGeneralError(exception: Exception, shutdownModel: ShutdownModel?) {
                errorMessage = textByExceptionType(context, exception, shutdownModel)
            }
        }
        ShutdownCommandTester(listener).startShutdownCommandTest(device)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.remote_shutdown_send_command_dialog_title)) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) }
        },
        text = {
            Column {
                StepRow(
                    completed = destinationReached,
                    textRes = if (destinationReached) R.string.test_shutdown_successful_destination else R.string.test_shutdown_initial_destination,
                )
                StepRow(
                    completed = authorized,
                    textRes = if (authorized) R.string.test_shutdown_successful_authorization else R.string.test_shutdown_initial_authorization,
                )
                StepRow(
                    completed = sessionCreated,
                    textRes = if (sessionCreated) R.string.test_shutdown_successful_session else R.string.test_shutdown_initial_session,
                )
                StepRow(
                    completed = commandExecuted,
                    textRes = if (commandExecuted) R.string.test_shutdown_successful_command_execute else R.string.test_shutdown_initial_command_execute,
                )
                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }
            }
        },
    )
}

@Composable
private fun StepRow(completed: Boolean, textRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        if (completed) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
        } else {
            Icon(
                Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(textRes))
    }
}

private fun textByExceptionType(
    context: android.content.Context,
    exception: Exception,
    shutdownModel: ShutdownModel?,
): String {
    return when {
        exception is ConnectException && shutdownModel != null ->
            context.getString(
                R.string.test_shutdown_error_connect_exception,
                shutdownModel.sshAddress,
                shutdownModel.sshPort
            )

        exception is UnknownHostException && shutdownModel != null ->
            context.getString(R.string.test_shutdown_error_unknown_host, shutdownModel.sshAddress)

        exception is UserAuthException && shutdownModel != null ->
            context.getString(
                R.string.test_shutdown_error_auth_exception,
                shutdownModel.username,
                shutdownModel.sshAddress
            )

        exception is ConnectionException && Throwables.getRootCause(exception) is TimeoutException && shutdownModel != null ->
            context.getString(R.string.test_shutdown_error_execution_timeout, shutdownModel.command)

        exception is CommandExecuteException && shutdownModel != null -> {
            val exitStatus = exception.exitStatus
            context.getString(
                R.string.test_shutdown_error_execution_exception,
                shutdownModel.command,
                exitStatus,
                exitCodeExplanation(context, exitStatus),
            )
        }

        else -> context.getString(R.string.test_shutdown_error_unknown_exception, exception.message)
    }
}

private fun exitCodeExplanation(context: android.content.Context, exitStatus: Int): String =
    when (exitStatus) {
        127 -> context.getString(R.string.execution_error_command_not_found)
        126 -> context.getString(R.string.execution_error_command_not_executable)
        else -> context.getString(R.string.execution_error_unknown)
    }


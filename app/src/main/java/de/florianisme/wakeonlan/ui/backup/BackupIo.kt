package de.florianisme.wakeonlan.ui.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import de.florianisme.wakeonlan.R
import de.florianisme.wakeonlan.persistence.models.Device
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository
import de.florianisme.wakeonlan.ui.backup.model.DeviceBackupModel
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Export/import logic extracted from the old Fragment-bound DataExporter/DataImporter so it can be
 * driven from a Compose activity-result launcher.
 */
object BackupIo {

    private const val FILE_MODE_WRITE = "w"

    fun exportDevices(context: Context, uri: Uri) {
        try {
            val devices = DeviceRepository.getInstance(context).all
                .map { DeviceBackupModel(it) }

            val content = Gson().toJson(devices).toByteArray(StandardCharsets.UTF_8)
            context.contentResolver.openOutputStream(uri, FILE_MODE_WRITE).use { outputStream ->
                requireNotNull(outputStream) { "Could not open File for writing" }
                outputStream.write(content)
            }

            Toast.makeText(
                context,
                context.getString(R.string.backup_message_export_success, devices.size),
                Toast.LENGTH_SHORT,
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.backup_message_export_error),
                Toast.LENGTH_SHORT
            ).show()
            Log.e("BackupIo", "Unable to export devices", e)
        }
    }

    fun importDevices(context: Context, uri: Uri) {
        try {
            val bytes = context.contentResolver.openInputStream(uri).use { inputStream ->
                requireNotNull(inputStream) { "Could not open File for reading" }
                ByteStreams.toByteArray(inputStream)
            }

            val reader = InputStreamReader(ByteArrayInputStream(bytes))
            val devices: Array<Device> =
                Gson().fromJson(reader, Array<DeviceBackupModel>::class.java)
                    .map { it.toModel() }
                    .toTypedArray()

            DeviceRepository.getInstance(context).replaceAllDevices(*devices)
            Toast.makeText(
                context,
                context.getString(R.string.backup_message_import_success, devices.size),
                Toast.LENGTH_LONG,
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.backup_message_import_error),
                Toast.LENGTH_SHORT
            ).show()
            Log.e("BackupIo", "Unable to import devices", e)
        }
    }
}


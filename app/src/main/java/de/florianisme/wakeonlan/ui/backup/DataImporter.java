package de.florianisme.wakeonlan.ui.backup;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.ui.backup.contracts.ChooseImportFileDestinationContract;
import de.florianisme.wakeonlan.ui.backup.model.DeviceBackupModel;

public class DataImporter implements ActivityResultCallback<Uri> {

    private final WeakReference<Context> contextWeakReference;
    private final ActivityResultLauncher<Object> activityResultLauncher;

    public DataImporter(Fragment fragment) {
        this.contextWeakReference = new WeakReference<>(fragment.getContext());
        activityResultLauncher = fragment.registerForActivityResult(new ChooseImportFileDestinationContract(), this);
    }

    public void importDevices() {
        activityResultLauncher.launch(null);
    }

    @Override
    public void onActivityResult(Uri uri) {
        if (uri == null) {
            return;
        }
        Context context = contextWeakReference.get();

        try {
            byte[] bytes = readContentFromFile(uri, context);
            Device[] devices = Arrays.stream(new ObjectMapper().readValue(bytes, DeviceBackupModel[].class))
                    .map(DeviceBackupModel::toModel)
                    .toArray(Device[]::new);

            replaceDevicesInDatabase(devices, context);
            Toast.makeText(context, context.getString(R.string.backup_message_import_success, devices.length), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.backup_message_import_error), Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "Unable to import devices", e);
        }
    }

    private void replaceDevicesInDatabase(Device[] devices, Context context) {
        DeviceRepository deviceRepository = DeviceRepository.getInstance(context);
        deviceRepository.replaceAllDevices(devices);
    }

    private byte[] readContentFromFile(Uri uri, Context context) throws IOException {
        byte[] content;

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {

            if (inputStream == null) {
                throw new IllegalStateException("Could not open File for reading");
            }

            content = ByteStreams.toByteArray(inputStream);
        }

        return content;
    }
}

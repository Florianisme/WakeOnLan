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

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.ui.backup.contracts.ChooseImportFileDestinationContract;

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
            Device[] devices = new ObjectMapper().readValue(bytes, Device[].class);

            replaceDevicesInDatabase(devices, context);
            Toast.makeText(context, context.getString(R.string.backup_message_import_success, devices.length), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.backup_message_import_error), Toast.LENGTH_SHORT).show();
            Log.e(getClass().getSimpleName(), "Unable to import devices", e);
        }
    }

    private void replaceDevicesInDatabase(Device[] devices, Context context) {
        DeviceDao deviceDao = DatabaseInstanceManager.getInstance(context).deviceDao();
        deviceDao.replaceAllDevices(devices);
    }

    private byte[] readContentFromFile(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] content = ByteStreams.toByteArray(inputStream);
        inputStream.close();

        return content;
    }
}

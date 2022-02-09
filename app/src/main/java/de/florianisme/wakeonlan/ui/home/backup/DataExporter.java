package de.florianisme.wakeonlan.ui.home.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class DataExporter implements OnActivityResultListener {

    public static final String FILE_MODE_WRITE = "w";

    public void exportDevices(BackupFragment fragment) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/json");
        intent.putExtra(Intent.EXTRA_TITLE, getFileName());

        fragment.startActivityForResult(intent, RequestCode.CREATE_EXPORT_FILE.getRequestCode());
    }

    @NonNull
    private String getFileName() {
        return "WakeOnLan_Export_" + System.currentTimeMillis() + ".json";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData, Context context, List<Device> devices) throws Exception {
        if (requestCode == RequestCode.CREATE_EXPORT_FILE.getRequestCode() && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                byte[] content = JsonConverter.toJson(devices);
                writeDevicesToFile(resultData.getData(), content, context);
            } else {
                throw new IllegalStateException("URI Request was not successful");
            }
        }
    }

    private void writeDevicesToFile(Uri uri, byte[] content, Context context) throws Exception {
        ParcelFileDescriptor fileDescriptor = context.getContentResolver().openFileDescriptor(uri, FILE_MODE_WRITE);
        FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
        fileOutputStream.write(content);

        fileOutputStream.close();
        fileDescriptor.close();
    }
}

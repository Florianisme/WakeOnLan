package de.florianisme.wakeonlan.ui.home.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DataImporter implements OnActivityResultListener {

    private final OnDeviceListAvailable onDeviceListAvailable;

    public DataImporter(OnDeviceListAvailable onDeviceListAvailable) {
        this.onDeviceListAvailable = onDeviceListAvailable;
    }

    public void importDevices(BackupFragment fragment) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        fragment.startActivityForResult(intent, RequestCode.READ_IMPORT_FILE.getRequestCode());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData, Context context) throws Exception {
        if (requestCode == RequestCode.READ_IMPORT_FILE.getRequestCode() && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                byte[] bytes = readContentFromFile(resultData.getData(), context);
                onDeviceListAvailable.onDeviceListAvailable(JsonConverter.toModel(bytes));
            } else {
                throw new IllegalStateException("URI Request was not successful");
            }
        }
    }

    private byte[] readContentFromFile(Uri uri, Context context) throws IOException {
        ByteArrayInputStream inputStream = (ByteArrayInputStream) context.getContentResolver().openInputStream(uri);
        byte[] content = ByteStreams.toByteArray(inputStream);
        inputStream.close();

        return content;
    }
}

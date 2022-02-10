package de.florianisme.wakeonlan.ui.home.backup.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChooseSaveFileDestinationContract extends ActivityResultContract<Object, Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Object input) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, getFileName());

        return intent;
    }

    @NonNull
    private String getFileName() {
        return "WakeOnLan_Export_" + System.currentTimeMillis() + ".json";
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            return null;
        }
        return intent.getData();
    }
}

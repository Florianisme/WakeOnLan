package de.florianisme.wakeonlan.ui.home.backup.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChooseImportFileDestinationContract extends ActivityResultContract<Object, Uri> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Object input) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");

        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            return null;
        }
        return intent.getData();
    }
}

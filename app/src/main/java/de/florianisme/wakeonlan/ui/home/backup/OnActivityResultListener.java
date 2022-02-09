package de.florianisme.wakeonlan.ui.home.backup;

import android.content.Context;
import android.content.Intent;

public interface OnActivityResultListener {

    void onActivityResult(int requestCode, int resultCode, Intent resultData, Context context) throws Exception;

}

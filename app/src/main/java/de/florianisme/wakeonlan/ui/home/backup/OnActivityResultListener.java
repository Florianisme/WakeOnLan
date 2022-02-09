package de.florianisme.wakeonlan.ui.home.backup;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

public interface OnActivityResultListener {

    void onActivityResult(int requestCode, int resultCode, Intent resultData, Context context, List<Device> devices) throws Exception;

}

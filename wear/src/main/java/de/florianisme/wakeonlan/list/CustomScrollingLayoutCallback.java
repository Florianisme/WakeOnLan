package de.florianisme.wakeonlan.list;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

import de.florianisme.wakeonlan.R;

// Copy from https://developer.android.com/training/wearables/overlays/lists#curved-layout and adjusted
public class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
    private static final float MAX_ICON_PROGRESS = 0.45f;

    private float progressToCenter;

    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {
        if (child.getId() == R.id.list_title || child.getId() == R.id.list_empty) {
            return;
        }

        // Figure out % progress from top to bottom
        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

        // Normalize for center
        progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
        // Adjust to the maximum scale
        progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

        child.setScaleX(1 - progressToCenter / 2);
        child.setScaleY(1 - progressToCenter / 2);
    }
}
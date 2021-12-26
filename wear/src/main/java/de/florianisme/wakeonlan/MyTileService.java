package de.florianisme.wakeonlan;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.wear.tiles.ColorBuilders;
import androidx.wear.tiles.DimensionBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.ModifiersBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class MyTileService extends TileService {
    private static final String RESOURCES_VERSION = "1";

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(
            @NonNull RequestBuilders.TileRequest requestParams) {
        return Futures.immediateFuture(new TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(10_000)
                .setTimeline(new TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(new LayoutElementBuilders.Layout.Builder()
                                        .setRoot(new LayoutElementBuilders.Column.Builder()
                                                .addContent(new LayoutElementBuilders.Text.Builder().setText("Hello World 1")
                                                        .setModifiers(new ModifiersBuilders.Modifiers.Builder().setBorder(
                                                                new ModifiersBuilders.Border.Builder()
                                                                        .setColor(new ColorBuilders.ColorProp.Builder().setArgb(Color.argb(255, 128, 128, 200))
                                                                                .build())
                                                                        .setWidth(new DimensionBuilders.DpProp.Builder().setValue(2f)
                                                                                .build())
                                                                        .build())
                                                                .setClickable(new ModifiersBuilders.Clickable.Builder().build())
                                                                .setPadding(new ModifiersBuilders.Padding.Builder().setAll(new DimensionBuilders.DpProp.Builder().setValue(10f).build())
                                                                        .build())
                                                                .build())
                                                        .build())
                                                .addContent(new LayoutElementBuilders.Text.Builder().setText("Hello World 2").build())
                                                .addContent(new LayoutElementBuilders.Text.Builder().setText("Hello World 3").build())
                                                .build()
                                        ).build()
                                ).build()
                        ).build()
                ).build()
        );
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(
            @NonNull RequestBuilders.ResourcesRequest requestParams) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build());
    }
}
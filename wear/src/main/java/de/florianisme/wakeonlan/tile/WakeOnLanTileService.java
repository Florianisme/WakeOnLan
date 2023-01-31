package de.florianisme.wakeonlan.tile;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.wear.tiles.ActionBuilders;
import androidx.wear.tiles.DeviceParametersBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.ModifiersBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.StateBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;
import androidx.wear.tiles.material.CompactChip;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Optional;

import de.florianisme.wakeonlan.mobile.MobileClient;
import de.florianisme.wakeonlan.mobile.OnDataReceivedListener;
import de.florianisme.wakeonlan.models.DeviceDto;

public class WakeOnLanTileService extends TileService {

    private static final String RESOURCES_VERSION = "1";

    private MessageClient messageClient;
    private DataClient dataClient;
    private NodeClient nodeClient;

    ListenableFuture<TileBuilders.Tile> temporaryTileContent;

    @Override
    public void onCreate() {
        nodeClient = Wearable.getNodeClient(this);
        dataClient = Wearable.getDataClient(this);
        messageClient = Wearable.getMessageClient(this);
    }

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(@NonNull RequestBuilders.TileRequest requestParams) {
        if (clickableIdIsSet(requestParams)) {
            sendWolCommand(requestParams.getState());
        }


        return CallbackToFutureAdapter.getFuture(completer -> {
            MobileClient.getDevicesList(nodeClient, dataClient, new OnDataReceivedListener() {
                @Override
                public void onDataReceived(List<DeviceDto> devices) {
                    LayoutElementBuilders.Column.Builder columnBuilder = new LayoutElementBuilders.Column.Builder();

                    for (DeviceDto device : devices) {
                        CompactChip chip = buildDeviceChip(device, requestParams);

                        columnBuilder.addContent(chip);
                    }

                    TileBuilders.Tile tile = new TileBuilders.Tile.Builder()
                            .setResourcesVersion(RESOURCES_VERSION)
                            .setTimeline(new TimelineBuilders.Timeline.Builder()
                                    .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                                            .setLayout(new LayoutElementBuilders.Layout.Builder()
                                                    .setRoot(columnBuilder
                                                            .build()
                                                    ).build()
                                            ).build()
                                    ).build()
                            ).build();

                    completer.set(tile);
                }

                @Override
                public void onError(Exception e) {
                    completer.setException(e);
                }
            });

            return "MobileClient.getDevicesList";
        });
    }

    private boolean clickableIdIsSet(RequestBuilders.TileRequest requestParams) {
        return requestParams.getState() != null && !Strings.isNullOrEmpty(requestParams.getState().getLastClickableId());
    }

    private void sendWolCommand(@NonNull StateBuilders.State state) {
        int lastClickedDeviceId = Integer.parseInt(state.getLastClickableId());
        MobileClient.sendDeviceClickedMessage(nodeClient, messageClient, lastClickedDeviceId);
    }

    @NonNull
    private CompactChip buildDeviceChip(DeviceDto deviceDto, @NonNull RequestBuilders.TileRequest requestParams) {
        DeviceParametersBuilders.DeviceParameters deviceParameters = Optional.ofNullable(requestParams.getDeviceParameters())
                .orElseGet(() -> new DeviceParametersBuilders.DeviceParameters.Builder().build());

        ModifiersBuilders.Clickable clickable = new ModifiersBuilders.Clickable.Builder()
                .setId(String.valueOf(deviceDto.getId()))
                .setOnClick(new ActionBuilders.LoadAction.Builder().build())
                .build();

        return new CompactChip.Builder(this, Strings.nullToEmpty(deviceDto.getName()), clickable,
                deviceParameters)
                .build();
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(@NonNull RequestBuilders.ResourcesRequest requestParams) {
        return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build());
    }
}

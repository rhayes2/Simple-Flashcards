package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.NearbyDevice;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.DeviceUtils;
import com.randomappsinc.simpleflashcards.utils.FileUtils;
import com.randomappsinc.simpleflashcards.utils.JSONUtils;
import com.randomappsinc.simpleflashcards.utils.MyApplication;
import com.randomappsinc.simpleflashcards.utils.StringUtils;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class NearbyConnectionsManager {

    public interface PreConnectionListener {
        void onNearbyDeviceFound(NearbyDevice device);

        void onNearbyDeviceLost(String endpointId);

        void onConnectionRequest(ConnectionInfo connectionInfo);

        /**
         * This is called when we fail to establish the connection. This happens when:
         * 1. The other side rejects the connection.
         * 2. We failed to establish the connection during the confirmation step.
         */
        void onConnectionFailed();

        void onConnectionSuccessful();
    }

    public interface PostConnectionListener {
        void onDisconnect();
    }

    public interface FlashcardSetTransferStatusListener {
        void onFlashcardSetTransferFailure(int flashcardSetId);
    }

    private static NearbyConnectionsManager instance;

    public static NearbyConnectionsManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized NearbyConnectionsManager getSync() {
        if (instance == null) {
            instance = new NearbyConnectionsManager();
        }
        return instance;
    }

    @Nullable protected PreConnectionListener preConnectionListener;
    private PreferencesManager preferencesManager = PreferencesManager.get();
    protected DatabaseManager databaseManager = DatabaseManager.get();
    @Nullable protected ConnectionsClient connectionsClient;
    protected String currentlyConnectedEndpoint;
    protected boolean isRejecter;
    protected String otherSideName;

    @Nullable protected PostConnectionListener postConnectionListener;
    @Nullable protected FlashcardSetTransferStatusListener flashcardSetTransferStatusListener;

    // For incoming payloads
    protected Map<Long, Payload> payloadIdToPayload = new HashMap<>();

    // For outgoing payloads
    protected Map<Long, Integer> payloadIdToFlashcardSetId = new HashMap<>();

    private NearbyConnectionsManager() {}

    public void setPreConnectionListener(@NonNull PreConnectionListener preConnectionListener) {
        this.preConnectionListener = preConnectionListener;
    }

    public void startAdvertisingAndDiscovering(Context context) {
        connectionsClient = Nearby.getConnectionsClient(context);
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();

        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder()
                .setStrategy(Strategy.P2P_POINT_TO_POINT)
                .build();

        String nearbyName = preferencesManager.getNearbyName() + "\n" + DeviceUtils.getDeviceName();
        connectionsClient.startAdvertising(
                nearbyName,
                context.getPackageName(),
                connectionLifecycleCallback,
                advertisingOptions)
                .addOnFailureListener(advertisingFailureListener);

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
                .setStrategy(Strategy.P2P_POINT_TO_POINT)
                .build();
        connectionsClient.startDiscovery(
                context.getPackageName(),
                endpointDiscoveryCallback,
                discoveryOptions)
                .addOnFailureListener(discoveryFailureListener);
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            currentlyConnectedEndpoint = endpointId;
            otherSideName = StringUtils.getSaneDeviceString(connectionInfo.getEndpointName());
            preConnectionListener.onConnectionRequest(connectionInfo);
        }

        @Override
        public void onConnectionResult(
                @NonNull String endpointId,
                @NonNull ConnectionResolution connectionResolution) {
            switch (connectionResolution.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    UIUtils.showLongToast(R.string.connection_successful);
                    if (preConnectionListener != null) {
                        preConnectionListener.onConnectionSuccessful();
                    }
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    if (!isRejecter) {
                        UIUtils.showLongToast(R.string.connection_rejected);
                        if (preConnectionListener != null) {
                            preConnectionListener.onConnectionFailed();
                        }
                    }
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    UIUtils.showLongToast(R.string.connection_confirmation_failed);
                    if (preConnectionListener != null) {
                        preConnectionListener.onConnectionFailed();
                    }
                    break;
            }
            isRejecter = false;
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            UIUtils.showLongToast(MyApplication
                    .getAppContext()
                    .getString(R.string.disconnected_from, otherSideName));
            if (postConnectionListener != null) {
                postConnectionListener.onDisconnect();
            }
        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(
                @NonNull String endpointId,
                @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            if (discoveredEndpointInfo
                    .getServiceId()
                    .equals(MyApplication.getAppContext().getPackageName()) && preConnectionListener != null) {
                NearbyDevice device = new NearbyDevice();
                device.setEndpointId(endpointId);

                String endpointName = discoveredEndpointInfo.getEndpointName();
                int newlinePos = endpointName.indexOf("\n");
                if (newlinePos == -1 || newlinePos == endpointName.length() - 1) {
                    device.setNearbyName(endpointName);
                } else {
                    device.setNearbyName(endpointName.substring(0, newlinePos));
                    device.setDeviceType(endpointName.substring(newlinePos + 1));
                }
                preConnectionListener.onNearbyDeviceFound(device);
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            if (preConnectionListener != null) {
                preConnectionListener.onNearbyDeviceLost(endpointId);
            }
        }
    };

    public void requestConnection(String endpointId) {
        if (connectionsClient == null) {
            return;
        }
        connectionsClient.requestConnection(
                preferencesManager.getNearbyName() + "\n" + DeviceUtils.getDeviceName(),
                endpointId,
                connectionLifecycleCallback)
                .addOnFailureListener(requestConnectionFailureListener);
    }

    public void acceptConnection() {
        if (connectionsClient == null || TextUtils.isEmpty(currentlyConnectedEndpoint)) {
            return;
        }
        connectionsClient.acceptConnection(currentlyConnectedEndpoint, payloadCallback);
    }

    public void rejectConnection() {
        if (connectionsClient == null || TextUtils.isEmpty(currentlyConnectedEndpoint)) {
            return;
        }
        isRejecter = true;
        connectionsClient.rejectConnection(currentlyConnectedEndpoint);
    }

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            payloadIdToPayload.put(payload.getId(), payload);
        }

        @Override
        public void onPayloadTransferUpdate(
                @NonNull String endpointId,
                @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            long payloadId = payloadTransferUpdate.getPayloadId();
            switch (payloadTransferUpdate.getStatus()) {
                case PayloadTransferUpdate.Status.SUCCESS:
                    if (payloadIdToFlashcardSetId.containsKey(payloadId)) {
                        payloadIdToFlashcardSetId.remove(payloadId);
                    } else {
                        Payload payload = payloadIdToPayload.get(payloadId);
                        if (payload == null) {
                            return;
                        }
                        File file = payload.asFile().asJavaFile();
                        FlashcardSet flashcardSet = JSONUtils.deserializeFlashcardSet(
                                FileUtils.getFileContents(file));
                        if (flashcardSet == null) {
                            return;
                        }
                        databaseManager.saveNearbyTransferSet(flashcardSet);
                        UIUtils.showLongToast(MyApplication.getAppContext().getString(
                                R.string.received_set,
                                flashcardSet.getName()));
                    }
                    break;
                case PayloadTransferUpdate.Status.FAILURE:
                    if (!payloadIdToFlashcardSetId.containsKey(payloadId)) {
                        return;
                    }
                    if (flashcardSetTransferStatusListener != null) {
                        flashcardSetTransferStatusListener
                                .onFlashcardSetTransferFailure(payloadIdToFlashcardSetId.get(payloadId));
                    }
                    payloadIdToFlashcardSetId.remove(payloadId);
                    break;
            }
        }
    };

    private final OnFailureListener advertisingFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            UIUtils.showLongToast(R.string.advertising_fail);
        }
    };

    private final OnFailureListener discoveryFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            UIUtils.showLongToast(R.string.discovery_fail);
        }
    };

    private final OnFailureListener requestConnectionFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (preConnectionListener != null) {
                preConnectionListener.onConnectionFailed();
            }
        }
    };

    public void setPostConnectionListener(@NonNull PostConnectionListener postConnectionListener) {
        this.postConnectionListener = postConnectionListener;
    }

    public String getOtherSideName() {
        return otherSideName;
    }

    public void disconnect() {
        if (connectionsClient != null) {
            connectionsClient.disconnectFromEndpoint(currentlyConnectedEndpoint);
        }
    }

    public void setFlashcardSetTransferStatusListener(@NonNull FlashcardSetTransferStatusListener listener) {
        flashcardSetTransferStatusListener = listener;
    }

    public void sendFlashcardSet(FlashcardSet flashcardSet) {
        if (connectionsClient == null) {
            return;
        }

        try {
            File flashcardSetFile = FileUtils.writeFlashcardSetToFile(flashcardSet);
            if (flashcardSetFile == null) {
                return;
            }
            Payload payload = Payload.fromFile(flashcardSetFile);
            payloadIdToFlashcardSetId.put(payload.getId(), flashcardSet.getId());
            connectionsClient.sendPayload(currentlyConnectedEndpoint, payload);
        } catch (FileNotFoundException exception) {
            if (flashcardSetTransferStatusListener != null) {
                flashcardSetTransferStatusListener.onFlashcardSetTransferFailure(flashcardSet.getId());
            }
        }
    }

    public void shutdown() {
        if (connectionsClient != null) {
            connectionsClient.stopAdvertising();
            connectionsClient.stopDiscovery();
            connectionsClient.stopAllEndpoints();
            connectionsClient = null;
        }
        preConnectionListener = null;
        postConnectionListener = null;
        flashcardSetTransferStatusListener = null;
    }
}

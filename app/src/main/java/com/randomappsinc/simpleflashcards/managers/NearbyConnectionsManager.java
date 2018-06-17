package com.randomappsinc.simpleflashcards.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.randomappsinc.simpleflashcards.models.NearbyDevice;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.utils.DeviceUtils;
import com.randomappsinc.simpleflashcards.utils.MyApplication;

public class NearbyConnectionsManager {

    public interface Listener {
        void onNearbyDeviceFound(NearbyDevice device);

        void onNearbyDeviceLost(String endpointId);
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

    @Nullable protected Listener listener;
    private PreferencesManager preferencesManager = PreferencesManager.get();
    @Nullable protected ConnectionsClient connectionsClient;

    private NearbyConnectionsManager() {}

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void startAdvertisingAndDiscovering(Context context) {
        ConnectionsClient connectionsClient = Nearby.getConnectionsClient(context);
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder()
                .setStrategy(Strategy.P2P_POINT_TO_POINT)
                .build();

        String nearbyName = preferencesManager.getNearbyName() + "\n" + DeviceUtils.getDeviceName();
        connectionsClient.startAdvertising(
                nearbyName,
                context.getPackageName(),
                connectionLifecycleCallback,
                advertisingOptions);

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder()
                .setStrategy(Strategy.P2P_POINT_TO_POINT)
                .build();
        connectionsClient.startDiscovery(
                context.getPackageName(),
                endpointDiscoveryCallback,
                discoveryOptions);
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {

        }

        @Override
        public void onConnectionResult(
                @NonNull String endpointId,
                @NonNull ConnectionResolution connectionResolution) {

        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {

        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(
                @NonNull String endpointId,
                @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            if (discoveredEndpointInfo
                    .getServiceId()
                    .equals(MyApplication.getAppContext().getPackageName()) && listener != null) {
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
                listener.onNearbyDeviceFound(device);
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            if (listener != null) {
                listener.onNearbyDeviceLost(endpointId);
            }
        }
    };

    public void shutdown() {
        if (connectionsClient != null) {
            connectionsClient.stopAdvertising();
            connectionsClient.stopDiscovery();
            connectionsClient.stopAllEndpoints();
            connectionsClient = null;
        }
        listener = null;
    }
}

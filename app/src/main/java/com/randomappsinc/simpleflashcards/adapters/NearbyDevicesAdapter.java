package com.randomappsinc.simpleflashcards.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.NearbyDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbyDevicesAdapter extends RecyclerView.Adapter<NearbyDevicesAdapter.NearbyDeviceViewHolder> {

    public interface Listener {
        void onNearbyDeviceChosen(NearbyDevice device);
    }

    @NonNull protected Listener listener;
    protected List<NearbyDevice> nearbyDevices = new ArrayList<>();

    public NearbyDevicesAdapter(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void addNearbyDevice(NearbyDevice device) {
        // De-dupe because sometimes the API puts out 2 entities for you
        for (NearbyDevice alreadyShown : nearbyDevices) {
            if (alreadyShown.getNearbyName().equals(device.getNearbyName())
                    && alreadyShown.getDeviceType().equals(device.getDeviceType())) {
                return;
            }
        }
        nearbyDevices.add(device);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeNearbyDevice(String endpointId) {
        for (int i = 0; i < nearbyDevices.size(); i++) {
            if (nearbyDevices.get(i).getEndpointId().equals(endpointId)) {
                nearbyDevices.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public NearbyDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.nearby_device_cell,
                parent,
                false);
        return new NearbyDeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyDeviceViewHolder holder, int position) {
        holder.loadDevice(position);
    }

    @Override
    public int getItemCount() {
        return nearbyDevices.size();
    }

    class NearbyDeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nearby_name) TextView nearbyName;
        @BindView(R.id.device_type) TextView deviceType;
        @BindString(R.string.unknown_device_type) String unknownDevice;

        NearbyDeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadDevice(int position) {
            NearbyDevice device = nearbyDevices.get(position);
            nearbyName.setText(device.getNearbyName());
            deviceType.setText(TextUtils.isEmpty(device.getDeviceType())
                    ? unknownDevice
                    : device.getDeviceType());
        }

        @OnClick(R.id.parent)
        public void onDeviceChosen() {
            listener.onNearbyDeviceChosen(nearbyDevices.get(getAdapterPosition()));
        }
    }
}

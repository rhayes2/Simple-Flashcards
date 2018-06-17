package com.randomappsinc.simpleflashcards.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.models.NearbyDevice;

import java.util.ArrayList;
import java.util.List;

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
        @BindView(R.id.device_name) TextView deviceName;

        NearbyDeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadDevice(int position) {
            deviceName.setText(nearbyDevices.get(position).getNearbyName());
        }

        @OnClick(R.id.parent)
        public void onDeviceChosen() {
            listener.onNearbyDeviceChosen(nearbyDevices.get(getAdapterPosition()));
        }
    }
}

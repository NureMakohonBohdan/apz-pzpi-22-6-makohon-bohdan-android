package com.example.apz_pzpi_22_6_makohon_bohdan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apz_pzpi_22_6_makohon_bohdan.R;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.SensorData;

import java.util.List;

public class SensorDataAdapter extends RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder> {

    private List<SensorData> dataList;

    public SensorDataAdapter(List<SensorData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public SensorDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new SensorDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorDataViewHolder holder, int position) {
        SensorData data = dataList.get(position);
        holder.tvTimestamp.setText(data.getTimestamp());
        holder.tvValue.setText(String.valueOf(data.getValue()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class SensorDataViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimestamp, tvValue;

        public SensorDataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}

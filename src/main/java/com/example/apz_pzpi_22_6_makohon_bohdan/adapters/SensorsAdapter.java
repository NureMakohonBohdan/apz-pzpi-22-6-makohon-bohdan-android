package com.example.apz_pzpi_22_6_makohon_bohdan.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apz_pzpi_22_6_makohon_bohdan.R;
import com.example.apz_pzpi_22_6_makohon_bohdan.SensorDetailsActivity;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.Sensor;

import java.util.List;

public class SensorsAdapter extends RecyclerView.Adapter<SensorsAdapter.SensorViewHolder> {

    public interface OnSensorDeleteListener {
        void onDeleteClick(Sensor sensor, int position);
    }

    public interface OnSensorEditListener {
        void onEditClick(Sensor sensor, int position);
    }

    private List<Sensor> sensors;
    private Context context;

    private OnSensorDeleteListener deleteListener;

    private OnSensorEditListener editListener;

    public SensorsAdapter(List<Sensor> sensors, Context context, OnSensorDeleteListener deleteListener, OnSensorEditListener editListener) {
        this.sensors = sensors;
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensors.get(position);
        holder.tvLocation.setText(sensor.getLocation());
        holder.tvSensorType.setText(sensor.getSensorType());

        holder.btnView.setOnClickListener(v ->
                Toast.makeText(context, "View " + sensor.getLocation(), Toast.LENGTH_SHORT).show()
        );

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SensorDetailsActivity.class);
            intent.putExtra("id", sensor.getId());
            intent.putExtra("location", sensor.getLocation());
            intent.putExtra("type", sensor.getSensorType());
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(sensor, position);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(sensor, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public void removeSensor(int position) {
        sensors.remove(position);
        notifyItemRemoved(position);
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation, tvSensorType;
        Button btnView, btnEdit, btnDelete;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSensorType = itemView.findViewById(R.id.tvSensorType);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

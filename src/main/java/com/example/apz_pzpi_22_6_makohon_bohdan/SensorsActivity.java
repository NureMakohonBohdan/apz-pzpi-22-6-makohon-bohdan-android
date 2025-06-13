package com.example.apz_pzpi_22_6_makohon_bohdan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apz_pzpi_22_6_makohon_bohdan.adapters.SensorsAdapter;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.Sensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SensorsActivity extends AppCompatActivity {

    private RecyclerView rvSensors;
    private SensorsAdapter adapter;
    private List<Sensor> sensorsList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        rvSensors = findViewById(R.id.rvSensors);
        // Initialize the adapter with the delete listener
        adapter = new SensorsAdapter(sensorsList, this, (sensor, position) -> {
            deleteSensor(sensor, position);
        }, (sensor, position) -> {
            showEditDialog(sensor, position);
        });
        rvSensors.setLayoutManager(new LinearLayoutManager(this));
        rvSensors.setAdapter(adapter);

        findViewById(R.id.btnAddSensor).setOnClickListener(v -> {
            Intent intent = new Intent(SensorsActivity.this, AddSensorActivity.class);
            startActivity(intent);
        });


        fetchSensors();
    }

    private void showEditDialog(Sensor sensor, int position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_sensor, null);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        etLocation.setText(sensor.getLocation());

        // Setup Spinner
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this, R.array.sensor_types, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterSpinner);

        // Set spinner to current type
        String sensorType = sensor.getSensorType();
        int spinnerPosition = adapterSpinner.getPosition(sensorType);
        spinnerType.setSelection(spinnerPosition);

        new AlertDialog.Builder(this)
                .setTitle("Edit Sensor")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newLocation = etLocation.getText().toString().trim();
                    String newType = spinnerType.getSelectedItem().toString().trim();
                    updateSensor(sensor, position, newLocation, newType);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void updateSensor(Sensor sensor, int position, String newLocation, String newType) {
        String url = "http://10.0.2.2:8080/api/sensors/" + sensor.getId();

        // Create JSON body
        JSONObject json = new JSONObject();
        try {
            json.put("id", sensor.getId()); // Include if your DTO requires id
            json.put("location", newLocation);
            json.put("sensorType", newType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SensorsActivity.this, "Update failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Update local list and refresh adapter
                        sensor.setLocation(newLocation);
                        sensor.setSensorType(newType);
                        adapter.notifyItemChanged(position);
                        Toast.makeText(SensorsActivity.this, "Sensor updated", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(SensorsActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }



    private void deleteSensor(Sensor sensor, int position) {
        String url = "http://10.0.2.2:8080/api/sensors/" + sensor.getId();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SensorsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        adapter.removeSensor(position);
                        Toast.makeText(SensorsActivity.this, "Sensor deleted", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(SensorsActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSensors();
    }

    private void fetchSensors() {


        int id = 0;
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userJsonStr = prefs.getString("user_dto", null);
        if (userJsonStr != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonStr);
                id = userJson.getInt("id");
            } catch (JSONException e) {
                // Handle error
            }
        }


        String url = "http://10.0.2.2:8080/api/sensors/user/"+id;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SensorsActivity.this, "Failed to load sensors", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(SensorsActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
                String jsonData = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(jsonData);
                    List<Sensor> loadedSensors = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String location = obj.optString("location");
                        String type = obj.optString("sensorType");
                        loadedSensors.add(new Sensor(id, location, type));
                    }
                    runOnUiThread(() -> {
                        sensorsList.clear();
                        sensorsList.addAll(loadedSensors);
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(SensorsActivity.this, "Parsing error", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}

package com.example.apz_pzpi_22_6_makohon_bohdan;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apz_pzpi_22_6_makohon_bohdan.adapters.SensorDataAdapter;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.Sensor;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.SensorData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorDetailsActivity extends AppCompatActivity {

    private TextView tvSensorId, tvSensorLocation, tvSensorType, tvSensorTitle;
    private RecyclerView rvSensorData;
    private SensorDataAdapter adapter;
    private List<SensorData> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        tvSensorId = findViewById(R.id.tvSensorId);
        tvSensorLocation = findViewById(R.id.tvSensorLocation);
        tvSensorType = findViewById(R.id.tvSensorType);
        tvSensorTitle = findViewById(R.id.tvSensorTitle);
        rvSensorData = findViewById(R.id.rvSensorData);

        // Get passed data
        int id = getIntent().getIntExtra("id", -1);
        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");

        tvSensorId.setText("ID: " + id);
        tvSensorLocation.setText("Location: " + location);
        tvSensorType.setText("Type: " + type);
        tvSensorTitle.setText("Sensor " + id + " Details");

        // Setup RecyclerView
        adapter = new SensorDataAdapter(sensorDataList);
        rvSensorData.setLayoutManager(new LinearLayoutManager(this));
        rvSensorData.setAdapter(adapter);

        // Load sensor data from backend
        fetchSensorData(id);
    }

    private void fetchSensorData(int sensorId) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/api/sensors/" + sensorId + "/data";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SensorDetailsActivity.this, "Failed to load sensor data", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(SensorDetailsActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
                String json = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    List<SensorData> loaded = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Long id = obj.optLong("id");
                        JSONObject sensorObj = obj.optJSONObject("sensor");
                        Sensor sensor = null;
                        if (sensorObj != null) {
                            sensor = new Sensor();
                            sensor.setId(sensorObj.optInt("id"));
                            sensor.setLocation(sensorObj.optString("location"));
                            sensor.setSensorType(sensorObj.optString("sensorType"));
                        }
                        Double value = obj.optDouble("value");
                        String timestamp = obj.optString("timestamp");

                        SensorData data = new SensorData();
                        data.setId(id);
                        data.setSensor(sensor);
                        data.setValue(value);
                        data.setTimestamp(timestamp);

                        loaded.add(data);
                    }
                    runOnUiThread(() -> {
                        sensorDataList.clear();
                        sensorDataList.addAll(loaded);
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(SensorDetailsActivity.this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

}

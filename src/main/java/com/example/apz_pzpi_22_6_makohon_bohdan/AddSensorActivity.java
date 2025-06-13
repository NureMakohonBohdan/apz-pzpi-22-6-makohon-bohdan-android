package com.example.apz_pzpi_22_6_makohon_bohdan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddSensorActivity extends AppCompatActivity {

    private EditText etLocation;
    private Spinner spinnerSensorType;
    private Button btnAddSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        etLocation = findViewById(R.id.etLocation);
        spinnerSensorType = findViewById(R.id.spinnerSensorType);
        btnAddSensor = findViewById(R.id.btnAddSensor);

        // Set up Spinner (dropdown) values
        String[] types = {"Temperature", "Humidity", "Pressure"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensorType.setAdapter(adapter);

        btnAddSensor.setOnClickListener(v -> {
            String location = etLocation.getText().toString().trim();
            String type = spinnerSensorType.getSelectedItem().toString();

            if (location.isEmpty()) {
                etLocation.setError("Please enter location");
                return;
            }

            sendAddSensorRequest(location, type);

            finish(); // Or navigate back / update list, as needed
        });
    }

    private void sendAddSensorRequest(String location, String sensorType) {
        OkHttpClient client = new OkHttpClient();

        // Prepare JSON
        JSONObject json = new JSONObject();
        try {
            json.put("location", location);
            json.put("sensorType", sensorType);
            // No id on create
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show());
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

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


        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/sensors/user/" + id)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AddSensorActivity.this, "Failed to add sensor", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddSensorActivity.this, "Sensor added successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AddSensorActivity.this, SensorsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddSensorActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
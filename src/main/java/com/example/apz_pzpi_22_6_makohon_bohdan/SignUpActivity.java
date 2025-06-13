package com.example.apz_pzpi_22_6_makohon_bohdan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.apz_pzpi_22_6_makohon_bohdan.api.AuthApi;
import com.example.apz_pzpi_22_6_makohon_bohdan.api.RetrofitClient;
import com.example.apz_pzpi_22_6_makohon_bohdan.model.UserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etRepeatPassword;
    private Button btnSignUp;
    private AuthApi authApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        btnSignUp        = findViewById(R.id.btnSignUp);

        // get the API
        authApi = RetrofitClient.getAuthApi();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        String email  = etEmail.getText().toString().trim();
        String pass   = etPassword.getText().toString();
        String repeat = etRepeatPassword.getText().toString();

        // basic validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (!pass.equals(repeat)) {
            etRepeatPassword.setError("Passwords do not match");
            etRepeatPassword.requestFocus();
            return;
        }

        // create DTO
        UserDTO user = new UserDTO(email, pass);

        // show a simple “loading” toast — you could swap in a ProgressBar
        Toast.makeText(this, "Signing up…", Toast.LENGTH_SHORT).show();

        // make the network call
        authApi.signup(user).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> res) {
                if (res.isSuccessful() && res.body() != null) {
                    // success! your backend returns the saved UserDTO
                    Toast.makeText(SignUpActivity.this,
                            "Welcome, " + res.body().getEmail(),
                            Toast.LENGTH_LONG).show();
                    finish(); // or navigate to HomeActivity
                } else {
                    // backend returned 400 or other error
                    String err = "Signup failed";
                    if (res.errorBody() != null) {
                        try { err = res.errorBody().string(); }
                        catch (Exception ignored) {}
                    }
                    Toast.makeText(SignUpActivity.this, err, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                // network error or unexpected exception
                Toast.makeText(SignUpActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

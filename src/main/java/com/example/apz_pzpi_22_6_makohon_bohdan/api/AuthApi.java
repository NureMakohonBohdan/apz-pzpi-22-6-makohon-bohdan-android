package com.example.apz_pzpi_22_6_makohon_bohdan.api;



import com.example.apz_pzpi_22_6_makohon_bohdan.model.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/api/users")
    Call<UserDTO> signup(@Body UserDTO user);
}

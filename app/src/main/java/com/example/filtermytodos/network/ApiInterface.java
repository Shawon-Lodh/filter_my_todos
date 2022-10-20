package com.example.filtermytodos.network;

import com.example.filtermytodos.model.ToDoResponse;
import com.example.filtermytodos.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<List<ToDoResponse>> getUserBasedToDos(@Url String url);

    @GET("/users")
    Call<List<UserResponse>> getAllUsers();
}
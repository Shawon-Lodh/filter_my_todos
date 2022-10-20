

package com.example.filtermytodos;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.filtermytodos.model.ToDoResponse;
import com.example.filtermytodos.model.UserResponse;
import com.example.filtermytodos.network.ApiInterface;
import com.example.filtermytodos.network.RetrofitApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ArrayList<ToDoResponse> toDoResponseList = new ArrayList<>();
    ArrayList<String> todoArray = new ArrayList<>();

    ArrayList<UserResponse> userResponseList = new ArrayList<>();
    ArrayList<String> userArray = new ArrayList<>(Arrays.asList("Select User"));
    ArrayList<Integer> userIDArray = new ArrayList<>();

    Spinner userListDropdown;
    ArrayAdapter todoListAdapter;
    ArrayAdapter userListAdapter;
    ListView listView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.todo_list);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        userListDropdown = (Spinner)findViewById(R.id.user_list_dropdown);
        userListDropdown.setOnItemSelectedListener(this);

        todoListAdapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview,R.id.title, todoArray);
        userListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, userArray);

        userListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fetchUsers();
    }

    public void showMyTodos(int id) {

        todoArray.clear();

        progressBar.setVisibility(View.VISIBLE); //network call will start. So, show progress bar
        listView.setVisibility(View.GONE);

        ApiInterface apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        String url = ("/todos/?userId=").concat(String.valueOf(id));
        Call<List<ToDoResponse>> call = apiInterface.getUserBasedToDos(url);
        call.enqueue(new Callback<List<ToDoResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ToDoResponse>> call, @NonNull Response<List<ToDoResponse>> response) {
                progressBar.setVisibility(View.GONE); //network call success. So hide progress bar
                listView.setVisibility(View.VISIBLE);
                Log.d("","Response code : "+response.code());
                if (response.code()==200) { //response code 200 means server call successful
                    for (int i = 0; i < response.body().size(); i++ ){
                        String title = response.body().get(i).getTitle();
                        todoArray.add(title);
                        Log.d("Assets ","Task Title is : " + title);
                    }
                    listView.setAdapter(todoListAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "network error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ToDoResponse>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE); //network call failed. So hide progress bar
            }
        });
    }

    public void fetchUsers() {

        userListDropdown.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE); //network call will start. So, show progress bar

        ApiInterface apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        Call<List<UserResponse>> call = apiInterface.getAllUsers();
        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                progressBar.setVisibility(View.GONE); //network call success. So hide progress bar

                Log.d("","Response code : "+response.code());
                if (response.code()==200) { //response code 200 means server call successful
                    for (int i = 0; i < response.body().size(); i++ ){
                        String username = response.body().get(i).getUsername();
                        int userId = response.body().get(i).getId();
                        userArray.add(username);
                        userIDArray.add(userId);
                        Log.d("Assets ","User Name is : " + username);
                    }
                    userListDropdown.setAdapter(userListAdapter);
                 userListDropdown.setVisibility(View.VISIBLE);
                } else {
//                    //somehow data not found. So error message showing in first TextView
//                    ipAddressTextView.setText(response.message());
//                    cityTextView.setText("");
//                    countryTextView.setText("");
                    Toast.makeText(MainActivity.this, "network error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE); //network call failed. So hide progress bar
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String selected = userListDropdown.getItemAtPosition(position).toString();

        if (!selected.equals("Select User"))
        {
            int userId = userIDArray.get(position-1);
            showMyTodos(userId);
            Log.v("item", (String) parent.getItemAtPosition(position));
        }else{
            todoArray.clear();
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
package com.mware.polyshoprestapi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mware.polyshoprestapi.R;
import com.mware.polyshoprestapi.api.APIServer;
import com.mware.polyshoprestapi.api.UserServices;
import com.mware.polyshoprestapi.models.User;
import com.mware.polyshoprestapi.ui.login.Login;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingFragment extends Fragment {
    TextInputEditText input_name, input_email, input_password;
    Button btnSaveChanges;
    SharedPreferences preferences;
    ImageView imageView;
    TextView textView4;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        input_name = view.findViewById(R.id.input_name);
        input_email = view.findViewById(R.id.input_email);
        input_password = view.findViewById(R.id.input_password);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        imageView = view.findViewById(R.id.imageView3);
        textView4 = view.findViewById(R.id.textView4);

        preferences = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserServices services = new APIServer().RETROFIT.create(UserServices.class);

        User user = new Gson().fromJson(preferences.getString("cookie", ""), User.class);

        Picasso.get().load("https://minhdevs.github.io/introduce/dev.png").into(imageView);

        input_name.setText(user.getFullName());
        input_email.setText(user.getEmail());
        input_password.setText(user.getPassword());

        btnSaveChanges.setOnClickListener(v -> {
            String name = input_name.getText().toString().trim();
            String email = input_email.getText().toString().trim();
            String password = input_password.getText().toString().trim();

            user.setFullName(name);
            user.setEmail(email);
            user.setPassword(password);

            Call<JsonElement> call = services.updateUser(user);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.body() != null) {

                        String cookie = new Gson().toJson(user);

                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("cookie", cookie);
                        editor.apply();
                        editor.commit();

                        Toast.makeText(getActivity(), "Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {

                }
            });

        });

        textView4.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            editor.commit();

            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        });
    }
}
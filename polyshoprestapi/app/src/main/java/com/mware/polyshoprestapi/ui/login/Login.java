package com.mware.polyshoprestapi.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mware.polyshoprestapi.MainActivity;
import com.mware.polyshoprestapi.R;
import com.mware.polyshoprestapi.api.APIServer;
import com.mware.polyshoprestapi.api.UserServices;
import com.mware.polyshoprestapi.models.User;
import com.mware.polyshoprestapi.ui.register.Register;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

	private TextInputEditText inputEmail, inputPassword;
	private Button btnGoToRegister;
	private Button btnLogin;
	private UserServices userServices;
	private SharedPreferences sharedPreferences;

	@SuppressLint("MissingInflatedId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sharedPreferences = getSharedPreferences("cookie", Context.MODE_PRIVATE);

		userServices = new APIServer().RETROFIT.create(UserServices.class);

		inputEmail = (TextInputEditText) findViewById(R.id.input_email);
		inputPassword = (TextInputEditText) findViewById(R.id.input_password);
		btnGoToRegister = (Button) findViewById(R.id.btnGoToRegister);
		btnLogin = (Button) findViewById(R.id.btnLogin);

		String userPref = sharedPreferences.getString("cookie", "");

		if (!userPref.equals("")) {
			User user = new Gson().fromJson(userPref, User.class);
			authLogin(user.getEmail(), user.getPassword());

		}

		btnLogin.setOnClickListener(v -> {
			String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
			String password = Objects.requireNonNull(inputPassword.getText()).toString().trim();
			Toast.makeText(this, "Please wait......", Toast.LENGTH_SHORT).show();
			authLogin(email, password);
		});

		btnGoToRegister.setOnClickListener(v -> {
			startActivity(new Intent(Login.this, Register.class));
			finish();
		});

	}

	private void authLogin(String email, String password) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("password", password);

		Call<JsonElement> call = userServices.authLogin(jsonObject);

		call.enqueue(new Callback<JsonElement>() {
			@Override
			public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
				JsonElement jsonElement = response.body();

				if (response.code() != 200) {
					Toast.makeText(Login.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
					return;
				}

				assert jsonElement != null;
				JsonObject responseData = jsonElement.getAsJsonObject();
				JsonObject userJson = responseData.getAsJsonObject("user");

				User userObject = new Gson().fromJson(userJson, User.class);

				String userString = new Gson().toJson(userObject).toString();

				SharedPreferences.Editor cookieEditor = sharedPreferences.edit();
				cookieEditor.putString("cookie", userString);
				cookieEditor.apply();

				Toast.makeText(Login.this, "Login successfully !", Toast.LENGTH_SHORT).show();

				startActivity(new Intent(Login.this, MainActivity.class));
				finish();

			}

			@Override
			public void onFailure(Call<JsonElement> call, Throwable t) {
				Toast.makeText(Login.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
			}
		});

	}
}
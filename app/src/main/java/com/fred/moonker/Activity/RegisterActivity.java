package com.fred.moonker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fred.moonker.Model.User;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {
    private EditText etUsername,etPassword,etEmail;
    private Button btnRegister;
    private Context context = this;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setListener();
    }

    private void initView() {
        etUsername = findViewById(R.id.reg_act_et_username);
        etPassword = findViewById(R.id.reg_act_et_password);
        etEmail = findViewById(R.id.reg_act_et_email);
        btnRegister = findViewById(R.id.reg_act_btn_register);
        requestQueue = NetTools.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    private void setListener() {
        btnRegister.setOnClickListener(v->{
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String email = etEmail.getText().toString();

            boolean usernameB = username.matches("^[a-zA-Z]\\w{5,17}$");
            boolean passwordB = password.matches("^[a-zA-Z]\\w{5,17}$");
            boolean emailB = email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");

            if(!usernameB){
                Toast.makeText(context,"用户名以字母开头由6-18位的数字或字母下划线组成",Toast.LENGTH_LONG).show();
                return;
            }
            if(!passwordB){
                Toast.makeText(context,"密码以字母开头由6-18位的数字或字母下划线组成",Toast.LENGTH_LONG).show();
                return;
            }
            if(!emailB){
                Toast.makeText(context,"邮箱格式不正确",Toast.LENGTH_LONG).show();
                return;
            }

            User user = new User(username,password,email);
            JSONObject jsonObject = JsonTools.toJsonObject(user);

            String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX+"/registry";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context,"注册成功", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onResponse: "+response.toString());
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsonObjectRequest);

        });
    }
}
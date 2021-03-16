package com.fred.moonker.Activity;


import android.app.Activity;
import android.app.Application;
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
import com.fred.moonker.MainActivity;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.Model.User;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {

    private Button btnLogin,btnJumpRegister;
    private EditText etUsername,etPassword;
    private final Context context = this;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
    }

    private void initView(){
        etUsername = findViewById(R.id.login_act_edit_username);
        etPassword = findViewById(R.id.login_act_edit_password);
        btnLogin = findViewById(R.id.login_act_btn_login);
        btnJumpRegister = findViewById(R.id.login_act_btn_register);
        requestQueue = NetTools.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    private void setListener() {
        btnLogin.setOnClickListener(v -> {
            String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX+"/login";

            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(username.isEmpty()){
                Toast.makeText(this,"输入用户名为空",Toast.LENGTH_SHORT).show();
                return;
            }else if(password.isEmpty()){
                Toast.makeText(this,"输入密码为空",Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(username,password);
            JSONObject jsonObject = JsonTools.toJsonObject(user);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "onResponse: "+response.toString());
                            CommonResult<User> userCommonResult = JsonTools.toCommonResult(response, User.class);
                            //登录成功验证 使用commonResult的code做判断
                            if(userCommonResult.getCode().equals(RetCode.OK)){
                                Log.i(TAG, "onResponse: "+userCommonResult.getData());
                                //将用户全局保存
                                ((MoonkerApplication)getApplication()).setUser(userCommonResult.getData());
                                Log.i(TAG, "onResponse: login:"+MoonkerApplication.getUser());
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(context, userCommonResult.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        });
        btnJumpRegister.setOnClickListener(v -> {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
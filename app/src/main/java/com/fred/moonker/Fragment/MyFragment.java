package com.fred.moonker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fred.moonker.Activity.LoginActivity;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.Model.User;
import com.fred.moonker.Model.UserDetail;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;
import com.fred.moonker.tools.PicTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.ContentValues.TAG;

public class MyFragment extends Fragment {

    private Context context = this.getActivity();
    private ImageButton userHeadPortrait;
    private TextView tvArticleNum,tvLikeNum,tvMarkNum;
    private EditText etIntroduction,etNickname;
    private Bitmap bitmap;
    private Button btnSave,btnExit,btnMyArticle;
    private User user;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater);
        setListener(view);
        requestQueue = NetTools.getInstance(context).getRequestQueue();
        initData();
        return view;
    }

    private View initView(@NonNull LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_my,null);

        etNickname = view.findViewById(R.id.set_f_tv_user_nickname);
        etIntroduction = view.findViewById(R.id.set_f_tv_user_introduction);
        tvArticleNum = view.findViewById(R.id.set_f_article_num);
        tvLikeNum = view.findViewById(R.id.set_f_like_num);
        tvMarkNum = view.findViewById(R.id.set_f_mark_num);

        userHeadPortrait = view.findViewById(R.id.set_f_img_btn_user_head);
        btnSave = view.findViewById(R.id.set_f_btn_save);
        btnExit = view.findViewById(R.id.set_f_btn_exit);
        btnMyArticle = view.findViewById(R.id.set_f_btn_my_article);
        return view;
    }

    private void setListener(View view) {
        userHeadPortrait.setOnClickListener(v -> {
            toPicture();
        });
        btnSave.setOnClickListener((v)->{
            String nickName = etNickname.getText().toString();
            String introduction = etIntroduction.getText().toString();
            if(nickName.isEmpty()){
                Toast.makeText(getActivity(),"用户昵称为空",Toast.LENGTH_SHORT).show();
                return;
            }else if(introduction.isEmpty()){
                Toast.makeText(getActivity(),"用户介绍为空",Toast.LENGTH_SHORT).show();
                return;
            }
            User user = MoonkerApplication.getUser();
            user.setNickname(nickName);
            user.setIntroduction(introduction);
            saveUserInfo(user);
        });

        btnExit = view.findViewById(R.id.set_f_btn_exit);
        btnExit.setOnClickListener((v)->{
            MoonkerApplication.exit();
            Intent it = new Intent(getActivity(), LoginActivity.class);
            startActivity(it);
        });

        btnMyArticle = view.findViewById(R.id.set_f_btn_my_article);
        btnMyArticle.setOnClickListener(v->{
            //TODO view MyArticle and edit it
//            Intent it = new Intent(getActivity(), MyArticleActivity.class);
//            startActivity(it);
        });
    }

    private void initData() {
        User user = MoonkerApplication.getUser();
        etNickname.setText(user.getNickname());
        etIntroduction.setText(user.getIntroduction());
        Long userID = user.getUserID();
        getUserDetail(userID);
        getUserPortrait(userID);
    }

    private void getUserPortrait(Long userID) {
        String httpUri = MoonkerApplication.PORTRAIT_PATH + String.format("i%05d.jpg", userID);
        Glide.with(getActivity())
                .load(httpUri)
                .into(userHeadPortrait);
    }

    private void saveUserInfo(User user) {
        JSONObject jsonObject = JsonTools.toJsonObject(user);
        String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonResult<User> commonResult = JsonTools.toCommonResult(response, User.class);
                        if(commonResult.getCode().equals(RetCode.OK)){
                            Toast.makeText(getActivity(),"保存成功"+url, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserDetail(Long userID) {
        String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX+"/detail/"+userID    ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonResult<UserDetail> udCommonResult = JsonTools.toCommonResult(response, UserDetail.class);
                        UserDetail userDetail = udCommonResult.getData();
                        checkUserDetail(userDetail);
                        tvArticleNum.setText(Long.toString(userDetail.getArticleNum()));
                        tvLikeNum.setText(Long.toString(userDetail.getLikeNum()));
                        tvMarkNum.setText(Long.toString(userDetail.getMarkNum()));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void checkUserDetail(UserDetail userDetail){
        if(userDetail.getLikeNum()==null){
            userDetail.setLikeNum(0L);
        }
        if(userDetail.getMarkNum()==null){
            userDetail.setMarkNum(0L);
        }
    }

    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent,100);
        Log.d(TAG, "toPicture: "+"跳转相册成功");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != RESULT_CANCELED){
            if(data != null){
                Uri uri = data.getData();
                userHeadPortrait.setImageURI(uri);
                InputStream inputStream = null;
                try {
                    inputStream = getActivity().getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    userHeadPortrait.setImageBitmap(bitmap);
                    String imgToBase64 = PicTools.imgToBase64(bitmap);
                    saveUserPortrait(imgToBase64);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    if(inputStream != null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void saveUserPortrait(String bitmap){
        Long userID = MoonkerApplication.getUser().getUserID();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userID);
            jsonObject.put("bitmap",bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX+"/image";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonResult<String> commonResult = JsonTools.toCommonResult(response, String.class);
                        if(commonResult.getCode().equals(RetCode.OK)){
                            Toast.makeText(getActivity(),"头像保存成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(),"头像保存失败,请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}

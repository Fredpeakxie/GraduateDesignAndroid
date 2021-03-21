package com.fred.moonker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.fred.moonker.Activity.LoginActivity;
import com.fred.moonker.MainActivity;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.Model.User;
import com.fred.moonker.Model.UserDetail;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class MyFragment extends Fragment {

    private Context context = getContext();
    private ImageButton userHeadPortrait;
    private TextView tvArticleNum,tvLikeNum,tvMarkNum;
    private EditText userIntroduction,userNickname;
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

        userNickname = view.findViewById(R.id.set_f_tv_user_nickname);
        userIntroduction = view.findViewById(R.id.set_f_tv_user_introduction);
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
            //TODO save
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
        userNickname.setText(user.getNickname());
        userIntroduction.setText(user.getIntroduction());
        Long userID = user.getUserID();
        getUserDetail(userID);
    }

    private void getUserDetail(Long userID) {
        String url = MoonkerApplication.URL+MoonkerApplication.USER_PREFIX+"/detail/"+userID    ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonResult<UserDetail> udCommonResult = JsonTools.toCommonResult(response, UserDetail.class);
                        UserDetail userDetail = udCommonResult.getData();
                        tvArticleNum.setText(Long.toString(userDetail.getArticleNum()));
                        tvLikeNum.setText(Long.toString(userDetail.getLikeNum()));
                        tvMarkNum.setText(Long.toString(userDetail.getMarkNum()));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent,100);
        Log.d(TAG, "toPicture: "+"跳转相册成功");
    }
}

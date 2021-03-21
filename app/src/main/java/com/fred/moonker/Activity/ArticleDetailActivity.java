package com.fred.moonker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ArticleDetailActivity extends Activity {
    private Context context = this;
    private TextView tvTitle,tvUserNickname,tvReadNum,tvLikeNum,tvMarkNum;
    private EditText etComment;
    private WebView wvArticleDetail;
    private ImageButton btnLike,btnMark;
    private Button btnComment;
    private Long articleId,authorId,myUserId,readNum,likeNum,markNum;
    private String html,title,userNickname;
    boolean liked,marked;
    private final MoonkerApplication application = (MoonkerApplication) getApplication();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        netToolInit();
        getDataFromPrevious();
        readArticle();
        initView();
        setListener();

        getArticle();

    }

    public void netToolInit(){
        requestQueue = NetTools.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    private void readArticle() {
        String url = MoonkerApplication.URL+MoonkerApplication.ARTICLE_PREFIX+"/readNumAdd/"+articleId;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getDataFromPrevious(){
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        userNickname = intent.getStringExtra("userNickname");
        articleId = intent.getLongExtra("articleId",-1L);
        authorId = intent.getLongExtra("authorID",-1L);
        readNum = intent.getLongExtra("readNum",-1L);
        likeNum = intent.getLongExtra("likeNum",-1L);
        markNum = intent.getLongExtra("markNum",-1L);
    }

    public void initView(){
        tvTitle = findViewById(R.id.activity_ad_title);
        tvTitle.setText(title);
        tvUserNickname = findViewById(R.id.activity_ad_userNickname);
        tvUserNickname.setText(userNickname);
        tvReadNum = findViewById(R.id.activity_ad_readNum);
        tvReadNum.setText(String.format(Locale.CHINA,"%d",readNum));
        tvLikeNum = findViewById(R.id.activity_ad_likeNum);
        tvLikeNum.setText(String.format("%d",likeNum));
        tvMarkNum = findViewById(R.id.activity_ad_markNum);
        tvMarkNum.setText(String.format("%d",markNum));

        btnLike = findViewById(R.id.article_detail_a_btn_like);
        btnMark = findViewById(R.id.article_detail_a_btn_mark);
        liked = application.likeArticles.contains(articleId);
        marked = application.markArticles.contains(articleId);
        if(liked){
            btnLike.setImageResource(R.mipmap.like_pink);
        }
        if(marked){
            btnMark.setImageResource(R.mipmap.mark2_yellow);
        }

        wvArticleDetail = findViewById(R.id.activity_ad_webView);
        WebSettings webSettings = wvArticleDetail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvArticleDetail.setWebViewClient(new ArticleWebViewClient());
    }

    private void setListener() {
        //做blogId与application中 存储的articleLikes 查找
        myUserId = application.getUser().getUserID();

        btnLike.setOnClickListener(v -> {
            ArticleDetailActivity ada = ArticleDetailActivity.this;
            if(!liked){
                application.likeArticles.add(articleId);
                btnLike.setImageResource(R.mipmap.like_pink);
                userLikeAdd();
                ada.likeNum++;
            }else {
                application.likeArticles.remove(articleId);
                btnLike.setImageResource(R.mipmap.like);
                userLikeRemove();
                ada.likeNum--;
            }
            tvLikeNum.setText(Long.toString(likeNum));
            ada.liked = !liked;
        });

        btnMark.setOnClickListener(v -> {
            ArticleDetailActivity ada = ArticleDetailActivity.this;
            if(!marked){
                application.markArticles.add(articleId);
                btnMark.setImageResource(R.mipmap.mark2_yellow);
                userMarkAdd();
                ada.markNum++;
            }else {
                application.markArticles.remove(articleId);
                btnMark.setImageResource(R.mipmap.mark2);
                userMarkRemove();
                ada.markNum--;
            }
            tvMarkNum.setText(Long.toString(markNum));
            ada.marked = !marked;
        });
    }

    public void getArticle(){
        String articleIdS = String.format("%05d", articleId);
        Log.i(TAG, "getArticle: "+articleIdS);
        String url = MoonkerApplication.HTML_PATH+ "mb" + articleIdS + ".html";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        wvArticleDetail.loadDataWithBaseURL(url,response,"text/html","UTF-8","");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private class ArticleWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view,String url){
            super.onPageFinished(view,url);
            //替换图片尺寸
            imgReset();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        private void imgReset() {
            wvArticleDetail.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                    "}" +
                    "})()");
        }
    }

    private void userLikeAdd() {
        String url = MoonkerApplication.UAR_PATH+"/like/"+MoonkerApplication.getUser().getUserID()+"/"+articleId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void userLikeRemove() {
        String url = MoonkerApplication.UAR_PATH+"/like/"+MoonkerApplication.getUser().getUserID()+"/"+articleId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void userMarkAdd() {
        String url = MoonkerApplication.UAR_PATH+"/mark/"+MoonkerApplication.getUser().getUserID()+"/"+articleId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void userMarkRemove() {
        String url = MoonkerApplication.UAR_PATH+"/mark/"+MoonkerApplication.getUser().getUserID()+"/"+articleId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"请求失败"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
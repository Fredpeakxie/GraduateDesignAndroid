package com.fred.moonker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
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
import com.fred.moonker.Model.ArticleDetail;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.adapter.ArticleDetailAdapter;
import com.fred.moonker.listener.ArticleClick;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MsgFragment extends Fragment {

    private ListView listView;
    private List<ArticleDetail> articleDetailList = new ArrayList<>();
    private ArticleDetailAdapter articleDetailAdapter;
    private View footer;
    private TextView tvFooter;
    private Context context = this.getActivity();
    private RequestQueue requestQueue;

    private Long index = 0L;
    private static final Long NUM = 20L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater);
        initData();
        initViewAfterInitData();
        return view;
    }

    private View initView(@NonNull LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_msg,null);
        listView = view.findViewById(R.id.list_msg);
        requestQueue = NetTools.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();
        //TODO footer
        return view;
    }

    private void initData(){
        addArticleDetailList(index,NUM);
    }

    private void initViewAfterInitData(){
        articleDetailAdapter = new ArticleDetailAdapter(getActivity(),R.layout.item_article_detail,articleDetailList);
        listView.setAdapter(articleDetailAdapter);
        listView.setOnItemClickListener(new ArticleClick(listView,getContext()));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    if(view.getLastVisiblePosition() == view.getCount()-1){
                        index = index + NUM;
                        addArticleDetailList(index,NUM);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    //触发加载文章详情
    private void addArticleDetailList(Long start,Long num){
        String url = MoonkerApplication.URL+MoonkerApplication.ARTICLE_PREFIX+"/ArticleDetail/"+start+"/"+num;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        CommonResult<List<ArticleDetail>> commonResult = JsonTools.toAdListCommonResult(response);
                        if(commonResult.getCode().equals(RetCode.OK)){
                            articleDetailList.addAll(commonResult.getData());
                            articleDetailAdapter.notifyDataSetChanged();
                            if(start==0){
                                listView.smoothScrollToPosition(0);
                            }
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

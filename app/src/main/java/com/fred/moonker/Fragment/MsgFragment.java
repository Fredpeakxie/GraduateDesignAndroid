package com.fred.moonker.Fragment;

import android.app.Application;
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
import androidx.appcompat.widget.SearchView;
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
    private SearchView searchView;
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
        setListener();
        initData();
        initViewAfterInitData();
        return view;
    }


    private void clearData() {
        index = 0L;
        articleDetailList.clear();
    }

    private View initView(@NonNull LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_msg,null);
        listView = view.findViewById(R.id.list_msg);
        requestQueue = NetTools.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();
        searchView = view.findViewById(R.id.msg_f_searchView);
        searchView.setSubmitButtonEnabled(true);
        //TODO footer
        return view;
    }

    private void setListener() {
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        clearData();
                        sendSearchReq(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
//                        sendSuggestReq(newText);
                        return false;
                    }
                }
        );

        searchView.setOnCloseListener(
                new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        clearData();
                        addArticleDetailList(index,NUM);
                        return false;
                    }
                }
        );

        searchView.setOnSuggestionListener(
                new SearchView.OnSuggestionListener() {
                    @Override
                    public boolean onSuggestionSelect(int position) {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int position) {
                        return false;
                    }
                }
        );
    }

    private void sendSearchReq(String queryText) {
        String url = MoonkerApplication.URL+ MoonkerApplication.ARTICLE_PREFIX +"/search/"+index+"/"+NUM+"/"+queryText;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        CommonResult<List<ArticleDetail>> commonResult = JsonTools.toAdListCommonResult(response);
                        if(commonResult.getCode().equals(RetCode.OK)){
                            articleDetailList.addAll(commonResult.getData());
                            articleDetailAdapter.notifyDataSetChanged();
                            index = Integer.toUnsignedLong(articleDetailList.size());
                            if(index==0){
                                listView.smoothScrollToPosition(0);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"????????????"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
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
                        String query = searchView.getQuery().toString();
                        Log.d(TAG, "onScrollStateChanged: "+query);
                        if(query != null && !query.equals("")){
                            sendSearchReq(query);
                        }else {
                            addArticleDetailList(index,NUM);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    //????????????????????????
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
                            index = Integer.toUnsignedLong(articleDetailList.size());
                            if(start==0){
                                listView.smoothScrollToPosition(0);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"????????????"+url, Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}

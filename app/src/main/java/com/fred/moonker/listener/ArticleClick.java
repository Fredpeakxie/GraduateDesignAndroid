package com.fred.moonker.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fred.moonker.Activity.ArticleDetailActivity;
import com.fred.moonker.Model.ArticleDetail;

public class ArticleClick implements AdapterView.OnItemClickListener {

    private ListView listView;
    private Context context;

    public ArticleClick(ListView listView, Context context) {
        this.listView = listView;
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取被点击的对象的数据模型
        ArticleDetail articleDetail = (ArticleDetail)listView.getAdapter().getItem(position);

        Intent intent = new Intent(context, ArticleDetailActivity.class);

        intent.putExtra("articleId",articleDetail.getArticleID());
        intent.putExtra("title",articleDetail.getTitle());
        intent.putExtra("readNum",articleDetail.getReadNum());
        intent.putExtra("likeNum",articleDetail.getLikeNum());
        intent.putExtra("markNum",articleDetail.getMarkNum());
        intent.putExtra("authorID",articleDetail.getAuthorID());
        intent.putExtra("nickname",articleDetail.getNickname());
        context.startActivity(intent);
    }
}

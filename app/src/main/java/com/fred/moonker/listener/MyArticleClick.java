package com.fred.moonker.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fred.moonker.Activity.ArticleDetailActivity;
import com.fred.moonker.Model.ArticleDetail;
import com.fred.moonker.richeditor.ui.RichTextEditActivity;

public class MyArticleClick implements AdapterView.OnItemClickListener {

    private ListView listView;
    private Context context;

    public MyArticleClick(ListView listView, Context context) {
        this.listView = listView;
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取被点击的对象的数据模型
        ArticleDetail articleDetail = (ArticleDetail)listView.getAdapter().getItem(position);

        Intent intent = new Intent(context, RichTextEditActivity.class);

        intent.putExtra("articleId",articleDetail.getArticleId());
        intent.putExtra("title",articleDetail.getTitle());
        intent.putExtra("readNum",articleDetail.getReadNum());
        intent.putExtra("likeNum",articleDetail.getLikeNum());
        intent.putExtra("markNum",articleDetail.getMarkNum());
        intent.putExtra("authorID",articleDetail.getAuthorId());
        intent.putExtra("authorName",articleDetail.getNickname());
        context.startActivity(intent);
    }
}

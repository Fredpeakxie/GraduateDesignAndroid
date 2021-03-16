package com.fred.moonker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fred.moonker.Model.ArticleDetail;
import com.fred.moonker.R;

import java.util.List;

public class ArticleDetailAdapter extends ArrayAdapter<ArticleDetail> {
    private int resourceId;

    static class ViewHolder{
        private TextView title;
        private TextView username;
        private TextView articleSimple;
        private TextView readNum;
        private TextView likeNum;
        private TextView markNum;
    }

    public ArticleDetailAdapter(Context context, int textViewResourceId, List<ArticleDetail> articleDetails){
        super(context,textViewResourceId, articleDetails);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ArticleDetail ad = getItem(position);
        View view;
        ViewHolder viewHolder;
        //通常convertView中会已经有这些资源注册了
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.ad_title);
            viewHolder.username = view.findViewById(R.id.ad_author_name);
            viewHolder.articleSimple = view.findViewById(R.id.msg_f_article_simple);
            viewHolder.readNum = view.findViewById(R.id.ad_readNum);
            viewHolder.likeNum = view.findViewById(R.id.ad_likeNum);
            viewHolder.markNum = view.findViewById(R.id.ad_markNum);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title.setText(ad.getTitle());
        viewHolder.username.setText(ad.getNickname());
        //从服务器加载文本 封装至articleSimple内部 那么具体加载多少 几行？4行
        viewHolder.articleSimple.setText(ad.getArticleContent());
        viewHolder.readNum.setText(Long.toString(ad.getReadNum()));
        viewHolder.likeNum.setText(Long.toString(ad.getLikeNum()));
        viewHolder.markNum.setText(Long.toString(ad.getMarkNum()));
        return view;
    }
}
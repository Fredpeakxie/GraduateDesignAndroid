package com.fred.moonker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.fred.moonker.Model.Comment;
import com.fred.moonker.R;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private int resourceId;
    static class ViewHolder{
        private TextView userNickname;
        private TextView comment;
    }

    public CommentAdapter(Context context, int textViewResourceId, List<Comment> articleDetails){
        super(context,textViewResourceId, articleDetails);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Comment comment = getItem(position);
        View view;
        ViewHolder viewHolder;
        //通常convertView中会已经有这些资源注册了
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.userNickname = view.findViewById(R.id.item_user_nickname);
            viewHolder.comment = view.findViewById(R.id.item_comment);

            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String userNickName = comment.getUserNickName();
        if(userNickName!=null && !userNickName.equals("")){
            viewHolder.userNickname.setText(userNickName);
        }else {
            viewHolder.userNickname.setText(R.string.anonymousUser);
        }
        viewHolder.comment.setText(comment.getComment());
        return view;
    }
}

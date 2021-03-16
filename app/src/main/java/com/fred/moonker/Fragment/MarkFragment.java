package com.fred.moonker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fred.moonker.R;

public class MarkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }
    private View initView(@NonNull LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_mark,null);
//        listView = view.findViewById(R.id.list_mark);
        return view;
    }
}

package com.fred.moonker.tools;

import com.alibaba.fastjson.JSONArray;
import com.fred.moonker.Model.ArticleDetail;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonTools {

    private static Gson gson = new Gson();

    public static JSONObject toJsonObject(Object o){
        try {
            return new JSONObject(gson.toJson(o));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> CommonResult<T>  toCommonResult(JSONObject jsonObject, Class<T> classOfT){
        CommonResult toCommonResult = gson.fromJson(jsonObject.toString(), CommonResult.class);
        T t = gson.fromJson(toCommonResult.getData().toString(), classOfT);
        return new CommonResult<T>().addData(t)
                .addCode(toCommonResult.getCode())
                .addMessage(toCommonResult.getMessage());
    }

    public static CommonResult<List<ArticleDetail>> toAdListCommonResult(JSONObject jsonObject){
        CommonResult commonResult = gson.fromJson(jsonObject.toString(), CommonResult.class);
        System.out.println(commonResult.getData().toString());
        String listAd = JSONArray.toJSONString(commonResult.getData());
        List<ArticleDetail> articleDetailList = com.alibaba.fastjson.JSONObject.parseArray(listAd, ArticleDetail.class);
        return new CommonResult<List<ArticleDetail>>().addData(articleDetailList)
                .addCode(commonResult.getCode())
                .addMessage(commonResult.getMessage());
    }

    public static CommonResult<List<Long>> toLongListCommonResult(JSONObject jsonObject){
        CommonResult commonResult = gson.fromJson(jsonObject.toString(), CommonResult.class);
        System.out.println(commonResult.getData().toString());
        String listAd = JSONArray.toJSONString(commonResult.getData());
        List<Long> LongList = com.alibaba.fastjson.JSONObject.parseArray(listAd, Long.class);
        return new CommonResult<List<Long>>().addData(LongList)
                .addCode(commonResult.getCode())
                .addMessage(commonResult.getMessage());
    }

}

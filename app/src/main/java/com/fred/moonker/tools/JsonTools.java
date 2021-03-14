package com.fred.moonker.tools;

import com.fred.moonker.Model.CommonResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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
        return new CommonResult<T>().addData(gson.fromJson(toCommonResult.getData().toString(),classOfT))
                .addCode(toCommonResult.getCode())
                .addMessage(toCommonResult.getMessage());
    }
}

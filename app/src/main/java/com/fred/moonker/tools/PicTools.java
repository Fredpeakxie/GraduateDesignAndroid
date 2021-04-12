package com.fred.moonker.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fred.moonker.MoonkerApplication;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PicTools {

    public static List<String> convertUriListToStringList(List<Uri> uriList, Context context){
        List<String> pics = new ArrayList<>();
        for (int i = 0; i < uriList.size(); i++) {
            Uri uri = uriList.get(i);
            //使用 工具类 将uri转换成为字符串
            String pic = UriTransToBase64String(context, uri);
            pics.add(pic);
        }
        return pics;
    }

    public static String UriTransToBase64String(Context context,Uri uri){
        ImageLoader imageLoader = NetTools.getInstance(context).getImageLoader();
        String imgToBase64 = "";
        InputStream inputStream = null;
        try {
            final Bitmap[] bitmap = new Bitmap[1];
            //网络图片 存在在原服务器中的图片
            if(uri.getPath().startsWith("/moonker")){
                imageLoader.get("http:" + uri.getSchemeSpecificPart(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        bitmap[0] = response.getBitmap();
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }else {
                //本地图片
                inputStream = context.getContentResolver().openInputStream(uri);
                bitmap[0] = BitmapFactory.decodeStream(inputStream);
            }
            imgToBase64 = imgToBase64(bitmap[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgToBase64;
    }

    public static String imgToBase64(Bitmap bitmap) {
        if (bitmap != null){
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                //进行格式压缩
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
                byte[] imgBytes = out.toByteArray();
                return Base64.encodeToString(imgBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "bitmap null";
    }
}

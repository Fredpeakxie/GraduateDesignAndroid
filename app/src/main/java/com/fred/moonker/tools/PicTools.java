package com.fred.moonker.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fred.moonker.MoonkerApplication;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

public class PicTools {


    public static List<String> convertUriListToStringList(List<Uri> uriList, Context context) {
//        CacheTool.clearAllCache(context);
        List<String> pics = new ArrayList<>();
        for (int i = 0; i < uriList.size(); i++) {
            Uri uri = uriList.get(i);
            //使用 工具类 将uri转换成为字符串
            String pic = "bitmap null";
            while (pic.equals("bitmap null"))
                pic = UriTransToBase64String(context, uri);
            pics.add(pic);
        }
        return pics;
    }

    public static String UriTransToBase64String(Context context, Uri uri) {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(configuration);
        ImageLoader imageLoader = NetTools.getInstance(context).getImageLoader();
        String imgToBase64 = "";
        InputStream inputStream = null;
        try {
            Bitmap bitmap;
            //网络图片 存在在原服务器中的图片
            if (uri.getPath().startsWith("/moonker")) {
                System.out.println("a" + uri.getSchemeSpecificPart());
                String urlString  = "http:" + uri.getSchemeSpecificPart();

                FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(() ->{
                    URL url = new URL(urlString);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    //这里就简单的设置了网络的读取和连接时间上线，如果时间到了还没成功，那就不再尝试
                    httpURLConnection.setReadTimeout(8000);
                    httpURLConnection.setConnectTimeout(8000);
                    InputStream is = httpURLConnection.getInputStream();
                    //这里直接就用bitmap取出了这个流里面的图片，哈哈，其实整篇文章不就主要是这一句嘛
                    return BitmapFactory.decodeStream(is);
                });
                futureTask.run();
                bitmap = futureTask.get();
            } else {
                //本地图片
                inputStream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imgToBase64 = imgToBase64(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
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
        if (bitmap != null) {
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                //进行格式压缩
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
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

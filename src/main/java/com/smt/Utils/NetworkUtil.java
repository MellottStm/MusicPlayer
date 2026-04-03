package com.smt.Utils;

import okhttp3.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class NetworkUtil {

    private static String TAG = "NetworkUtil";

    public final static Logger logger = Logger.getLogger(TAG);

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)    // 建立连接的超时时间（TCP + TLS）
            .writeTimeout(15, TimeUnit.SECONDS)       // 写请求体
            .readTimeout(20, TimeUnit.SECONDS)        // 读取响应数据的超时时间
            .callTimeout(30, TimeUnit.SECONDS)        // 整个调用
            .build();

    public static void okHttpGet(String msg,String url,HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url + msg).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.callBackFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                logger.info("后端返回的信息：" + result);
                httpCallBack.callBackSuccess(result);
            }
        });

    }

    public interface HttpCallBack{

        void callBackFail(String error);

        void callBackSuccess(String response);
    }

}

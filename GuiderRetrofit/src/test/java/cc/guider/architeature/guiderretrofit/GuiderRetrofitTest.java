package cc.guider.architeature.guiderretrofit;


import org.junit.Test;

import cc.guider.architeature.guiderretrofit.http.Field;
import cc.guider.architeature.guiderretrofit.http.GET;
import cc.guider.architeature.guiderretrofit.http.POST;
import cc.guider.architeature.guiderretrofit.http.Query;
import okhttp3.Call;
import okhttp3.Response;

public class GuiderRetrofitTest {

    private final static String IP = "144.34.161.97";
    private final static String KEY = "aa205eeb45aa76c6afe3c52151b52160";
    private final static String BASE_URL = "http://apis.juhe.cn/";

    @Test
    public void testGuiderRetrofit() {
        GuiderRetrofit retrofit = new GuiderRetrofit.Builder().baseUrl(BASE_URL).build();
        GuiderRetrofitApi host = retrofit.create(GuiderRetrofitApi.class);
        // Retrofit GET同步请求
        {
            Call call = host.get(IP, KEY);
            Response response = null;
            try {
                response = call.execute();
                if (response != null && response.body() != null) {
                    System.out.println("Retrofit GET同步请求 >>> " + response.body().string());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        // Retrofit POST同步请求
        {
            Call call = host.post(IP, KEY);
            Response response = null;
            try {
                response = call.execute();
                if (response != null && response.body() != null) {
                    System.out.println("Retrofit POST同步请求 >>> " + response.body().string());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    interface GuiderRetrofitApi {
        @GET("/ip/ipNew")
        Call get(@Query("ip") String ip, @Query("key") String key);

        @POST("/ip/ipNew")
        Call post(@Field("ip") String ip, @Field("key") String key);

    }
}

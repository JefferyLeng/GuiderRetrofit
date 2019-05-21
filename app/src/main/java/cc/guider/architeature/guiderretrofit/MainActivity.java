package cc.guider.architeature.guiderretrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import cc.guider.architeature.guiderretrofit.http.Field;
import cc.guider.architeature.guiderretrofit.http.GET;
import cc.guider.architeature.guiderretrofit.http.POST;
import cc.guider.architeature.guiderretrofit.http.Query;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author jefferyleng
 */
public class MainActivity extends AppCompatActivity {

    private final static String IP = "144.34.161.97";
    private final static String KEY = "aa205eeb45aa76c6afe3c52151b52160";
    private final static String BASE_URL = "http://apis.juhe.cn/";

    private TextView mTvGetContent;
    private TextView mTvPostContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvGetContent = findViewById(R.id.tv_get_content);
        mTvPostContent = findViewById(R.id.tv_post_content);
        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRequest();
            }
        });
    }

    private void doRequest() {
        GuiderRetrofit retrofit = new GuiderRetrofit.Builder().baseUrl(BASE_URL).build();
        GuiderRetrofitApi host = retrofit.create(GuiderRetrofitApi.class);
        // Retrofit GET同步请求
        {
            Call call = host.get(IP, KEY);
            try {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.body() != null) {
                            String responseStr = response.body().string();
                            Log.d("GuiderRetrofit", "Retrofit POST同步请求 >>> " + responseStr);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvGetContent.setText("GET RESPONSE:" + responseStr);
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Retrofit POST同步请求
        {
            Call call = host.post(IP, KEY);
            try {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null && response.body() != null) {
                            String responseStr = response.body().string();
                            Log.d("GuiderRetrofit", "Retrofit POST同步请求 >>> " + responseStr);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvPostContent.setText("POST RESPONSE:" + responseStr);
                                }
                            });
                        }
                    }
                });

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

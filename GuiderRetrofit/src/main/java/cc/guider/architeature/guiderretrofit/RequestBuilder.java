package cc.guider.architeature.guiderretrofit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 请求的构建者
 * @author JefferyLeng
 * @date 2019-05-21
 */
class RequestBuilder {
    /**
     * 请求方式 （"GET" "POST"）
     */
    private final String method;

    /**
     * base url ： www.guider.com
     */
    private final HttpUrl baseUrl;

    /**
     * 请求的相对路径 : /test/testPath
     */
    private String relativeUrl;

    /**
     * okhttp的url构建器
     */
    private HttpUrl.Builder urlBuilder;

    /**
     * okhttp 表单构建器
     */
    private FormBody.Builder formBuilder;

    /**
     * okhttp的请求构建器
     */
    private final Request.Builder requestBuilder;


    RequestBuilder(String method,HttpUrl baseUrl,String relativeUrl,boolean hasBody) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        requestBuilder = new Request.Builder();
        if (hasBody) {
            formBuilder = new FormBody.Builder();
        }
    }

    /**
     * 拼接 @Query的注解
     * @param name
     * @param value
     */
    void addQueryParam(String name,String value) {
        if (relativeUrl != null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            if (urlBuilder == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
            //每次请求都实例化一次，需要重置 重新赋值
            relativeUrl = null;
        }
        urlBuilder.addQueryParameter(name,value);
    }

    /**
     * 拼接field的参数
     * @param name
     * @param value
     */
    void addFormField(String name,String value) {
        formBuilder.add(name,value);
    }

    Request build() {
        /*
        定义在此处的原因：1，每次请求都是不同的对象 2.gc容易回收
         */
        HttpUrl url;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
        }

        RequestBody requestBody = null;
        if (formBuilder != null) {
            requestBody = formBuilder.build();
        }

        return requestBuilder
                .url(url)
                .method(method,requestBody)
                .build();
    }


}

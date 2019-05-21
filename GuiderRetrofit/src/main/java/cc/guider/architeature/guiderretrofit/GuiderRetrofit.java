package cc.guider.architeature.guiderretrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * retrofit核心类
 * @author jefferyleng
 */
public class GuiderRetrofit {

    /**
     * ServiceMethod的内存缓存
     */
    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    private HttpUrl httpBaseUrl;
    private Call.Factory callFactory;

    private GuiderRetrofit(Builder builder) {
        this.httpBaseUrl = builder.httpBaseUrl;
        this.callFactory = builder.callFactory;
    }

    Call.Factory callFactory() {
        return callFactory;
    }

    HttpUrl baseUrl() {
        return httpBaseUrl;
    }

    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return new OkHttpCall(serviceMethod,args);
            }
        });
    }

    ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }
        //可能存在多个请求同时执行的线程并发问题
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }


    public static final class Builder {
        private HttpUrl httpBaseUrl;
        private Call.Factory callFactory;
        Builder() {

        }

        public Builder baseUrl(String baseUrl) {
            if (baseUrl.isEmpty()) {
                throw new IllegalArgumentException("baseUrl must be not null !");
            }
            this.httpBaseUrl = HttpUrl.parse(baseUrl);
            return this;

        }

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public GuiderRetrofit build() {
            if (httpBaseUrl == null) {
                throw new IllegalStateException("baseUrl must be not null!");
            }
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            return new GuiderRetrofit(this);

        }
    }

}

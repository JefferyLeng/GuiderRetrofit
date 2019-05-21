package cc.guider.architeature.guiderretrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import cc.guider.architeature.guiderretrofit.http.Field;
import cc.guider.architeature.guiderretrofit.http.GET;
import cc.guider.architeature.guiderretrofit.http.POST;
import cc.guider.architeature.guiderretrofit.http.Query;
import okhttp3.Call;
import okhttp3.HttpUrl;

/**
 * 保存所有请求方法的内容：方法名、方法注解、参数注解、参数值
 *
 * @author JefferyLeng
 * @date 2019-05-21
 */
class ServiceMethod {

    /**
     * okhttpclient的 实现接口
     */
    private final Call.Factory callFactory;
    /**
     * okhttp url处理
     */
    private final HttpUrl baseUrl;
    /**
     * 请求方式 GET POST
     */
    private final String requestMethod;
    /**
     * 请求的相对路径
     */
    private final String relativeUrl;
    /**
     * 参数处理器数组
     */
    private ParameterHandler[] parameterHandlers;
    /**
     * 是否存在请求体（post）
     */
    private final boolean hasBody;

    private ServiceMethod(Builder builder) {
        callFactory = builder.retrofit.callFactory();
        baseUrl = builder.retrofit.baseUrl();
        requestMethod = builder.httpMethod;
        this.hasBody = builder.hasBody;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.paramterHandlers;
    }

    Call toCall(Object... args) {
        RequestBuilder requestBuilder = new RequestBuilder(requestMethod, baseUrl, relativeUrl, hasBody);
        // 参考源码
        ParameterHandler[] handlers = parameterHandlers;
        int argumentCount = args != null ? args.length : 0;
        // Proxy方法的参数个数是否等于参数的数组（手动添加）的长度，此处理解为校验
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }
        for (int i = 0; i < argumentCount; i++) {
            handlers[i].apply(requestBuilder,args[i].toString());
        }
        return callFactory.newCall(requestBuilder.build());
    }


    static final class Builder {
        GuiderRetrofit retrofit;
        Method method;
        /**
         * 方法注解
         */
        Annotation[] methodAnnotations;
        /**
         * 方法参数注解 可能有多个注解 so二维数组定义
         */
        Annotation[][] parameterAnnotationsArray;
        /**
         * 请求方法 GET POST
         */
        String httpMethod;
        /**
         * 请求访问相对路径
         */
        String relativeUrl;
        /**
         * 参数处理器集合
         */
        ParameterHandler[] paramterHandlers;
        /**
         * 是否有请求体 post
         */
        boolean hasBody;

        Builder(GuiderRetrofit retrofit,Method method) {
            this.retrofit = retrofit;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        ServiceMethod build() {
            for (Annotation methodAnnotation : methodAnnotations) {
                parseMethodAnnotation(methodAnnotation);
            }

            int parameterCount = parameterAnnotationsArray.length;
            paramterHandlers = new ParameterHandler[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
                //参数必须有retrofit注解 不允许空注解参数
                if (parameterAnnotations == null) {
                    throw new IllegalArgumentException("No Retrofit annotation found." + " (parameter #" + (i + 1) + ")");
                }
                paramterHandlers[i] = parseParameter(i,parameterAnnotations);

            }

            return new ServiceMethod(this);
        }

        private ParameterHandler parseParameter(int i, Annotation[] parameterAnnotations) {
            ParameterHandler result = null;
            for (Annotation parameterAnnotation : parameterAnnotations) {
                ParameterHandler parameterHandler = parseParameterAnnotation(parameterAnnotation);
                if (parameterHandler == null) {
                    continue;
                }
                result = parameterHandler;
            }
            if (result == null) {
                throw new IllegalArgumentException("No Retrofit annotation found." + " (parameter #" + (i + 1) + ")");
            }
            return result;
        }

        /**
         * 解析参数注解
         * @param parameterAnnotation
         * @return
         */
        private ParameterHandler parseParameterAnnotation(Annotation parameterAnnotation) {
            if (parameterAnnotation instanceof Query) {
                Query query = (Query) parameterAnnotation;
                String name = query.value();
                // 注意：传过去的参数是注解的值，并非参数值。参数值由Proxy方法传入
                return new ParameterHandler.Query(name);
            } else if (parameterAnnotation instanceof Field) {
                Field field = (Field) parameterAnnotation;
                String name = field.value();
                return new ParameterHandler.Field(name);
            }
            return null;
        }

        /**
         * 解析方法注解
         * @param methodAnnotation
         */
        private void parseMethodAnnotation(Annotation methodAnnotation) {
            if (methodAnnotation instanceof GET) {
                parseHttpMethodAndPath("GET",((GET) methodAnnotation).value(), false);
            } else if (methodAnnotation instanceof POST) {
                parseHttpMethodAndPath("POST",((POST) methodAnnotation).value(),true);
            }
        }

        private void parseHttpMethodAndPath(String httpMethod, String annotationValue, boolean hasBodyFlag) {
            this.httpMethod = httpMethod;
            this.relativeUrl = annotationValue;
            this.hasBody = hasBodyFlag;
        }

    }
}

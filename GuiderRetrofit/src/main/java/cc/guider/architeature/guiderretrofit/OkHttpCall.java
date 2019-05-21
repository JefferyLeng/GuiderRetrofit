package cc.guider.architeature.guiderretrofit;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okio.Timeout;

/**
 * 封装一个okhttp实现的call
 * @author JefferyLeng
 * @date 2019-05-21
 */
class OkHttpCall implements Call {

    private ServiceMethod serviceMethod;
    private Call realCall;
    private Object[] args;

    public OkHttpCall(ServiceMethod serviceMethod,Object[] args) {
        this.serviceMethod = serviceMethod;
        this.args = args;
        this.realCall = serviceMethod.toCall(args);
    }

    @Override
    public Request request() {
        return realCall.request();
    }

    @Override
    public Response execute() throws IOException {
        return realCall.execute();
    }

    @Override
    public void enqueue(Callback responseCallback) {
        realCall.enqueue(responseCallback);
    }

    @Override
    public void cancel() {
        realCall.cancel();
    }

    @Override
    public boolean isExecuted() {
        return realCall.isExecuted();
    }

    @Override
    public boolean isCanceled() {
        return realCall.isCanceled();
    }

    @Override
    public Timeout timeout() {
        return realCall.timeout();
    }

    @Override
    public Call clone() {
        return new OkHttpCall(serviceMethod,args);
    }
}

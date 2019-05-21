package cc.guider.architeature.guiderretrofit;

/**
 * 参数处理器 将参数信息 添加到builder中
 * @author JefferyLeng
 * @date 2019-05-21
 */
abstract class ParameterHandler {

    abstract void apply(RequestBuilder builder,String paramValue);

    static final class Query extends ParameterHandler {

        private final String paramName;

        Query(String paramName) {
            this.paramName = paramName;
        }

        @Override
        void apply(RequestBuilder builder, String paramValue) {
            builder.addQueryParam(paramName,paramValue);
        }
    }

    static final class Field extends ParameterHandler {

        private final String paramName;

        Field(String paramName) {
            this.paramName = paramName;
        }

        @Override
        void apply(RequestBuilder builder, String paramValue) {
            builder.addFormField(paramName,paramValue);
        }
    }
}

package framework.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import framework.sdk.Framework;
import framework.sdk.HttpConfig;

public class TransformFilter implements Filter {
        public static final String MODULE_NAME = "TransformFilter";

        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
                /*
                 * 设置header
                 */
                HttpServletResponse hsr = (HttpServletResponse) response;
                if (null != HttpConfig.HTTP_HEADER) {
                        for (int i = 0; i < HttpConfig.HTTP_HEADER.length; i++) {
                                hsr.setHeader(HttpConfig.HTTP_HEADER[i].split(HttpConfig.HTTP_HEADER_SPLIT)[0], HttpConfig.HTTP_HEADER[i].split(HttpConfig.HTTP_HEADER_SPLIT)[1]);
                        }
                }
                if (HttpConfig.EVERY_ORIGIN) {
                        String originHeader = ((javax.servlet.http.HttpServletRequest) request).getHeader("Origin");
                        hsr.setHeader("Access-Control-Allow-Origin", originHeader);
                }
                try {
                        request.setCharacterEncoding(HttpConfig.REQUEST_CHARACTER_ENCODING);
                        response.setCharacterEncoding(HttpConfig.RESPONSE_CHARACTER_ENCODING);
                        response.setContentType(HttpConfig.RESPONSE_CONTENT_TYPE_ENCODING);
                        chain.doFilter(request, response);
                } catch (Exception e) {
                        StackTraceElement ste = e.getStackTrace()[0];
                        String fileName = ste.getFileName();
                        String lineNumber = String.valueOf(ste.getLineNumber());
                        String methodName = ste.getMethodName();
                        Framework.LOG.error(TransformFilter.MODULE_NAME, "[" + fileName + ":" + lineNumber + "] " + methodName);
                }
        }

        @Override
        public void destroy() {
        }
}
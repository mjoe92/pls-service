package de.vw.paso.pll.dev.contextvisualizer.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

  public static class MethodChain {
    List<Method> methods = new ArrayList<>();

    public void addMethod(Method m) {
      methods.add(m);
    }

    public List<Method> getMethods() {
      return methods;
    }

    public Object getValue(Object rootObject) {
      Object obj = rootObject;
      for (Method m : methods) {
        if (obj == null) {
          return null;
        }
        obj = getValue(obj, m);
      }
      return obj;
    }

    private Object getValue(Object o, Method method) {
      try {
        return method.invoke(o);
      } catch (Exception e) {}
      return null;
    }
  }

  public static MethodChain findChain(String getter, Object root) {
    if (root == null){
      return null;
    }

    Method foundMethod = getMethod(getter, root);
    if (foundMethod != null) {
      MethodChain result = new MethodChain();
      result.addMethod(foundMethod);
      return result;
    }

    for (Method method : root.getClass().getMethods()) {
      if (checkReference(method)) {
        Object invoke = invoke(method, root);
        if (invoke != null) {
          MethodChain subChain = findChain(getter, invoke);
          if (subChain != null){
            subChain.getMethods().add(0, method);
            return subChain;
          }
        }
      }
    }
    return null;
  }

  private static boolean checkReference(Method method) {
    return !method.getReturnType().isEnum()
      && !method.getReturnType().isAssignableFrom(Number.class)
      && !method.getReturnType().isPrimitive()
      && !method.getReturnType().toString().contains("java");
  }

  private static Method getMethod(String methodName, Object obj) {
    if (obj != null) {
      try {
        return obj.getClass().getMethod(methodName);
      } catch (NoSuchMethodException e) {
        return null;
      }
    }
    return null;
  }

  private static Object invoke(Method m, Object o) {
    if (o == null) {
      return null;
    }
    try {
      return m.invoke(o);
    } catch (Exception e) {
      return null;
    }
  }
}

package de.vw.paso.util;

import java.util.EnumMap;
import java.util.Map;

import de.vw.paso.logic.user.RequestDataKey;

public class RequestData {

  private static final ThreadLocal<Map<RequestDataKey, String>> userThreadLocal = new ThreadLocal<>();

  public static String getRequestData(RequestDataKey key) {
    Map<RequestDataKey, String> authDataMap = userThreadLocal.get();
    if (authDataMap == null) {
      return null;
    }
    return authDataMap.get(key);
  }

  public static void setRequestData(RequestDataKey key, String value) {
    Map<RequestDataKey, String> map = userThreadLocal.get();
    if (map == null) {
      map = new EnumMap<>(RequestDataKey.class);
      userThreadLocal.set(map);
    }
    map.put(key, value);
  }

  public static void clearRequestData() {
    userThreadLocal.remove();
  }
}

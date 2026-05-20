package de.vw.paso.client.stueckliste.compare.partlist.combine.selection;

import java.lang.reflect.Method;

public record MethodWrapper(Method method) {

  @Override
  public String toString() {
    String name = method.getName();

    if (name.startsWith("get")) {
      return name.replaceFirst("get", "");
    }
    if (name.startsWith("is")) {
      return name.replaceFirst("is", "");
    }
    if (name.startsWith("has")) {
      return name.replaceFirst("has", "");
    }

    return name;
  }

}

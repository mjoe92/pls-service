package de.vw.paso.client.stueckliste.compare.partlist;

public interface Setter<T, S> {
  void set(T object, S value);

  static <T, S> Setter<T, S> NO_OP() {
    return (ojbect, value) -> {
    };
  }
}

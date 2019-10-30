package io.github.henryssondaniel.teacup.protocol.server;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

interface TimeoutSupplier<T> extends Supplier<List<T>> {
  void addRequest(T request);

  void stop();

  void whenStopped(Consumer<? super List<T>> consumer);
}

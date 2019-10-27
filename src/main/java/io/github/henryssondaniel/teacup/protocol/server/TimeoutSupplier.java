package io.github.henryssondaniel.teacup.protocol.server;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Timeout supplier.
 *
 * @param <T> the request
 * @since 1.0
 */
public interface TimeoutSupplier<T extends Request> extends Supplier<List<T>> {
  /**
   * Add a request.
   *
   * @param request the request
   */
  void addRequest(T request);

  /** Stop. */
  void stop();

  /**
   * When stopped.
   *
   * @param consumer the consumer
   */
  void whenStopped(Consumer<? super List<T>> consumer);
}

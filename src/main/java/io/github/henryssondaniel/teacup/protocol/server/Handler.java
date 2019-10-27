package io.github.henryssondaniel.teacup.protocol.server;

import java.util.List;

/**
 * Handler.
 *
 * @param <T> the request
 * @since 1.0
 */
public interface Handler<T extends Request> {
  /**
   * Adds a new timeout supplier.
   *
   * @param timeoutSupplier the timeout supplier
   */
  void addTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier);

  /**
   * Get timeout suppliers.
   *
   * @return the timeout suppliers
   */
  List<TimeoutSupplier<T>> getTimeoutSuppliers();

  /**
   * Remove timeout supplier.
   *
   * @param timeoutSupplier the timeout supplier
   */
  void removeTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier);
}

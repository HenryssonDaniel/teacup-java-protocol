package io.github.henryssondaniel.teacup.protocol;

import java.util.List;
import java.util.function.Supplier;

/**
 * Server interface.
 *
 * @param <T> the context
 * @param <U> the request
 * @since 1.0
 */
public interface Server<T, U> {
  /**
   * Removes the supplier from the context.
   *
   * @param supplier the supplier
   * @since 1.0
   */
  void removeSupplier(Supplier<List<U>> supplier);

  /**
   * Sets the context to the server and returns a supplier.
   *
   * @param context the context
   * @return the supplier
   * @since 1.0
   */
  Supplier<List<U>> setContext(T context);

  /**
   * Set up.
   *
   * @since 1.0
   */
  void setUp();

  /**
   * Tear down.
   *
   * @since 1.0
   */
  void tearDown();
}

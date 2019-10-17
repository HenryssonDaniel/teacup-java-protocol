package io.github.henryssondaniel.teacup.protocol;

/**
 * A node in a request/response message.
 *
 * @param <T> the actual type
 * @since 1.0
 */
@FunctionalInterface
public interface Node<T> {
  /**
   * Verifies the actual.
   *
   * @param actual the actual
   * @since 1.0
   */
  void verify(T actual);
}

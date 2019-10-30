package io.github.henryssondaniel.teacup.protocol.server;

/**
 * Handler.
 *
 * @param <T> the request
 * @since 1.0
 */
@FunctionalInterface
public interface Handler<T> {
  /**
   * Adds a request to the handler.
   *
   * @param request the request
   */
  void addRequest(T request);
}

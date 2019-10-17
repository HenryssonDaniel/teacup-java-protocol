package io.github.henryssondaniel.teacup.protocol;

import io.github.henryssondaniel.teacup.core.assertion.GenericObjectAssert;

/**
 * Node builder.
 *
 * @param <T> the actual type
 * @param <U> the node type
 * @param <V> the node builder type
 * @since 1.0
 */
public interface NodeBuilder<T, U extends Node<T>, V extends NodeBuilder<T, U, V>>
    extends Builder<U> {
  /**
   * Sets the assertion.
   *
   * @param assertion the assertion
   * @return the node builder
   * @since 1.0
   */
  V setAssertion(GenericObjectAssert<T, ?> assertion);
}

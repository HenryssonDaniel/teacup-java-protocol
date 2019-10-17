package io.github.henryssondaniel.teacup.protocol;

import io.github.henryssondaniel.teacup.core.logging.Factory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of the {@link Builder}.
 *
 * @param <T> the interface type
 * @param <U> the implementation type
 * @since 1.0
 */
public abstract class DefaultBuilder<T, U> implements Builder<T> {
  private static final Logger LOGGER = Factory.getLogger(DefaultBuilder.class);
  private U implementation;

  /**
   * Constructor.
   *
   * @param implementation the implementation
   * @since 1.0
   */
  protected DefaultBuilder(U implementation) {
    this.implementation = implementation;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T build() {
    LOGGER.log(Level.FINE, "Building");

    var node = implementation;
    implementation = createImplementation();

    return (T) node;
  }

  /**
   * Create implementation.
   *
   * @return the implementation
   * @since 1.0
   */
  protected abstract U createImplementation();

  /**
   * Returns the implementation.
   *
   * @return the implementation
   * @since 1.0
   */
  protected U getImplementation() {
    return implementation;
  }
}

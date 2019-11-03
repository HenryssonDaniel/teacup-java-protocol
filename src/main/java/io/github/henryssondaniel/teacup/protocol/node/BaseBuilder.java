package io.github.henryssondaniel.teacup.protocol.node;

import io.github.henryssondaniel.teacup.core.assertion.GenericObjectAssert;
import io.github.henryssondaniel.teacup.core.logging.Factory;
import io.github.henryssondaniel.teacup.protocol.DefaultBuilder;
import io.github.henryssondaniel.teacup.protocol.Node;
import io.github.henryssondaniel.teacup.protocol.NodeBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation of the {@link NodeBuilder}.
 *
 * @param <T> the actual type
 * @param <U> the node type
 * @param <V> the node setter type
 * @param <X> the node builder type
 * @since 1.0
 */
public abstract class BaseBuilder<
        T, U extends Node<T>, V extends Node<T>, X extends NodeBuilder<T, U, X>>
    extends DefaultBuilder<U, V> implements NodeBuilder<T, U, X> {
  private static final Logger LOGGER = Factory.getLogger(BaseBuilder.class);

  /**
   * Constructor.
   *
   * @param setter the setter
   * @since 1.0
   */
  protected BaseBuilder(V setter) {
    super(setter);
  }

  @Override
  @SuppressWarnings("unchecked")
  public X setAssertion(GenericObjectAssert<T, ?> assertion) {
    LOGGER.log(Level.FINE, "Setting the assertion");
    doAssertion(assertion);
    return (X) this;
  }

  /**
   * Define what should happen when {@link #setAssertion(GenericObjectAssert)} is called.
   *
   * @param assertion the assertion
   * @since 1.0
   */
  protected abstract void doAssertion(GenericObjectAssert<T, ?> assertion);
}

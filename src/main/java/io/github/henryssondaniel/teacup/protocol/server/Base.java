package io.github.henryssondaniel.teacup.protocol.server;

import io.github.henryssondaniel.teacup.core.logging.Factory;
import io.github.henryssondaniel.teacup.protocol.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation of a server.
 *
 * @param <T> the context
 * @param <U> the protocol context
 * @param <V> the request
 * @since 1.0
 */
public abstract class Base<T, U, V> implements Server<T, V> {
  private static final Logger LOGGER = Factory.getLogger(Base.class);

  private final Object lock = new Object();
  private final Map<String, HandlerContext<U, V>> map = new HashMap<>(0);

  private boolean waiting = true;

  @Override
  public void removeSupplier(Supplier<List<V>> supplier) {
    LOGGER.log(Level.FINE, "Remove supplier");
    if (supplier instanceof TimeoutSupplier) ((TimeoutSupplier<V>) supplier).stop();
  }

  @Override
  public Supplier<List<V>> setContext(T context) {
    TimeoutSupplier<V> timeoutSupplier = new TimeoutSupplierImpl<>(new ReentrantLock());

    try {
      var protocolContext = addSupplier(context, timeoutSupplier);
      timeoutSupplier.whenStopped(consumer -> cleanup(context, protocolContext, timeoutSupplier));
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, "The server got interrupted", e);
      Thread.currentThread().interrupt();
    }

    return timeoutSupplier;
  }

  /**
   * Creates a new protocol context.
   *
   * @param context the context
   * @param handler the handler
   * @return the protocol context
   */
  protected abstract U createProtocolContext(T context, Handler<V> handler);

  /**
   * Returns a unique key for the context.
   *
   * @param context the context
   * @return the key
   */
  protected abstract String getKey(T context);

  /**
   * Compare the context with the protocol context.
   *
   * @param context the context
   * @param protocolContext the protocol context
   * @return whether equal or not
   */
  protected abstract boolean isEquals(T context, U protocolContext);

  /**
   * Cleanup on the server side.
   *
   * @param protocolContext the protocol context
   */
  protected abstract void serverCleanup(U protocolContext);

  private U addSupplier(T context, TimeoutSupplier<V> timeoutSupplier) throws InterruptedException {
    U protocolContext;

    var key = getKey(context);

    synchronized (lock) {
      if (map.containsKey(key)) protocolContext = tryAddSupplier(context, timeoutSupplier);
      else {
        SupplierHandler<V> supplierHandler = new SupplierHandlerImpl<>();
        supplierHandler.addTimeoutSupplier(timeoutSupplier);

        protocolContext = createProtocolContext(context, supplierHandler);
        map.put(key, new HandlerContextImpl<>(supplierHandler, protocolContext));
      }
    }

    return protocolContext;
  }

  private void cleanup(T context, U protocolContext, TimeoutSupplier<V> timeoutSupplier) {
    var key = getKey(context);

    var supplierHandler = map.get(key).getHandler();
    supplierHandler.removeTimeoutSupplier(timeoutSupplier);

    synchronized (lock) {
      if (supplierHandler.getTimeoutSuppliers().isEmpty()) {
        serverCleanup(protocolContext);
        map.remove(key);
        waiting = false;
        lock.notifyAll();
      }
    }
  }

  private U tryAddSupplier(T context, TimeoutSupplier<V> timeoutSupplier)
      throws InterruptedException {
    var handlerContext = map.get(getKey(context));

    var protocolContext = handlerContext.getProtocolContext();

    if (isEquals(context, protocolContext))
      handlerContext.getHandler().addTimeoutSupplier(timeoutSupplier);
    else
      synchronized (lock) {
        while (waiting) lock.wait(1L);

        waiting = true;
        protocolContext = addSupplier(context, timeoutSupplier);
      }

    return protocolContext;
  }

  private interface HandlerContext<T, U> {
    SupplierHandler<U> getHandler();

    T getProtocolContext();
  }

  private static final class HandlerContextImpl<T, U> implements HandlerContext<T, U> {
    private final SupplierHandler<U> handler;
    private final T protocolContext;

    private HandlerContextImpl(SupplierHandler<U> handler, T protocolContext) {
      this.handler = handler;
      this.protocolContext = protocolContext;
    }

    @Override
    public SupplierHandler<U> getHandler() {
      return handler;
    }

    @Override
    public T getProtocolContext() {
      return protocolContext;
    }
  }
}

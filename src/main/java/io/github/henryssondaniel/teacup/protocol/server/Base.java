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
public abstract class Base<T extends Context, U, V extends Request> implements Server<T, V> {
  private static final Logger LOGGER = Factory.getLogger(Base.class);

  private final Object lock = new Object();
  private final Map<String, U> map = new HashMap<>(0);

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
   * @param timeoutSupplier the timeout supplier
   * @return the protocol context
   */
  protected abstract U createProtocolContext(T context, TimeoutSupplier<V> timeoutSupplier);

  /**
   * Returns the handler.
   *
   * @param protocolContext the protocol context
   * @return the handler
   */
  protected abstract Handler<V> getHandler(U protocolContext);

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

    synchronized (lock) {
      if (map.containsKey(getKey(context)))
        protocolContext = tryAddSupplier(context, timeoutSupplier);
      else {
        protocolContext = createProtocolContext(context, timeoutSupplier);
        map.put(getKey(context), protocolContext);
      }
    }

    return protocolContext;
  }

  private void cleanup(T context, U protocolContext, TimeoutSupplier<V> timeoutSupplier) {
    var handler = getHandler(protocolContext);
    handler.removeTimeoutSupplier(timeoutSupplier);

    synchronized (lock) {
      if (handler.getTimeoutSuppliers().isEmpty()) {
        serverCleanup(protocolContext);
        map.remove(getKey(context));
        waiting = false;
        lock.notifyAll();
      }
    }
  }

  private U tryAddSupplier(T context, TimeoutSupplier<V> timeoutSupplier)
      throws InterruptedException {
    var protocolContext = map.get(getKey(context));

    if (isEquals(context, protocolContext))
      getHandler(protocolContext).addTimeoutSupplier(timeoutSupplier);
    else
      synchronized (lock) {
        while (waiting) lock.wait(1L);

        waiting = true;
        protocolContext = addSupplier(context, timeoutSupplier);
      }

    return protocolContext;
  }
}

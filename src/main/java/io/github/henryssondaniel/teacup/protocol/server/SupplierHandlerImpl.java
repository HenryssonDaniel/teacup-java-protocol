package io.github.henryssondaniel.teacup.protocol.server;

import io.github.henryssondaniel.teacup.core.logging.Factory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class SupplierHandlerImpl<T> implements SupplierHandler<T> {
  private static final Logger LOGGER = Factory.getLogger(SupplierHandlerImpl.class);
  private static final String MESSAGE = "{0}ing the timeout supplier{1}";

  private final List<TimeoutSupplier<T>> timeoutSuppliers = new LinkedList<>();

  @Override
  public void addRequest(T request) {
    LOGGER.log(Level.FINE, "Add request");
    timeoutSuppliers.forEach(timeoutSupplier -> timeoutSupplier.addRequest(request));
  }

  @Override
  public void addTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier) {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"Add", ""});
    timeoutSuppliers.add(timeoutSupplier);
  }

  @Override
  public List<TimeoutSupplier<T>> getTimeoutSuppliers() {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"Sett", "s"});
    return new ArrayList<>(timeoutSuppliers);
  }

  @Override
  public void removeTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier) {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"Remov", ""});
    timeoutSuppliers.remove(timeoutSupplier);
  }
}

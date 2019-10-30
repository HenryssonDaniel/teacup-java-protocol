package io.github.henryssondaniel.teacup.protocol.server;

import java.util.List;

interface SupplierHandler<T> extends Handler<T> {
  void addTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier);

  List<TimeoutSupplier<T>> getTimeoutSuppliers();

  void removeTimeoutSupplier(TimeoutSupplier<T> timeoutSupplier);
}

package io.github.henryssondaniel.teacup.protocol.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SupplierHandlerImplTest {
  private SupplierHandler<String> supplierHandler = new SupplierHandlerImpl<>();
  @Mock private TimeoutSupplier<String> timeoutSupplier;

  @Test
  void addRequest() {
    supplierHandler.addTimeoutSupplier(timeoutSupplier);
    var request = "request";

    supplierHandler.addRequest(request);

    verify(timeoutSupplier).addRequest(request);
    verifyNoMoreInteractions(timeoutSupplier);
  }

  @Test
  void addTimeoutSupplier() {
    supplierHandler.addTimeoutSupplier(timeoutSupplier);
    verifyNoMoreInteractions(timeoutSupplier);
  }

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getTimeoutSuppliers() {
    assertThat(supplierHandler.getTimeoutSuppliers()).isEmpty();
  }

  @Test
  void removeTimeoutSupplier() {
    supplierHandler.removeTimeoutSupplier(timeoutSupplier);
    verifyNoInteractions(timeoutSupplier);
  }
}

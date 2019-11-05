package io.github.henryssondaniel.teacup.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.protocol.server.Base;
import io.github.henryssondaniel.teacup.protocol.server.Handler;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class ServerTest {
  private static final String CONTEXT = "context";
  private static final String REQUEST = "request";

  private final Server<String, String> server = new TestBase();

  @Test
  void removeSupplier() {
    var supplier = server.setContext(CONTEXT);
    server.removeSupplier(supplier);
    assertThat(supplier.get()).containsExactly(REQUEST);
  }

  @Test
  void removeSupplierWhenDuplicate() {
    var supplier = server.setContext(CONTEXT);
    var supplierSecond = server.setContext(CONTEXT);

    assertThat(supplier).isNotSameAs(supplierSecond);

    server.removeSupplier(supplier);
    assertThat(supplier.get()).containsExactly(REQUEST);

    server.removeSupplier(supplierSecond);
    assertThat(supplierSecond.get()).isEmpty();
  }

  @Test
  void removeSupplierWhenEmpty() {
    Supplier<List<String>> supplier = new TestSupplier();
    server.removeSupplier(supplier);
    assertThat(supplier.get()).isEmpty();
  }

  private static final class TestBase extends Base<String, String, String> {
    private static final Logger LOGGER = Logger.getLogger(TestBase.class.getName());

    @Override
    public void setUp() {
      LOGGER.log(Level.FINE, "Set up");
    }

    @Override
    public void tearDown() {
      LOGGER.log(Level.FINE, "Tear down");
    }

    @Override
    protected String createProtocolContext(String context, Handler<String> handler) {
      handler.addRequest(REQUEST);
      return "protocolContext";
    }

    @Override
    protected String getKey(String context) {
      return "key";
    }

    @Override
    protected boolean isEquals(String context, String protocolContext) {
      return true;
    }

    @Override
    protected void serverCleanup(String protocolContext) {}
  }

  private static final class TestSupplier implements Supplier<List<String>> {
    private static final Logger LOGGER = Logger.getLogger(TestSupplier.class.getName());

    @Override
    public List<String> get() {
      LOGGER.log(Level.FINE, "Get");
      return Collections.emptyList();
    }
  }
}

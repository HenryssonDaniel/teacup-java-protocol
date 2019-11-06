package io.github.henryssondaniel.teacup.protocol.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.henryssondaniel.teacup.protocol.Server;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BaseTest {
  private static final String KEY = "key";

  private final Context context = mock(Context.class);
  private final Object lock = new Object();
  private final Object lockSecond = new Object();
  private final Server<Context, String> server = new TestBase();

  @Mock private Supplier<List<String>> supplier;
  @Mock private TimeoutSupplier<String> timeoutSupplier;
  private boolean waiting = true;
  private boolean waitingSecond = true;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void removeSupplier() {
    server.removeSupplier(timeoutSupplier);

    verify(timeoutSupplier).stop();
    verifyNoMoreInteractions(timeoutSupplier);
  }

  @Test
  void removeSupplierWhenNotTimeoutSupplier() {
    server.removeSupplier(supplier);
    verifyNoInteractions(supplier);
  }

  @Test
  void setContextWhenContextEquals() throws InterruptedException {
    when(context.getKey())
        .thenReturn(KEY, KEY, KEY)
        .thenAnswer(invocation -> waitKeyFinished())
        .thenReturn(KEY)
        .thenAnswer(invocation -> notifyKey())
        .thenReturn(KEY, KEY)
        .thenAnswer(invocation -> notifyKey());

    var createdSupplier = server.setContext(context);
    assertThat(createdSupplier).isNotNull();

    var supplierArray = createSupplier();

    removeSupplier(createdSupplier);

    createThread();

    lockWait();

    server.removeSupplier(supplierArray[0]);

    synchronized (lock) {
      while (waiting) lock.wait(1L);
    }

    verify(context, atMost(11)).getKey();
    verifyNoMoreInteractions(context);
  }

  @Test
  void setContextWhenContextEqualsInterrupted() throws InterruptedException {
    when(context.getKey())
        .thenReturn(KEY, KEY, KEY)
        .thenAnswer(invocation -> notifySecondKey())
        .thenAnswer(invocation -> waitForever());

    var createdSupplier = server.setContext(context);
    assertThat(createdSupplier).isNotNull();

    var thread = createThread();

    new Thread(() -> notifyRemoveSupplier(createdSupplier)).start();

    synchronized (lock) {
      while (waiting) lock.wait(1L);

      thread.interrupt();
    }

    verify(context, times(5)).getKey();
    verifyNoMoreInteractions(context);
  }

  @Test
  void setContextWhenUniqueContext() {
    when(context.getKey()).thenReturn(KEY, KEY, KEY, "protocolContext", KEY);

    var createdSupplier = server.setContext(context);
    assertThat(createdSupplier).isNotNull();

    assertThat(server.setContext(context)).isNotNull();

    server.removeSupplier(createdSupplier);

    verify(context, times(5)).getKey();
    verifyNoMoreInteractions(context);
  }

  @Test
  void setContextWhenUniqueKey() {
    var createdSupplier = server.setContext(context);
    assertThat(createdSupplier).isNotNull();

    server.removeSupplier(createdSupplier);

    verify(context, times(2)).getKey();
    verifyNoMoreInteractions(context);
  }

  private Supplier<List<String>>[] createSupplier() {
    Supplier<List<String>>[] supplierArray = new Supplier[1];

    var thread =
        new Thread(
            () -> {
              supplierArray[0] = server.setContext(context);
              assertThat(supplierArray[0]).isNotNull();
            });
    thread.start();

    return supplierArray;
  }

  private Thread createThread() {
    var thread = new Thread(() -> assertThat(server.setContext(context)).isNotNull());
    thread.start();

    return thread;
  }

  private void lockWait() throws InterruptedException {
    synchronized (lock) {
      while (waiting) lock.wait(1L);

      waiting = true;
    }
  }

  private Object notifyKey() {
    synchronized (lock) {
      waiting = false;
      lock.notifyAll();
    }

    return KEY;
  }

  private void notifyRemoveSupplier(Supplier<List<String>> createdSupplier) {
    synchronized (lockSecond) {
      while (waitingSecond)
        try {
          lockSecond.wait(1L);
        } catch (InterruptedException ignore) {
          // Empty
        }

      waitingSecond = true;
    }

    server.removeSupplier(createdSupplier);
  }

  private Object notifySecondKey() {
    synchronized (lockSecond) {
      waitingSecond = false;
      lockSecond.notifyAll();
    }

    return KEY;
  }

  private void removeSupplier(Supplier<List<String>> createdSupplier) throws InterruptedException {
    lockWait();

    server.removeSupplier(createdSupplier);

    synchronized (lockSecond) {
      waitingSecond = false;
      lockSecond.notifyAll();
    }

    lockWait();
  }

  private Object waitForever() throws InterruptedException {
    synchronized (lock) {
      waiting = false;
      lock.notifyAll();
    }

    synchronized (lockSecond) {
      while (waitingSecond) lockSecond.wait(1L);
    }

    return KEY;
  }

  private Object waitKeyFinished() throws InterruptedException {
    synchronized (lock) {
      waiting = false;
      lock.notifyAll();
    }

    synchronized (lockSecond) {
      while (waitingSecond) lockSecond.wait(1L);

      waitingSecond = true;
    }

    return KEY;
  }

  @FunctionalInterface
  private interface Context {
    String getKey();
  }

  private static final class TestBase extends Base<Context, String, String> {
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
    protected String createProtocolContext(Context context, Handler<String> handler) {
      return "protocolContext";
    }

    @Override
    protected String getKey(Context context) {
      return context.getKey();
    }

    @Override
    protected boolean isEquals(Context context, String protocolContext) {
      return context.getKey().equals(protocolContext);
    }

    @Override
    protected void serverCleanup(String protocolContext) {}
  }
}

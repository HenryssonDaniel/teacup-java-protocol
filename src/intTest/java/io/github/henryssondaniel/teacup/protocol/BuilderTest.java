package io.github.henryssondaniel.teacup.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BuilderTest {
  private static final String VALUE = "value";

  @Test
  void build() {
    assertThat(new TestDefaultBuilder().build()).isSameAs(VALUE);
  }

  private static final class TestDefaultBuilder extends DefaultBuilder<CharSequence, String> {
    private TestDefaultBuilder() {
      super(VALUE);
    }

    @Override
    protected String createImplementation() {
      return getImplementation();
    }
  }
}

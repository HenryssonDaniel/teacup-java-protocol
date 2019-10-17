package io.github.henryssondaniel.teacup.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DefaultBuilderTest {
  private static final String VALUE = "value";
  private static final String VALUE_NEW = "valueNew";

  @Test
  void build() {
    Builder<CharSequence> defaultNodeBuilder = new TestDefaultBuilder();

    assertThat(defaultNodeBuilder.build()).isSameAs(VALUE);
    assertThat(defaultNodeBuilder.build()).isSameAs(VALUE_NEW);
  }

  @Test
  void getImplementation() {
    assertThat(new TestDefaultBuilder().getImplementation()).isSameAs(VALUE);
  }

  private static final class TestDefaultBuilder extends DefaultBuilder<CharSequence, String> {
    private TestDefaultBuilder() {
      super(VALUE);
    }

    @Override
    protected String createImplementation() {
      return VALUE_NEW;
    }
  }
}

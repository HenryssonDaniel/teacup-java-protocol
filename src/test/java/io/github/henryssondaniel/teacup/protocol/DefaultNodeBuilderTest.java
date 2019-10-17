package io.github.henryssondaniel.teacup.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.henryssondaniel.teacup.core.assertion.GenericObjectAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DefaultNodeBuilderTest {
  private static final String ACTUAL = "actual";

  @Mock private Node<String> node;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void setAssertion() {
    NodeBuilder<String, Node<String>, TestDefaultNodeBuilder> defaultNodeBuilder =
        new TestDefaultNodeBuilder(node);
    assertThat(defaultNodeBuilder.setAssertion(null)).isSameAs(defaultNodeBuilder);
    verify(node).verify(ACTUAL);
  }

  private static final class TestDefaultNodeBuilder
      extends DefaultNodeBuilder<String, Node<String>, Node<String>, TestDefaultNodeBuilder> {
    private final Node<String> setterNew;

    private TestDefaultNodeBuilder(Node<String> setterNew) {
      super(null);
      this.setterNew = setterNew;
    }

    @Override
    protected Node<String> createImplementation() {
      return setterNew;
    }

    @Override
    protected void doAssertion(GenericObjectAssert<String, ?> assertion) {
      setterNew.verify(ACTUAL);
    }
  }
}

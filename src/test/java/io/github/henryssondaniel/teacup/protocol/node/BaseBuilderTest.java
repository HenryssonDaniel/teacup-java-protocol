package io.github.henryssondaniel.teacup.protocol.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.henryssondaniel.teacup.core.assertion.GenericObjectAssert;
import io.github.henryssondaniel.teacup.protocol.Node;
import io.github.henryssondaniel.teacup.protocol.NodeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BaseBuilderTest {
  private static final String ACTUAL = "actual";
  @Mock private Node<String> node;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void setAssertion() {
    NodeBuilder<String, Node<String>, TestBaseBuilder> defaultNodeBuilder =
        new TestBaseBuilder(node);
    assertThat(defaultNodeBuilder.setAssertion(null)).isSameAs(defaultNodeBuilder);
    verify(node).verify(ACTUAL);
  }

  private static final class TestBaseBuilder
      extends BaseBuilder<String, Node<String>, Node<String>, TestBaseBuilder> {
    private final Node<String> setterNew;

    private TestBaseBuilder(Node<String> setterNew) {
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

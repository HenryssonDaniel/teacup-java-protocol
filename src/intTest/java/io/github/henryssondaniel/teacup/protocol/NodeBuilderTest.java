package io.github.henryssondaniel.teacup.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.core.assertion.Factory;
import io.github.henryssondaniel.teacup.core.assertion.GenericObjectAssert;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class NodeBuilderTest {
  @Test
  void build() {
    var value = "value";

    GenericObjectAssert<String, ?> genericObjectAssert =
        Factory.createStringAssert().isSameAs(value);
    TestNode testNode = new DefaultTestNode();

    Node<String> node =
        new TestDefaultNodeBuilder(testNode).setAssertion(genericObjectAssert).build();
    node.verify(value);

    assertThat(node).isSameAs(testNode);
    assertThat(testNode.getAssertion()).isSameAs(genericObjectAssert);
  }

  private interface TestNode extends Node<String> {
    GenericObjectAssert<String, ?> getAssertion();

    void setAssertion(GenericObjectAssert<String, ?> assertion);
  }

  private static final class DefaultTestNode implements TestNode {
    private static final Logger LOGGER = Logger.getLogger(DefaultTestNode.class.getName());
    private GenericObjectAssert<String, ?> assertion;

    @Override
    public GenericObjectAssert<String, ?> getAssertion() {
      return assertion;
    }

    @Override
    public void setAssertion(GenericObjectAssert<String, ?> assertion) {
      this.assertion = assertion;
    }

    @Override
    public void verify(String actual) {
      LOGGER.log(Level.FINE, "Verify");
      assertion.verify(actual);
    }
  }

  private static final class TestDefaultNodeBuilder
      extends DefaultNodeBuilder<String, TestNode, TestNode, TestDefaultNodeBuilder> {
    private TestDefaultNodeBuilder(TestNode testNode) {
      super(testNode);
    }

    @Override
    protected TestNode createImplementation() {
      return new DefaultTestNode();
    }

    @Override
    protected void doAssertion(GenericObjectAssert<String, ?> assertion) {
      getImplementation().setAssertion(assertion);
    }
  }
}

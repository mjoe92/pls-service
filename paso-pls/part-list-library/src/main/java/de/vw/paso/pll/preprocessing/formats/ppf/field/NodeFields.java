package de.vw.paso.pll.preprocessing.formats.ppf.field;

import de.vw.paso.pll.model.PlsEfsElement;
import de.vw.paso.pll.preprocessing.formats.ppf.NodePPF;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.vw.paso.pll.preprocessing.formats.ppf.field.PPFField.toInteger;

public enum NodeFields implements PPFField<NodePPF> {
  NODE_LABEL(node -> node.getRawNode().getNodeLabel()),
  NODE_ID(NodePPF::getNodeId),
  NODE_LEVEL(node -> node.getRawNode().getNodeLevel()),
  NODE_PARENT_ID(node -> node.getRawNode().getNodeType().equalsIgnoreCase("Z_HD") ? "" : node.getParentNodeId()),
  NODE_SORT(NodePPF::getSort),
  NODE_TYPE(node -> node.getRawNode().getNodeType()),
  NODE_VALUE_PARENT(node -> node.getRawNode().getNodeValueParent()),
  NODE_VALUE(node -> node.getRawNode().getNodeValue());

  static final Map<NodeFields, FieldSetter> setter = new HashMap<>();
  static {
    setter.put(NODE_LABEL, PlsEfsElement::setNodeLabel);
    setter.put(NODE_ID, PlsEfsElement::setOriginNodeId);
    setter.put(NODE_LEVEL, ((element, value) -> element.setNodeLevel(toInteger(value))));
    setter.put(NODE_PARENT_ID, PlsEfsElement::setOriginParentNodeId);
    setter.put(NODE_SORT, (element, value) -> element.setNodeSort(toInteger(value)));
    setter.put(NODE_TYPE, PlsEfsElement::setNodeType);
    setter.put(NODE_VALUE_PARENT, PlsEfsElement::setNodeValueParent);
    setter.put(NODE_VALUE, PlsEfsElement::setNodeValue);
  }

  final Function<NodePPF, ?> valueProvider;

  <T> NodeFields(Function<NodePPF, T> valueProvider) {
    this.valueProvider = valueProvider;
  }

  @Override
  public Function<NodePPF, ?> getValueProvider() {
    return valueProvider;
  }

  public void setValue(PlsEfsElement element, String value) {
    setter.get(this).set(element, value);
  }
}

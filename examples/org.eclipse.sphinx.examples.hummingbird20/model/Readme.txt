Required manual modifications after regeneration of Hummingbird 2.0 metamodel:
* Common20XMI.xsd:
  - xsd:complexType element for Description: add mixed="true" attribute
* TypeModel20XMI.xsd:
  - xsd:complexType element for Platform: add mixed="true" attribute
* InstanceModel20XMI.xsd:
  - xsd:complexType element for Application: add mixed="true" attribute
  - xsd:complexType element for ParameterExpression: add mixed="true" attribute

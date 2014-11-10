Required manual modifications after regeneration of Hummingbird 2.0 metamodel:
* Common20XMI.xsd:
  - xsd:complexType element for Identifiable: add mixed="true" attribute
  - xsd:complexType element for Description: add mixed="true" attribute
* TypeModel20XMI.xsd:
  - schemaLocation attribute of xsd:import element: change value to Common20XMI.xsd
  - xsd:complexType element for Platform: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for ComponentType: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for Port: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for Interface: add mixed="true" attribute to contained xsd:complexContent element
* InstanceModel20XMI.xsd:
  - schemaLocation attributes of xsd:import elements: change values to TypeModel20XMI.xsd and Common20XMI.xsd
  - xsd:complexType element for Application: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for Component: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for Connection: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for ParameterValue: add mixed="true" attribute to contained xsd:complexContent element
  - xsd:complexType element for ParameterExpression: add mixed="true" attribute

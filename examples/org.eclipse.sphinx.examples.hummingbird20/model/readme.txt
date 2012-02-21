Please note that certain extra steps are necessary after the hummingbird20 model is re-generated via hummingbird20.genmodel > "Generate All":
- The XML schemas need to be re-exported via the context menu of hummingbird20.genmodel > Export Model... > XML Schema for XMI
- The schemas need to manually patched:
 - Add mixed="true" to the complexTypes for Description and Application
 - Restore the contents of complexType[name="Application"]/complexContent/extension from a previous revision so that it lists the "components" element correctly
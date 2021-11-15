package de.bsd.x2svg.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Parser for JsonSchema documents
 */
public class JsonSchemaParser implements InputParser {

    private static final String MODE_STRING = "json"; //$NON-NLS-1$
   	private static final String FILE_SUFFIX_STRING = ".json"; //$NON-NLS-1$

    private File schemaFile = null;
    private boolean debug = false;

    private final Log log = LogFactory.getLog(JsonSchemaParser.class);

    JsonNode jsonRootNode;

    Set<String> primitiveTypes = Set.of("string","number","integer");

    @Override
    public Container parseInput() throws Exception {

        log.info("Input " + schemaFile.getAbsolutePath());
        jsonRootNode = JsonLoader.fromFile(schemaFile);

        Container rootContainer = new Container();
        rootContainer.name = schemaFile.getName();
        rootContainer.cardinality=new Cardinality(true);
        rootContainer.content = ContentModel.SEQUENCE;
        if (jsonRootNode.isEmpty()) {
            return rootContainer;
        }

        getContainer(jsonRootNode, rootContainer, "properties");
        return rootContainer;
    }

    private Container getContainer(JsonNode root, Container parentContainer, String keyToSearch) {
        JsonNode props = root.get(keyToSearch);
        if (props != null) {
            Iterator<String> iter = props.fieldNames();
            while (iter.hasNext()) {
                String fieldName = iter.next();
                JsonNode element = props.get(fieldName);
                Container type = new Container();
                parentContainer.children.add(type);
                type.parent = parentContainer;
                type.name = fieldName;
//                System.out.println("Name " + fieldName + " type " + element.getNodeType().name());

                if (element.isArray()) {
                    type.cardinality = new Cardinality("0", "*"); // TODO that is of the inner elements?
                }
                JsonNode dataType = element.get("type");
                if (dataType!=null) {
                    String dataTypeString = dataType.asText();
                    if (dataTypeString.equals("array")) {
                        // complex type, we can recurse
                        int min = 0, max = Integer.MAX_VALUE;
                        JsonNode minItems = element.get("minItems");
                        if (minItems != null) {
                            min = minItems.asInt();
                        }
                        JsonNode maxItems = element.get("maxItems");
                        if (maxItems != null) {
                            max = maxItems.asInt();
                        }
                        type.cardinality = new Cardinality(min, max);

                        JsonNode items = element.get("items");
                        if (items != null) {
                            // These may be null if e.g. prefixItems are present
                            // TODO: items may be 'false' explicitly
                            getContainer(items, type, "");
                        }
                        JsonNode prefixItems = element.get("prefixItems");
                        if (prefixItems != null) {
                            // Adjust cardinality
                            System.out.println(prefixItems);
                            type.cardinality = new Cardinality(prefixItems.size(), prefixItems.size());
                            type.content = ContentModel.SEQUENCE;
                            Iterator<JsonNode> elementIter = prefixItems.elements();
                            while (elementIter.hasNext()) {
                                JsonNode pin = elementIter.next();
                                Container c = new Container();
                                c.parent = type;
                                type.children.add(c);
                                String piName = pin.fieldNames().next();
                                String val;
                                if (piName.equals("type")) {
                                    val = pin.get(piName).asText();
                                } else if (piName.equals("enum")) {
                                    val = pin.get(piName).toString();
                                } else {
                                    val = "-not handled yet-";
                                }
                                c.name = "<" + val + ">";
                            }

                        }
                    } else if (dataTypeString.equals("object")) {
                        getContainer(element,type,"properties");
                    } else {
                        Container dType = new Container();
                        String extras = getExtras(element);
                        dType.name = "<" + dataTypeString + extras +  ">";
                        dType.parent=type;
                        dType.isType=true;
                        type.children.add(dType);
                        type.content = ContentModel.TYPE_ON_RIGHT;
                        if (primitiveTypes.contains(dataTypeString)) {
                            type.isPcData = true; // content is literal data
                        }

                    }
                } else { // elements embedded dataType is null... Check if it is a ref and use this
                    JsonNode refNode = element.get("$ref");
                    if (refNode != null) {
                        getContainerViaRef(refNode, type );
                        type.content=ContentModel.SUBSTITUTION; // TODO omit if != none?
                    }
                }
            }
        }
        JsonNode ref = root.get("$ref");
        if (ref != null) {
            Container containerViaRef = getContainerViaRef(ref, parentContainer );
            parentContainer.content = ContentModel.SUBSTITUTION; // TODO substitution group?
            return containerViaRef;
        }
        if (root.fieldNames().hasNext() && root.fieldNames().next().equals("enum")) {
            JsonNode enumNode = root.get("enum");
            System.out.println(enumNode.toPrettyString());
            Container enumContainer = new Container();
            enumContainer.name="<" + enumNode.toPrettyString() + ">";
            enumContainer.parent = parentContainer;
            parentContainer.content = ContentModel.CHOICE;
            parentContainer.children.add(enumContainer);

        }

        return parentContainer;
    }

    private String getExtras(JsonNode element) {
        JsonNode formatNode = element.findValue("format");
        if (formatNode!=null) {
            return ":" + formatNode.asText();
        }
        JsonNode patternNode = element.findValue("pattern");
        if (patternNode!=null) {
            return  ":" + patternNode.asText();
        }
        return "";
    }

    private Container getContainerViaRef(JsonNode ref, Container parent) {
        // This refers to a type definition somewhere else
        String reference = ref.asText();
        // Now look this up via json pointer
        if (reference.startsWith("#")) {
            // Local reference within the document
            reference = reference.substring(1);
        }
        String refShortName;
        if (reference.contains(".")) {
            refShortName = reference.substring(reference.lastIndexOf(".") + 1);
        }
        else {
            refShortName = reference.substring(reference.lastIndexOf("/") +1);
        }
        JsonNode deref = jsonRootNode.at(reference);

        Container refNameContainer = new Container();
        refNameContainer.name = refShortName;
        refNameContainer.parent = parent;
        parent.children.add(refNameContainer);
        refNameContainer.content = ContentModel.SEQUENCE;

        getContainer(deref, refNameContainer, "properties");
        return parent;
    }

    @Override
    public void setInputFile(File inputFile) {
        schemaFile = inputFile;
    }

    @Override
    public void setParserOptions(String[] options) {
        // TODO: Customise this generated block
    }

    @Override
    public String getSpecificHelp() {
        return "Nothing to see yet";
    }

    @Override
    public String getMode() {
        return MODE_STRING;
    }

    @Override
    public String getFileSuffix() {
        return FILE_SUFFIX_STRING;
    }

    @Override
    public void setDebug() {
        debug = true;
    }

    private boolean isDebug() {
    		return debug && log.isDebugEnabled();
    	}

    @Override
    public void setWithAttributes(boolean value) {
        // TODO: Customise this generated block
    }

    @Override
    public void setWithElementComments(boolean value) {
        // TODO: Customise this generated block
    }
}

package de.bsd.x2svg.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
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
    String rootPath;

    Set<String> primitiveTypes = Set.of("string","number","integer");
    private boolean keepRoot = false;

    @Override
    public Container parseInput() throws Exception {

        rootPath = schemaFile.getParent() + "/";

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
        if (keepRoot) {
            return rootContainer;
        }
        if (rootContainer.children.size()==1) {
            Container c = rootContainer.children.get(0);
            c.parent = null;
            return c;
        }
        return rootContainer;
    }

    private Container getContainer(JsonNode root, Container parentContainer, String keyToSearch) {
        JsonNode props = root.get(keyToSearch);
        JsonNode ref = root.get("$ref");
        if (props != null) {
            Iterator<String> iter = props.fieldNames();
            while (iter.hasNext()) {
                String fieldName = iter.next();
                JsonNode element = props.get(fieldName);
                Container type = new Container();
                parentContainer.children.add(type);
                type.parent = parentContainer;
                type.name = fieldName;

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

        else if (ref != null) {
            Container containerViaRef = getContainerViaRef(ref, parentContainer );
            parentContainer.content = ContentModel.SUBSTITUTION; // TODO substitution group?
            return containerViaRef;
        }
        else if (contains(root.fieldNames(),"enum")) {
            JsonNode enumNode = root.get("enum");
            System.out.println("Enum content: " + enumNode.toPrettyString());
            Container enumContainer = new Container();
            enumContainer.name="<" + enumNode.toPrettyString() + ">";
            enumContainer.parent = parentContainer;
            parentContainer.content = ContentModel.CHOICE;
            parentContainer.children.add(enumContainer);
        }
        else if (contains(root.fieldNames(), "type")) {
            if (ref==null) {
                System.err.println("Ref is null ");
                getContainer(root, parentContainer, "properties");
            } else {
                System.out.println("Not yet handled ref " + ref.asText());
            }
        }
        else {
            log.error("Case not yet handled");
        }

        return parentContainer;
    }

    /**
     * Return true if one of the iterated over Strings is the one to be found
     * @param iter An iterator over Strings
     * @param fieldToFind The string to search
     * @return True if the string was found, false otherwise
     */
    boolean contains(Iterator<String> iter, String fieldToFind) {
        if (!iter.hasNext()) {
            return false;
        }
        while (iter.hasNext()) {
            String field = iter.next();
            if (field.equals(fieldToFind)) {
                return true;
            }
        }
        return false;
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
        JsonNode deref=null;

        // This refers to a type definition somewhere else
        String reference = ref.asText();
        String refShortName;
        // Now look this up via json pointer
        if (reference.startsWith("#")) {
            // Local reference within the document
            reference = reference.substring(1);

            if (reference.contains(".")) {
                refShortName = reference.substring(reference.lastIndexOf(".") + 1);
            } else {
                refShortName = reference.substring(reference.lastIndexOf("/") + 1);
            }
            deref = jsonRootNode.at(reference);
        } else {
            JsonNode node = getNodeFromUrl(reference);
            refShortName = getShortNameFromUrl(reference);
            deref = node;
        }

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
        for (String opt : options) {
            switch (opt) {
                case "-kr":
                    keepRoot = true;
                    break;
                default:
                    log.warn("Unknown option >" + opt + "< ignored");
            }
        }
    }

    /*
     * Return a JsonNode from an "url".
     * The URL is for the moment not fetched remotely, but the data is
     * supposed to exist in the local file system. For
     * this purpose, the path is evaluated and taken as local file path
     */
    JsonNode getNodeFromUrl(String urlString)  {

        JsonNode node;
        try {
            URL u = new URL(urlString);
            String path = u.getPath();
            if (path.contains("/")) {
                path = path.substring(path.lastIndexOf('/') +1);
            }

            // prepend the working dir
            path = rootPath + path;

            node = JsonLoader.fromPath(path);

            // If we have a #ref at the end, we select that node
            if (u.getRef() != null && !u.getRef().isBlank()) {
                node = node.get(u.getRef());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (node==null) {
            throw new IllegalArgumentException("Bla bla, need a better message");

        }

        return node;

    }

    String getShortNameFromUrl(String urlString) {
        try {
            URL u = new URL(urlString);
            if (u.getRef() != null && !u.getRef().isBlank()) {
                return u.getRef();
            }

            String path = u.getPath();
            path = path.substring((path.lastIndexOf('/') + 1));
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSpecificHelp() {
        return "-kr: keep root object (filename/id) even if it has only one child";
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

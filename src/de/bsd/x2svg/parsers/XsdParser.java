/*******************************************************************************
 * Copyright (c) 2007, 2008 Heiko W. Rupp. 	All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package de.bsd.x2svg.parsers;

import java.io.File;
import java.net.URI;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaGrammar.BuiltinSchemaGrammar;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * Parser for XML Schema files
 * @author hwr@pilhuhn.de
 *
 */
public class XsdParser implements InputParser
{
	private static final String MODE_STRING = "xsd"; //$NON-NLS-1$
	private static final String FILE_SUFFIX_STRING = ".xsd"; //$NON-NLS-1$
	private File xsdFile=null;
	private String rootElementName;
	private XSModel model=null;
	private int treeDepth = Integer.MAX_VALUE; // default = unlimited
	private boolean limitTypes = false; // true to stop at elements from types and print the type
	private int depth =0;
	private boolean startIsType = false;
	private boolean limitSubstitutions = false;
	private boolean debug = false;
	private boolean alsoAttributes = false;


	private final Log log = LogFactory.getLog(XsdParser.class);

	/**
	 * Parse the input. This can work on elements and types
	 * @see de.bsd.x2svg.parsers.InputParser#parseInput()
	 */
	public Container parseInput() throws Exception
	{

		String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
        System.setProperty(DOMImplementationRegistry.PROPERTY, name);

		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		Object o = registry.getDOMImplementation("XS-Loader");
		XSImplementation impl = (XSImplementation) o;

		XSLoader schemaLoader = impl.createXSLoader(null);

		// fix the next two, check for / in xsdFile path
		URI uri = new URI("file://" + xsdFile.getAbsolutePath());
		model = schemaLoader.loadURI(uri.toString());

        if (model==null) {
            throw new ParserProblemException("Was not able to read the model");
        }

		/*
		/* model is our schema, so lets go
		 */

		// first guess a namespace
		XSNamespaceItemList nsList = model.getNamespaceItems();
		String namespace = "";
		for (int i= 0 ; i < nsList.getLength(); i++)
		{
			XSNamespaceItem item = nsList.item(i);
			if (item instanceof BuiltinSchemaGrammar)
				continue;
			if (item instanceof SchemaGrammar)
			{
				namespace = item.getSchemaNamespace();
				log.info("Guessed target namespace: " + namespace);
				break;
			}
		}

		/*
		 * Now get the rootElement and start the show.
		 */
		Container root = new Container();
		root.cardinality=new Cardinality(true);
		if (startIsType) {
			XSTypeDefinition rootDefinition = model.getTypeDefinition(rootElementName, namespace);
			if (rootDefinition==null) {
				throw new ParserProblemException("Type [" + rootElementName + "] not found");
			}
			root.name=rootDefinition.getName();
			parseDefinition(rootDefinition,root);

		}
		else {
			XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			XSElementDeclaration rootElement = null;
            if (rootElementName != null)
                rootElement =  model.getElementDeclaration(rootElementName, namespace);

            if (isDebug()  && rootElement==null) {
				for (int i = 0 ; i < map.getLength() ; i++) {
					XSObject ob = map.item(i);
						log.debug("Possible root: " + ob.getName());
				}
			}
            if (rootElementName == null || rootElement == null)
            	throw new ParserProblemException("RootElement [" + rootElementName + "] not found");

			root.name=rootElement.getName();
			parseElement(rootElement,root);
		}
		return root;


	}

    /**
     * Start parsing from a given Type Definition
     * @param rootDefinition  The definition to start with
     * @param root The Container that is the root of the tree
     */
    private void parseDefinition(XSTypeDefinition rootDefinition, Container root)
	{
		depth++;
		if (depth>treeDepth)
			return;

		if (isDebug())
			log.debug("parseDefinition: " + rootDefinition.getName());

        workOnTypeDefinition(root, rootDefinition);
    }


    /**
     * Parses an Element recursively
     * @param rootElement The root element with which parsing starts
     * @param root The container on which children and attributes gets added.
     */
    private void parseElement(XSElementDeclaration rootElement, Container root)
	{
		if (isDebug())
			log.debug("Entering parseElement ---");
		depth++;

		if (depth>treeDepth)
			return;

		XSElementDeclaration sg = rootElement.getSubstitutionGroupAffiliation();
		if (sg != null) {
			if (isDebug())
				log.debug("We ( " + rootElement.getName() + " ) are derived from " + sg.getName());
			if (limitSubstitutions) {
				Container type = new Container();
				root.children.add(type);
				type.isSubstitution=true;
				type.name= "<" + sg.getName() + ">";
				type.parent=root;
				root.content = ContentModel.SUBSTITUTION;

				// TODO fix issue with positioning of the type.name

				// TODO check if we have other elements in addition to the
				// base substitution and handle them
				// be careful not to include the sg in that case.
				//return;
			}
		}
		if (rootElement.getAbstract()) {
			root.isAbstract=true;
		}

		XSTypeDefinition tdef = rootElement.getTypeDefinition();
		workOnTypeDefinition(root, tdef);

		// see if we elements that inherit from us and process them
		// TODO: they inherit from the base, so no need to display the
		// base sub-elements here and on the base
		// also need to find a way to express that the base can not be used
		// as element (if abstract only ?), but only the children
		XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
		boolean withInheritance = false;
		for (int i = 0 ; i < map.getLength() ; i++)
		{
			XSObject ob = map.item(i);
			if (ob instanceof XSElementDeclaration) {
				XSElementDeclaration decl = (XSElementDeclaration) ob;
				XSElementDeclaration substitutionGroupAffiliation = decl.getSubstitutionGroupAffiliation();
				if (substitutionGroupAffiliation != null && substitutionGroupAffiliation.equals(rootElement)) {
					if (isDebug())
						log.debug("## child : " + decl.getName());
					Container c = new Container();
					root.children.add(c);
					c.parent=root;
					c.name = decl.getName();
					parseElement(decl, c);
					withInheritance = true;
				}
			}

		}
		if (withInheritance) {
			root.content=ContentModel.INHERITANCE;
		}

	}

	/**
     * Parse a type definition. This can be directly be used as starting point or
     * from within parsing elements
	 * @param root the starting container to which attributes and children will be added.
	 * @param tdef the type definition to start with
	 */
	private void workOnTypeDefinition(Container root, XSTypeDefinition tdef) {
		if (tdef instanceof XSComplexTypeDefinition) {
			XSComplexTypeDefinition cdef = (XSComplexTypeDefinition)tdef;
			if (isDebug()) {
				String out = "  +- Complex type";
				if (cdef.getAbstract())
					out += ", abstract";
				out += " with name " + cdef.getName();
				log.debug(out);
			}

			XSObjectList attrs = cdef.getAttributeUses();
			if (attrs.getLength()>0 && alsoAttributes) {
				for (int i = 0 ; i < attrs.getLength() ; i++) {
					XSObject xso = attrs.item(i);
					if (xso instanceof XSAttributeUse) {
						XSAttributeUse xau = (XSAttributeUse)xso;
						String name = xau.getAttrDeclaration().getName();
						root.attributes.add(name);
					}
				}
			}

			if (limitTypes && cdef.getName()!=null) {
				Container type = new Container();
				root.children.add(type);
				type.isType=true;
				type.name="<" + cdef.getName() + ">";
				type.parent=root;
				root.content = ContentModel.SUBSTITUTION;
				return;
			}

			XSParticle particle = cdef.getParticle();
			if (particle==null) {
				if (isDebug())
					log.debug("Particle is null for: " + cdef.getName());
				// check for a type
				XSTypeDefinition xsTdef = cdef.getBaseType();  // simple or complex
				short type = xsTdef.getType(); // type like e.g. element decl
				String name = xsTdef.getName();
				if (isDebug())
					log.debug("name is " + name);
				if (type == XSConstants.TYPE_DEFINITION)
				{
					/*
					 * Base type is a definition, so lets follow this now
					 */
					if (!"anyType".equals(name))
						log.warn("Don't know yet how to handle type def for [" + name + "]");
				}
				else {
					// This is a plain element, so add it to the container tree
					Container ele = new Container();
					ele.name=cdef.getName();
					if (isDebug())
						log.debug("Plain element with name " + ele.name + "(?)");
					// TODO set cardinality for this container
					root.children.add(ele);
					ele.parent=root;

				}

			} // particle==null
			else {
				if (isDebug())
					log.debug("particle [" + particle.getName() + "]");
				XSTerm term = particle.getTerm();
				short type = term.getType();
				if (isDebug())
					log.debug("Term type " + termTypeToString(type));
				if (type == XSConstants.MODEL_GROUP) {
					XSModelGroup mg = (XSModelGroup) term;
					setContentModelForModelGroup(root, mg);
					workOnParticlesForModelGroup(root, mg);
				}
				else {
					log.warn("No modelgroup");
				}
			}
		}
		else {
			XSSimpleTypeDefinition sdef = (XSSimpleTypeDefinition)tdef;
			if (isDebug())
				log.debug("  simple type with name [" + sdef.getName()
					+ "] and base type [" + sdef.getBaseType().getName() +"]"
					);
			// TODO but not really relevant for now, as this is only a restriction on Strings
		}
	}

	private void workOnParticlesForModelGroup(Container container,
			XSModelGroup modelGroup)
	{
		XSObjectList particles = modelGroup.getParticles();
		for (int i=0 ; i<particles.getLength() ; i++)
		{
			XSParticle part = (XSParticle) particles.item(i);
			XSTerm partTerm = part.getTerm();
			if (isDebug())
				log.debug("wop: term with name [" + partTerm.getName()
					+ "] and type " + termTypeToString(partTerm.getType()));
			// TODO check stack for presence of ele -> endless loop
			Container ele = new Container();
			container.children.add(ele);
			if (part.getMaxOccursUnbounded())
				ele.cardinality=new Cardinality(part.getMinOccurs(),-1);
			else
				ele.cardinality=new Cardinality(part.getMinOccurs(), part.getMaxOccurs());

			ele.parent=container;
			if (partTerm.getType() == XSConstants.ELEMENT_DECLARATION)
			{
				XSElementDeclaration xse = (XSElementDeclaration) partTerm;
				ele.name=xse.getName();
				// check for recursion
				if (container.isElementInHierarchy(ele.name)) {
					ele.isReference=true;
				}
				else {
					parseElement(xse,ele);
				}
			}
			else if (partTerm.getType() == XSConstants.MODEL_GROUP) {
				XSModelGroup pmg = (XSModelGroup) partTerm;
				parseModelGroup(pmg, ele);
			}
			else
				log.warn("unknown type " + partTerm.getType() + " for " + partTerm.getName());
		}
	}

	private void parseModelGroup(XSModelGroup group, Container container)
	{
		if (group.getName()==null && isDebug())
			System.out.println("AnonContainer, compo ist " + getCompositorForModelGroup(group.getCompositor()) );
		container.name = group.getName();
		setContentModelForModelGroup(container, group);
		workOnParticlesForModelGroup(container, group);
		// TODO ...more...

	}

	private String getCompositorForModelGroup(short compositor) {
		String res;
		switch(compositor)
		{
			case 1: res = "Sequence"; break;
			case 2: res = "Choice"; break;
			case 3: res = "All"; break;
			default: res = "- undef -"; break;
		}
		return res;
	}

	/**
     * Set the content model for the passed container
	 * @param root The container
	 * @param mg The XSModelGroup of the XSModel
	 */
	private void setContentModelForModelGroup(Container root, XSModelGroup mg) {
		int compo = mg.getCompositor();
		if (compo == XSModelGroup.COMPOSITOR_CHOICE)
			root.content=ContentModel.CHOICE;
		else if (compo == XSModelGroup.COMPOSITOR_SEQUENCE)
			root.content = ContentModel.SEQUENCE;
		else
			log.warn("Unknown compositor for now: " + compo);
	}


	/**
     * Return the file suffix for this parser (.xsd)
     * @return file suffix for this parser
	 * @see de.bsd.x2svg.parsers.InputParser#getFileSuffix()
	 */
	public String getFileSuffix()
	{
		return FILE_SUFFIX_STRING;
	}

	/**
     * Return the mode string for this parser (xsd)
     * @return mode String for this parser (xsd)
	 * @see de.bsd.x2svg.parsers.InputParser#getMode()
	 */
	public String getMode()
	{
		return MODE_STRING;
	}

	/* (non-Javadoc)
	 * @see de.bsd.x2svg.parsers.InputParser#getSpecificHelp()
	 */
	public String getSpecificHelp() {

		return "[-ls] [-d tree-depth] [-lt] [-st] root-element\n"+
                "-ls: limit substitutions\n"+
                "-lt: limit types\n"+
                "-st root element is a type";
	}



	/* (non-Javadoc)
	 * @see de.bsd.x2svg.parsers.InputParser#setInputFile(java.io.File)
	 */
	public void setInputFile(File inputFile)
	{
		xsdFile = inputFile;
	}

	/* (non-Javadoc)
	 * @see de.bsd.x2svg.parsers.InputParser#setParserOptions(java.lang.String[])
	 */
	public void setParserOptions(String[] options) {
		if (options == null || options.length <1 )
			return;
		int pos=0;
		while(pos < options.length) {
			if (options[pos].equals("-ls")) {
				limitSubstitutions  = true;
				pos ++;
			}
			else if (options[pos].equals("-lt")) {
				limitTypes = true;
				pos ++;
			}

			else if (options[pos].equals("-d")) {
				String td = options[pos+1];
				try {
					treeDepth = Integer.parseInt(td);
				}
				catch (NumberFormatException nfe) {
					 treeDepth = Integer.MAX_VALUE; // unlimited
				}
				pos+=2;
			}
			else if (options[pos].equals("-st")) {
				startIsType  = true;
				pos++;
			}
			else {
				rootElementName = options[pos];
				pos++;
			}
		}

	}

	public void setDebug()
	{
		debug = true;
	}

	/**
	 * Return if the debug flag got passed in and debug logging is enabled
	 * in the logging framework
	 * @return true if debug is enabled by the logger and the user.
	 */
	private boolean isDebug() {
		return debug && log.isDebugEnabled();
	}

	/**
	 * Return the TermType as a String instead of a number.
	 * The Java language bindings for XML Schema don't use Enums *sigh*
	 * @param type A XSTerm type
	 * @return a string representing the term
	 */
	private String termTypeToString(final int type)
	{
		String ret;
		switch (type)
		{
			case 2 : ret = "ElementDeclaration"; break;
			case 7 : ret = "ModelGroup"; break;
			case 9 : ret = "WildCard";	break;
			default : ret = "-unknown: " + type + " -" ;
		}
		return ret;
	}

	public void setWithAttributes(boolean value) {
		alsoAttributes = value;
	}

	public void setWithElementComments(boolean value) {
		// nothing for now

	}
}

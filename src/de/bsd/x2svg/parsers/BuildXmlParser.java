/*******************************************************************************
 * Copyright (c) 2007 Heiko W. Rupp. 	All rights reserved. 
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;
import de.bsd.x2svg.Messages;

public class BuildXmlParser implements InputParser 
{

	private static final String MODE_STRING = "ant"; //$NON-NLS-1$
	private static final String FILE_SUFFIX_STRING = "build.xml"; //$NON-NLS-1$
	private File inputFile = null;
	private Document doc=null;
	private boolean forest = false; 
	private String rootElement;
	
	/**
	 * Parse the passed build file. First we try to see which element
	 * is the root from the &lt;project&gt; tag, then we recursively
	 * walk the tree of the targets and build the tree of containers.
	 * @return at least one initialized container.
	 */
	public Container parseInput() throws Exception 
	{
		DocumentBuilder doBui = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = doBui.parse(inputFile);
		Element root = doc.getDocumentElement();
		Container cont = new Container();
		cont.content=ContentModel.SEQUENCE;

		String defaultTarget;
		if (rootElement == null)
			defaultTarget=root.getAttribute("default"); //$NON-NLS-1$
		else
			defaultTarget=rootElement;
		
		if (forest) {
			cont.name=inputFile.getName();
			cont.content=ContentModel.CHOICE;
			NodeList nodes = root.getElementsByTagName("target"); //$NON-NLS-1$
			for (int i = 0 ; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node instanceof Element) {
					Element element = (Element)node;
					String name = element.getAttribute("name"); //$NON-NLS-1$
					Container myCont = new Container(); // each target is an element
					myCont.name=name;
					myCont.parent=cont;
					myCont.content=ContentModel.SEQUENCE;
					cont.children.add(myCont);
					System.out.println("-> " + name);
					findChildren(name, myCont);
				}
			}
		}
		else {
			cont.name=defaultTarget; // TODO can this only be one?
			findChildren(defaultTarget, cont);
		}
		
		return cont;
	}
	
	
	/**
	 * Find the children of the target with name rootName and fill them
	 * into the passed container.
	 * @param rootName Name of the &lt;target&gt; to work on
	 * @param rootCont The container to fill with children.
	 * @throws ParserProblemException If a target with the passed name can not be found.
	 */
	private void findChildren(String rootName, Container rootCont) throws ParserProblemException
	{
		Element target = getTargetByName(rootName);
		if (target==null)
			throw new ParserProblemException(Messages.getString("BuildXmlParser.3") + rootName + Messages.getString("BuildXmlParser.4")); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Look at dependencies in the depends attribute of the target
		String depends = target.getAttribute("depends"); //$NON-NLS-1$
		if (depends!=null &&  !("".equals(depends))) //$NON-NLS-1$
		{
			String[] tokens = depends.split(","); //$NON-NLS-1$
			for (String token : tokens) {
				token = token.trim();
				Container cont = new Container();
				cont.name=token;
				cont.parent=rootCont;
				cont.content=ContentModel.SEQUENCE;
				rootCont.children.add(cont);
				findChildren(token, cont);
			}
		}
		// see if the target uses &lt;antcall&gt; to call other targets
		NodeList nodes = target.getElementsByTagName("antcall"); //$NON-NLS-1$
		for (int i=0; i< nodes.getLength(); i++) {
			Node node = nodes.item(i);
			Element targetElement = (Element)node;
			String token = targetElement.getAttribute("target"); //$NON-NLS-1$
			Container cont = new Container();
			cont.name=token;
			cont.parent=rootCont;
			cont.content=ContentModel.SEQUENCE;
			rootCont.children.add(cont);
			findChildren(token, cont);
		}
	}


	/**
	 * Get a &lt;target&gt; by its 'name' attribute
	 * @param rootName the name to look for
	 * @return a target element or null
	 */
	private Element getTargetByName(String rootName) {
		NodeList targets = doc.getElementsByTagName("target"); //$NON-NLS-1$
		int um = targets.getLength();
		for (int i = 0 ; i < um ; i++) {
			Node target = targets.item(i);
			Element ele = (Element)target;
			String name = ele.getAttribute("name"); //$NON-NLS-1$
			if (name.equals(rootName))
				return ele;
		}
		return null;
	}

	/**
	 * Sets the input file to work on
	 * @param inputFile a vaild file object to work on
	 */
	public void setInputFile(File inputFile) 
	{
		this.inputFile = inputFile;
	}


	/**
	 * Pass in options to this parser.
	 * So far there is nothing to do.
	 */
	public void setParserOptions(String[] options) 
	{
		if (options==null || options.length<1)
			return;
		if (options[0].equals("-f")) { //$NON-NLS-1$
			forest = true;
		}
		else	
			rootElement=options[0];	
	}


	public String getSpecificHelp() 
	{
		return Messages.getString("BuildXmlParser.7"); //$NON-NLS-1$
	}

	/**
	 * Out suffix is 'build.xml' - whole string
	 */
	public String getFileSuffix() {
		return FILE_SUFFIX_STRING;
	}

	/**
	 * Our mode is 'ant'
	 */

	public String getMode() {
		return MODE_STRING;
	}
	
	public void setDebug()
	{
		// nothing for now
	}


	public void setWithAttributes(boolean value) {
		// nothing for now
	}


	public void setWithElementComments(boolean value) {
		// nothing for now
	}


}

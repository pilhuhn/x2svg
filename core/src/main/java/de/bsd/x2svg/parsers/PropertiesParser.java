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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;
import de.bsd.x2svg.Messages;
import de.bsd.x2svg.util.IOUtil;

/**
 * Simple example of a InputParser for Property files.
 * This is mostly here to show what is needed to accomplish
 * a simple parsing task.
 * @author hwr@pilhuhn.de
 *
 */
public class PropertiesParser implements InputParser 
{

	private static final String FILE_SUFFIX_STRING = ".properties"; //$NON-NLS-1$
	private static final String MODE_STRING = "props"; //$NON-NLS-1$
	/** Properties file to use */
	private File pFile = null;
	private Properties properties = new Properties();
	
	
	/**
	 * Parse the input an construct a Tree of Containers.
	 * The properties file needs be passed in via other means. 
	 * @return A valid container
	 * @see de.bsd.x2svg.Container
	 */
	public Container parseInput() throws Exception 
	{

		Container root = new Container();
		root.name="</>"; //$NON-NLS-1$
		root.content=ContentModel.CHOICE;
		root.cardinality=new Cardinality(true); 
		
		loadPropertiesFile();
		
		Set<Object> keys = properties.keySet();
        for (Object key1 : keys) {
            String key = (String) key1;
            Container cont = new Container();
            cont.name = key;
            cont.parent = root;
            root.children.add(cont);
            cont.content = ContentModel.CHOICE;
            cont.cardinality = new Cardinality(Cardinality.DTDValues.ZERO_OR_ONE);
            cont.isPcData = true;
        }
		return root;
	}



	/**
	 * Load the properties that we want to display from the file set via
	 * {@link #setInputFile(File)} and store it in the variable <em>properties</em>
	 * @throws ParserProblemException if the input file is not acessible 
	 */
	private void loadPropertiesFile() throws ParserProblemException 
    {
		FileInputStream fis = null;
		try 
        {
			fis = new FileInputStream(pFile);

            // File is there, load the properties from it
			properties.load(fis);
		}
		catch (FileNotFoundException fne) 
		{
		    throw new ParserProblemException(pFile.getName() + " in " + pFile.getAbsolutePath() + Messages.getString("PropertiesParser.4")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch(IOException ioe) 
        {
			throw new ParserProblemException(Messages.getString("PropertiesParser.5") + pFile.getName() + ": IOException"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		finally 
        {
            IOUtil.close(fis);
		}
	}


	
	/**
	 * Sets the input file to work on
	 * @param inputFile a vaild file object to work on
	 */
	public void setInputFile(File inputFile) 
	{
		pFile = inputFile;
	}


	/**
	 * Pass in options to this parser.
	 * So far there is nothing to do.
	 */
	public void setParserOptions(String[] options) 
	{
		// nothing to do so far.
	}


	/**
	 * We have no options so far ...
	 * @see de.bsd.x2svg.parsers.InputParser#getSpecificHelp()
	 */
	public String getSpecificHelp() {
		return null;
	}

	/**
	 * We react on .properties
	 */
	public String getFileSuffix() {
		return FILE_SUFFIX_STRING;
	}

	/**
	 * Our mode is 'props'
	 */
	public String getMode() {
		return MODE_STRING;
	}
	
	public void setDebug()
	{ 
		// no debug here 
	}



	public void setWithAttributes(boolean value) {
		// nothing for now
	}



	public void setWithElementComments(boolean value) {
		// nothing for now
	}

}

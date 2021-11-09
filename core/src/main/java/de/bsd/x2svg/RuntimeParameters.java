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
package de.bsd.x2svg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.bsd.x2svg.outputConverter.OutputFormat;
import de.bsd.x2svg.parsers.InputParser;

/**
 * Object to hold various passed parameters
 * @author hwr@pilhuhn.de
 * @since 1.0
 */
public class RuntimeParameters
{
	/** Name of the input file that should be converted */
	private String inputFileName;
	/** Name of the file were the SVG output will be written to */
	private String svgOutputFile;
	/** 
	 * Mode of operation - individual for each parser. This is meant
	 * for explicit parser selection in case x2svg can not determine the
	 * right parser from the file name.
	 * @see InputParser#getMode() 
	 */
	private String opMode;
	/**
	 * Options specific to each parser.
	 * @see InputParser#setParserOptions(String[])
	 */
	private String[] parserSpecificOptions;
	private boolean debug;
	/**
	 * List of formats into which the generated SVG will be converted
	 */
	private List<OutputFormat> outputs = new ArrayList<OutputFormat>();
	/**
	 * A comment to be printed on the generated SVG
	 */
	private String comment;

    /** The location of the x2svg.properties file, can be empty - if so this is read from the classpath. */
    private String propertiesLocation;
	
	/** Should the parser also parse attributes if it supports it? */
	private Boolean withAttributes = null;
	/** Should the parser also parse comments of elements (and possibly attributes)? */
	private Boolean withElementComments = null;
    
	/**
	 * Default constructor to create empty runtime parameters.
	 */
	public RuntimeParameters()
	{
		// Default constructor
	}
	
	/**
	 * Create <code>RuntimeParameters</code> based on an existing set of parameters.
	 * (Copy constructor).
	 * 
	 * @param parameters The existing parameter set to copy as a new instance.
	 */
	public RuntimeParameters(final RuntimeParameters parameters)
	{
		if (parameters != null)
		{
			this.inputFileName = parameters.getInputFileName();
			this.svgOutputFile = parameters.getSvgOutputFile();
			this.opMode = parameters.getOpMode();
			final String[] existing = parameters.getParserSpecificOptions();
			if (existing != null)
			{
				this.parserSpecificOptions = new String[existing.length];
				System.arraycopy(existing, 0, this.parserSpecificOptions, 0, existing.length);
			}
			this.debug = this.isDebug();
			this.outputs.addAll(parameters.getOutputFormats());
            this.propertiesLocation = parameters.getPropertiesLocation();
		}
	}
	
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Return the name of the input file that should be converted
     * @return the name of the input file to convert
     */
	public String getInputFileName() {
		return inputFileName;
	}
	
	/**
	 * Set the name of the input file that should be converted
     * @param fileName the name/path of the file to convert
     */
	public void setInputFileName(String fileName) {
		this.inputFileName = fileName;
	}
	
	
	/**
	 * Return mode of operation - individual for each parser. This is meant
	 * for explicit parser selection in case x2svg can not determine the
	 * right parser from the file name.
	 * @see InputParser#getMode()
     * @return the operation mode
	 */
	public String getOpMode() {
		return opMode;
	}
	
	/**
	 * Set mode of operation - individual for each parser. This is meant
	 * for explicit parser selection in case x2svg can not determine the
	 * right parser from the file name.
	 * @param opMode the mode of operation
     * @see InputParser#getMode()
	 */
	public void setOpMode(String opMode) {
		this.opMode = opMode;
	}
	
	
	/**
	 * Get the options that are specific to the current parser.
	 * 
	 * @return The array of Strings that represent options specific to the parser.
	 * @see InputParser#setParserOptions(String[])
	 */
	public String[] getParserSpecificOptions() 
	{
		String[] options = null;
		if (parserSpecificOptions != null) 
		{
			options = new String[parserSpecificOptions.length];
			System.arraycopy(parserSpecificOptions, 0, options, 0, parserSpecificOptions.length);
		}
		
		return options;
	}
	
	
	/**
	 * Set the options that are specific to the current parser.
	 * 
	 * @param options The array of Strings that represent the options for this parser.
	 * @see InputParser#setParserOptions(String[])
	 */
	public void setParserSpecificOptions(final String[] options) 
	{
		if (options == null)
		{
			this.parserSpecificOptions = null;
		}
		else
		{
			this.parserSpecificOptions = new String[options.length];
			System.arraycopy(options, 0, parserSpecificOptions, 0, options.length);
		}
	}
	
	/** 
	 * Return the name of the file were the SVG output will be written to
	 * @return file name of the svg file 
	 */
	public String getSvgOutputFile() {
		return svgOutputFile;
	}
	/**
	 * Set the name of the file were the SVG output will be written to 
	 * @param svgOutputFile the new file name of the svg file
	 */
	public void setSvgOutputFile(String svgOutputFile) {
		this.svgOutputFile = svgOutputFile;
	}
	
	/**
	 * Return the list of defined output formats.
	 * This List is never null, but can be empty
	 * @return List of OutputFormat items
	 */
	public List<OutputFormat> getOutputFormats() 
	{
		return Collections.unmodifiableList(outputs);
	}
	
	/**
	 * Add a new OutputFormat to the list of output formats
	 * @param outputFormat a new OutputFormat
	 */
	public void addOutputFormat(OutputFormat outputFormat)
	{
		outputs.add(outputFormat);
	}
    
    /**
     * Get the location of the x2svg properties file.
     * 
     * @return The location of the properties file, null indicates load from classpathl.
     */
    public String getPropertiesLocation()
    {
        return propertiesLocation;
    }
    
    
    /**
     * Set the location of the x2svg properties file.
     * 
     * @param propertiesLocation The location to set, or null to indicate load from classpath.
     */
    public void setPropertiesLocation(final String propertiesLocation)
    {
        this.propertiesLocation = propertiesLocation;
    }

	/**
	 * A comment that is printed on the generated diagram
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * A comment that is printed on the generated diagram
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the withAttributes
	 */
	public Boolean getWithAttributes() {
		return withAttributes;
	}

	/**
	 * @param withAttributes the withAttributes to set
	 */
	public void setWithAttributes(Boolean withAttributes) {
		this.withAttributes = withAttributes;
	}

	/**
	 * @return the withElementComments
	 */
	public Boolean getWithElementComments() {
		return withElementComments;
	}

	/**
	 * @param withElementComments the withElementComments to set
	 */
	public void setWithElementComments(Boolean withElementComments) {
		this.withElementComments = withElementComments;
	}

}
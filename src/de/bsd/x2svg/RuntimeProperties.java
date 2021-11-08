/*******************************************************************************
 * Copyright (c) 2007,2008 Heiko W. Rupp. 	All rights reserved. 
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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.bsd.x2svg.util.IOUtil;

/**
 * This singleton provides properties that are needed at runtime. 
 * @author hwr@pilhuhn.de
 *
 */
public class RuntimeProperties 
{
	
	private static final String ERR_STRING = Messages.getString("Runner.1")
			+ Constants.SYSTEM_PROPERTIES_FILE + Messages.getString("Runner.2");
	private static volatile RuntimeProperties properties;
	private Font fontSmall;
	private FontMetrics fontMetrics;
	private int fontHeight;
	private Color textColor;
	private String commentText;
	private boolean commentTextOn;
	private Font commentTextFont;
	private Color commentTextColor;
	private Font attributeFont;
	private Color attributeColor;
	/** Should the drawing of the elements be more condensed? */
	private boolean optimizeDrawing=false;
	/**
	 * Font for cardinalities. Property is x2svg.font.cardinality 
	 */
	private String FONT_SMALL_NAME;
	/**
	 * Font for element names. Property is x2svg.font.element 
	 */
	private String FONT_NORMAL_NAME;
	private Font font;
	
	/** Should the parser also parse attributes if it supports it? */
	private boolean withAttributes = false;
	/** Should the parser also parse comments of elements (and possibly attributes)? */
	private boolean withElementComments = false;

    /** Should a simple (=one color) shadow be drawn or a more spohistic one? */
    private boolean simple_shadow = false;

    private RuntimeProperties()
	{	
		// empty
	}
	
	/**
	 * Obtain the singleton instance
	 * @return the only instane of this class 
	 */
	public static RuntimeProperties getInstance()
	{
		if (properties==null)
			properties= new RuntimeProperties();
		return properties;
	}

	/**
	 * Name of the font used for small characters
	 * @return Font name
	 * @see java.awt.Font#decode(String)
	 */
	public String getFontSmallName()
	{
		return FONT_SMALL_NAME;
	}
	
	public void setFontSmallName(String name)
	{
		FONT_SMALL_NAME = name;
	}
	
	/**
	 * Name of the font for the element names 
	 * @return font name
	 * @see java.awt.Font#decode(String)
	 */
	public String getFontName()
	{
		return FONT_NORMAL_NAME;
	}
	
	public void setFontName(String name)
	{
		FONT_NORMAL_NAME = name;
	}

	/**
	 * Height of the normal size font
	 * @return height in pixels
	 */
	public int getFontHeight() {
		return fontHeight;
	}

	public void setFontHeight(int fontHeight) {
		this.fontHeight = fontHeight;
	}

	/**
	 * Font metrics for the normal size font
	 * @return Metrics for the normal size font
	 */
	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}

	public void setFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
	}

	/**
	 * Font object for the small font used by cardinalities
	 * @return the small font 
	 */
	public Font getFontSmall() {
		return fontSmall;
	}

	public void setFontSmall(Font fontSmall) {
		this.fontSmall = fontSmall;
	}

	/**
	 * Font object for the normal size font
	 * @return the normal size font
	 */
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * The Color of the text (element name, cardinality)
	 * This needs to be given as 24-bit rgb color number
	 * in the preferences file. 
	 * @see java.awt.Color#decode(String)
	 * @return a color object to use for text
	 */
	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	/**
	 * A comment text to write on the diagram
	 * @return the commentText
	 */
	public String getCommentText() {
		return commentText;
	}

	/**
	 * A comment text to write on the diagram
	 * @param commentText the commentText to set
	 */
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	/**
	 * Is the comment text from the properties visible by default
	 * @return the commentTextOn
	 * @see #getCommentText()
	 */
	public boolean isCommentTextOn() {
		return commentTextOn;
	}

	/**
	 * Is the comment text from the properties visible by default
	 * @param commentTextOn the commentTextOn to set
	 */
	public void setCommentTextOn(boolean commentTextOn) {
		this.commentTextOn = commentTextOn;
	}

	/**
	 * Font of the diagram comment
	 * @return the commentTextFont
	 * @see #getCommentText()
	 */
	public Font getCommentTextFont() {
		return commentTextFont;
	}

	/**
	 * Font of the diagram comment
	 * @param commentTextFont the commentTextFont to set
	 */
	public void setCommentTextFont(Font commentTextFont) {
		this.commentTextFont = commentTextFont;
	}

	/**
	 * Color of the diagram comment
	 * @return the commentTextColor
	 * @see #getCommentText()
	 */
	public Color getCommentTextColor() {
		return commentTextColor;
	}

	/**
	 * Color of the diagram comment
	 * @param commentTextColor the commentTextColor to set
	 */
	public void setCommentTextColor(Color commentTextColor) {
		this.commentTextColor = commentTextColor;
	}

	/**
	 * Should the drawing of the elements be more condensed?
	 * @return the optimizeDrawing
	 */
	public boolean isOptimizeDrawing() {
		return optimizeDrawing;
	}

	/**
	 * Should the drawing of the elements be more condensed? 
	 * @param optimizeDrawing the optimizeDrawing to set
	 */
	public void setOptimizeDrawing(boolean optimizeDrawing) {
		this.optimizeDrawing = optimizeDrawing;
	}

	/** 
	 * Load properties from a file to initialize some system properties.
	 * 
	 * @param propertiesLocation The location of the x2svg properties file, if null, try to load
	 * from x2svg.properties in current directory, else try to load from classpath. 
	 * @param debug Should additional debugging output be written to stdout?
	 */
	public void loadProperties(final String propertiesLocation, boolean debug) 
	{
		final Properties p = new Properties();
		InputStream is = null;
	    try
	    {
	        if (propertiesLocation != null)
	        {
	            // If a location is specified for properties, use it ...
	            is = new FileInputStream(new File(propertiesLocation));
	            if (debug)
	            {
	                System.out.println("Using properties file from - " + new File(propertiesLocation).getAbsolutePath());
	            }
	        }
	        else
	        {
	        	// try to load it from the base dir
	        	try {
	        		is = new FileInputStream(new File("x2svg.properties"));
	        	}
	        	catch (FileNotFoundException fnfe) {
	        		// 	... Otherwise, try to load the properties from the classpath.
	        		is = ClassLoader.getSystemResourceAsStream(Constants.SYSTEM_PROPERTIES_FILE);
	        	}
	        }
			
	        if (is != null)
	        {
	            // If there is a stream, load properties.
				p.load(is);
			}
			else
	        {
	            // Unable to locate properties file.
				System.err.println(ERR_STRING);
	        }
	    }
	    catch (FileNotFoundException fnfe)
	    {
	        // If there is a problem loading the properties from the specified location, tell people.
	        System.out.println(ERR_STRING);
	    }
	    catch (IOException e) 
	    {
	        // If there is a problem loading the properties, tell people.
	        System.out.println(ERR_STRING);
	    }
	    finally
	    {
	        IOUtil.close(is);
	    }
	    
		RuntimeProperties props = RuntimeProperties.getInstance();		
		props.setFontName(p.getProperty("x2svg.font.element",Constants.DEFAULT_FONT_ELEMENTS)); //$NON-NLS-1$
		props.setFontSmallName(p.getProperty("x2svg.font.cardinality",Constants.DEFAULT_FONT_SMALL)); //$NON-NLS-1$
		String tColor = p.getProperty("x2svg.color.text", "0x000000"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			props.setTextColor(Color.decode(tColor));
		}
		catch (NumberFormatException nfe) {
			System.err.println(Messages.getString("Runner.29")); //$NON-NLS-1$
			props.setTextColor(Color.BLACK);
		}
		props.setCommentText(p.getProperty("x2svg.comment.defaultText",null)); // $NON-NLS-1$
		try {
			String color = p.getProperty("x2svg.comment.color","0x000000"); // $NON-NLS-1$ $NON-NLS-2$
			Color col = Color.decode(color);
			props.setCommentTextColor(col); 
		}
		catch (NumberFormatException nfe) {
			System.err.println(Messages.getString("Runner.30")); //$NON-NLS-1$
			props.setCommentTextColor(Color.BLACK);
		}
		try {
			String color = p.getProperty("x2svg.attribute.color","0x000000"); // $NON-NLS-1$ $NON-NLS-2$
			Color col = Color.decode(color);
			props.setAttributeColor(col); 
		}
		catch (NumberFormatException nfe) {
			System.err.println(Messages.getString("Runner.31")); //$NON-NLS-1$
			props.setAttributeColor(Color.BLACK);
		}
		
		String fontString = p.getProperty("x2svg.comment.font",Constants.DEFAULT_FONT_ELEMENTS); //$NON-NLS-1$
		props.setCommentTextFont(Font.decode(fontString));
		fontString = p.getProperty("x2svg.attribute.font",Constants.DEFAULT_FONT_ATTRIBUTES); //$NON-NLS-1$
		props.setAttributeFont(Font.decode(fontString));
		String boolString = p.getProperty("x2svg.comment.defaultOn","false"); //$NON-NLS-1$ $NON-NLS-2$
		props.setCommentTextOn(Boolean.parseBoolean(boolString));
		
		boolString = p.getProperty("x2svg.draw.optimize","false"); //$NON-NLS-1$ $NON-NLS-2$
		props.setOptimizeDrawing(Boolean.parseBoolean(boolString));
	
		boolString = p.getProperty("x2svg.parser.attributes", "false"); //$NON-NLS-1$ $NON-NLS-2$
		props.setWithAttributes(Boolean.parseBoolean(boolString));
		
		boolString = p.getProperty("x2svg.parser.comments", "false"); //$NON-NLS-1$ $NON-NLS-2$
		props.setWithElementComments(Boolean.parseBoolean(boolString));

        boolString = p.getProperty("x2svg.draw.simple_shadow","false"); //$NON-NLS-1$ $NON-NLS-2$
        props.setSimple_shadow(Boolean.parseBoolean(boolString));

    }

	/**
	 * @return the attributeFont
	 */
	public Font getAttributeFont() {
		return attributeFont;
	}
	
	/**
	 * @param attributeFont the attributeFont to set
	 */
	public void setAttributeFont(Font attributeFont) {
		this.attributeFont = attributeFont;
	}

	/**
	 * @return the attributeColor
	 */
	public Color getAttributeColor() {
		return attributeColor;
	}

	/**
	 * @param attributeColor the attributeColor to set
	 */
	public void setAttributeColor(Color attributeColor) {
		this.attributeColor = attributeColor;
	}

	/**
	 * @return the withAttributes
	 */
	public boolean isWithAttributes() {
		return withAttributes;
	}

	/**
	 * @param withAttributes the withAttributes to set
	 */
	public void setWithAttributes(boolean withAttributes) {
		this.withAttributes = withAttributes;
	}

	/**
	 * @return the withElementComments
	 */
	public boolean isWithElementComments() {
		return withElementComments;
	}

	/**
	 * @param withElementComments the withElementComments to set
	 */
	public void setWithElementComments(boolean withElementComments) {
		this.withElementComments = withElementComments;
	}


    public boolean isSimple_shadow() {
        return simple_shadow;
    }

    public void setSimple_shadow(boolean simple_shadow) {
        this.simple_shadow = simple_shadow;
    }
}

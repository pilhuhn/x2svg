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

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * Various system wide constants
 * @author hwr@pilhuhn.de
 */
public abstract class Constants {

	/** Height of the border of an element box */
	public static final int BORDER_HEIGHT = 8;
	/** Width of the border of an element box */
	public static final int BORDER_WIDTH = 12;
	/**
	 * X offset between two element box columns. This is basically the
	 * space for the content box in between
	 */ 
	public static final int X_OFFSET = 42;
	/** Stroke with rounded ends and joins */
	public static final Stroke ROUNDED = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	/** Dashes for a dashed stroke */
	static final float[] DASHES = {2f, 2f};
	/** A dashed stroke */
	public static final Stroke DASHED = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, DASHES, 0);
	/** Default font for element names */
	static final String DEFAULT_FONT_ELEMENTS = "SansSerif-PLAIN-12";
	/** Default font for cardinalities */
	static final String DEFAULT_FONT_SMALL = "SansSerif-PLAIN-8";
	/** name of a properties file to read user specific defaults from */
	static final String SYSTEM_PROPERTIES_FILE = "x2svg.properties";
	/** Default font for attribute names */
	static final String DEFAULT_FONT_ATTRIBUTES = "SansSerif-ITALIC-9";
}

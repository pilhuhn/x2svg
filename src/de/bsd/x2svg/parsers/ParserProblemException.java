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

import de.bsd.x2svg.Messages;

/**
 * This excption it thrown if the parser encounters
 * an internal problem and can not go on building the container
 * tree.
 * @author hwr@pilhuhn.de
 */
public class ParserProblemException extends Exception
{
	
	private static final long serialVersionUID = -6527316342567317073L;

	/**
	 * Contstructor
	 * @param what Reason why this exception is thrown.
	 */
	public ParserProblemException(String what) 
	{
		super(what);
	}

	/**
	 * Contstructor with help display
	 * @param what Reason why this exception is thrown.
	 * @param helpString A help string for this specific parser to be shown.
	 */
	public ParserProblemException(String what, String helpString)
	{
		super(what + "\n" + Messages.getString("ParserProblemException.1") + helpString); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

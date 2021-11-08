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
package de.bsd.x2svg.test;

import org.testng.annotations.Test;

import de.bsd.x2svg.RuntimeParameters;

/**
 * Test the runtime parameters object.
 * 
 * @author gareth floodgate
 */
public class TestRuntimeParameters 
{

	/**
	 * Test that parser specific options array is copied on assignment and return.
	 */
	@Test
	public void testParserSpecificOptionsAreCopiedForRobustness()
	{
		final String before = "Before";
		final String after = "after";
		final String[] test = { before };
		
		// Set the parser specific options.
		final RuntimeParameters params = new RuntimeParameters();
		params.setParserSpecificOptions(test);
		
		// Check that the getter returns them.
		final String[] optionsBefore = params.getParserSpecificOptions();
		assert before.equals(optionsBefore[0]) : "The parser specific option is wrong.";
		
		// Now ensure, that they where copied, not referenced.
		test[0] = after;
		final String[] optionsAfter = params.getParserSpecificOptions();
		assert before.equals(optionsAfter[0]) : "The parser specific option is wrong, it was not copied for store.";
	}

}

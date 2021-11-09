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

import java.io.File;

import org.testng.annotations.Test;

import de.bsd.x2svg.Container;
import de.bsd.x2svg.parsers.DtdParser;
import de.bsd.x2svg.parsers.InputParser;

/**
 * Test parsing of DTDs
 * @author hwr@pilhuhn.de
 */
public class TestDtdEntityParsing
{
	private static final String BASEDIR="src/test/";

	/**
	 * Test the simple dtd.
	 * @throws Exception on any (assertion) error
	 */
	@Test
	public void testEntityDtd() throws Exception
	{
		File input = new File(BASEDIR + "resources/entity.dtd");
		InputParser ip = new DtdParser();
		ip.setParserOptions(new String[] {"root"});
		ip.setInputFile(input);
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("root") : "The root container was not named 'root'";
		assert cont.children.size()==1 : "root did not have 1 child";
		Container c = cont.children.get(0);
		assert c.name.equals("hello");
		assert c.children.size()==1;
		c = c.children.get(0);
		assert c.name.equals("world");
	}

}

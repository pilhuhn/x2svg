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
import de.bsd.x2svg.parsers.InputParser;
import de.bsd.x2svg.parsers.XsdParser;

/**
 * Test parsing of XSDs
 * @author hwr@pilhuhn.de
 */
public class TestXsdParsing
{
	private static final String BASEDIR="src/test/";

	/**
	 * Test the simple schema.
	 * @throws Exception if anything goes wrong
	 */
	@Test
	public void testSampleXsd() throws Exception
	{
		File input = new File(BASEDIR + "resources/sample.xsd");
		InputParser ip = new XsdParser();
		ip.setInputFile(input);
		ip.setParserOptions(new String[]{"root"});
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("root") : "The root container was not named 'root'";
		assert cont.children.size()==3 : "root did not have 3 children";
		int found = 0;
		for (Container c: cont.children) {
			if (c.name.equals("a")) {
				found++;
				assert c.children.size()==0;
			}
			if (c.name.equals("b")) {
				found++;
				assert c.children.size()==2;
				for (Container co : c.children) {
					if (co.name==null) {
						assert co.children.size()==3;
					}
					else if ("a".equals(co.name)) {
						assert co.children.size()==0;
					}
				}
				// TODO check more
			}
			if (c.name.equals("c")) {
				found++;
				assert c.children.size()==2;
			}
		}
		assert found == 3 : "Did not find the desired children of root";
	}

	/**
	 * Test the a schema with a recursive element 'b'
	 * @throws Exception if anything goes wrong
	 */
	@Test
	public void testRecursiveXsd() throws Exception
	{
		File input = new File(BASEDIR + "resources/recursive.xsd");
		InputParser ip = new XsdParser();
		ip.setInputFile(input);
		ip.setParserOptions(new String[]{"root"});
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("root") : "The root container was not named 'root'";
		assert cont.children.size()==3 : "root did not have 3 children";
		int found = 0;
		for (Container c: cont.children) {
			if (c.name.equals("a")) {
				found++;
				assert c.children.size()==0;
			}
			if (c.name.equals("b")) {
				found++;
				assert c.children.size()==2;
				for (Container co : c.children) {
					if (co.name==null) {
						assert co.children.size()==3;
					}
					else if ("a".equals(co.name)) {
						assert co.children.size()==0;
					}
				}
				// TODO check more
			}
			if (c.name.equals("c")) {
				found++;
				assert c.children.size()==2;
			}
		}
		assert found == 3 : "Did not find the desired children of root";
	}

	/**
	 * Test a schema where one element is abstract and has children that
	 * inherit from it. This element also appears later on again and should
	 * there be marked as "reference"
	 * @throws Exception if anything goes wrong
	 */
	@Test
	public void testXsdWithInheritance() throws Exception
	{
		File input = new File(BASEDIR + "resources/inheritance.xsd");
		InputParser ip = new XsdParser();
		ip.setInputFile(input);
		ip.setParserOptions(new String[]{"root"});
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("root") : "The root container was not named 'root'";
		assert cont.children.size()==1 : "The root container did not have exactly one child";
		Container pt = cont.children.get(0);
		assert pt.name.equals("parent-type");
		assert pt.isAbstract : "Pt was not abstract but should be ";
		assert pt.children.size()==4 : "Pt did not have 4 children, but " + pt.children.size();
		for (Container co : pt.children) {
			if (co.name.equals("joe")) {
				assert co.children.size()== 2 ;
				for (Container ch : co.children) {
					assert ch.name==null;
					assert ch.children.size()==1;
					Container cc = ch.children.get(0);
					if (cc.name.equals("parent-type")) {
						assert !cc.isAbstract;
						assert cc.isReference;
					}
				}
			}
		}
	}

	@Test
	public void testXsdParseType() throws Exception
	{
		File input = new File(BASEDIR + "resources/inheritance.xsd");
		InputParser ip = new XsdParser();
		ip.setInputFile(input);
		ip.setParserOptions(new String[]{"-st","constraintType"});
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("constraintType") : "The root container was not named 'constraintType'";
		assert cont.children.size()==3 : "root did not have 3 children";
		int found = 0;
		for (Container c : cont.children) {
			if (c.name.equals("integer-constraint"))
				found++;
			if (c.name.equals("float-constraint"))
				found++;
			if (c.name.equals("regex-constraint"))
				found++;
		}
		assert found == 3: "Did not find all three expected children";
	}

	/**
	 * Test the parsing of attributes on an XSD
	 * @throws Exception if anything goes wrong
	 */
	@Test
	public void testXsdWithAttributes() throws Exception
	{
		File input = new File(BASEDIR + "resources/attributes.xsd");
		InputParser ip = new XsdParser();
		ip.setInputFile(input);
		ip.setParserOptions(new String[]{"root"});
		ip.setWithAttributes(true);
		Container cont = ip.parseInput();
		assert cont != null : "The returned container was null, but should not be";
		assert cont.name.equals("root") : "The root container was not named 'root'";
		assert cont.children.size()==3 : "root did not have 3 children";
		int found = 0;
		for (Container c: cont.children) {
			if (c.name.equals("a")) {
				found++;
				assert c.attributes.size() == 2;
			}
		}
		assert found == 1 : "Child 'a' was not found on 'root'";
	}

}

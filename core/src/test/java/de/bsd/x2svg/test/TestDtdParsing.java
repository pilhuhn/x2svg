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

import de.bsd.x2svg.Container;
import de.bsd.x2svg.parsers.DtdParser;
import de.bsd.x2svg.parsers.InputParser;

import org.testng.annotations.Test;

/**
 * Test parsing of DTDs
 *
 * @author hwr@pilhuhn.de
 */
public class TestDtdParsing {
    private static final String BASEDIR = "src/test/";

    /**
     * Test the simple dtd.
     *
     * @throws Exception on any (assertion) error
     */
    @Test
    public void testSampleDtd() throws Exception {
        File input = new File(BASEDIR + "resources/sample.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";
        assert cont.name.equals("root") : "The root container was not named 'root'";
        assert cont.children.size() == 3 : "root did not have 3 children";
        int found = 0;
        for (Container c : cont.children) {
            if (c.name.equals("a")) {
                found++;
                assert c.children.size() == 0;
            }
            if (c.name.equals("b")) {
                found++;
                assert c.children.size() == 2;
                int found2 = 0;
                for (Container co : c.children) {
                    if (co.name == null) {
                        found2++;
                        assert co.children.size() == 4;
                    }
                    if ("a".equals(co.name))
                        found2++;
                }
                assert found2 == 2 : "B did not have the desired 2 children";
            }
            if (c.name.equals("c")) {
                found++;
                assert c.children.size() == 2;
            }
        }
        assert found == 3 : "Did not find the desired children of root";
    }

    /**
     * Test a DTD that contains recursive elements.
     * <br/>
     * &lt;ELEMENT root (a|b)&gt;<br/>
     * &lt;ELEMENT a (a|b)&gt;
     *
     * @throws Exception on any (assertion) error
     */
    @Test
    public void testDtdWithRecursion() throws Exception {
        File input = new File(BASEDIR + "resources/recursive.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";
        assert cont.name.equals("root") : "Name should be 'root', but was " + cont.name;
        cont = cont.children.iterator().next();
        assert cont.name.equals("a") : "Name should be 'a', but was " + cont.name;
        assert !cont.isReference : "First occourence of a should be no reference";
        assert cont.children.size() == 2 : "Expected to see 2 children of a, but we found " + cont.children.size();

        int found = 0;
        for (Container c : cont.children) {
            if (c.name.equals("a")) {
                found++;
                assert c.isReference : "This occourence of a should be a reference";
            }
            if (c.name.equals("b"))
                found++;
        }
        assert found == 2;
    }

    /**
     * Test a DTD that contains recursive elements.
     * <br/>
     * &lt;ELEMENT root (a|b)&gt;<br/>
     * &lt;ELEMENT a (a|b)&gt;
     *
     * @throws Exception on any (assertion) error
     */
    @Test
    public void testDtdWithRecursion2() throws Exception {
        File input = new File(BASEDIR + "resources/recursive2.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";

    }

    /**
     * Test a DTD that contains recursive elements and an
     * AnonymousContainer
     *
     * @throws Exception on any (assertion) error
     */
    @Test
    public void testDtdWithRecursion3() throws Exception {
        File input = new File(BASEDIR + "resources/recursive3.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";
        assert "root".equals(cont.name);
        assert cont.children.size() == 1 : "Root did not have 1 child, but " + cont.children.size();
        Container b = cont.children.get(0);
        assert "b".equals(b.name);
        assert b.children.size() == 2 : "B did not have 2 children, but " + b.children.size();
        Container a = b.children.get(0);
        assert "a".equals(a.name);
        Container x = b.children.get(1);
        assert x != null;
        assert x.name == null : "2nd child of b was not anonymous";
        assert x.children.size() == 2;
        Container b2 = x.children.get(0);
        assert "b".equals(b2.name) : "b2.name should be 'b', but was " + b2.name;

    }

    /**
     * Test if the parser can limit parsing of a
     * DTD that is deeper than 1 level below to 1 level.
     *
     * @throws Exception on any (assertion) error
     */
    @Test
    public void testWithDepthLimit() throws Exception {
        File input = new File(BASEDIR + "resources/sample.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        String[] options = new String[]{"-d", "1"};
        ip.setParserOptions(options);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";
        assert cont.children.size() == 3 : "Did not find the expected 3 children";
        for (Container c : cont.children) {
            assert c.children.size() == 0 : c.name + "Had unexpected children";
        }
    }

    @Test
    public void testSDocBookDtd() throws Exception {
        File input = new File(BASEDIR + "resources/sdocbook.dtd");
        InputParser ip = new DtdParser();
        String[] options = new String[]{"-d", "5"};
        ip.setParserOptions(options);
        ip.setInputFile(input);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";

    }

    @Test
    public void testWithAttributes() throws Exception {
        File input = new File(BASEDIR + "resources/sample.dtd");
        InputParser ip = new DtdParser();
        ip.setInputFile(input);
        ip.setWithAttributes(true);
        Container cont = ip.parseInput();
        assert cont != null : "The returned container was null, but should not be";
        assert cont.children.size() == 3 : "Did not find the expected 3 children";

        for (Container c : cont.children) {
            assert !c.name.equals("a") || c.attributes.size() == 1 : "Did not find exactly one attribute for 'a'";
            assert !c.name.equals("b") || c.attributes.size() == 2 : "Did not find the expected two attributes for 'b'";
            assert !c.name.equals("c") || c.attributes.size() == 0 : "Did not expect any attributes for 'c'";
        }
    }
}

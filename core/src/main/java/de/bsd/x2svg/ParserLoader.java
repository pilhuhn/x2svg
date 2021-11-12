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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.bsd.x2svg.parsers.InputParser;

/**
 * This singleton class is used to load the Parsers as plugins.
 * Parsers are expected to live in the package de.bsd.x2svg.parsers
 * and to implement the interface {@link InputParser}.
 * <p/>
 * The loader currently expects that all parsers live in the main
 * jar file (x2svg) within the classpath.
 * <p/>
 * Before the any other methods can be called, it is necessary to at lease
 * once call {@link #load()}.
 *
 * @author hwr@pilhuhn.de
 * @see de.bsd.x2svg.parsers
 * @since 1.0
 */
public class ParserLoader {
    private static final String PACKAGE_STRING = "de/bsd/x2svg/parsers"; //$NON-NLS-1$
    private static volatile ParserLoader theLoader = null;
    private final Map<String, Class<InputParser>> parserByMode = new HashMap<String, Class<InputParser>>();
    private final Map<String, Class<InputParser>> parserBySuffix = new HashMap<String, Class<InputParser>>();
    private final Map<String, String> helpByMode = new HashMap<String, String>();
    private final Map<String, String> modeAndSuffix = new HashMap<String, String>();

    private static boolean debug = false;
    private static final String NO_SPECIFIC_HELP_AVAILABLE = " - no specific help available -";
    private boolean loaded = false;

    /**
     * Private constructor as we are a singleton
     */
    private ParserLoader() {
    }

    /**
     * Get the instance of ParserLoader
     *
     * @return The instance of the ParserLoader
     */
    public static ParserLoader getLoader() {
        if (theLoader == null) {
            theLoader = new ParserLoader();
        }
        return theLoader;
    }

    /**
     * Return a parser for a given mode.
     *
     * @param mode a Mode that needs to be registered by the parser.
     * @return a valid {@link InputParser} if found
     * @throws NoParserException      if the passed mode is invalid or no parser was found
     * @throws IllegalAccessException If the parser class is not accessible
     * @throws InstantiationException If the parser class can not be instantiated
     * @throws IllegalStateException  If {@link #load()} has not yet been called
     */
    public InputParser getParserByMode(String mode) throws NoParserException, IllegalAccessException, InstantiationException {
        if (!loaded)
            throw new IllegalStateException(Messages.getString("ParserLoader.1")); //$NON-NLS-1$
        if (mode == null || mode.equals("")) //$NON-NLS-1$
            throw new NoParserException(Messages.getString("ParserLoader.2") + mode); //$NON-NLS-1$

        Class<InputParser> parser = parserByMode.get(mode);
        if (parser == null)
            throw new NoParserException(Messages.getString("ParserLoader.3") + mode + Messages.getString("ParserLoader.4")); //$NON-NLS-1$ //$NON-NLS-2$
        return parser.newInstance();
    }

    /**
     * Return a parser instance by selecting a matching suffix for the
     * file name.
     *
     * @param filename Name of the file to determine the suffix
     * @return a valid {@link InputParser}
     * @throws NoParserException      if the input is invalid or no parser was found for the passed filename.
     * @throws IllegalAccessException If the parser class is not accessible
     * @throws InstantiationException If the parser class can not be instantiated
     * @throws IllegalStateException  If {@link #load()} has not yet been called
     * @see java.lang.String#endsWith(String)
     */
    public InputParser getParserForFilename(String filename) throws NoParserException, InstantiationException, IllegalAccessException {
        if (!loaded)
            throw new IllegalStateException(Messages.getString("ParserLoader.2")); //$NON-NLS-1$

        if (filename == null || filename.equals("")) //$NON-NLS-1$
            throw new NoParserException(Messages.getString("ParserLoader.6") + filename); //$NON-NLS-1$

        Set<String> suffixes = parserBySuffix.keySet();
        for (String suffix : suffixes) {
            if (filename.endsWith(suffix)) {
                Class<InputParser> parser = parserBySuffix.get(suffix);
                return parser.newInstance();
            }
        }
        throw new NoParserException(Messages.getString("ParserLoader.7") + filename); //$NON-NLS-1$
    }

    /**
     * Load the parsers from the main jar and register them in our list.
     * This method needs to be called <em>before</em> calling one of the
     * getParser*(), listParsers() or getHelpForMode() methods.
     * <p/>
     * We first open the main jar, get the entries in the package
     * de.bsd.x2svg.parsers and then look at those entries if they
     * implement {@link InputParser}. After this, we pass the the
     * matching class to the addToParserList() method, which does the magic
     * for us.
     */
    public void load() {
        try {
            // Get the absolute class name (formatted as a path).
            String classname = "/" + InputParser.class.getName();
            classname = classname.replace('.', '/');
            classname = classname + ".class";

            // Get the filename of a known class in the package we want.
            final URL url = InputParser.class.getResource(classname);
            final String classlocation = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);

            // This the base location of the class.
            String classbase = classlocation.substring(0, classlocation.length() - classname.length());
            if (classbase.endsWith("!")) {
                // IF the base path ends with !, it is inside a jar, strip of the jar: and ! bits/
                classbase = classbase.substring(classbase.indexOf(":") + 1, classbase.length() - 1);
            }

            // classbase is either now a jar (file), or folder.
            File classBaseFile = new File(classbase);
            if (classBaseFile.isDirectory()) {
                // Classes are in a folder.
                File packageFile = new File(classBaseFile, PACKAGE_STRING);
                File[] classFiles = packageFile.listFiles(new ClassNameFileFilter());

                // Iterate the found classes, looking for InputParser classes.
                for (File classFile : classFiles) {
                    String name = classFile.getCanonicalPath();
                    name = name.substring(name.indexOf(PACKAGE_STRING));
                    checkClassIsParser(name);
                }
            } else {
                // Classes are in a jar.
                JarFile jf = new JarFile(classbase); //$NON-NLS-1$
                Enumeration<JarEntry> entries = jf.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith(PACKAGE_STRING) && name.endsWith(".class")) //$NON-NLS-1$
                    {
                        checkClassIsParser(name);
                    }
                }
            }
            loaded = true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Checks if the class passed in is a valid InputParser.
     * This is done by looking at the interfaces of that class.
     *
     * @param classname Name of a class in form of a path
     * @throws ClassNotFoundException If the passed class could not be found in the classpath
     */
    @SuppressWarnings("unchecked")
    private void checkClassIsParser(final String classname) throws ClassNotFoundException {
        if (debug) {
            System.out.println(Messages.getString("ParserLoader.10") + classname + Messages.getString("ParserLoader.11")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        String foundclassname = classname.substring(0, classname.lastIndexOf(".class")); //$NON-NLS-1$
        foundclassname = foundclassname.replace('/', '.'); // create fully qualified name
        Class clazz = Class.forName(foundclassname);
        Class[] interfaces = clazz.getInterfaces();
        for (Class interf : interfaces) {
            if (interf.getName().equals("de.bsd.x2svg.parsers.InputParser")) //$NON-NLS-1$
            {
                if (debug) {
                    System.out.println(Messages.getString("ParserLoader.15")); //$NON-NLS-1$
                }

                addToParserList(clazz);
                break;
            }
        }
    }


    /**
     * Enter the passed class into the lists of parsers
     *
     * @param clazz The main class of an InputParser
     */
    private void addToParserList(Class<InputParser> clazz) {
        try {
            InputParser ip = clazz.newInstance();
            parserByMode.put(ip.getMode(), clazz);
            parserBySuffix.put(ip.getFileSuffix(), clazz);

            String helpString = ip.getSpecificHelp();
            if (helpString == null || helpString.equals(""))
                helpString = NO_SPECIFIC_HELP_AVAILABLE;
            helpByMode.put(ip.getMode(), helpString);
            modeAndSuffix.put(ip.getMode(), ip.getFileSuffix());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * List all parsers that we know about
     *
     * @return A string with a list of parsers separated by newlines
     * @throws IllegalStateException If {@link #load()} has not yet been called
     */
    public String listParsers() {
        if (!loaded)
            throw new IllegalStateException(Messages.getString("ParserLoader.2")); //$NON-NLS-1$

        StringBuffer result = new StringBuffer();
        for (String mode : modeAndSuffix.keySet()) {
            result.append(mode).append(": ").append(modeAndSuffix.get(mode));
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Return the parser specific help for the passed mode.
     * If mode is '*', then help is shown for all parsers.
     *
     * @param mode a valid mode or '*'
     * @return the parser specific help
     * @throws IllegalStateException If {@link #load()} has not yet been called
     */
    public String getHelpForMode(String mode) {
        if (!loaded)
            throw new IllegalStateException(Messages.getString("ParserLoader.2")); //$NON-NLS-1$

        if (helpByMode.containsKey(mode))
            return helpByMode.get(mode);
        else if ("*".equals(mode)) {
            StringBuffer buf = new StringBuffer();
            for (String key : helpByMode.keySet()) {
                buf.append(key).append(":\n");
                buf.append(helpByMode.get(key));
                buf.append("\n\n");
            }
            return buf.toString();
        } else
            return "-no help available for : " + mode + " -";
    }

    /**
     * Filter that accepts files ending in .class
     */
    private static class ClassNameFileFilter implements FilenameFilter {
        public boolean accept(final File dir, final String name) {
            return name.endsWith(".class");
        }
    }

    /**
     * For debugging the ParserLoader separately
     *
     * @param args No command line options needed.
     */
    public static void main(String[] args) {
        debug = true;
        ParserLoader pl = getLoader();
        pl.load();
    }
}

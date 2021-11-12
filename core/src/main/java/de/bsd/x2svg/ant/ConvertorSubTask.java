package de.bsd.x2svg.ant;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import de.bsd.x2svg.outputConverter.ConversionException;
import de.bsd.x2svg.outputConverter.OutputType;
import de.bsd.x2svg.outputConverter.SvgConverter;
import de.bsd.x2svg.util.SantasLittleHelper;

/**
 * The X2SVG conversion sub-task. This task is capable of batch converting
 * sets of SVG documents into output as declared in the parent X2SVG task.
 *
 * @author gfloodgate
 * @since 1.1
 */
public class ConvertorSubTask extends X2SvgSubTask {

    /**
     * The expected element name in ant for tasks instantiated from this class.
     */
    public static final String ELEMENT_NAME = "convertor";


    /**
     * Create a hold for FileSet objects that need to be processed.
     */
    private final ArrayList<FileSet> fileSets = new ArrayList<FileSet>();


    /**
     * Add any <code>FileSet</code> <Code>Type</code> instances
     * that are to be processed by this task.
     *
     * @param fileSet The file set, as from the build.xml that should be used when executing this task.
     */
    public void addFileSet(final FileSet fileSet) {
        // Add the file set that is to be processed.
        fileSets.add(fileSet);
    }


    /**
     * {@inheritDoc}
     */
    public void execute() throws BuildException {
        // Create an instance of the SVG convertor object.
        SvgConverter converter = new SvgConverter();

        for (final FileSet fileSet : fileSets) {
            // For each fileset, iterate the included files.
            final DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
            final File baseDir = scanner.getBasedir();

            final String[] files = scanner.getIncludedFiles();
            for (final String file : files) {
                // For each included file ... check eligability, and parse.
                final File source = new File(baseDir, file);

                // For each required output type, generate an output.
                for (final OutputType type : getOutputTypes()) {
                    // Work out the output file path and name.
                    final File output = new File(SantasLittleHelper.attachSuffixToFileName(source.getAbsolutePath(),
                            type.asFileExtension()));

                    try {
                        // Convert the SVG file to each requested target format.
                        converter.convert(type, source, output);
                    } catch (ConversionException ce) {
                        // There was a problem converting an SVG to a target format.
                        // FOR NOW: We just log an error, in future, we may add an option
                        //          to allow the build to be failed.
                        log("Failed conversion of: " + source.getAbsolutePath() + " error was: " + ce.getMessage());
                    }
                }
            }
        }
    }

}

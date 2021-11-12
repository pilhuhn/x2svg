package de.bsd.x2svg.ant;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import de.bsd.x2svg.Runner;
import de.bsd.x2svg.RuntimeParameters;
import de.bsd.x2svg.outputConverter.OutputFormat;
import de.bsd.x2svg.outputConverter.OutputType;

/**
 * The X2SVG parsing sub-task. This task is responsible for parsing
 * sets of files to create visualised output.
 *
 * @author gfloodgate
 * @since 1.1
 */
public class ParserSubTask extends X2SvgSubTask {

    /**
     * The expected element name in ant for tasks instantiated from this class.
     */
    public static final String ELEMENT_NAME = "parser";


    /**
     * A holder for FileSet objects that need to be processed.
     */
    private final ArrayList<FileSet> fileSets = new ArrayList<FileSet>();

    /**
     * A comment to be added to all generated SVG items.
     */
    private Comment comment;

    /**
     * The parser mode, valid values are: auto, props, dtd, ant.
     */
    private String mode;


    /**
     * Add a comment to be put in each generated file.
     *
     * @param comment The comment to add to the generated outputs.
     */
    public void addComment(final Comment comment) {
        this.comment = comment;
    }


    /**
     * Set the mode that this sub-task should operate in.
     *
     * @param mode The mode that represents the parser type to be used (auto - to detect).
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }


    /**
     * Get the mode that this parser is configured to operate in.
     *
     * @return The mode that represents the parser type to be used (auto - to detect).
     */
    public String getMode() {
        return mode;
    }


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
        // Ensure the this parser task has valid runtime parameters.
        if (getRuntimeParameters() == null) {
            log("Expected runtime parameters to be set for Parser task, setting defaults.", Project.MSG_WARN);
            setRuntimeParameters(new RuntimeParameters());
        }

        // TODO: check mode is set to something that is alllowed
        if (mode == null || "auto".equals(mode.trim())) {
            getRuntimeParameters().setOpMode(null);
        } else {
            getRuntimeParameters().setOpMode(mode);
        }

        for (final FileSet fileSet : fileSets) {
            // For each fileset, iterate the included files.
            final DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
            final File baseDir = scanner.getBasedir();

            final String[] files = scanner.getIncludedFiles();
            for (String file : files) {
                // For each included file ... check eligability, and parse.
                final File source = new File(baseDir, file);

                try {
                    // Create runtime properties for this file, and run the parser.
                    log("Parsing - " + source);
                    final RuntimeParameters localParameters = prepareRuntimeParameters(source);
                    final Runner runner = new Runner();
                    runner.run(localParameters);
                } catch (Exception e) {
                    e.printStackTrace();

                    // If the runner failed, fail the task.
                    throw new BuildException("Unexpected error running parser - " + e.getMessage() + ", " + e.getClass().getName(), e);
                }

            }
        }
    }


    /**
     * Create runtime parameters specific to the file about to be processed, and the
     * required output formats.
     *
     * @param source The source file (thing being parsed) that is being also being converted.
     * @return The runtime parameters specific to this file, and it's required output formats.
     */
    private RuntimeParameters prepareRuntimeParameters(final File source) {
        // Create parameters specific for this file.
        final RuntimeParameters localParameters = new RuntimeParameters(getRuntimeParameters());
        localParameters.setInputFileName(source.getAbsolutePath());
        localParameters.setSvgOutputFile(source.getAbsolutePath() + ".svg");

        if (comment != null) {
            // If there is a comment set, pass it to the output generator.
            localParameters.setComment(comment.getMessage());
        }

        if (getOutputTypes() != null) {
            // For each output type being used ...
            for (final OutputType type : getOutputTypes()) {
                // ... create and output format descriptor.
                final String outputFilename = source.getAbsolutePath() + "." + type.asFileExtension();
                localParameters.addOutputFormat(new OutputFormat(outputFilename, type, false));
            }
        }

        return localParameters;
    }

}

package de.bsd.x2svg.ant;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import de.bsd.x2svg.RuntimeParameters;

/**
 * The main X2SVG task, that is responsible for global settings 
 * management. and execution of parsing sub-tasks.
 * 
 * @author gfloodgate
 * @since 1.1
 */
public class X2SvgTask extends X2SvgBaseTask 
{

	/** Create a hold for sub-task objects that need to be executed. */
	private final ArrayList<X2SvgSubTask> tasks = new ArrayList<X2SvgSubTask>();

    /** The output format type collection. */
    private Output output = null;
	
	/** The global runtime properties used by all sub-tasks. */
	private RuntimeParameters runtimeParameters = new RuntimeParameters();
	
	
	/**
	 * Is debug (verbose output mode) enabled for this task ?
	 * 
	 * @return true, If debug (verbose mode) should be enabled for this task, false otherwise.
	 */
	public boolean isDebugEnabled()
	{
		return runtimeParameters.isDebug();
	}
	
	
	/**
	 * Set wether or not debug mode should be activated.
	 * 
	 * @param isDebugEnabled true, If debug should be enabled, false otherwise.
	 */
	public void setDebugEnabled(final boolean isDebugEnabled)
	{
		runtimeParameters.setDebug(isDebugEnabled);
	}
    
    
    /**
     * Get the x2svg properties location.
     * 
     * @return Get location of the properties file specified globally.
     */
    public String getPropertiesLocation()
    {
       return runtimeParameters.getPropertiesLocation(); 
    }
    
    
    /**
     * Set the x2svg properties location.
     * 
     * @param propertiesLocation The location of the x2svg.properties file to use.
     */
    public void setPropertiesLocation(final String propertiesLocation)
    {
        runtimeParameters.setPropertiesLocation(propertiesLocation);
    }
    
    
    /**
     * Add an <code>Output<code> instance as the collection
     * of output formats to be used by X2SVG.
     * 
     * @param output The output conversion type instance.
     */
    public void addConfiguredOutput(final Output output)
    {
        this.output = output;
    }

    
    /**
     * Add a <code>Parser</code> instance as a task to execure.
     * 
     * @param task The prepared task, ready for execution later.
     */
    public void addConfiguredParser(final ParserSubTask task)
    {
        log("Adding parser task to list.", Project.MSG_INFO);
        task.setRuntimeParameters(runtimeParameters);
        task.setOutputTypes(output.getOutputTypes());
        tasks.add(task);
    }
    
    
    /**
     * Add a <code>Convertor</code> instance as a task to execure.
     * 
     * @param task The prepared task, ready for execution later.
     */
    public void addConfiguredConvertor(final ConvertorSubTask task)
    {
        log("Adding convertor task to list.", Project.MSG_INFO);
        task.setRuntimeParameters(runtimeParameters);
        task.setOutputTypes(output.getOutputTypes());
        tasks.add(task);
    }
	
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws BuildException 
	{
		log("Starting X2Svg ...");
        log("Debug enabled: " + String.valueOf(runtimeParameters.isDebug()));
        if (runtimeParameters.getPropertiesLocation() != null)
        {    
            log("Properties location: " + new File(runtimeParameters.getPropertiesLocation()).getAbsolutePath());
        }
        
        if (output == null)
        {
            // If there is no output conversion type, implicitly create one.
            output = new Output();
        }

		for (final Task task : tasks)
		{
			// Iterate each task, and execute(perform) it.
			task.perform();
		}
		
		log("Finished X2Svg.");
	}
	
}

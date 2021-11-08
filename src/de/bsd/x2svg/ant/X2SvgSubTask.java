package de.bsd.x2svg.ant;

import java.util.HashSet;
import java.util.Set;

import de.bsd.x2svg.RuntimeParameters;
import de.bsd.x2svg.outputConverter.OutputType;


/**
 * The base task that is used by all X2SVG sub-tasks for common functionality.
 * 
 * @author gfloodgate
 * @since 1.1
 */
public abstract class X2SvgSubTask extends X2SvgBaseTask
{

    /** The base runtime parameters for this sub-task. */
    private RuntimeParameters runtimeParameters;
    
    /** The collection of output types to also convert to. */
    private Set<OutputType> types;

    
    /**
     * Set the base runtime parameters to be used by this task.
     * <p>
     * NB: This setter makes a copy of the supplied instance. It does
     * not keep a reference to the parameters. This allows for heirarchical
     * addition of new parameters down the tree.
     * </p>
     * 
     * @param runtimeParameters The runtime parameters to use.
     */
    public void setRuntimeParameters(final RuntimeParameters runtimeParameters)
    {
    	this.runtimeParameters = new RuntimeParameters(runtimeParameters);
    }

    
    /**
     * Get the runtime parameters intended for this class, or it's subtypes.
     * 
     * @return The runtime parameters for this task instance.
     */
    protected RuntimeParameters getRuntimeParameters()
    {
        return runtimeParameters;
    }
    
    
    /**
     * Set the output types that task output will also be converted to.
     * <p>
     * NB: This setter makes a copy of the supplied instance. It does
     * not keep a reference to the Set. This allows for heirarchical
     * addition of new output types down the tree.
     * </p>
     * 
     * @param types The set of output types to convert output to.
     */
    public void setOutputTypes(final Set<OutputType> types)
    {
        this.types = new HashSet<OutputType>(types);
    }
	
    
	/**
     * Get the output types that this task will convert to.
     * 
	 * @return The set of output types this task is using.
	 */
	protected Set<OutputType> getOutputTypes()
    {
        return types;
    }

}

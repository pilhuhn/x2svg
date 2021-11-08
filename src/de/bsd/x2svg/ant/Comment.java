package de.bsd.x2svg.ant;

import org.apache.tools.ant.types.DataType;

/**
 * A data type that is used to represent a comment to be applied to a set of generated diagrams.
 * 
 * @author gfloodgate
 * @since 1.1
 */
public class Comment extends DataType
{

	/** The message that is to be used in the comment. */
	private String message;
    
	
	/**
	 * Get the message to be used in the comment.
	 * 
	 * @return The comment message to be used.
	 */
	public String getMessage()
	{
		return message;
	}
	
	
	/**
	 * Set the message to be used in the comment.
	 * 
	 * @param message The message to be used in the comment.
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}
	
}

/**
 * 
 */
package de.bsd.x2svg.outputConverter;

/**
 * This class represents an output file plus its type.
 *
 * @author hwr@pilhuhn.de
 * @since 1.1
 */
public class OutputFormat 
{
	private String fileName;
	private String directory;
	private OutputType type;
	private boolean noFile = false;

	/**
	 * Construct the output format and tries to guess the
	 * desired format by the file name suffix
	 * 
	 * @param fileName the name of the output file
	 */
	public OutputFormat(String fileName) {
		this.fileName = fileName;
		for (OutputType oType : OutputType.values())
		{
			if ((fileName.toUpperCase()).endsWith("." + oType.name())) {
				type = oType;
				break;
			}
		}
		if (type==null)
			System.err.println("Unknown output type. File will be ignored");
	}


	/**
	 * Construct the output format
	 * @param fileName the name of the output file
	 * @param type the type of the output
	 * @param isDirectory Is the passed fileName a real file or a directory?
	 */
	public OutputFormat(String fileName, OutputType type, boolean isDirectory) {
		if (isDirectory) {
			directory = fileName;
			noFile = true;
		}
		else
			this.fileName = fileName;
		this.type = type;
	}

	/**
	 * Construct the output format partially.
	 * @param type the type of the output
	 */
	public OutputFormat(OutputType type) {
		this.type = type;
		noFile = true;
	}

	/**
	 * Returns the name of the output file
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}


	/**
	 * Return the file/converter type
	 * @return the type
	 */
	public OutputType getType() {
		return type;
	}
	
	/**
	 * Sets the file name. Should only be used when the file name was
	 * not given in the constructor.
	 * @param name file name
	 * @throws IllegalStateException if we already have a file present
	 */
	public void updateFileName(String name) {
		if (noFile==false)
			throw new IllegalStateException("Only use this method if the file was not yet set");
		
		if (directory!=null) 
			fileName = directory + name;
		else
			fileName = name;
	}
}

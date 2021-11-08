/**
 * 
 */
package de.bsd.x2svg.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Input file filter that is used in a file chooser to 
 * limit the files displayed
 * @author hwr@pilhuhn.de
 *
 */
public class X2SvgInputFileFilter extends FileFilter 
{

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) 
	{
		if (f.isDirectory())
			return true;
		// TODO loop over InputParsers to determine this ...
		String fileName = f.getName();
		if (fileName.endsWith("build.xml")) //$NON-NLS-1$
			return true;
		if (fileName.endsWith(".properties")) //$NON-NLS-1$
			return true;
		if (fileName.endsWith(".dtd")) //$NON-NLS-1$
			return true;
		if (fileName.endsWith(".xsd")) //$NON-NLS-1$
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.getString("X2SvgInputFileFilter.0"); //$NON-NLS-1$
	}

}

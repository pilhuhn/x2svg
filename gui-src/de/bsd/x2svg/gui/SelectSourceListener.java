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
package de.bsd.x2svg.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Action that fires a file chooser to select the input
 * files for rendering.
 * 
 * @author hwr@pilhuhn.de
 * @see X2SvgInputFileFilter
 */
public class SelectSourceListener implements ActionListener 
{

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JFileChooser jf = new JFileChooser();
		jf.setDialogTitle(Messages.getString("SelectSourceListener.0")); //$NON-NLS-1$
		jf.setDialogType(JFileChooser.OPEN_DIALOG);
		FileFilter fifi = new X2SvgInputFileFilter(); 
		jf.setFileFilter(fifi);
		jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = jf.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File selected = jf.getSelectedFile();
	       final String selectedFile = selected.getAbsolutePath();
	       jf.getSelectedFile().getAbsolutePath();
	       JButton jb = (JButton) e.getSource();
	       Container pan =  jb.getParent();
	       Component c = pan.getComponent(1);
	       JTextField jt = (JTextField) c;
	       jt.selectAll();
	       jt.setText(selectedFile);
	    }
	}

}

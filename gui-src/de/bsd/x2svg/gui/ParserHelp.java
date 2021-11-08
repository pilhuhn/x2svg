/*******************************************************************************
 * Copyright (c) 2007,2008 Heiko W. Rupp. 	All rights reserved. 
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

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import de.bsd.x2svg.ParserLoader;

/**
 * Show the parser specific help in the gui
 * @author hwr@pilhuhn.de
 *
 */
public class ParserHelp extends JFrame {

	private static final long serialVersionUID = 1L;

	ParserHelp() 
	{
		Container outerCont = getContentPane();
		JPanel cont = new JPanel();
		outerCont.add(cont);
		JTextArea textArea = new JTextArea();
		
		ParserLoader pl = ParserLoader.getLoader();
		pl.load();
		String help = pl.getHelpForMode("*");
		textArea.setText(help);
		cont.add(textArea);
		pack();
		setTitle(Messages.getString("X2SvgGui.21"));
	}
}

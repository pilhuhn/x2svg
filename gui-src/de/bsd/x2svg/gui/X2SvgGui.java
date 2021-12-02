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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.bsd.x2svg.Runner;
import de.bsd.x2svg.RuntimeParameters;
import de.bsd.x2svg.output_converter.OutputFormat;
import de.bsd.x2svg.output_converter.OutputType;
import de.bsd.x2svg.util.SantasLittleHelper;

/**
 * Main class of the simple Swing GUI for X2svg.
 * @author hwr@pilhuhn.de
 */
public class X2SvgGui extends JFrame implements ActionListener
{
	/**
	 * Serial version UUID, as this class is serializable, even if don't
	 * intend to use it ever that way.
	 */
	private static final long serialVersionUID = 1L;


	/** Put all elements on the Gui frame */
	public X2SvgGui()
	{
		Container outerCont = getContentPane();
		JPanel cont = new JPanel();
		cont.setBorder(new EmptyBorder(10,10,10,10));
		cont.setLayout(new GridLayout(7,3,5,5));
		outerCont.add(cont);

		JLabel jl = new JLabel(Messages.getString("X2SvgGui.0"), SwingConstants.LEFT); //$NON-NLS-1$
		cont.add(jl);
		JTextField tf = new JTextField("sample.dtd", 20); //$NON-NLS-1$
		cont.add(tf);
		JButton jb = new JButton("..."); //$NON-NLS-1$
		jb.setToolTipText(Messages.getString("X2SvgGui.3")); //$NON-NLS-1$
		jb.addActionListener(new SelectSourceListener());
		cont.add(jb);

		JLabel outLabel = new JLabel(Messages.getString("X2SvgGui.4"), SwingConstants.LEFT); //$NON-NLS-1$
		cont.add(outLabel);
		JTextField outFile = new JTextField("sample.svg", 20); //$NON-NLS-1$
		cont.add(outFile);
		JButton jb2 = new JButton("..."); //$NON-NLS-1$
		jb2.addActionListener(new SelectSvgOutputListener());
		jb2.setToolTipText(Messages.getString("X2SvgGui.7")); //$NON-NLS-1$
		cont.add(jb2);

		JCheckBox checkBox = new JCheckBox(Messages.getString("X2SvgGui.8"), false); //$NON-NLS-1$
		checkBox.setToolTipText(Messages.getString("X2SvgGui.9")); //$NON-NLS-1$
		cont.add(checkBox);

		Vector<String> items = new Vector<String>();
		items.add(Messages.getString("X2SvgGui.10")); //$NON-NLS-1$
		for (OutputType type : OutputType.values()) {
			items.add(type.asFileExtension());
		}
		JComboBox combo = new JComboBox(items);
		cont.add(combo);


		JLabel empty = new JLabel(""); //$NON-NLS-1$
		cont.add(empty);

		//-----------------

		// TODO read the next from properties
		JCheckBox attrCheckBox = new JCheckBox(Messages.getString("X2SvgGui.22")); //$NON-NLS-1$
		cont.add(attrCheckBox);
		JCheckBox commCheckBox = new JCheckBox(Messages.getString("X2SvgGui.23")); //$NON-NLS-1$
		cont.add(commCheckBox);
		JLabel empty4 = new JLabel(""); //$NON-NLS-1$
		cont.add(empty4);

		//-----------------

		JLabel optionLabel = new JLabel(Messages.getString("X2SvgGui.19")); //$NON-NLS-1$
		cont.add(optionLabel);

		JTextField optionsField = new JTextField();
		cont.add(optionsField);

		// show a button that displays help
		JButton helpButton = new JButton(Messages.getString("X2SvgGui.20")); //$NON-NLS-1$
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserHelp ph = new ParserHelp();
				ph.setLocation(300,300);
				ph.pack();
                ph.setVisible(true);
			}
		});
		cont.add(helpButton);

		//-----------------

		JLabel commentLabel = new JLabel(Messages.getString("X2SvgGui.11")); //$NON-NLS-1$
		cont.add(commentLabel);

		JTextField commentField = new JTextField();
		cont.add(commentField);

		JCheckBox commentBox = new JCheckBox(Messages.getString("X2SvgGui.12"), false);  //$NON-NLS-1$
		commentBox.setToolTipText(Messages.getString("X2SvgGui.13")); //$NON-NLS-1$
		cont.add(commentBox);

		JLabel empty3 = new JLabel(""); //$NON-NLS-1$
		cont.add(empty3);
		JCheckBox resultBox = new JCheckBox(Messages.getString("X2SvgGui.17")); //$NON-NLS-1$
		resultBox.setToolTipText(Messages.getString("X2SvgGui.18")); //$NON-NLS-1$
		cont.add(resultBox);

		JButton go = new JButton(Messages.getString("X2SvgGui.14")); //$NON-NLS-1$
		go.addActionListener(this);
		go.setToolTipText(Messages.getString("X2SvgGui.15")); //$NON-NLS-1$
		cont.add(go);

	}

	/**
	 * Start the show
	 * @param args none needed
	 */
	public static void main(String[] args)
	{

		X2SvgGui x = new X2SvgGui();
		Dimension d = new Dimension(450,260);
		x.setLocation(100,100);
		x.pack();
		x.setVisible(true);
		x.setPreferredSize(d);
		x.setSize(d);
		x.setResizable(false);
		x.setTitle(Messages.getString("X2SvgGui.16")); //$NON-NLS-1$
	}

	/**
	 * This one is called when the 'Go...' button is pressed
	 * TODO rewrite to make it easier to understand what is going on
	 */
	public void actionPerformed(ActionEvent e)
	{
		JButton jb = (JButton) e.getSource();
	    Container pan =  jb.getParent();
	    JTextField jt = (JTextField)pan.getComponent(1);
		String input = jt.getText();
		jt = (JTextField)pan.getComponent(4);
		String svgOut = jt.getText();
		jt = (JTextField)pan.getComponent(16);
		String comment = jt.getText();


		JCheckBox cb = (JCheckBox)pan.getComponent(6);
		String outFormat=null;
		if (cb.isSelected())
		{
			JComboBox jc = (JComboBox) pan.getComponent(7);
			outFormat = (String) jc.getSelectedItem();
			if (outFormat.equals(Messages.getString("X2SvgGui.10"))) //$NON-NLS-1$
				outFormat=null;
		}

		RuntimeParameters params = new RuntimeParameters();
		params.setInputFileName(input);
		params.setSvgOutputFile(svgOut);
		if (outFormat!=null)
		{
			String outFile = SantasLittleHelper.attachSuffixToFileName(svgOut, outFormat);
			OutputFormat of = new OutputFormat(outFile);
			params.addOutputFormat(of);
		}

		JTextField optionsField = (JTextField)pan.getComponent(13);
		String options = optionsField.getText();
		if (options!=null && options.length()>0) {
			params.setParserSpecificOptions(options.split(" ")); //$NON-NLS-1$
		}


		JCheckBox ccb = (JCheckBox)pan.getComponent(17);
		if (ccb.isSelected())
		{
			params.setComment(null); // Use default
		}
		else if (comment != null)
		{
			params.setComment(comment);
		}

		boolean showResult = false;
		JCheckBox resultBox = (JCheckBox)pan.getComponent(19);
		if (resultBox.isSelected())
			showResult = true;

		JCheckBox attrParseBox = (JCheckBox)pan.getComponent(9);
		if (attrParseBox.isSelected())
			params.setWithAttributes(true);
		JCheckBox commentParseBox = (JCheckBox)pan.getComponent(10);
		if (commentParseBox.isSelected())
			params.setWithElementComments(true);


		Runner runner = new Runner();
		try {
			runner.run(params);
			if (showResult) {
				SVGDisplayFrame frame = new SVGDisplayFrame(params);
				frame.setVisible(true);
			}

		} catch (Exception e1) {
			// display a popup
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
		}

	}

}

/*
 * $Id: AboutBox.java,v 1.9 2005/09/25 19:28:32 bpfoley Exp $
 * Copyright 2001 Phil C. Mueller
 *
 * This file is part of the Warwick maze courseware.
 *
 * warwickmaze is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * warwickmaze is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with warwickmaze; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * http://warwickmaze.sourceforge.net/
 */
 
package uk.ac.warwick.dcs.maze.gui;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyListener;
import java.awt.event.ContainerListener;
import java.awt.event.ContainerEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

/**
 *
 * @author Phil C. Mueller
 */
public class AboutBox extends JFrame implements ActionListener
{
	private static final String OK_CMD = "OK";
	
	private JPanel aboutPanel, closePanel;
	private JTextArea aboutText;
	private JButton okButton;
	
	/* ActionListener methods */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(OK_CMD)) {
			this.setVisible(false);
		}
	}

	/** Creates new form AboutBox */
	public AboutBox(JFrame frame, Properties props) {
		initComponents();
		
		Image icon = null;
		URL iconURL = ClassLoader.getSystemResource("mazeicon.gif");

		if (iconURL != null)
			icon = Toolkit.getDefaultToolkit().getImage(iconURL);

		setIconImage(icon);
		setSize(320, 360);

		aboutText.setText(
			"University of Warwick CS118 Maze Courseware\n"
				+ "Version " + props.getProperty("version") + "\n"
				+ "Built on " + props.getProperty("build") + "\n\n"
				+ "Developers:\n"
				+ "Phil C. M\u00fcller\n"
				+ "Brian P. Foley\n"
				+ "John Fearnley\n"
				+ "\n"
				+ "For updates, visit:\n"
				+ props.getProperty("site") + "\n\n"
				+ "\u00A9 2001-2005 University of Warwick, UK\n"
				+ "Look and feel by Incors GmbH (www.incors.com)\n\n"
				+ "Comments and bug reports are welcome.\n"
				+ "See the project website for more details.\n\n");
				
		setLocation(frame.getLocationOnScreen());
		this.setLocation(
			frame.getLocationOnScreen().x
				+ frame.getWidth() / 2
				- getWidth() / 2,
			frame.getLocationOnScreen().y
				+ frame.getHeight() / 2
				- getHeight() / 2);

	}

	private void initComponents() {
		aboutPanel = new JPanel();
		aboutText = new JTextArea();
		closePanel = new JPanel();
		okButton = new JButton();

		setTitle("About");
		setBackground(Color.white);

		aboutPanel.setLayout(new GridLayout(1, 0));

		aboutPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		aboutPanel.setBackground(java.awt.Color.white);
		Dimension d = new Dimension(300, 300);
		aboutPanel.setPreferredSize(d);
		aboutPanel.setMinimumSize(d);
		aboutPanel.setMaximumSize(d);
		aboutText.setLineWrap(true);
		aboutText.setEditable(false);
		aboutPanel.add(aboutText);

		getContentPane().add(aboutPanel, BorderLayout.CENTER);

		closePanel.setBackground(Color.white);
		
		okButton.setText("OK");
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setActionCommand(OK_CMD);
		okButton.addActionListener(this);
		
		closePanel.add(okButton);
		
		getContentPane().add(closePanel, BorderLayout.SOUTH);
		
		// Set OK to be the default & the cancel buttons
		getRootPane().setDefaultButton(okButton);
		getRootPane().registerKeyboardAction(this, OK_CMD,
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		pack();
		
		okButton.requestFocusInWindow();
	}
}


/*
 * $Id: MazeControlPanel.java,v 1.6 2005/09/23 17:44:42 bpfoley Exp $
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.warwick.dcs.maze.gui.RobotControlPanel;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

/**
 *
 * @author  Phil C. Mueller
 */
public class MazeControlPanel extends javax.swing.JPanel implements IEventClient, ActionListener {
	
	private static final String CLOSE_CMD = "CLOSE";
   	private IMazeGenerator currentGenerator;
   	private JFrame advancedFrame;
    private RobotControlPanel robotControlPanel;
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE_CMD)) {
			advancedFrame.setVisible(false);
		}
	}
	
    public MazeControlPanel() {
       initComponents();
       EventBus.addClient(this);
    }

    private void initComponents() {
       robotControlPanel = new RobotControlPanel();
       setLayout(new java.awt.GridLayout(1, 0));
       add(robotControlPanel);
    }

    public void notify(IEvent event) {
       int message = event.getMessage();
       if (message == IEvent.ADVANCED_CONFIG_SHOW) {
          //MazeApp.printDebug(this, "Showing advanced config");
          if (currentGenerator != null) {
             JPanel configPanel = currentGenerator.getConfigurator();
             
             if (configPanel == null) {
                String genName = currentGenerator.getClass().getName();
                genName = genName.substring(genName.lastIndexOf('.')+1);
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "The Generator \""+genName+"\" is not configurable.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
             
             advancedFrame = new JFrame();
             advancedFrame.setTitle(currentGenerator.getDescription());
             advancedFrame.getContentPane().setLayout(new java.awt.BorderLayout());
             advancedFrame.getContentPane().add(configPanel, java.awt.BorderLayout.CENTER);
             JPanel panel = new JPanel();
             panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
             JButton closeButton = new JButton("Close");
			 closeButton.setMnemonic(KeyEvent.VK_C);
			 closeButton.setActionCommand(CLOSE_CMD);
             closeButton.addActionListener(this);
             panel.add(closeButton);
			 
             advancedFrame.getContentPane().add(panel, java.awt.BorderLayout.SOUTH);
             advancedFrame.pack();
             advancedFrame.setLocation(getTopLevelAncestor().getLocation().x + getTopLevelAncestor().getWidth()/2 - advancedFrame.getWidth()/2,
                  getTopLevelAncestor().getLocation().y + getTopLevelAncestor().getHeight()/2 - advancedFrame.getHeight()/2);
             advancedFrame.setVisible(true);
			 
			 advancedFrame.getRootPane().setDefaultButton(closeButton);
			 advancedFrame.getRootPane().registerKeyboardAction(this, CLOSE_CMD,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
			
          }
       }
       else if (message == IEvent.CURRENT_GENERATOR) {
          currentGenerator = (IMazeGenerator)event.getData();
       }
    }
}


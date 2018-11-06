/*
 * $Id: CurrentClassInfo.java,v 1.5 2005/11/28 17:52:48 bpfoley Exp $
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

import javax.swing.JPanel;
import javax.swing.JTextField;

import uk.ac.warwick.dcs.maze.controllers.PolledControllerWrapper;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobotController;

/**
 *
 * @author Phil C. Mueller
 */
public class CurrentClassInfo extends javax.swing.JPanel implements IEventClient {

	private JPanel genPanel, contPanel;
	private JTextField genClass, genName;
	private JTextField contClass, contName, contRuns;

	private int runs;
   
	public CurrentClassInfo() {
		initComponents();
		EventBus.addClient(this);
	}
   
    private void initComponents() {
       genPanel = new JPanel();
       genClass = new JTextField();
       genName = new JTextField();
       contPanel = new JPanel();
       contClass = new JTextField();
       contName = new JTextField();
       contRuns = new JTextField();
       
       setLayout(new java.awt.GridBagLayout());
       java.awt.GridBagConstraints gridBagConstraints1;
       
       setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(20, 5, 10, 5)));
       genPanel.setLayout(new java.awt.GridLayout(2, 0));
       
       genPanel.setBorder(new javax.swing.border.TitledBorder("Current Generator"));
       genClass.setEditable(false);
       genPanel.add(genClass);
       
       genName.setEditable(false);
       genName.setFont(new java.awt.Font("Dialog", 0, 10));
       genPanel.add(genName);
       
       gridBagConstraints1 = new java.awt.GridBagConstraints();
       gridBagConstraints1.gridx = 0;
       gridBagConstraints1.gridy = 0;
       gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
       gridBagConstraints1.weightx = 1.0;
       gridBagConstraints1.weighty = 1.0;
       add(genPanel, gridBagConstraints1);
       
       contPanel.setLayout(new java.awt.GridLayout(3, 0));
       
       contPanel.setBorder(new javax.swing.border.TitledBorder("Current Controller"));
       contPanel.setToolTipText("");
       contClass.setEditable(false);
       contPanel.add(contClass);
       
       contName.setEditable(false);
       contName.setFont(new java.awt.Font("Dialog", 0, 10));
       contPanel.add(contName);
       
       contRuns.setEditable(false);
       contRuns.setFont(new java.awt.Font("Dialog", 0, 10));
       contPanel.add(contRuns);
       
       gridBagConstraints1 = new java.awt.GridBagConstraints();
       gridBagConstraints1.gridx = 0;
       gridBagConstraints1.gridy = 1;
       gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
       gridBagConstraints1.weightx = 1.0;
       gridBagConstraints1.weighty = 1.0;
       add(contPanel, gridBagConstraints1);
       
    }
  
    public void notify(IEvent ievent) {
       int i = ievent.getMessage();
       switch(i) {
          case IEvent.ROBOT_CHANGE_HEADING: // 'h'
          case IEvent.ROBOT_HEADING_CHANGED: // 'i'
          case IEvent.ROBOT_FINISHED: // 'j'
          case IEvent.ADD_CONTROLLER: // 'm'
          default:      break;

          case IEvent.CURRENT_GENERATOR: // 'n'
             String s = ievent.getData().getClass().getName();
             genClass.setText(s.substring(s.lastIndexOf('.') + 1));
             genName.setText(((IMazeGenerator)ievent.getData()).getDescription());
             break;
          case IEvent.CURRENT_CONTROLLER: // 'l'
             IRobotController irobotcontroller = (IRobotController)ievent.getData();
             String s1 = irobotcontroller.getClass().getName();
             if(irobotcontroller instanceof PolledControllerWrapper)
                s1 = ((PolledControllerWrapper)irobotcontroller).getControlObject().getClass().getName();
             contClass.setText(s1.substring(s1.lastIndexOf('.') + 1));
             contName.setText(((IRobotController)ievent.getData()).getDescription());
             setRuns(0);
             break;
             
          case IEvent.NEW_MAZE: // 'k'
             setRuns(0);
             break;
             
          case IEvent.ROBOT_RESET: // 'g'
             setRuns(runs + 1);
             break;
       }
    }
    
    private void setRuns(int i) {
		if (i==1) {
			contRuns.setText(i + " Run");
		} else {
			contRuns.setText(i + " Runs");
		}
    }
}

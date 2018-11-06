/*
 * $Id: RobotControlPanel.java,v 1.10 2005/09/25 19:28:32 bpfoley Exp $
 * Copyright 2001 Phil C. Mueller
 * Copyright 2004 Brian P. Foley
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

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.ac.warwick.dcs.maze.logic.Event;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobotController;
import uk.ac.warwick.dcs.maze.logic.Maze;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
/**
 *
 * @author  Phil C. Mueller
 * @author  Brian P. Foley
 */
public class RobotControlPanel extends JPanel implements IEventClient, ChangeListener, ActionListener
{
	
    private JButton advancedButton;
    private JLabel delayLabel;
    private JSlider delaySlider;
    private JButton loadMazeButton;
    private JButton newMazeButton;
    private JButton resetButton;
    private JButton saveMazeButton;
    private JButton startButton;
    
	/* Actions performed when a button is pressed etc */
	public void actionPerformed(ActionEvent e)
	{
		try {
		int i = Integer.parseInt(e.getActionCommand());
		EventBus.broadcast(new Event(i, null));
		} catch (NumberFormatException nfe) {
			// Do nothing... the actionCommand wasn't set up.
		}
	}
	

   private IRobotController controller;
   private IMazeGenerator generator;
   private Maze maze;
   
   /** Creates new form RobotControlPanel */
    public RobotControlPanel() {
        initComponents();
        EventBus.addClient(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        startButton = new JButton();
        resetButton = new JButton();
        newMazeButton = new JButton();
        advancedButton = new JButton();
        delaySlider = new JSlider();
        delayLabel = new JLabel();
        loadMazeButton = new JButton();
        saveMazeButton = new JButton();

        setLayout(new java.awt.GridBagLayout());

        startButton.setText("Begin");
		startButton.setMnemonic(KeyEvent.VK_B);
        startButton.setEnabled(true);
        startButton.setActionCommand(Integer.toString(IEvent.ROBOT_START));
        startButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(startButton, gridBagConstraints);

        resetButton.setText("Reset");
		resetButton.setMnemonic(KeyEvent.VK_R);
        resetButton.setEnabled(false);
		resetButton.setActionCommand(Integer.toString(IEvent.ROBOT_RESET_REQUEST));
        resetButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(resetButton, gridBagConstraints);

        newMazeButton.setText("New Maze");
		newMazeButton.setMnemonic(KeyEvent.VK_N);
        newMazeButton.setEnabled(false);
		newMazeButton.setActionCommand(Integer.toString(IEvent.GENERATE_MAZE_REQUEST));
        newMazeButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(newMazeButton, gridBagConstraints);

        advancedButton.setText("Advanced...");
		advancedButton.setMnemonic(KeyEvent.VK_V);
        advancedButton.setEnabled(false);
		advancedButton.setActionCommand(Integer.toString(IEvent.ADVANCED_CONFIG_SHOW));
        advancedButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        add(advancedButton, gridBagConstraints);
		
        delaySlider.setBorder(new EmptyBorder(new Insets(8, 8, 0, 8)));
        delaySlider.setEnabled(false);
		delaySlider.getModel().setRangeProperties(10, 0, 0, 20, false);
        delaySlider.setMajorTickSpacing(5);
        delaySlider.setMinorTickSpacing(1);
		delaySlider.setPaintTicks(true);
		delaySlider.setSnapToTicks(true);
        
        delaySlider.addChangeListener(this);
		Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer(0), new JLabel("0s"));
        labelTable.put( new Integer(5), new JLabel(".25s"));
        labelTable.put( new Integer(10), new JLabel(".5s"));
        labelTable.put( new Integer(15), new JLabel(".75s"));
        labelTable.put( new Integer(20), new JLabel("1s"));
		delaySlider.setLabelTable(labelTable);
        delaySlider.setPaintLabels(true);
		
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        add(delaySlider, gridBagConstraints);
		
        delayLabel.setForeground(java.awt.Color.lightGray);
        delayLabel.setText("Robot Delay");
		delayLabel.setLabelFor(delaySlider);
		delayLabel.setDisplayedMnemonic(KeyEvent.VK_D);
        delayLabel.setBorder(new EmptyBorder(new Insets(0, 0, 10, 0)));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        add(delayLabel, gridBagConstraints);

        loadMazeButton.setText("Load Maze...");
		loadMazeButton.setMnemonic(KeyEvent.VK_O);
		loadMazeButton.setActionCommand(Integer.toString(IEvent.LOAD_MAZE));
        loadMazeButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(loadMazeButton, gridBagConstraints);

        saveMazeButton.setText("Save Maze...");
		saveMazeButton.setMnemonic(KeyEvent.VK_S);
		saveMazeButton.setActionCommand(Integer.toString(IEvent.SAVE_MAZE));
        saveMazeButton.setEnabled(false);
        saveMazeButton.addActionListener(this);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(saveMazeButton, gridBagConstraints);

    }


    public void notify(IEvent event) {
       int message = event.getMessage();
       if (message == IEvent.ROBOT_START) {
          resetButton.setEnabled(true);
          startButton.setEnabled(false);
          newMazeButton.setEnabled(false);
          advancedButton.setEnabled(false);
       }
       else if (message == IEvent.ROBOT_RESET) {
          startButton.setEnabled(true);
          newMazeButton.setEnabled(true);
          advancedButton.setEnabled(true);
          resetButton.setEnabled(false);
       }
       else if (message == IEvent.CURRENT_CONTROLLER ) {
          this.controller = (IRobotController)event.getData();
          delaySlider.setEnabled(true);
          delaySlider.setValue(controller.getDelay());
          delayLabel.setForeground(java.awt.Color.black);
          if (maze != null)
             startButton.setEnabled(true);
       }
       else if (message == IEvent.NEW_MAZE) {
          this.maze = (Maze)event.getData();
          if (controller != null) {
             startButton.setEnabled(true);
          }
		  saveMazeButton.setEnabled(true);
       }
       else if (message == IEvent.CURRENT_GENERATOR ) {
          generator = (IMazeGenerator)event.getData();
          newMazeButton.setEnabled(true);
          advancedButton.setEnabled(true);
       }
    }

    public void stateChanged(ChangeEvent changeEvent) {
       // slider has moved.
       Event event = new Event(IEvent.DELAY, new Integer(delaySlider.getValue() * 50));
       EventBus.broadcast(event);
    }

}


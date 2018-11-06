/*
 * $Id: ControllerManager.java,v 1.9 2005/11/28 17:52:48 bpfoley Exp $
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
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import uk.ac.warwick.dcs.maze.controllers.PolledControllerWrapper;
import uk.ac.warwick.dcs.maze.controllers.RandomRobotController;
import uk.ac.warwick.dcs.maze.logic.ControllerPool;
import uk.ac.warwick.dcs.maze.logic.Event;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IRobotController;
import uk.ac.warwick.dcs.maze.logic.InvalidControllerException;
import java.awt.event.KeyEvent;
/**
 * Three different types of controllers: System (defined by FQCN), implementing
 * IRobotController, or simply implementing a controlRobot(IRobot robot) method.
 *
 * @author Phil C. Mueller
 */
public class ControllerManager extends javax.swing.JPanel implements IEventClient, ListSelectionListener, ActionListener
{
	private static final String ADD_CMD = "ADD";
	private static final String RELOAD_CMD = "RELOAD";
	private File lastDirectory = new File(System.getProperty("user.dir"));
	private JScrollPane jScrollPane1;
	private JButton addButton, reloadButton;
	private JList ctrlList;
	private JPanel buttonPanel;

	/* ActionListener methods */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(RELOAD_CMD)) {
			reloadController(ctrlList.getSelectedIndex());
			return;
		}
		if (e.getActionCommand().equals(ADD_CMD)) {
			selectNewController();
			return;
		}
	}

   private ControllerPool cPool;

   /** Creates new form ControllerManager */
	public ControllerManager(ControllerPool pool)
	{
		cPool = pool;
		initComponents();
		ctrlList.setModel(cPool);
		ctrlList.addListSelectionListener(this);

		// Find the last pre-loaded controller.
		int i = ctrlList.getModel().getSize() - 1;

		// Load the default robot controller
      	//RandomRobotController rrc = new RandomRobotController();
      	//cPool.addController(rrc, null);

		// Select the last pre-loaded controller.
		if (i != -1) ctrlList.setSelectedIndex(i);

		EventBus.addClient(this);
	}

   private void initComponents() {
      jScrollPane1 = new JScrollPane();
      ctrlList = new JList();
      buttonPanel = new JPanel();
      addButton = new JButton();
      reloadButton = new JButton();

      setLayout(new java.awt.BorderLayout());

      setBorder(new javax.swing.border.TitledBorder("Robot Controllers"));
      jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      ctrlList.setBorder(new javax.swing.border.EtchedBorder());
      ctrlList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
      jScrollPane1.setViewportView(ctrlList);

      add(jScrollPane1, java.awt.BorderLayout.CENTER);

      buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

      addButton.setText("Add");
	  addButton.setMnemonic(KeyEvent.VK_A);
	  addButton.setActionCommand(ADD_CMD);
      addButton.addActionListener(this);

      buttonPanel.add(addButton);

      reloadButton.setText("Reload");
	  reloadButton.setMnemonic(KeyEvent.VK_R);
      reloadButton.setEnabled(false);
	  reloadButton.setActionCommand(RELOAD_CMD);
      reloadButton.addActionListener(this);

      buttonPanel.add(reloadButton);

      add(buttonPanel, java.awt.BorderLayout.SOUTH);

   }

	public void notify(IEvent event) {
		int message = event.getMessage();
		switch(message) {
			case IEvent.ADD_CONTROLLER:
				addController((IRobotController)event.getData());
				break;
			case IEvent.CURRENT_CONTROLLER:
				setController((IRobotController)event.getData());
				break;
			case IEvent.ROBOT_START:
				ctrlList.setEnabled(false);
				reloadButton.setEnabled(false);
				addButton.setEnabled(false);
				break;
			case IEvent.ROBOT_RESET:
			  	ctrlList.setEnabled(true);
				reloadButton.setEnabled(true);
				addButton.setEnabled(true);
			   	break;
			case IEvent.CHECK_NEW_CONTROLLERS:
			   reloadChangedClasses();
			   break;
		}
	}

   private void addController(IRobotController rc) {
   }

   public void setController(IRobotController rc) {
      int i = cPool.getIndex(rc);
      if (i >= 0)
         ctrlList.setSelectedIndex(i);
   }

   public void valueChanged(ListSelectionEvent lse) {
      if (ctrlList.getSelectedIndex() > -1) {
         Event event = new Event(IEvent.CURRENT_CONTROLLER, cPool.getControllerAt(ctrlList.getSelectedIndex()));
         EventBus.broadcast(event);
         reloadButton.setEnabled(true);
      }
   }

	private void selectNewController() {
		JFileChooser jfc = new JFileChooser(lastDirectory);
		jfc.setFileFilter(new ClassFileFilter());
		if (jfc.showOpenDialog(getTopLevelAncestor()) != 0) return;

	   	File f = jfc.getSelectedFile();
		try {
			cPool.loadController(f);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "The file '" + f + "' cannot be found.", "Cannot load controller", JOptionPane.WARNING_MESSAGE);
		} catch (ClassFormatError e) {
			JOptionPane.showMessageDialog(null, "The file '" + f + "' is probably not a Java class file.", "Cannot load controller", JOptionPane.WARNING_MESSAGE);
		} catch (NoSuchMethodException e) {
			JOptionPane.showMessageDialog(null, "The classfile '" + f + "' doesn't implement the IRobot interface.", "Cannot load controller", JOptionPane.WARNING_MESSAGE);
		} catch (InvalidControllerException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot load controller", JOptionPane.WARNING_MESSAGE);
		}

		lastDirectory = jfc.getCurrentDirectory();
	}

   private void reloadController(int i) {
      try {
         cPool.reloadController(i);
         IRobotController irobotcontroller = cPool.getControllerAt(i);
         if(irobotcontroller instanceof PolledControllerWrapper)
            JOptionPane.showMessageDialog(getTopLevelAncestor(), "Polled controller " + ((PolledControllerWrapper)irobotcontroller).getControlObject().getClass().getName() + " reloaded.");
         else
            JOptionPane.showMessageDialog(getTopLevelAncestor(), irobotcontroller.getClass().getName() + " reloaded.");
      }
      catch(Exception exception) {
         JOptionPane.showMessageDialog(getTopLevelAncestor(), exception.getMessage(), "Reload Error", 0);
      }
   }

	public void reloadChangedClasses() {
		File files[] = cPool.getChangedClassFiles();
		if (files.length < 1) return;

	    String s = "", msg;
		for(int i = 0; i < files.length; i++) {
			s += files[i].getAbsolutePath() + "\n";
	    }

	    msg = "The following ";
		msg += (files.length==1)?"class has":"classes have";
        msg += " changed:\n\n" + s + "\nWould you like to reload ";
		msg += (files.length==1)?"it?":"them?";

		int j = JOptionPane.showConfirmDialog(getTopLevelAncestor(), msg, "Controllers changed", 0);

		if (j == 0) {
			cPool.reloadChangedClasses();
		    if (files.length == 1) {
			    msg = "Changed class reloaded.";
		    } else {
			    msg = "Changed clases reloaded.";
		    }
			JOptionPane.showMessageDialog(getTopLevelAncestor(), msg);
		}
	}
}

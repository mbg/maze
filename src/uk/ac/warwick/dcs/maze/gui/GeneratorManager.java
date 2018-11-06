/*
 * $Id: GeneratorManager.java,v 1.7 2005/09/23 17:44:42 bpfoley Exp $
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.ac.warwick.dcs.maze.generators.LoopyGenerator;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.loader.AdaptiveClassLoader;
import uk.ac.warwick.dcs.maze.logic.Event;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
import java.awt.event.KeyEvent;
/**
 *
 * @author Phil C. Mueller
 */
public class GeneratorManager extends JPanel implements IEventClient, ActionListener, ListSelectionListener {
	private static final String ADD_CMD="ADD";
	
    private JScrollPane generatorScrollPane;
    private JList generatorList;
    private JPanel buttonPanel;
    private JButton addButton;
    
	private Vector generators, names;
	private File lastDirectory;
   
   	/* ActionListener methods */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ADD_CMD)) {
			addClass();
			return;
		}
	}
   public GeneratorManager() {
      generators = new Vector();
      names = new Vector();
      lastDirectory = new File(System.getProperty("user.home"));
      initComponents();
      generatorList.setListData(names);
      generatorList.addListSelectionListener(this);
      EventBus.addClient(this);
		
	  /* Add two default maze generators for Prim's alg, and loopy mazes */
      PrimGenerator pg = new PrimGenerator();
	  String pgName = pg.getClass().getName();
	  LoopyGenerator lg = new LoopyGenerator();
	  String lgName = lg.getClass().getName();
	  
      generators.add(pg);
      names.add(pgName.substring(pgName.lastIndexOf('.') + 1));
	  
	  generators.add(lg);
      names.add(lgName.substring(pgName.lastIndexOf('.') + 1));
	  
      generatorList.setListData(names);
      EventBus.broadcast(new Event(IEvent.CURRENT_GENERATOR, pg));
   }
   
    private void initComponents() {
       generatorScrollPane = new JScrollPane();
       generatorList = new JList();
       buttonPanel = new JPanel();
       addButton = new JButton();
       
       setLayout(new java.awt.BorderLayout());
       
       setBorder(new javax.swing.border.TitledBorder("Maze Generators"));
       generatorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       generatorList.setBorder(new javax.swing.border.EtchedBorder());
       generatorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
       generatorScrollPane.setViewportView(generatorList);
       
       add(generatorScrollPane, java.awt.BorderLayout.CENTER);
       
       buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
       
       addButton.setText("Add");
	   addButton.setMnemonic(KeyEvent.VK_A);
	   addButton.setActionCommand(ADD_CMD);
       addButton.addActionListener(this);
       
       buttonPanel.add(addButton);
       
       add(buttonPanel, java.awt.BorderLayout.SOUTH);
       
    }
    
    public void notify(IEvent ievent) {
       int i = ievent.getMessage();
       switch(i) {
          case IEvent.ADD_GENERATOR: // 'o'
             addGenerator((IMazeGenerator)ievent.getData());
             break;
             
          case IEvent.CURRENT_GENERATOR: // 'n'
             setGenerator((IMazeGenerator)ievent.getData());
             break;
             
          case IEvent.ROBOT_START: // 'e'
             generatorList.setEnabled(false);
             break;
             
          case IEvent.ROBOT_RESET: // 'g'
             generatorList.setEnabled(true);
             break;
       }
    }
        
    private void addGenerator(IMazeGenerator imazegenerator) {
       generators.add(imazegenerator);
       int i = generatorList.getSelectedIndex();
       names.add(imazegenerator.getClass().getName());
       generatorList.setListData(names);
       generatorList.setSelectedIndex(i);
    }
    

    public void valueChanged(ListSelectionEvent listselectionevent) {
       if(generatorList.getSelectedIndex() > -1) {
          IMazeGenerator imazegenerator = (IMazeGenerator)generators.elementAt(generatorList.getSelectedIndex());
          Event event = new Event(IEvent.CURRENT_GENERATOR, imazegenerator);
          EventBus.broadcast(event);
       }
    }
    
    public void setGenerator(IMazeGenerator imazegenerator) {
       for(int i = 0; i < generators.size(); i++)
          if(((IMazeGenerator)generators.elementAt(i)).getClass().getName().equals(imazegenerator.getClass().getName()))
             generatorList.setSelectedIndex(i);
       
    }
    
    private void addClass() {
       JFileChooser jfc = new JFileChooser(lastDirectory);
       jfc.setFileFilter(new ClassFileFilter());
       if(jfc.showOpenDialog(getTopLevelAncestor()) == 0) {
          try {
             File file = jfc.getSelectedFile();
             String s = file.getName();
             URLClassLoader urlclassloader = URLClassLoader.newInstance(new URL[] {
                file.getParentFile().toURL()
             });
             Class class1 = urlclassloader.loadClass(s.substring(0, s.lastIndexOf('.')));
             Object obj = class1.newInstance();
             if(obj instanceof IMazeGenerator)
                EventBus.broadcast(new Event(IEvent.ADD_GENERATOR, obj));
             else
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "The selected class does not implement IMazeGenerator", "Incompatible Controller", 0);
          }
          catch(Exception exception) {
             exception.printStackTrace();
          }
          lastDirectory = jfc.getCurrentDirectory();
       }
    }
}

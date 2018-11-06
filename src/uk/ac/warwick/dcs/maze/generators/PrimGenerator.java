/*
 * $Id: PrimGenerator.java,v 1.5 2005/09/23 17:44:42 bpfoley Exp $
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

package uk.ac.warwick.dcs.maze.generators;

import java.awt.Point;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.event.ChangeListener;

import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
import uk.ac.warwick.dcs.maze.logic.Maze;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import java.awt.event.TextListener;
import java.awt.event.TextEvent;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.util.Hashtable;
import java.awt.TextField;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner;
import java.text.NumberFormat;
import java.awt.event.KeyListener;
import javax.swing.Action;

/**
 *
 * @author  Phil C. Mueller
 */
public class PrimGenerator implements IMazeGenerator {
   
   private final static int IN      = 1;
   private final static int FRONTIER = 2;
   private final static int OUT     = 3;
   private final static int NORTH   = 10;
   private final static int EAST    = 11;
   private final static int SOUTH   = 12;
   private final static int WEST    = 13;
   
   private MMGConfigPanel configPanel;
   private int mazeWidth = 15;
   private int mazeHeight = 15;
   private int[][] primGrid;
   private LinkedList frontierList = new LinkedList();
   
   /** Creates new MyMazeGenerator */
   public PrimGenerator() {
      configPanel = new MMGConfigPanel();
   }
   
   public Maze generateMaze() {
      if ((mazeWidth < 1) || (mazeHeight < 1))
         throw new RuntimeException("Maze too small");
         
      
      int realWidth = (2*mazeWidth)+1;
      int realHeight = (2*mazeHeight)+1;
      
      
      Maze m = new Maze(realWidth, realHeight);

      m.setStart(1,1);
      m.setFinish(realWidth-2, realHeight-2);
      
      primGrid = new int[realWidth][realHeight];
      int[] direction = new int[4];
      int neighbours = 0;
      
      for (int i=0; i<realWidth; i++)
         for (int j=0; j<realHeight; j++)
            primGrid[i][j] = OUT;
      
      // select cell ;at random' from inside the outer wall
      int originX = realWidth-2;
      int originY = realHeight-2;
   
      setPrimCellType(m,originX,originY,IN);
      if (originX > 1)
         setPrimCellType(m,originX-2,originY,FRONTIER);
      if (originY > 1)
         setPrimCellType(m,originX,originY-2,FRONTIER);
      if (originX < primGrid.length-2)
         setPrimCellType(m,originX+2,originY,FRONTIER);
      if (originY > primGrid[0].length-2)
         setPrimCellType(m,originX,originY+2,FRONTIER);

      Point frontier = new Point();
      // start Prim loop
      while (frontierList.size() > 0) {
         // choose frontier point at random
         frontier = (Point)frontierList.get(randomInt(frontierList.size()));
         setPrimCellType(m, frontier.x, frontier.y, IN);
         if (frontier.x > 1)
            if (primGrid[frontier.x-2][frontier.y] == OUT)
               setPrimCellType(m, frontier.x-2, frontier.y, FRONTIER);
         if (frontier.y > 1)
            if (primGrid[frontier.x][frontier.y-2] == OUT)
               setPrimCellType(m, frontier.x, frontier.y-2, FRONTIER);
         if (frontier.x < primGrid.length-2)
            if (primGrid[frontier.x+2][frontier.y] == OUT)
               setPrimCellType(m, frontier.x+2, frontier.y, FRONTIER);
         if (frontier.y > primGrid[0].length-2)
            if (primGrid[frontier.x][frontier.y+2] == OUT)
               setPrimCellType(m, frontier.x, frontier.y+2, FRONTIER);
         
         // find neighbours separated by 1 WALL
         // max of 4
         neighbours = 0;
         if (frontier.x-2 > 0)
            if (primGrid[frontier.x-2][frontier.y] == IN)
               direction[neighbours++] = WEST;
         if (frontier.y-2 > 0)
            if (primGrid[frontier.x][frontier.y-2] == IN)
               direction[neighbours++] = NORTH;
         if (frontier.x < primGrid.length-2)
            if (primGrid[frontier.x+2][frontier.y] == IN)
               direction[neighbours++] = EAST;
         if (frontier.y < primGrid[0].length-2)
            if (primGrid[frontier.x][frontier.y+2] == IN)
               direction[neighbours++] = SOUTH;
         
         // choose n intermediate point at random
         int path = direction[randomInt(neighbours)];
         switch (path) {
            case NORTH:    setPrimCellType(m,frontier.x,frontier.y-1,IN);
                           break;
            case EAST:     setPrimCellType(m,frontier.x+1,frontier.y,IN);
                           break;
            case SOUTH:    setPrimCellType(m,frontier.x,frontier.y+1,IN);
                           break;
            case WEST:    setPrimCellType(m,frontier.x-1,frontier.y,IN);
         }
         
         frontierList.remove(frontier);
      }
      
      return m;
   }
   
   private int randomInt(int n) {
      return (int)Math.floor(((double)n)*Math.random());
   }
   
   private void setPrimCellType(Maze m, int x, int y, int type) {
      if (type == IN) {
         m.setCellType(x, y, Maze.PASSAGE);
      }
      if (type == FRONTIER) {
         frontierList.add(new Point(x,y));
	  }
      primGrid[x][y] = type;
   }
   
   public JPanel getConfigurator() {
      return configPanel;
   }
   
   public String getDescription() {
      return "Prim's Algorithm, \u00a9 Phil C. M\u00fcller";
   }
   
   class MMGConfigPanel extends javax.swing.JPanel implements ChangeListener, FocusListener
   {
       private JPanel panel;
       private JPanel sizePanel;
       private JSpinner heightSpinner;
       private JLabel heightLabel;
       private JSlider heightSlider;
       private JLabel widthLabel;
       private JSpinner widthSpinner;
       private JSlider widthSlider;
       
       public MMGConfigPanel() {
          initComponents();
       }
	   
       private void initComponents() {
          panel = new JPanel();
          sizePanel = new JPanel();

          Dimension dim = new Dimension(350, 120);
          setLayout(new BorderLayout());
		  setPreferredSize(dim);
          setMinimumSize(dim);
		  
		  Dimension panelDim = new Dimension(250, 120);
          panel.setLayout(new GridLayout(1,0));
          panel.setPreferredSize(panelDim);
          panel.setMinimumSize(panelDim);
          sizePanel.setLayout(new GridBagLayout());

          sizePanel.setBorder(new TitledBorder("Maze Dimensions"));

		  Hashtable labelTable = new Hashtable();
          labelTable.put( new Integer(1), new JLabel("1"));
          labelTable.put( new Integer(50), new JLabel("50"));
          labelTable.put( new Integer(100), new JLabel("100"));
          labelTable.put( new Integer(150), new JLabel("150"));
          labelTable.put( new Integer(200), new JLabel("200"));
		 
          /*
           * Height stuff
           */
		  heightSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 200, 1));
		  JTextField heightTextField = ((JSpinner.DefaultEditor)heightSpinner.getEditor()).getTextField();
		  heightTextField.addFocusListener(this);
		   
          heightLabel = new JLabel("Height");
		  heightSlider = new JSlider(1, 200, 15);
		  heightLabel.setLabelFor(heightSlider);
		  heightLabel.setDisplayedMnemonic(KeyEvent.VK_H);
          heightSlider.setMajorTickSpacing(50);
          heightSlider.setMinorTickSpacing(10);
		  heightSlider.setPaintTicks(true);
		  heightSlider.setLabelTable(labelTable);
          heightSlider.setPaintLabels(true);
          heightSlider.addChangeListener(this);
		 
          GridBagConstraints gbc = new GridBagConstraints();
          gbc.gridx = 0;
          gbc.gridy = 1;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.EAST;
          gbc.insets = new Insets(0, 0, 0, 5);
          sizePanel.add(heightLabel, gbc);
          
          gbc = new GridBagConstraints();
          gbc.gridx = 1;
          gbc.gridy = 1;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.WEST;
          sizePanel.add(heightSlider, gbc);

          gbc = new GridBagConstraints();
          gbc.gridx = 2;
          gbc.gridy = 1;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.WEST;
          sizePanel.add(heightSpinner, gbc);
          
          /*
           * Width stuff
           */
          widthSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 200, 1));
		  widthSpinner.addChangeListener(this);
		  JTextField widthTextField = ((JSpinner.DefaultEditor)widthSpinner.getEditor()).getTextField();
		  widthTextField.addFocusListener(this);
		  
          widthLabel = new JLabel();
          widthLabel.setText("Width");
          widthSlider = new JSlider(1, 200, 15);
		  widthLabel.setLabelFor(widthSlider);
		  widthLabel.setDisplayedMnemonic(KeyEvent.VK_W);
          widthSlider.setMajorTickSpacing(50);
          widthSlider.setMinorTickSpacing(10);
		  widthSlider.setPaintTicks(true);
          widthSlider.setLabelTable(labelTable);
          widthSlider.setPaintLabels(true);
          widthSlider.addChangeListener(this);
		  

          gbc = new GridBagConstraints();
          gbc.gridx = 0;
          gbc.gridy = 0;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.EAST;
          gbc.insets = new Insets(0, 0, 0, 5);
          sizePanel.add(widthLabel, gbc);

          gbc = new GridBagConstraints();
          gbc.gridx = 1;
          gbc.gridy = 0;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.WEST;
          sizePanel.add(widthSlider, gbc);

          gbc = new GridBagConstraints();
          gbc.gridx = 2;
          gbc.gridy = 0;
          gbc.weightx = 0.5;
          gbc.anchor = GridBagConstraints.WEST;
          sizePanel.add(widthSpinner, gbc);

          panel.add(sizePanel);

          add(panel, java.awt.BorderLayout.CENTER);

       }
	
	   /* FocusListener methods */
	   /* When we tab into a JSpinner we want to select all the text in its
		textfield */
	   public void focusGained(FocusEvent e)
	   {
		   Component c = e.getComponent();
		   if (c instanceof JFormattedTextField) {
			   final JFormattedTextField ftf = (JFormattedTextField)c;
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() { ftf.selectAll(); }
               });
		   } else if (c instanceof JTextField) {
			   ((JTextField)c ).selectAll();
		   }
	   }
	   
	   public void focusLost(FocusEvent e) { }
	   

	   /* ChangeListener methods */
       public void stateChanged(ChangeEvent e) {
           if (e.getSource() == heightSlider) {
			   Integer i = new Integer(heightSlider.getValue());
               heightSpinner.setValue(i);
               mazeHeight = heightSlider.getValue();
			   return;
           }
           if (e.getSource() == widthSlider) {
			   Integer i = new Integer(widthSlider.getValue());
               widthSpinner.setValue(i);
               mazeWidth = widthSlider.getValue();
			   return;
           }
		   if (e.getSource() == heightSpinner) {
			   try {
				   int i = Integer.parseInt(heightSpinner.getValue().toString());
			   	   heightSlider.setValue(i);
			   } catch (NumberFormatException nfe) {}
		   }
		   if (e.getSource() == widthSpinner) {
			   try {
				   int i = Integer.parseInt(widthSpinner.getValue().toString());
			   	   widthSlider.setValue(i);
			   } catch (NumberFormatException nfe) {}
		   }
       }
   }
}

/*
 * $Id: Maze.java,v 1.5 2005/09/23 18:43:18 bpfoley Exp $
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
 
package uk.ac.warwick.dcs.maze.logic;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 *
 * @author Phil C. Mueller
 * @author Brian P. Foley
 */
public class Maze {

   public static final int PASSAGE = 1;
   public static final int WALL = 2;
   
   private int[][] grid;
   private Point start, finish;
   
   /** Creates new Maze */
   public Maze(int columns, int rows) {
      grid = new int[columns][rows];
      for (int i=0; i<columns; i++) {
         for (int j=0; j<rows; j++) {
            grid[i][j] = WALL;
         }
      }
   }
   
   /** Create a maze from an input file */
   public Maze(File f, IRobot r) throws Exception {
       
       try {
           FileInputStream fis = new FileInputStream(f.getAbsolutePath());
		   InputStreamReader isr = new InputStreamReader(fis);
           BufferedReader br = new BufferedReader(isr);
	
           String s;
           int width = 0, height = 0, line = 0;
  
		   do {
			   s = br.readLine();
			   line++;
			   if (line == 1 && !s.equals("Type: WarwickMaze")) {
			   		throw new Exception("File has invalid header");
			   }
			   if (s.startsWith("Version:") && !s.equals("Version: 1.0")) {
			   	    throw new Exception("File has invalid version");
			   }
			   if (s.startsWith("Width: ")) {
                   width = Integer.parseInt(s.substring(7));
           	   }
			   if (s.startsWith("Height: ")) {
                   height = Integer.parseInt(s.substring(8));
           	   }
           
		   } while (!s.equals("HeaderEnd"));
		   
		   if (width < 1) { throw new Exception("File has invalid width"); }
		   if (height < 1) { throw new Exception("File has invalid height"); }
           
		   grid = new int[width][height];
           for(int j=0; j<height; j++) {
               s = br.readLine();
			   if (s == null || s.length() != width) {
				   throw new Exception("Invalid maze data on line " + line);
			   }
               for(int i=0; i<width; i++) {
                   char c = s.charAt(i);
                   if (c == '^') {
					   setStart(i,j);
					   if (r != null) r.setHeading(IRobot.NORTH);
				   }
				   if (c == 'v') {
					   setStart(i,j);
					   if (r != null) r.setHeading(IRobot.SOUTH);
				   }
				   if (c == '>') {
					   setStart(i,j);
					   if (r != null) r.setHeading(IRobot.EAST);
				   }
				   if (c == '<') {
					   setStart(i,j);
					   if (r != null) r.setHeading(IRobot.WEST);
				   }
                   if (c == 'F') setFinish(i,j);
                   grid[i][j] = (c == '#') ? WALL : PASSAGE;
               }
           }
		   line++;
       } catch (Exception e)
       {
		   throw e;
       }
   }
   
   public void writeToFile(File f, IRobot r) throws Exception {
       int width = getWidth(), height = getHeight();
	   
	   FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
	   DataOutputStream dos = new DataOutputStream(fos);
	   dos.writeBytes("Type: WarwickMaze\n");
	   dos.writeBytes("Version: 1.0\n");
	   dos.writeBytes("Width: " + width + "\n");
	   dos.writeBytes("Height: " + height +"\n");
	   dos.writeBytes("HeaderEnd\n");
	   char dir = '^';
	   if (r != null) {
		   switch (r.getHeading()) {
			   case IRobot.NORTH: dir = '^'; break;
			   case IRobot.SOUTH: dir = 'v'; break;
			   case IRobot.EAST:  dir = '>'; break;
			   case IRobot.WEST:  dir = '<'; break;
			   default:           dir = '^'; break;
		   }
	   }
	   for(int j=0; j<height; j++) {
		   for(int i=0; i<width; i++) {
			   char c = (grid[i][j] == WALL) ? '#':'.';
			   if (i==start.x && j==start.y) c = dir;
			   if (i==finish.x && j==finish.y) c = 'F';
			   dos.writeByte(c);
		   }
		   dos.writeByte('\n');
	   }
   }
   
   public int getWidth() {
      return grid.length;
   }
   
   public int getHeight() {
      return grid[0].length;
   }
   
   public int getCellType(int column, int row) {
      return grid[column][row];
   }
   
   public int getCellType(Point p) {
      return getCellType(p.x, p.y);
   }
   
   public void setCellType(int x, int y, int type) {
      grid[x][y] = type;
   }
   
   public void toggleCellType(int x, int y) {
       grid[x][y] = (grid[x][y] == WALL) ? PASSAGE : WALL;
   }
   
   public Point getStart() {
      return new Point(start.x, start.y);
   }

   public void setStart(int x, int y) {
      start = new Point(x, y);
   }

   public Point getFinish() {
      return new Point(finish.x, finish.y);
   }

   public void setFinish(int x, int y) {
      finish = new Point(x, y);
   }

}

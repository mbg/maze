/*
 * $Id: IRobot.java,v 1.5 2005/09/25 19:28:32 bpfoley Exp $
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

package uk.ac.warwick.dcs.maze.logic;
import java.awt.Point;

/**
 *
 * This interface provides a set of methods that can be used to control a
 * robot in the maze environment.
 * @author  Phil C. Mueller
 **/
public interface IRobot {

	/** The absolute direction North. Used when getting and setting the robot's heading. **/
	public static final int NORTH = 1000;
	/** The absolute direction South. Used when getting and setting the robot's heading. **/
	public static final int EAST = 1001;
	/** The absolute direction East. Used when getting and setting the robot's heading. **/
	public static final int SOUTH = 1002;
	/** The absolute direction West. Used when getting and setting the robot's heading. **/
	public static final int WEST = 1003;

	/** The relative direction ahead. Used when looking and facing the robot. **/
	public static final int AHEAD = 2000;
	/** The relative direction right. Used when looking and facing the robot. **/
	public static final int RIGHT = 2001;
	/** The relative direction behind. Used when looking and facing the robot. **/
	public static final int BEHIND = 2002;
	/** The relative direction left. Used when looking and facing the robot. **/
	public static final int LEFT = 2003;
	/** The relative direction centre. Used when looking and facing the robot. **/
	public static final int CENTRE = 2004;

	/** A square in the maze which the robot can't enter. **/
	public static final int WALL = 3000;
	/** A square in the maze which the robot can enter. **/
	public static final int PASSAGE = 3001;
	/** A square in the maze which the robot can enter, and has entered before. **/
	public static final int BEENBEFORE = 4000;

	// We handle errors such as invalid arguments and attempts to move off the maze
	// by throwing RuntimeExceptions. This is *not* a good habit, as per
	// http://java.sun.com/docs/books/tutorial/essential/exceptions/runtime.html
	//
	// However the maze environment is going to be used by beginner Java students,
	// and we want to be able to display errors and a stack trace without
	// having to explain to them how exceptions work. Loops and variables first! :)

	/** Returns the number of times the robot has attempted to complete a maze
	 *  and then been reset using a particular controller.
	 **/
	public int getRuns();

	/** The robot looks at one of the 4 squares around it, and returns the state
	 *  of that square.
	 *  @param direction The direction of the square relative to the robot's position.
	 **/
	public int look(int direction) throws RuntimeException;

	/** Gets the robot to turn in a particular direction relative to its current
	 *  heading.
	 *  @param direction The direction relative to the robot's heading.
	 **/
	public void face(int direction) throws RuntimeException;

	/** Sets the robot's heading using absolute directions (ie N/S/E/W)
	 *  @param heading The robot's heading in absolute coordinates.
	 **/
	public void setHeading(int heading) throws RuntimeException;

	/** Returns the robot's absolute heading **/
	public int getHeading();

	/** Returns the X and Y coordinates of the robot's current location.
	 *  In the robot coordinate system, (0,0) is top left. The X coordinate
	 *  increases as the robot moves East, and the Y coordinate as it moves South.
	 **/
	public Point getLocation();
	/** Returns the X coordinate (column number) of the robot's current location.
	 *  In the robot coordinate system, (0,0) is top left. The X coordinate
	 *  increases as the robot moves East, and the Y coordinate as it moves South.
	 **/
	public int getLocationX();
	/** Returns the Y coordinate (row number) of the robot's current location.
	 *  In the robot coordinate system, (0,0) is top left. The X coordinate
	 *  increases as the robot moves East, and the Y coordinate as it moves South.
	 **/
	public int getLocationY();
	/** Makes the robot attempt to move forward one square. The robot cannot
	 * move off the edge of the maze, or move into a wall square.
	 **/
	public void advance() throws RuntimeException;
	/** Returns the location of the target square
	 *  In the robot coordinate system, (0,0) is top left. The X coordinate
	 *  increases as the robot moves East, and the Y coordinate as it moves South.
	 **/
	public Point getTargetLocation();

	/** Pauses for the specified number of milliseconds **/
	public void sleep(int millis);

    /** Gets the movement logger **/
    public MovementLogger getLogger();
}

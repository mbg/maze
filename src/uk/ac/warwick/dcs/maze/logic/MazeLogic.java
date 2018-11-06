/*
 * $Id: MazeLogic.java,v 1.8 2005/09/25 19:28:32 bpfoley Exp $
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

import java.util.*;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
/**
 *
 * @author Phil C. Mueller
 */
public class MazeLogic implements IEventClient {
   
   public static boolean debug = false;
   
   private Maze maze;
   private RobotImpl robot;
   private ControllerThread controllerThread;
   private LinkedList generators;
   private ControllerPool cPool;
   private IRobotController currentController;
   private IMazeGenerator currentGenerator;
   
	public MazeLogic() {
		cPool = new ControllerPool();
		robot = new RobotImpl();
		maze = (new PrimGenerator()).generateMaze();
		robot.setMaze(maze);
		EventBus.addClient(this);
	}
   
   public void setMaze(Maze maze1) {
      maze = maze1;
      robot.setMaze(maze1);
   }
   
   public Maze getMaze() {
      return maze;
   }
   
   public IRobot getRobot() {
      return robot;
   }
   
   public void startController() {
      controllerThread = new ControllerThread(currentController);
      controllerThread.start();
   }
   
   public void resetController() {
      controllerThread.reset();
      controllerThread = null;
   }
   
   public ControllerPool getControllerPool() {
      return cPool;
   }
   
   public void addMazeGenerator(IMazeGenerator img) {
      generators.add(img);
   }
   
   public void removeMazeGenerator(IMazeGenerator img) {
      generators.remove(img);
   }
   
   public IMazeGenerator[] getMazeGenerators() {
      return (IMazeGenerator[])generators.toArray(new IMazeGenerator[generators.size()]);
   }
   
   public void setCurrentGenerator(IMazeGenerator img) {
      currentGenerator = img;
   }
   
   public IMazeGenerator getCurrentGenerator() {
      return currentGenerator;
   }
   
   public void setCurrentController(IRobotController irc) {
      currentController = irc;
      currentController.setRobot(robot);
   }
   
   public IRobotController getCurrentController() {
      return currentController;
   }
   
   public void notify(IEvent ievent) {
      int i = ievent.getMessage();
      if(i == IEvent.CURRENT_CONTROLLER)
         setCurrentController((IRobotController)ievent.getData());
      else if(i == IEvent.CURRENT_GENERATOR)
         setCurrentGenerator((IMazeGenerator)ievent.getData());
      else if(i == IEvent.ROBOT_START)
         startController();
      else if(i == IEvent.ROBOT_RESET_REQUEST)
         resetController();
      else if(i == IEvent.GENERATE_MAZE_REQUEST) {
         Maze maze = currentGenerator.generateMaze();
         EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
      }
      else if(i == IEvent.NEW_MAZE)
         setMaze((Maze)ievent.getData());
      else if(i == IEvent.DELAY && currentController != null)
         currentController.setDelay(((Integer)ievent.getData()).intValue());
   }
   
}


/*
 * $Id: RobotImpl.java,v 1.8 2005/09/23 18:43:18 bpfoley Exp $
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

public class RobotImpl implements IRobot, IEventClient {
   private MovementLogger logger;
   private Point location = new Point();
   private Point target = new Point();
   private Maze maze;
   private boolean active;
   private boolean[][] trackerGrid;
   private int heading = EAST;
   private long steps = 0L;
   private long collisions = 0L;
   private int runs;

   public RobotImpl() {
      logger = new MovementLogger();
      active = true;
      location = new Point(0, 0);
      maze = null;
      EventBus.addClient(this);
   }

   public void setMaze(Maze maze) {
      this.maze = maze;
      location.x = maze.getStart().x;
      location.y = maze.getStart().y;
      target.x = maze.getFinish().x;
      target.y = maze.getFinish().y;
      runs = 0;
      trackerGrid = new boolean[maze.getWidth()][maze.getHeight()];
      for (int x = 0; x < trackerGrid.length; x++) {
         for (int y = 0; y < trackerGrid[0].length; y++) {
            trackerGrid[x][y] = false;
         }
      }
      trackerGrid[maze.getStart().x][maze.getStart().y] = true;

      EventBus.broadcast(new Event(IEvent.ROBOT_RELOCATE, maze.getStart()));
   }

   public int getLocationY() {
      return location.y;
   }

   public int getLocationX() {
      return location.x;
   }

   public int look(int dir) throws RuntimeException {
	  if (dir < AHEAD || dir > LEFT) {
		throw new RuntimeException("The robot can only look AHEAD, BEHIND, LEFT and RIGHT.");
	  }

      int newHeading = heading + dir;
      Point point = new Point();
      switch (newHeading % 4) {
         case 0:
            point = new Point(getLocationX(), getLocationY() - 1);
            break;
         case 1:
            point = new Point(getLocationX() + 1, getLocationY());
            break;
         case 2:
            point = new Point(getLocationX(), getLocationY() + 1);
            break;
         case 3:
            point = new Point(getLocationX() - 1, getLocationY());
            break;
      }
      int type = maze.getCellType(point);
      if (trackerGrid[point.x][point.y])
         return IRobot.BEENBEFORE;
      if (type == 1)
         return IRobot.PASSAGE;
      if (type == 2)
         return IRobot.WALL;
      return IRobot.WALL;
   }

   public void advance() throws RuntimeException {
      if (active) {
         int x = location.x;
         int y = location.y;
         switch (getHeading()) {
            case IRobot.NORTH:
               y--;
               break;
            case IRobot.EAST:
               x++;
               break;
            case IRobot.SOUTH:
               y++;
               break;
            case IRobot.WEST:
               x--;
               break;
         }

		 if (x<0 || y<0 || x>=maze.getWidth() || y>=maze.getHeight()) {
			throw new RuntimeException("Robot cannot advance off the edge of the maze!");
		 }

         switch (maze.getCellType(x, y)) {
            case 1:
               steps++;
               EventBus.broadcast(new Event(IEvent.ROBOT_RELOCATE, new Point(x, y)));
               break;
            default: {
               Event event = new Event(IEvent.ROBOT_COLLISION, new Point(x, y));
               EventBus.broadcast(event);
               collisions++;
               return;
            }
         }
         trackerGrid[x][y] = true;
      }
   }

   public void face(int dir) throws RuntimeException {
   	  if (dir < AHEAD || dir > LEFT) {
		throw new RuntimeException("The robot can only face AHEAD, BEHIND, LEFT and RIGHT.");
	  }
      switch (dir) {
         case IRobot.BEHIND:
            setHeading((heading + 2) % 4 + IRobot.NORTH);
            break;
         case IRobot.LEFT:
            setHeading((heading + 3) % 4 + IRobot.NORTH);
            break;
         case IRobot.RIGHT:
            setHeading((heading + 1) % 4 + IRobot.NORTH);
            break;
      }
   }

   public int getHeading() {
      return heading;
   }

   public void setHeading(int newHeading) throws RuntimeException {
	  if (newHeading < NORTH || newHeading > WEST) {
		throw new RuntimeException("The robot's heading can only be NORTH, SOUTH, EAST or WEST.");
	  }
      heading = newHeading;
      Event event = new Event(IEvent.ROBOT_HEADING_CHANGED, new Integer(heading));
      EventBus.broadcast(event);
   }

   public void setLocation(Point point) {
      location.x = point.x;
      location.y = point.y;
   }

   public Point getLocation() {
      return new Point(location.x, location.y);
   }

   public Point getTargetLocation() {
      return new Point(target.x, target.y);
   }

   public void setTargetLocation(Point point) {
      target.x = point.x;
      target.y = point.y;
   }

   public void sleep(int millis) {
      try {
         Thread.sleep((long) millis);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public long getSteps() {
      return steps;
   }

   public long getCollisions() {
      return collisions;
   }

   public void notify(IEvent ievent) {
      if (uk.ac.warwick.dcs.maze.logic.MazeLogic.debug)
         System.out.println(ievent.getMessage() + "::" + ievent.getData() + "::Curr " + this.getLocation() + "::Goal " + this.getTargetLocation());

      if (ievent.getMessage() == IEvent.NEW_MAZE)
         setMaze((Maze) ievent.getData());
      else if (ievent.getMessage() == IEvent.ROBOT_RESET)
         reset();
      else if (ievent.getMessage() == IEvent.CURRENT_CONTROLLER)
         runs = 0;
      else if (ievent.getMessage() == IEvent.ROBOT_CHANGE_HEADING)
         face(((Integer)ievent.getData()).intValue());
      else if (ievent.getMessage() == IEvent.ROBOT_RELOCATE)
         location = (Point) ievent.getData();
   }

   public void reset() {
      active = false;
      RobotReport robotreport = new RobotReport();
      robotreport.setRunNumber(runs);
      robotreport.setSteps(steps);
      robotreport.setCollisions(collisions);
      if (location.equals(getTargetLocation()))
         robotreport.setGoalReached(true);
      for (int x = 0; x < trackerGrid.length; x++) {
         for (int y = 0; y < trackerGrid[0].length; y++) {
            trackerGrid[x][y] = false;
         }
      }
      trackerGrid[maze.getStart().x][maze.getStart().y] = true;
      steps = 0L;
      collisions = 0L;
      this.logger.reset();
      runs++;
      EventBus.broadcast(new Event(IEvent.ROBOT_RELOCATE, maze.getStart()));
      EventBus.broadcast(new Event(IEvent.ROBOT_REPORT, robotreport));
      active = true;
   }

   private void broadcastLocation() {
      Event event = new Event(IEvent.ROBOT_RELOCATE, location);
      EventBus.broadcast(event);
   }

   public int getRuns() {
      return runs;
   }

   public MovementLogger getLogger() {
       return this.logger;
   }
}

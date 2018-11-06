/*
 * $Id: BOSSReflectionHarness.java,v 1.3 2005/09/25 19:28:32 bpfoley Exp $
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

package uk.ac.warwick.dcs.maze.assess;

import java.lang.reflect.Method;

import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
/**
 *
 * @author Phil C. Mueller
 */
public class BOSSReflectionHarness {

   private Object bhLock;
   
   /** Creates new BOSSReflectionHarness */
    public BOSSReflectionHarness() {
    }

    public boolean testObject(Object o, Maze m, int steps) {
       if (!objectIsValid(o))
          return false;
       
       if (m == null) {
          System.out.println("Maze is null!");
          return false;
       }
       
       // object has a controlRobot method
       Method controlRobot = null;
       try {
          controlRobot = o.getClass().getMethod("controlRobot", new Class [] { IRobot.class } );
       } catch (Exception e) {
          return false;
       }

       RobotImpl robot = new RobotImpl();
       robot.setMaze(m);
       
       int stepCount = 0;
       while(!robot.getLocation().equals(robot.getTargetLocation())) {
           try {
              controlRobot.invoke(o, new Object[] { robot });
           } catch (Exception e) {
              return false;
           }
          stepCount++;
          if (stepCount >= steps)
             return false;
       }
       //System.out.println(stepCount);
       return true;
    }
    

    public boolean objectIsValid(Object o) {
       try {
          if (o.getClass().getMethod("controlRobot", new Class [] { IRobot.class } ) == null)
             return false;
       } catch (Exception e) {
          return false;
       }
       
       return true;
    }
    
}

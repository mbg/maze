/* 
 * $Id: RandomRobotController.java,v 1.2 2004/12/08 01:38:36 bpfoley Exp $
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

package uk.ac.warwick.dcs.maze.controllers;

import uk.ac.warwick.dcs.maze.logic.*;

/**
 *
 * @author  Phil C. Mueller
 */
public class RandomRobotController implements IRobotController {

    private IRobot robot;
    private boolean active = false;
    private int delay;
    
    /** Creates new MyRobotController */
    public RandomRobotController() {
    }

    public void start() {
       active = true;
       /* 
        * run until we've hit the end
       */ 
       
       while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {

          int rand = (int)Math.round(Math.random()*3);

          switch (rand) {
              case 0:   robot.face(IRobot.AHEAD);
                        break;
              case 1:   robot.face(IRobot.LEFT);
                        break;
              case 2:   robot.face(IRobot.BEHIND);
                        break;
              case 3:   robot.face(IRobot.RIGHT);
          }
          
          /*
           * Move on every iteration
           */

          robot.advance();
          
          /*
           * Slow ourselves down a bit...
           */
          
          if (delay > 0)
             robot.sleep(delay);
       }
    }
    
    public void setDelay(int millis) {
       delay = millis;   
    }
    
    public int getDelay() {
       return delay;   
    }
    
    public void reset() {
       active = false;
    }
    
    public void setRobot(IRobot robot) {
       this.robot = robot;
    }
    
    public String getDescription() {
       return "Random controller (pretty useless)";
    }
    
}

/*
 * $Id: PolledControllerWrapper.java,v 1.4 2005/11/28 17:52:48 bpfoley Exp $
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

import java.lang.reflect.*;
import uk.ac.warwick.dcs.maze.logic.IRobotController;
import uk.ac.warwick.dcs.maze.logic.IRobot;
/**
 *
 * @author Phil C. Mueller
 */
public class PolledControllerWrapper implements IRobotController {

    protected IRobot robot;
    private boolean active = false;
    private int delay = 200;
    private Object pollObj;
    private Method controlRobot;
    private Method reset;
    
    public PolledControllerWrapper(Object obj) {
        pollObj = obj;
        try {
           controlRobot = obj.getClass().getMethod("controlRobot", new Class [] { IRobot.class } );
        } catch (Exception e) {
           e.printStackTrace();
        }
        
        try {
           reset = obj.getClass().getMethod("reset", (Class[]) null);
        } catch (Exception e) {
           //e.printStackTrace();
        }
    }

    
    public void start() {
       active = true;
       /*
        * run until we've hit the end
        */
       
       while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
           if (uk.ac.warwick.dcs.maze.logic.MazeLogic.debug)
              System.out.println("POLL");
           
           try {
              controlRobot.invoke(pollObj, new Object[] { robot });
           } catch (Exception e) {
              e.printStackTrace();
           }
          
          if (delay > 0)
             robot.sleep(delay);
       }
       
       // once inactive, call reset on user object
       if (reset != null)
          try {
             reset.invoke(pollObj, (Object[]) null);
          } catch (Exception e) {
             e.printStackTrace();
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
    
    public Object getControlObject() {
       return pollObj;
    }
    
    public String getDescription() {
       String tag = "Polled Controller";
       if (reset != null)
          tag += " with reset()";
       else
          tag += " w/o reset()";

       return tag;
    }
    
}

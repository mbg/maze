/* 
 * $Id: ControllerThread.java,v 1.2 2004/12/08 01:38:37 bpfoley Exp $
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

/**
 *
 * @author Phil C. Mueller
 */
public class ControllerThread extends Thread {

    private IRobotController controller;
    private boolean run = false;
    
    /** Creates new ControllerThread */
    public ControllerThread(IRobotController rc) {
       controller = rc;
    }

    public void setController(IRobotController controller) {
       this.controller = controller;
    }

    
    public void run() {
       controller.start();
       EventBus.broadcast(new Event(IEvent.ROBOT_FINISHED, null));
    }

    
    public void reset() {
       controller.reset();
       EventBus.broadcast(new Event(IEvent.ROBOT_RESET, null));
    }
}

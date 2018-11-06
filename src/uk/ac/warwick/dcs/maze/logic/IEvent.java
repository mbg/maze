/* 
 * $Id: IEvent.java,v 1.4 2004/12/08 01:38:37 bpfoley Exp $
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

/**
 *
 * @author  Phil C. Mueller
 * @author  Brian P. Foley
 */
public interface IEvent {
   
    /*
     * Stateless status information
     */
    public final static int ROBOT_RELOCATE = 100;

    public final static int ROBOT_START = 101;

    public final static int ROBOT_COLLISION = 102;
    
    public final static int ROBOT_RESET = 103;
    
    public final static int ROBOT_CHANGE_HEADING = 104;
    
    public final static int ROBOT_HEADING_CHANGED = 105;
    
    public final static int ROBOT_FINISHED = 106;
    
    //public final static int ROBOT_TO_ORIGIN = 106;
    
    public final static int NEW_MAZE = 107;
    
    public final static int CURRENT_CONTROLLER = 108;
    
    public final static int ADD_CONTROLLER = 109;
    
    public final static int RELOAD_CONTROLLER = 209;
    
    public final static int CHECK_NEW_CONTROLLERS = 210;
    
    public final static int CURRENT_GENERATOR = 110;
    
    public final static int ADD_GENERATOR = 111;
    
    public final static int ADVANCED_CONFIG_SHOW = 112;
    
    public final static int DELAY = 113;
    
    public final static int ROBOT_REPORT = 114;
    
    public final static int LOAD_MAZE = 115;
    
    public final static int SAVE_MAZE = 116;
    
   /*
    * Requests
    */
    public final static int GENERATE_MAZE_REQUEST = 201;

    public final static int ROBOT_RESET_REQUEST = 202;
    
    public int getMessage();

    public Object getData();
}


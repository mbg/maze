/*
 * $Id: MazeApp.java,v 1.4 2005/09/25 17:48:35 bpfoley Exp $
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
 
package uk.ac.warwick.dcs.maze.gui;

import java.awt.Color;

import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.MazeLogic;
import uk.ac.warwick.dcs.maze.logic.Maze;
/**
 *
 * @author  Phil C. Mueller
 */
public class MazeApp implements IEventClient {

   private MazeFrame mf;
   

   /** Creates new MazeApp */
    public MazeApp(MazeLogic framework) {
       try {
          javax.swing.UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
       } catch (Exception e) {
          e.printStackTrace();
       }

       // initialise GUI stuff
       mf = new MazeFrame(framework);
       mf.setVisible(true);
       
       // initialise maze
       EventBus.addClient(this);
    }

    public void notify(IEvent event) {
        
    }
    
    public static void printDebug(Object origin, String message) {
      if (MazeLogic.debug)
         System.err.println(origin.getClass().getName() + ": " + message);
    }
    
}

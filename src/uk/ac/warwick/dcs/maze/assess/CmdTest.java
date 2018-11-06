/* 
 * $Id: CmdTest.java,v 1.2 2004/12/08 01:38:36 bpfoley Exp $
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

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.loader.AdaptiveClassLoader;
import uk.ac.warwick.dcs.maze.logic.IMazeGenerator;
/**
 *
 * @author Phil C. Mueller
 */
public class CmdTest {

   /** Creates new CmdTest */
    public CmdTest() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) throws Exception {
       if (args.length != 1)
          System.exit(1);
       
       Properties p = System.getProperties();
       Enumeration keys = p.keys();
       //while (keys.hasMoreElements()) {
       //   String key = (String)keys.nextElement();
       //   System.out.println(key + ": " + System.getProperty(key));  
       //}
       
       //File f = new File(System.getProperty("user.dir") + File.separator + args[0]);
       File f = new File(args[0]);
       if (!f.exists()) {
          System.out.println("File doesn't exist");
          System.exit(2);
       }
       
       AdaptiveClassLoader acl = new AdaptiveClassLoader();
       Class c = acl.loadClass(f);
       Object o = c.newInstance();
       
       BOSSReflectionHarness bh = new BOSSReflectionHarness();
       
       IMazeGenerator mg = new PrimGenerator();
       //Maze maze = mg.generateMaze();
       //System.out.println("Generated maze with size " + maze.getWidth() + "x" + maze.getHeight());
       
       boolean success = bh.testObject(o, mg.generateMaze(), 30000);
       
       if (success)
          System.out.println("Test succeeded");
       else
          System.out.println("Test failed");
       
       
       
       System.out.println("Done!");
       System.exit(0);
    }

}

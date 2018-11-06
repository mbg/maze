/*
 * $Id: AppLauncher.java,v 1.7 2005/11/28 17:52:48 bpfoley Exp $
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

package uk.ac.warwick.dcs.maze;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import uk.ac.warwick.dcs.maze.gui.MazeApp;
import uk.ac.warwick.dcs.maze.logic.InvalidControllerException;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.logic.MazeLogic;
/**
 *
 * @author  Phil C. Mueller
 */
public class AppLauncher {

    /* Make the constructor private, so instances can't be created */
    private AppLauncher() {}

    public static void main (String args[]) {
		MazeLogic logic = new MazeLogic();
		StringBuffer sb = new StringBuffer();
		LongOpt longopts[] = {
			new LongOpt("load-controller", LongOpt.REQUIRED_ARGUMENT, sb, 'c'),
			new LongOpt("debug", LongOpt.NO_ARGUMENT, null, 'd'),
			new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
			new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v')
		};
		Getopt getopt = new Getopt("mazeenvironment", args, "-c:dg:hv", longopts);
		int c;
		File f;
		while ( (c = getopt.getopt()) != -1) {
			switch (c)
			{
				case 1:
					f = new File(getopt.getOptarg());
					try {
						Maze maze = new Maze(f, logic.getRobot());
						logic.setMaze(maze);
					} catch (Exception e) { }
					break;
				case 'c':
					f = new File(getopt.getOptarg());
					try {
						logic.getControllerPool().loadController(f);
					} catch (ClassNotFoundException e) {
						System.out.println("Warning: Cannot load controller. The file '" + f + "' cannot be found.");
					} catch (ClassFormatError e) {
						System.out.println("Warning: Cannot load controller. The file '" + f + "' is probably not a Java class file.");
					} catch (NoSuchMethodException e) {
						System.out.println("Warning: Cannot load controller. The classfile '" + f + "' doesn't implement the IRobot interface.");
					} catch (InvalidControllerException e) {
						System.out.println("Warning: Cannot load controller from file '" + f + "':");
						System.out.println(e.getMessage());
					}
					break;
				case 'd':
					MazeLogic.debug = true;
					break;
				case 'h':
					usage();
					System.exit(0);
					break;
				case 'v':
					version();
					System.exit(0);
					break;
				default: // unrecognised
					usage();
					System.exit(0);
					break;
			}
		}
		
		new MazeApp(logic);
    }
	
	private static void version()
	{
		String version = "??", build = "unknown";
		Properties p = new Properties();
		URL u = AppLauncher.class.getResource("/.courseware.properties");
		try {
			p.load(u.openStream());
			String s;
			s = p.getProperty("version");
			if (s != null) version = s;
			s = p.getProperty("build");
			if (s != null) build = s;
		} catch (IOException e) { }
	
		System.err.println("CS118 Maze environment v" + version + ", built on " + build);
		System.err.println("");
		
	}
	private static void usage()
	{
		version();
		System.err.println("Usage: java -jar mazeenvironment.jar [arguments] [mazefile]");
		System.err.println(" -c --load-controller ctl.class Load the controller ctl.class on startup. This");
		System.err.println("                                can be used repeatedly to load multiple classes.");
		System.err.println(" -d --debug                     Enable debugging.");
		System.err.println(" -h --help                      Display this help.");
		System.err.println(" -v --version                   Display version number.");
	}
}


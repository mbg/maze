/*
 * $Id: RelatedClassFilter.java,v 1.4 2005/11/28 17:52:48 bpfoley Exp $
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
 
package uk.ac.warwick.dcs.maze.loader;


import java.io.*;

/**
 *
 * @author Phil C. Mueller
 */
public class RelatedClassFilter implements java.io.FileFilter {
	private static RelatedClassFilter rcf = new RelatedClassFilter();
	private static String classFilePath = null;
   
	/* FIXME: This defines related classes as 'classes in the same directory as the
	  class we're loading'. This is bogus; we should really inspect the classfile
	  for dependancies (does reflection let us do this?) and recursively return
	  a list of dependant classes. For this to be effective, we'll need to strip
	  out builtin classes like java.*, javax.*
	*/
	public static File [] getRelatedFiles(File f)
	{
		if (f == null) return null;
		
		// argument is the original class file
		int i = f.getPath().lastIndexOf(".class");
		if (i == -1) return null;
		classFilePath = f.getPath().substring(0, i);
		File dir = f.getParentFile();
		if (dir == null) dir = new File(".");
		File [] files = dir.listFiles(rcf);
		return files;
	}
	
	/** Creates new RelatedClassFilter */
	public RelatedClassFilter() { }

    public boolean accept(java.io.File file)
	{
		if (file.getPath().startsWith(classFilePath + "$") &&
			file.getPath().endsWith(".class") && !file.getPath().equals(classFilePath + ".class"))
			return true;
	
		return false;
	}
}

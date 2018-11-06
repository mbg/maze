/*
 * $Id: AdaptiveClassLoader.java,v 1.4 2005/11/28 17:52:48 bpfoley Exp $
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
import java.util.*;

public class AdaptiveClassLoader extends ClassLoader {
   
   protected static SecurityManager sm;
   protected Hashtable cache;
   protected Vector repository;

   private static class ClassCacheEntry {
      
      Class loadedClass;
      File origin;
      long lastModified;
      
      private ClassCacheEntry() {
      }
      
   }
   
   
   public AdaptiveClassLoader() {
      cache = new Hashtable();
      repository = new Vector();
   }
   
   void log(String s) {
      System.out.println("AdaptiveClassLoader: " + s);
   }
   
   public Class loadClass(String s, boolean flag)
   throws ClassNotFoundException {
      ClassCacheEntry classcacheentry = (ClassCacheEntry)cache.get(s);
      if(classcacheentry != null)
         cache.remove(s);
      ClassCacheEntry classcacheentry1 = new ClassCacheEntry();
      String s1 = s + ".class";
      for(Enumeration enumeration = repository.elements(); enumeration.hasMoreElements();) {
         File file = new File(enumeration.nextElement() + File.separator + s1);
         if(file.exists())
            return loadClass(file);
      }
      
      Class class1 = super.loadClass(s, flag);
      classcacheentry1.loadedClass = class1;
      cache.put(s, classcacheentry1);
      return classcacheentry1.loadedClass;
   }
   
   public Class loadClass(String s)
   throws ClassNotFoundException {
      return loadClass(s, false);
   }
   
   public Class loadSingleClass(File file)
   throws ClassNotFoundException {
      return loadClass(file, false);
   }
   
    /** Load the classfile (if possible) and any related classfiles **/
	public Class loadClass(File file) throws ClassNotFoundException {
		Class class1 = loadClass(file, false);
		File afile[] = RelatedClassFilter.getRelatedFiles(file);
		
	   	if (afile != null) {
			for(int i = 0; i < afile.length; i++) {
				loadClass(new File(afile[i].getPath()), false);
	    	}
		}
		return class1;
	}
   
   protected synchronized Class loadClass(File file, boolean flag)
   throws ClassNotFoundException {
      String s = file.getName().substring(0, file.getName().lastIndexOf('.'));
      repository.add(file.getParent());
      Object obj = null;
      ClassCacheEntry classcacheentry = (ClassCacheEntry)cache.get(s);
      if(classcacheentry != null)
         cache.remove(s);
      ClassCacheEntry classcacheentry1 = new ClassCacheEntry();
      byte abyte0[] = null;
      try {
         abyte0 = loadClassFromFile(file, classcacheentry1);
      }
      catch(SecurityException securityexception) { }
      catch(IOException ioexception) {
         abyte0 = null;
      }
      catch(Exception exception) { }
      if(abyte0 != null) {
         classcacheentry1.lastModified = classcacheentry1.origin.lastModified();
         cache.put(s, classcacheentry1);
         classcacheentry1.loadedClass = doDefineClass(s, abyte0);
         if(flag)
            resolveClass(classcacheentry1.loadedClass);
         return classcacheentry1.loadedClass;
      } else {
         throw new ClassNotFoundException(s);
      }
   }
   
   public AdaptiveClassLoader reinstantiate() {
      AdaptiveClassLoader adaptiveclassloader = new AdaptiveClassLoader();
      adaptiveclassloader.setCache(cache);
      adaptiveclassloader.setRepository(repository);
      return adaptiveclassloader;
   }
   
   protected void setCache(Hashtable hashtable) {
      cache = hashtable;
   }
   
   protected void setRepository(Vector vector) {
      repository = vector;
   }
   
   public boolean fileLoaded(File file) {
      String s = file.getName().substring(0, file.getName().lastIndexOf('.'));
      return cache.get(s) != null;
   }
   
   public File getFile(String s) {
      Object obj = cache.get(s);
      if(obj == null)
         return null;
      else
         return ((ClassCacheEntry)obj).origin;
   }
   
   public File[] validate() {
      LinkedList linkedlist = new LinkedList();
      for(Enumeration enumeration = cache.keys(); enumeration.hasMoreElements();) {
         String s = (String)enumeration.nextElement();
         ClassCacheEntry classcacheentry = (ClassCacheEntry)cache.get(s);
         if(classcacheentry != null && classcacheentry.origin != null && classcacheentry.origin.lastModified() > classcacheentry.lastModified)
            linkedlist.add(classcacheentry.origin);
      }
      
      return (File[])linkedlist.toArray(new File[linkedlist.size()]);
   }
   
    private byte[] loadClassFromFile(File file, ClassCacheEntry classcacheentry) throws IOException
    {
	    if (!file.exists() || !file.isFile()) return null;
	  	classcacheentry.origin = file;
        FileInputStream fileinputstream = new FileInputStream(file);
        try {
            byte abyte0[] = loadBytesFromStream(fileinputstream, (int)file.length());
            return abyte0;
        } finally {
            fileinputstream.close();
        }
    }
   
   private byte[] loadBytesFromStream(InputStream inputstream, int i)
   throws IOException {
      byte abyte0[] = new byte[i];
      int k = 0;
      int j;
      for(; i > 0 && (j = inputstream.read(abyte0, k, i)) != -1; i -= j)
         k += j;
      
      
      return abyte0;
   }
   
   protected Class doDefineClass(String s, byte abyte0[]) {
      Class class1 = defineClass(s, abyte0, 0, abyte0.length);
      return class1;
   }
   
}

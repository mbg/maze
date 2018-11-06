/*
 * $Id: ControllerPool.java,v 1.5 2005/11/28 17:52:48 bpfoley Exp $
 * Copyright 2002 Phil C. Mueller
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

import java.io.File;
import java.util.*;
import javax.swing.ListModel;
import javax.swing.event.*;
import uk.ac.warwick.dcs.maze.controllers.PolledControllerWrapper;
import uk.ac.warwick.dcs.maze.loader.AdaptiveClassLoader;

/**
 *
 * @author Phil C. Mueller
 */
public class ControllerPool implements ListModel, IEventClient {

   private LinkedList cl;
   private AdaptiveClassLoader loader;
   private EventListenerList listenerList;

   class ControllerListEntry {

      private IRobotController ctlr;
      private Class ctlrClass;
      private File file;

      public ControllerListEntry(IRobotController irc, File f) {
         ctlr = irc;
         file = f;
         if (ctlr instanceof PolledControllerWrapper) {
            ctlrClass = ((PolledControllerWrapper)ctlr).getControlObject().getClass();
         } else {
            ctlrClass = irc.getClass();
         }
      }

      public String getDescription() {
         return ctlrClass.getName().substring(ctlrClass.getName().lastIndexOf('.') + 1);
      }

      public Class getControllerClass() {
         return ctlrClass;
      }

      public void setControllerClass(Class c) {
         ctlrClass = c;
      }

      public void setController(IRobotController irc) {
         ctlr = irc;
      }

      public IRobotController getController() {
         return ctlr;
      }

      public File getFile() {
         return file;
      }

   }

   /** Creates a new instance of ControllerPool */
   public ControllerPool() {
      cl = new LinkedList();
      listenerList = new EventListenerList();
      loader = new AdaptiveClassLoader();
      EventBus.addClient(this);
   }

   public void addListDataListener(javax.swing.event.ListDataListener listDataListener) {
      listenerList.add(javax.swing.event.ListDataListener.class, listDataListener);
   }

   public Object getElementAt(int param) {
      return ((ControllerListEntry)cl.get(param)).getDescription();
   }

   public int getSize() {
      return cl.size();
   }

   public void removeListDataListener(javax.swing.event.ListDataListener listDataListener) {
      listenerList.remove(javax.swing.event.ListDataListener.class, listDataListener);
   }

   public void addController(IRobotController irc) {
       this.addController(irc, null);
   }

   public void addController(IRobotController irc, File f) {
      ControllerListEntry cle = new ControllerListEntry(irc, f);
      cl.add(cle);
      fireIntervalAdded(this, cl.size() - 1, cl.size() - 1);
      EventBus.broadcast(new Event(IEvent.CURRENT_CONTROLLER, irc));
   }

   public void reloadController(int i) throws Exception {
      ControllerListEntry cle = (ControllerListEntry)cl.get(i);
      if (cle.getFile() == null)
         throw new Exception("This controller is hard-coded. It cannot be reloaded.");
      loader = loader.reinstantiate();
      Class class1 = loader.loadClass(cle.getFile());
      Object obj = class1.newInstance();
      if (obj instanceof IRobotController) {
         cle.setController((IRobotController)obj);
         EventBus.broadcast(new Event(IEvent.CURRENT_CONTROLLER, obj));
      }
      else {
         if (obj.getClass().getMethod("controlRobot",
            new Class [] { uk.ac.warwick.dcs.maze.logic.IRobot.class }) != null) {

            PolledControllerWrapper pcw = new PolledControllerWrapper(obj);
            cle.setController(pcw);
            EventBus.broadcast(new Event(IEvent.CURRENT_CONTROLLER, pcw));
         }
      }
   }

   public void removeController(IRobotController irc) {
   }

   public IRobotController getControllerAt(int i) {
      return ((ControllerListEntry)cl.get(i)).getController();
   }

   public int getIndex(IRobotController irc) {
      Iterator iter = cl.iterator();

      int i = 0;
      if (irc instanceof PolledControllerWrapper) {
         String ircName = ((PolledControllerWrapper)irc).getControlObject().getClass().getName();
         while(iter.hasNext()) {
            IRobotController rc1 = ((ControllerListEntry)iter.next()).getController();
            if ((rc1 instanceof PolledControllerWrapper) && ((PolledControllerWrapper)rc1).getControlObject().getClass().getName().equals(ircName))
                return i;
            i++;
         }
      } else {
         while(iter.hasNext()) {
            if (((ControllerListEntry)iter.next()).getController().getClass().getName().equals(irc.getClass().getName()))
               return i;
            i++;
         }
      }
      return -1;
   }

   public void loadController(File f) throws NoSuchMethodException, ClassFormatError, ClassNotFoundException, InvalidControllerException {
      if (loader.fileLoaded(f))
         throw new InvalidControllerException("The class you selected is already loadeed");
      try {
         Thread.currentThread().setContextClassLoader(loader);
         Class class1 = loader.loadClass(f);
         if (class1 == null)
            throw new InvalidControllerException("Could not instantiate class " + f.getName());
         Object obj = class1.newInstance();
         if (obj instanceof IRobotController) {
            addController((IRobotController)obj, f);
            EventBus.broadcast(new Event(IEvent.ADD_CONTROLLER, obj));
         }
         else if (obj.getClass().getMethod("controlRobot", new Class [] { uk.ac.warwick.dcs.maze.logic.IRobot.class }) != null) {
            PolledControllerWrapper pcw = new PolledControllerWrapper(obj);
            addController(pcw, f);
            EventBus.broadcast(new Event(IEvent.ADD_CONTROLLER, pcw));
         }
      }
	  catch (ClassFormatError e) {
		  throw e;
	  }
	  catch (NoSuchMethodException e) {
		  throw e;
	  }
	  catch (ClassNotFoundException e) {
		  throw e;
	  }
      catch (Throwable e) {
         throw new InvalidControllerException(e.toString());
      }
   }

   public File [] getChangedClassFiles() {
      return loader.validate();
   }

   public void reloadChangedClasses() {
      File afile[] = loader.validate();
      for (int i=0; i < afile.length; i++)
         try {
            loader = loader.reinstantiate();
            Class class1 = loader.loadClass(afile[i]);
            if (class1 != null) {
               Object obj = class1.newInstance();
               if (obj instanceof IRobotController) {
                  if(getIndex((IRobotController)obj) >= 0)
                     EventBus.broadcast(new Event(IEvent.CURRENT_CONTROLLER, obj));
               }
               else if (obj.getClass().getMethod("controlRobot", new Class [] { uk.ac.warwick.dcs.maze.logic.IRobot.class }) != null) {
                  PolledControllerWrapper pcw = new PolledControllerWrapper(obj);
                  if (getIndex(pcw) >= 0)
                     EventBus.broadcast(new Event(IEvent.CURRENT_CONTROLLER, pcw));
               }
            }
         }
         catch (Exception e) {}
   }

   private void fireIntervalAdded(Object obj, int i, int j) {
      ListDataEvent lde = new ListDataEvent(obj, 1, i, j);
      Object aobj[] = listenerList.getListenerList();
      for (int k = aobj.length - 2; k >= 0; k-= 2)
         if (aobj[k] == (javax.swing.event.ListDataListener.class))
            ((ListDataListener)aobj[k+1]).intervalAdded(lde);
   }

   public void notify(IEvent ievent) {
      int i = ievent.getMessage();
   }
}

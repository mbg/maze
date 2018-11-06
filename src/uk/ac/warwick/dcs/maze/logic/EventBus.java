/*
 * $Id: EventBus.java,v 1.4 2005/04/14 21:59:36 bpfoley Exp $
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

import java.util.LinkedList;
import java.util.Iterator;
/**
 *
 * @author  Phil C. Mueller
 */
public class EventBus {

    private static LinkedList clients = new LinkedList();
   
   /* Nobody should create an instance of EventBus */
    private EventBus() {
    }

    public static void addClient(IEventClient client) {
       clients.add(client);
    }
    
    public static void removeClient(IEventClient client) {
       clients.remove(client);
    }

	public static synchronized void broadcast(IEvent event) {
		Iterator iter = clients.iterator();
		while (iter.hasNext()) {
			try {
				IEventClient ec = ((IEventClient)iter.next());
				ec.notify(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

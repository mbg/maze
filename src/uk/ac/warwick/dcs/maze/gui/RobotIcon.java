/*
 * $Id: RobotIcon.java,v 1.5 2005/09/23 18:43:18 bpfoley Exp $
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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IRobot;
/**
 *
 * @author Phil C. Mueller
 */
public class RobotIcon extends javax.swing.JPanel implements IEventClient, MouseListener, MouseMotionListener
{
	private IRobot robot = null;
	
    /** Creates new form RobotIcon */
    public RobotIcon(IRobot robot) {
       initComponents();
	   this.robot = robot;
       setOpaque(false);
       EventBus.addClient(this);
    }
	
    private void initComponents() {
       setLayout(null);
       
       addMouseListener(this);
       addMouseMotionListener(this);
    }
	
	/* MouseListener interface */
	
	public void mousePressed(MouseEvent e) { }
	
	public void mouseEntered(MouseEvent e) { }
	
	public void mouseExited(MouseEvent e) { }
	
    public void mouseReleased(MouseEvent e) {
       e.translatePoint(this.getX(), this.getY());
       this.getParent().dispatchEvent(e);
    }

    public void mouseClicked(MouseEvent e) {
       e.translatePoint(this.getX(), this.getY());
       this.getParent().dispatchEvent(e);
    }
	
	/* MouseMotionListener interface */
	public void mouseMoved(MouseEvent e) { }
	
    public void mouseDragged(MouseEvent e) {
       e.translatePoint(this.getX(), this.getY());
       this.getParent().dispatchEvent(e);
    }
    
    public void paint(Graphics g) {
       // paint triangle
        g.setColor(Color.blue);
        
        Polygon p = new Polygon();

        switch (robot.getHeading()) {
            case IRobot.NORTH:   p = new Polygon(new int[] {0, getWidth()/2, getWidth() - 1}, new int [] {getHeight() - 1 , 0, getHeight() - 1}, 3);
                                 break;
            case IRobot.SOUTH:   p = new Polygon(new int[] {0, getWidth()/2, getWidth() - 1}, new int [] {0 , getHeight() - 1, 0}, 3);
                                 break;
            case IRobot.EAST:    p = new Polygon(new int[] {0, getWidth()-1, 0}, new int [] {0 , getHeight() / 2, getHeight() - 1}, 3);
                                 break;
            case IRobot.WEST:    p = new Polygon(new int[] {getWidth()-1, 0, getWidth()-1}, new int [] {0 , getHeight() / 2, getHeight() - 1}, 3);
        }
           
           
        g.fillPolygon(p);
    }
    
    public void notify(IEvent event) {
       if (event.getMessage() == IEvent.ROBOT_HEADING_CHANGED) repaint();
    }
    
}

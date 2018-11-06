/*
 * $Id: MazeGridPanel.java,v 1.14 2005/09/25 17:48:35 bpfoley Exp $
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import uk.ac.warwick.dcs.maze.logic.Event;
import uk.ac.warwick.dcs.maze.logic.EventBus;
import uk.ac.warwick.dcs.maze.logic.IEvent;
import uk.ac.warwick.dcs.maze.logic.IEventClient;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.logic.MazeLogic;

public class MazeGridPanel extends JPanel implements IEventClient, MouseListener, MouseMotionListener
{

    private static final Color BEENBG = new Color(202, 0, 0);
    private static final Color WALLBG = new Color(100, 100, 100);
    private static final Color PASSAGEBG = new Color(245, 245, 245);

    private MazeLogic mazeLogic;
    private RobotIcon ri;

    private int uWidth;
    private int uHeight;
    private int xOffset;
    private int yOffset;

    private boolean running = false;
    private boolean collisionPainted = true;
    private boolean[][] beenbefore;
    private boolean dragStartCell = false;
    private boolean dragFinishCell = false;

    private JLabel noGeneratorLabel;

    public MazeGridPanel(MazeLogic mazeLogic) {
		this.mazeLogic = mazeLogic;
        initComponents();
        setBackground(new Color(180, 180, 180));
        ri = new RobotIcon(mazeLogic.getRobot());
        ri.setVisible(false);
        add(ri);
        EventBus.addClient(this);
		if (mazeLogic.getMaze() != null) {
			setMaze();
		}
    }

    private void initComponents() {
        noGeneratorLabel = new JLabel();
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
	/* MouseListener interface */
	public void mousePressed(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mouseClicked(MouseEvent e) {
		Maze maze = mazeLogic.getMaze();
        if (maze != null && !running) {
            Point point = translateCoord(e.getPoint());

			// If the click is on the background outside the maze bounds,
			// then ignore it.
			if (point.x<0 || point.x>=maze.getWidth()) return;
			if (point.y<0 || point.y>=maze.getHeight()) return;

            // if it's a double click on the robot, rotate it
            if (e.getClickCount() == 2 && point.equals(maze.getStart())) {
                EventBus.broadcast(new Event(IEvent.ROBOT_CHANGE_HEADING,
                        new Integer(IRobot.RIGHT)));
            }
            else if (!point.equals(maze.getFinish()) && !point.equals(maze.getStart())) {
                maze.toggleCellType(point.x, point.y);
                EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (mazeLogic.getMaze() != null && !running) {
            if (dragFinishCell)
                dragFinishCell = false;
            else if (dragStartCell)
                dragStartCell = false;
        }
    }

	// MouseMotionListener methods
	public void mouseDragged(MouseEvent e)
	{
		Maze maze = mazeLogic.getMaze();
        if (maze != null && !running) {
            Point point = translateCoord(e.getPoint());

            // only allow dragging within maze
            if (point.x < 0 || point.y < 0 || point.x >= maze.getWidth()
                    || point.y >= maze.getHeight())
                return;

            if (point.equals(maze.getFinish()))
                dragFinishCell = true;
            else if (point.equals(maze.getStart()))
                dragStartCell = true;

            if (dragFinishCell && !point.equals(maze.getFinish())) {
                if (maze.getCellType(point) == 1) {
                    maze.setFinish(point.x, point.y);
                    EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
                }
                repaint();
            } else if (dragStartCell && !point.equals(maze.getStart())) {
                if (maze.getCellType(point) == 1) {
                    maze.setStart(point.x, point.y);
                    EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
                }
                repaint();
            }
        }
    }

	public void mouseMoved(MouseEvent e) { }

    public void setMaze() {
		Maze maze = mazeLogic.getMaze();
        beenbefore = new boolean[maze.getWidth()][maze.getHeight()];
        initBeenbefore();
        beenbefore[maze.getStart().x][maze.getStart().y] = true;
        removeAll();
        setBounds(getX(), getY(), getWidth(), getHeight());
        add(ri);
        ri.setVisible(true);
        repaint();
    }

    public void notify(IEvent event) {
        if (event.getMessage() == IEvent.NEW_MAZE)
            setMaze();
        else if (event.getMessage() == IEvent.ROBOT_RELOCATE) {
            Point point = (Point) event.getData();
            moveRobotIcon(point);
        } else if (event.getMessage() == IEvent.ROBOT_START)
            running = true;
        else if (event.getMessage() == IEvent.ROBOT_RESET) {
            running = false;
            initBeenbefore();
            collisionPainted = true;
			repaint();
        } else if (event.getMessage() == IEvent.ROBOT_COLLISION) {
            collisionPainted = false;
			repaint();
		}
    }


    public void setBounds(int x, int y, int width, int height) {
        Maze maze = mazeLogic.getMaze();
		IRobot robot = mazeLogic.getRobot();
		super.setBounds(x, y, width, height);
        if (maze != null) {
            uWidth = Math.min(getWidth() / maze.getWidth(), getHeight() / maze.getHeight());
            uHeight = uWidth;
            xOffset = (getWidth() - uWidth * maze.getWidth()) / 2;
            yOffset = (getHeight() - uHeight * maze.getHeight()) / 2;
            ri.setSize(uWidth, uHeight);
            ri.setLocation(robot.getLocationX() * uWidth + xOffset,
						   robot.getLocationY() * uHeight + yOffset);
        }
    }

    public void paintComponent(Graphics graphics) {
		Maze maze = mazeLogic.getMaze();
		IRobot robot = mazeLogic.getRobot();
        super.paintComponent(graphics);
        if (maze != null) {
            for (int x = 0; x < maze.getWidth(); x++) {
                for (int y = 0; y < maze.getHeight(); y++) {
                    if (beenbefore[x][y])
                        fillCell(x, y, graphics, BEENBG);
                    else {
                        switch (maze.getCellType(x, y)) {
                        case 1:
                            graphics.setColor(PASSAGEBG);
                            break;
                        case 2:
                            graphics.setColor(WALLBG);
                            break;
                        }
                        fillCell(x, y, graphics, graphics.getColor());
                    }
                }
            }
            if (!collisionPainted) {
                fillCell(robot.getLocationX(), robot.getLocationY(),
                        graphics, Color.red);
                collisionPainted = true;
            }
            fillCell(maze.getFinish().x, maze.getFinish().y, graphics,
                    Color.green);
        }
    }

    private void fillCell(int x, int y, Graphics graphics, Color color) {
        graphics.setColor(color);
        graphics.fillRect(x * uWidth + xOffset, y * uHeight + yOffset,
                uWidth, uHeight);
    }

    private void moveRobotIcon(Point point) {
        ri.setLocation(point.x * uWidth + xOffset, point.y * uHeight + yOffset);
        if (beenbefore != null)
            beenbefore[point.x][point.y] = true;
        ri.repaint();
    }

    private void initBeenbefore() {
		IRobot robot = mazeLogic.getRobot();
        for (int x = 0; x < beenbefore.length; x++) {
            for (int y = 0; y < beenbefore[0].length; y++) {
                beenbefore[x][y] = false;
            }
        }
        beenbefore[robot.getLocationX()][robot.getLocationY()] = true;
    }

	/* Translates coordinates from 'Panel space'
	 into cell coordinates in the maze. */
	private Point translateCoord(Point p)
	{
		// Round the coordinates so that 0.0-0.999 gets cell 0 rather
		// than -0.5-0.4999 being cell 0.
		float x = (float)(p.x - xOffset) / uWidth;
		float y = (float)(p.y - yOffset) / uHeight;
        return new Point((int)Math.floor(x),(int)Math.floor(y));
	}
}

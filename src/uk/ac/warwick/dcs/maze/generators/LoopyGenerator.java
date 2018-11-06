/* 
 * $Id: LoopyGenerator.java,v 1.2 2004/12/08 17:53:49 bpfoley Exp $
 * Copyright John Fearnley
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

package uk.ac.warwick.dcs.maze.generators;

import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.Maze;

public class LoopyGenerator extends PrimGenerator
{
	//takes a Prim maze and punches a load of holes in it
	public Maze generateMaze()
	{
		Maze maze = super.generateMaze();
		for(int i = 1; i < maze.getWidth() - 1; i++) {
			for(int j = 1; j < maze.getHeight() - 1; j++) {
				if (isValid(maze, i, j) && getWalls(maze, i, j) < 3 && Math.random() > 0.5)
					maze.setCellType(i, j, Maze.PASSAGE);
			}
		}
		centerTarget(maze);
		return maze;
	}

	// Checks the squares around the one passed and returns false
	// if removing this square would create four empty squares
	private boolean isValid(Maze m, int x, int y)
	{
		for(int i = x - 1; i <= x; i++) {
			for(int j = y - 1; j <= y; j++) {
			
				boolean invalidSquare = true;
				for(int a = i; a <= i+1; a++) {
					for(int b = j; b <= j+1; b++) {
						if (m.getCellType(a, b) == Maze.WALL && !((x == a) && (y == b)))
							invalidSquare = false;
					}
				}
				if (invalidSquare) return false;
				
			}
		}
		return true;
	}

	// Returns the number of walls around the square passed
	private int getWalls(Maze m, int x, int y)
	{
		int count = 0;
		for (int i = x - 1; i <= x+1; i+=2) {
			if(m.getCellType(i, y) == Maze.WALL) count++;
		}
		
		for (int i = y - 1; i <= y+1; i+=2) {
			if(m.getCellType(x, i) == Maze.WALL) count++;
		}
		
		return count;
	}

	// Attempts to put the target as close as possible to the center of the maze
	private void centerTarget(Maze m)
	{
		int x = m.getWidth() / 2;
		int y = m.getHeight() / 2;

		while (true)
		{
			if(m.getCellType(x, y) == Maze.PASSAGE)
			{
				m.setFinish(x, y);
				return;
			}
			if(Math.random() > 0.5)
			{
				x = (x+1) % m.getWidth();
			} else {
				y = (y+1) % m.getHeight();
			}
		}
	}

	public String getDescription()
	{
		return "Loopy Maze Generator, \u00a9 John Fearnley";
	}
}

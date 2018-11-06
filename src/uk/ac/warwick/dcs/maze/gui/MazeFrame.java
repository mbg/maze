/*
 * $Id: MazeFrame.java,v 1.11 2005/09/25 17:48:35 bpfoley Exp $
 * Copyright 2001 Phil C. Mueller
 * Copyright 2004 Brian P. Foley
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import uk.ac.warwick.dcs.maze.logic.*;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.filechooser.FileFilter;
import uk.ac.warwick.dcs.maze.gui.ControllerManager;
import uk.ac.warwick.dcs.maze.gui.CurrentClassInfo;
import uk.ac.warwick.dcs.maze.gui.GeneratorManager;
import uk.ac.warwick.dcs.maze.gui.MazeControlPanel;
import uk.ac.warwick.dcs.maze.gui.MazeGridPanel;
import uk.ac.warwick.dcs.maze.logic.Event;


/**
 *
 * @author  Phil C. Mueller
 * @author  Brian P. Foley
 */
public class MazeFrame extends JFrame implements IEventClient, ActionListener, FocusListener, WindowListener
{
	private static final String ABOUT_CMD="ABOUT";
	private static final String EXIT_CMD="EXIT";

	private JTabbedPane controllerPanel;
	private JMenuBar menuBar;
	private JMenu fileMenu, helpMenu;
	private JMenuItem aboutItem, exitItem;
	private JPanel mazePanel;
	private CurrentClassInfo curClassInfo;
	private MazeControlPanel mazeCtlPanel;
	private GeneratorManager generatorMgr;
	private ControllerManager controllerMgr;
	private MazeGridPanel mazeGridPanel;

	/* FocusListener methods */
	public void focusGained(FocusEvent e)
	{
		//this.JOptionPane.showMessageDialog(controllerManager, "Foo");
		//if (!doneValidate && !robotActive)
		//   EventBus.broadcast(new Event(IEvent.CHECK_NEW_CONTROLLERS, null));

		//  controllerManager.reloadChangedClasses();
		//doneValidate = true;
	}

	public void focusLost(FocusEvent e) {
		doneValidate = false;
	}

	/* WindowListener methods */
	public void windowOpened(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }

	public void windowActivated(WindowEvent e) {
		if (!doneValidate && !robotActive) {
			 EventBus.broadcast(new Event(IEvent.CHECK_NEW_CONTROLLERS, null));
		}
		doneValidate = true;
	}

	public void windowDeactivated(WindowEvent e) {
		doneValidate = false;
	}

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	/* ActionListener methods */
    public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ABOUT_CMD)) {
			showAbout();
			return;
		}
		if (e.getActionCommand().equals(EXIT_CMD)) {
			System.exit(0);
		}
    }

    private boolean doneValidate = true;
    private boolean robotActive = false;
    private Properties props;
    private MazeLogic mazeLogic;

   /** Creates new form MazeFrame */
    public MazeFrame(MazeLogic mazeLogic) {
       this.setTitle("CS118 Maze Courseware");
       props = new Properties();
       this.mazeLogic = mazeLogic;
       try {
          URL u = MazeFrame.class.getResource("/.courseware.properties");
          props.load(u.openStream());
          this.setTitle("The Maze" /* (Built: " + props.get("build") + ")" */);
       } catch (Exception e) {
          e.printStackTrace();
       }

       Image icon = null;
       URL iconURL = ClassLoader.getSystemResource("mazeicon.gif");

       if (iconURL != null)
          icon = Toolkit.getDefaultToolkit().getImage(iconURL);

       setIconImage(icon);
       initComponents();
       setSize(800, 600);
       doLayout();
	   Dimension screen =Toolkit.getDefaultToolkit().getScreenSize();
       this.setLocation((screen.width - getWidth()) /2,
                    (screen.height - getHeight()) /2);

       EventBus.addClient(this);
    }

   private void initComponents() {
      java.awt.GridBagConstraints gridBagConstraints;

      curClassInfo = new CurrentClassInfo();
      mazeCtlPanel = new MazeControlPanel();
      controllerMgr = new ControllerManager(mazeLogic.getControllerPool());
      generatorMgr = new GeneratorManager();
      mazeGridPanel = new MazeGridPanel(mazeLogic);

      controllerPanel = new JTabbedPane();
      mazePanel = new JPanel();
      menuBar = new JMenuBar();
      fileMenu = new JMenu();
      exitItem = new JMenuItem();
      helpMenu = new JMenu();
      aboutItem = new JMenuItem();

      getContentPane().setLayout(new GridBagLayout());

      addFocusListener(this);
      addWindowListener(this);

      controllerPanel.setTabPlacement(JTabbedPane.BOTTOM);
      controllerPanel.addTab("Controllers", null, controllerMgr, "");
      controllerPanel.setMnemonicAt(0, KeyEvent.VK_C);
      controllerPanel.addTab("Generators", null, generatorMgr, "");
	  controllerPanel.setMnemonicAt(1, KeyEvent.VK_G);


      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.weighty = 1.0;
      getContentPane().add(controllerPanel, gridBagConstraints);

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      getContentPane().add(mazeCtlPanel, gridBagConstraints);

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      getContentPane().add(curClassInfo, gridBagConstraints);

      mazePanel.setLayout(new java.awt.GridLayout(1, 0));

      mazePanel.setBackground(new Color(180, 180, 180));
      mazePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
      mazePanel.add(mazeGridPanel);

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      getContentPane().add(mazePanel, gridBagConstraints);

      fileMenu.setText("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

      exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
      exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.setText("Exit");
		exitItem.setActionCommand(EXIT_CMD);
      exitItem.addActionListener(this);

      fileMenu.add(exitItem);

      menuBar.add(fileMenu);

      helpMenu.setText("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
      aboutItem.setText("About...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setActionCommand(ABOUT_CMD);
      aboutItem.addActionListener(this);

      helpMenu.add(aboutItem);

      menuBar.add(helpMenu);

      setJMenuBar(menuBar);

      pack();
   }

    public void notify(IEvent event) {
       int message = event.getMessage();
       switch (message) {
           case IEvent.ADD_CONTROLLER:  controllerPanel.setSelectedIndex(0);
                                        break;
           case IEvent.ADD_GENERATOR:   controllerPanel.setSelectedIndex(1);
                                        break;
           case IEvent.ROBOT_REPORT:    showReport((IRobotReport)event.getData());
                                        break;
           case IEvent.ROBOT_START:     robotActive = true;
                                        break;
           case IEvent.ROBOT_RESET:     robotActive = false;
                                        break;

           case IEvent.LOAD_MAZE:       loadMaze();
                                        break;

           case IEvent.SAVE_MAZE:       saveMaze();
                                        break;

       }
    }
    private class MazeFileFilter extends  FileFilter
	{
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".warwickmaze");
		}

		public String getDescription() {
			return "Warwick maze files (*.warwickmaze)";
		}
	}

    private void loadMaze() {
        JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new MazeFileFilter());
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
				Maze maze = new Maze(jfc.getSelectedFile(), mazeLogic.getRobot());

                EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
			} catch (Exception e) {
				String msg = "The maze could not be loaded.\n"
					+ e.getMessage();

				JOptionPane.showMessageDialog(this,
											  msg,
											  "Error",
											  JOptionPane.ERROR_MESSAGE);
			}
        }
    }

	private String getFileExtension(File f) {
        String e = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            e = s.substring(i+1).toLowerCase();
        }
        return e;
    }

    private void saveMaze() {
        JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new MazeFileFilter());
		if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();
			if (getFileExtension(f) == null) {
				f = new File(f.getAbsolutePath() + ".warwickmaze");
			}
            try {
				mazeLogic.getMaze().writeToFile(f, mazeLogic.getRobot());
			} catch (Exception e) {
				String msg = "The maze could not be saved.\n"
					+ e.getMessage();
				JOptionPane.showMessageDialog(this,
											  msg,
											  "Error",
											  JOptionPane.ERROR_MESSAGE);
					System.out.println(e.getMessage());
			}
        }
    }

    private void showReport(IRobotReport rr) {

        JOptionPane.showMessageDialog(this,
                ordinal(rr.getRunNumber()+1) + " Run\n" +
                "Steps: " + rr.getSteps() + "\n" +
                "Collisions: " + rr.getCollisions() + "\n" +
                "Goal reached: " + rr.goalReached());
    }

    private void showAbout() {
       AboutBox ab = new AboutBox(this, props);
       ab.setVisible(true);
    }

    private String ordinal(int n) {
       switch (n) {
           case 1:      return "1st";
           case 2:      return "2nd";
           case 3:      return "3rd";
           default:     return n+"th";
       }
    }

}

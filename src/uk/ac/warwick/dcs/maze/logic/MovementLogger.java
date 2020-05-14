package uk.ac.warwick.dcs.maze.logic;

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class MovementLogger {

    public static final int DEADEND = 5000;
    public static final int CORRIDOR = 5001;
    public static final int JUNCTION = 5002;

    private long movesForward = 0;
    private long movesLeft = 0;
    private long movesRight = 0;
    private long movesBackwards = 0;

    public long getMovesForward() {
        return this.movesForward;
    }

    public long getMovesLeft() {
        return this.movesLeft;
    }

    public long getMovesRight() {
        return this.movesRight;
    }

    public long getMovesBackwards() {
        return this.movesBackwards;
    }

    public MovementLogger() {

    }

    /* Resets the move counters */
    public void reset() {
        this.movesForward = 0;
        this.movesLeft = 0;
        this.movesRight = 0;
        this.movesBackwards = 0;
    }

    /* Logs a direction */
    public void log(int direction) {
        // increment the direction
        switch(direction) {
            case IRobot.AHEAD:
                this.movesForward++;
                break;
            case IRobot.RIGHT:
                this.movesRight++;
                break;
            case IRobot.BEHIND:
                this.movesBackwards++;
                break;
            case IRobot.LEFT:
                this.movesLeft++;
                break;
        }

        // output a message summarising moves
        System.out.printf(
            "Summary of moves: Forward=%d Left=%d Right=%d Backwards=%d\n",
            this.movesForward, this.movesLeft, this.movesRight, this.movesBackwards);
    }

    public void log(int direction, int surroundings) {

    }
}

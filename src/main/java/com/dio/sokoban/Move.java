/*
 *  sokoban - a Sokoban game for android devices
 *  Copyright (C) 2010 Dedi Hirschfeld
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.dio.sokoban;

/**
 * A class describing a single game move.
 *
 * @author Dedi Hirschfeld
 */
public class Move
{
    //
    // Constants - directions.
    //

    /**
     * The 'up' direction.
     */
    public final static int DIR_UP = 0;

    /**
     * The 'down' direction.
     */
    public final static int DIR_DOWN = 1;

    /**
     * The 'left' direciton.
     */
    public final static int DIR_LEFT = 2;

    /**
     * The 'right' direciton.
     */
    public final static int DIR_RIGHT = 3;

    //
    // Members.
    //

    /**
     * The move direction.
     */
    private int m_dir;

    /**
     * A flag to indicate that this move moved a block. Used for undoing a
     * move.
     */
    private boolean m_isMoving = false;

    //
    // Operation.
    //

    /**
     * Create a move object describing a move in the given direction.
     */
    public Move(int dir)
    {
        m_dir = dir;
    }

    /**
     * Get the change to the 'x' coordinate when performing this move.
     */
    public int getXDelta()
    {
        int xDelta;
        switch (m_dir)
        {
            case DIR_LEFT:
                xDelta = -1;
                break;
            case DIR_RIGHT:
                xDelta = 1;
                break;
            default:
                xDelta = 0;
                break;
        }
        return xDelta;
    }

    /**
     * Get the change to the 'y' coordinate when performing this move.
     */
    public int getYDelta()
    {
        int yDelta;
        switch (m_dir)
        {
            case DIR_UP:
                yDelta = -1;
                break;
            case DIR_DOWN:
                yDelta = 1;
                break;
            default:
                yDelta = 0;
                break;
        }
        return yDelta;
    }

    /**
     * Get the move direction.
     * @return The move direction.
     */
    public int getDir()
    {
        return m_dir;
    }

    /**
     * Get the 'isMoving' flag, which indicates that this move is moving a
     * block.
     * @return the value of the 'isMoving' flag.
     */
    public boolean isMoving()
    {
        return m_isMoving;
    }

    /**
     * Set the 'isMoving' flag, to indicate that this move is moving a block.
     * @param isMoving true if the move is moving a block.
     */
    public void setMoving(boolean isMoving)
    {
        m_isMoving = isMoving;
    }
}

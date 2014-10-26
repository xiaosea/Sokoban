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

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * The Sokoban board.
 *
 * @author Dedi Hirschfeld
 */
public class Board
{
    //
    // Constants.
    //

    /**
     * The prefix for level files.
     */
    private final static String LEVEL_FILE_PREFIX =
    	"levels/level-";

    /**
     * The postfix for level files.
     */
    private final static String LEVEL_FILE_POSTFIX = ".txt";

    //
    // Members.
    //

    /**
     * The array of board squares.
     */
    private BoardSquare[][] m_boardSquares;

    /**
     * The x-coordinate of the player piece.
     */
    private int m_playerX;

    /**
     * The y-coordinate of the player piece.
     */
    private int m_playerY;

    /**
     * The board width.
     */
    private int m_boardWidth;

    /**
     * The board height.
     */
    private int m_boardHeight;

    /**
     * The number of empty target squares, which should have boxes but don't.
     */
    private int m_unsolvedTargets;


    //
    // Operations.
    //

    /**
     * Read The board from a level file.
     *
     * @param level The level to read.
     * @param assetsMgr The application's asset manager
     * @throws java.io.IOException if something went wrong in reading the board.
     */
    public void read(int level, AssetManager assetsMgr) throws IOException
    {
        read(LEVEL_FILE_PREFIX + level + LEVEL_FILE_POSTFIX, assetsMgr);
    }

    /**
     * Read The board from a file.
     *
     * @param filename The filename to read from.
     * @param assetsMgr The application's asset manager
     * @throws java.io.IOException if something went wrong in reading the board.
     */
    private void read(String filename, AssetManager assetsMgr)
        throws IOException
    {
        //InputStream inStream = new FileInputStream(filename);
        InputStream inStream = assetsMgr.open(filename);
        read(inStream);
        inStream.close();
    }

    /**
     * Read The board from an input stream.
     *
     * @param filename The filename to read from.
     * @throws java.io.IOException if something went wrong in reading the board.
     */
    private void read(InputStream inStream) throws IOException
    {
        int newPlayerX = -1;
        int newPlayerY = -1;
        Vector<Vector<BoardSquare>> newBoardVector =
            new Vector<Vector<BoardSquare>>(20);
        int newBoardWidth = 0;
        int curChar;

        m_unsolvedTargets= 0;
        do
        {
            Vector<BoardSquare> lineContents = new Vector<BoardSquare>(20);

            // Process one line of data.
            while(true)
            {
                curChar = inStream.read();
                if (curChar == '\n' || curChar == -1)
                    break;
                BoardSquare square = new BoardSquare((char)curChar);
                if (square.isStartPoint())
                {
                    newPlayerX = lineContents.size();
                    newPlayerY = newBoardVector.size();
                }
                if (square.isTarget() && !square.hasBox())
                {
                    m_unsolvedTargets++;
                }
                lineContents.addElement(square);
            }
            if (lineContents.size() > 0)
                newBoardVector.addElement(lineContents);
            if (newBoardWidth < lineContents.size())
                newBoardWidth = lineContents.size();
        }
        while (curChar != -1);

        m_boardSquares = boardFromVector(newBoardVector, newBoardWidth);
        m_playerX = newPlayerX;
        m_playerY = newPlayerY;
        m_boardHeight = m_boardSquares.length;
        m_boardWidth = newBoardWidth;

        // markInsideSquares needs a point inside the board to start it's
        // marking. it seems safe to assume that the player is inside the board.
        markInsideSquares(m_playerX, m_playerY);
    }

    /**
     * Helper method: mark all squares that are inside the game board. This uses
     * a flood-fill algorithm. When called with a point inside the board, it
     * marks it, and all other connected points until walls are reached.
     *
     * The algorithm used is a simple recursive fill - chosen for it's
     * simplicity. The downside, though, is that a large board might cause a
     * stack overflow. But it seems like current Android screens aren't big
     * enough to display such large boards anyway.
     *
     * @param initialX The initial point X coordinate
     * @param initialY The initial point Y coordinate
     */
    private void markInsideSquares(int initialX, int initialY)
    {
        BoardSquare square = getSquare(initialX, initialY);
        if (square == null || square.isInsideBoard() || square.isWall())
            return;

        square.setIsInsideBoard(true);
        markInsideSquares(initialX - 1, initialY);
        markInsideSquares(initialX + 1, initialY);
        markInsideSquares(initialX, initialY - 1);
        markInsideSquares(initialX, initialY + 1);
    }

    /**
     * Helper method - create a board array from a vector of vectors of
     * squares.
     * @param boardVec The vector to read from.
     * @param width The new board width.
     * @return
     */
    private BoardSquare[][] boardFromVector(Vector<Vector<BoardSquare>>boardVec,
            int width)
    {
        BoardSquare[][] newBoard = new BoardSquare[boardVec.size()][width];
        for (int curLine = 0; curLine < newBoard.length; curLine++)
        {
            Vector<BoardSquare> lineVector =
                (Vector<BoardSquare>)boardVec.elementAt(curLine);
            int squaresInLine = lineVector.size();
            for (int curSquare = 0; curSquare < squaresInLine; curSquare++)
            {
                newBoard[curLine][curSquare] =
                    (BoardSquare)lineVector.elementAt(curSquare);
            }
        }
        return newBoard;
    }

    /**
     * Get the board string representation (for debugging).
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < m_boardHeight; i++)
        {
            for (int j = 0; j < m_boardWidth; j++)
            {
                BoardSquare square = m_boardSquares[i][j];
                if (square != null)
                {
                    buffer.append(square.toChar());
                }
            }
            if ( i < m_boardHeight - 1)
                buffer.append('\n');
        }
        return buffer.toString();
    }

    /**
     * Get the current board's width.
     * @return
     */
    public int getBoardHeight()
    {
        return m_boardHeight;
    }

    /**
     * Get the current board's height.
     * @return
     */
    public int getBoardWidth()
    {
        return m_boardWidth;
    }

    /**
     * Get board square at the given position. The returned square should
     * be treated as read-only, and shouldn't be changed. The square might be
     * undefined (if it was not defined in the original board data), in which
     * case null is returned.
     *
     * @return the contents of the square, or null if the square is undefined.
     */
    public BoardSquare getSquare(int x, int y)
    {
        try {
            return m_boardSquares[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Return the player's X coordinate.
     * @return The player's X coordinate.
     */
    public int getPlayerX()
    {
        return m_playerX;
    }

    /**
     * Return the player's Y coordinate.
     * @return The player's Y coordinate.
     */
    public int getPlayerY()
    {
        return m_playerY;
    }

    /**
     * Try to perform a move on the board, making sure it is legal.
     * @param move The move to perform.
     * @return true if the move could be performed, false if it was illegal.
     */
    public boolean move(Move move)
    {
        boolean moveOk = false;
        int xDelta = move.getXDelta();
        int yDelta = move.getYDelta();

        // First, let's figure out if the move is legal. The assumption
        // is, the game area is closed on all sides with walls.
        // Otherwise, the code bellow could throw an exception:
        int targetX = m_playerX + xDelta;
        int targetY = m_playerY + yDelta;
        BoardSquare playerTargetSquare = getSquare(targetX, targetY);
        BoardSquare newBoxSquare = null;
        if (!playerTargetSquare.isWall())
        {
            if (!playerTargetSquare.hasBox())
            {
                moveOk = true;
            }
            else // We're moving to a boxed position.
            {
                int newBoxX = targetX + xDelta;
                int newBoxY = targetY + yDelta;
                newBoxSquare = getSquare(newBoxX, newBoxY);
                moveOk = !newBoxSquare.hasBox() && !newBoxSquare.isWall();
            }
        }

        // Now do the actual move.
        if (moveOk)
        {
            m_playerX = targetX;
            m_playerY = targetY;

            if (newBoxSquare != null)
            {
                move.setMoving(true);
                moveBox(playerTargetSquare, newBoxSquare);
            }
        }
        return moveOk;
    }

    /**
     * Helper method - move the box from one square to another
     * @param srcSquare - the original square.
     * @param targetSquare - the target to move the box to.
     */
    private void moveBox(BoardSquare srcSquare, BoardSquare targetSquare)
    {
        srcSquare.setBox(false);
        targetSquare.setBox(true);
        if (srcSquare.isTarget())
        {
            m_unsolvedTargets++;
        }

        if (targetSquare.isTarget())
        {
            m_unsolvedTargets--;
        }
    }

    /**
     * Undo a move done in the board. This has to be called when the board
     * is set up correctly, otherwise unpredictable things will happen. No
     * validity checks are performed.
     *
     * @param move The move to undo.
     */
    public void undoMove(Move move)
    {
        int xDelta = move.getXDelta();
        int yDelta = move.getYDelta();

        if (move.isMoving())
        {
            int boxX = m_playerX + xDelta;
            int boxY = m_playerY + yDelta;
            BoardSquare boxSquarePostMove = getSquare(boxX, boxY);
            BoardSquare boxSquarePreMove = getSquare(m_playerX, m_playerY);
            moveBox(boxSquarePostMove, boxSquarePreMove);
        }

        m_playerX = m_playerX - xDelta;
        m_playerY = m_playerY - yDelta;
    }

    /**
     * Check to see if this board was solved.
     */
    public boolean isSolved()
    {
        return m_unsolvedTargets == 0;
    }
}

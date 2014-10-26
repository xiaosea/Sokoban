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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * The sokoban game view. The game view is a view that is associated with a
 * board, and can draw it.
 *
 * @author Dedi Hirschfeld
 */
public class SokoView extends View
{
    //
    // Members.
    //

    /**
     * The associated game activity.
     */
    private SokoGameActivity m_game;

    /**
     * The paint object to use (for now).
     */
    private Paint m_paint = new Paint();

    /**
     * The resource manager.
     */
    private GameResourceManager m_resourceManager;


    //
    // Operations.
    //

    /**
     * Create a view. In order to make the view actually show anything,
     * call setGame() to set the actual game activity (and board).
     */
    public SokoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        m_resourceManager = new GameResourceManager(getResources());
    }

    /**
     * Create a view. In order to make the view actually show anything,
     * call setGame() to set the actual game activity (and board).
     */
    public SokoView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    /**
     * set the game activity object associated with this view.
     */
    public void setGame(SokoGameActivity game)
    {
        m_game = game;
        invalidate();
    }

    /**
     * Refresh the canvas.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
        // This will draw the background for the view.
        super.onDraw(canvas);

        if (m_game == null)
            return;

        m_paint = new Paint();
        m_paint.setAntiAlias(true);
        setPadding(3, 3, 3, 3);

        Board board = m_game.getBoard();
        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

        int squareRealSize = getSquareSize();

        for (int row = 0; row < boardHeight; row++)
        {
            for (int column = 0; column < boardWidth; column++)
            {
                drawSquare(column, row, squareRealSize, board, canvas);
            }
        }
    }

    /**
     * Calculate the best size for a game square, based on the current board
     * coordinates and view size.
     * @return
     */
    private int getSquareSize()
    {
        int scrWidth = getWidth();
        int scrHeight = getHeight();

        Board board = m_game.getBoard();
        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

        int squareWidth = scrWidth / boardWidth;
        int squareHeight = scrHeight / boardHeight;
        // Since we want all squares to be - well, square - we need to choose
        // just one size for width and height.
        int squareBestSize = (squareWidth < squareHeight) ?
                squareWidth : squareHeight;
        return squareBestSize;
    }

    /**
     * draw the contents of the given square.
     *
     * @param column The square column.
     * @param row The square row.
     * @param squareSize The square width and height.
     * @param board The game board.
     * @param g The graphics object to draw on.
     */
    private void drawSquare(int column, int row,
                             int squareSize, Board board,
                             Canvas canvas)
    {
        BoardSquare square = board.getSquare(column, row);
        if (square == null)
            return;

        if (square.isWall())
        {
            drawBitmap(m_resourceManager.getWallBitmap(), column, row,
                    squareSize, canvas);
            return;
        }

        if (square.isInsideBoard())
        {
            drawBitmap(m_resourceManager.getTileBitmap(), column, row,
                    squareSize, canvas);
        }

        if (square.isTarget())
        {
            drawBitmap(m_resourceManager.getTargetBitmap(), column, row,
                       squareSize, canvas);
        }

        if (square.hasBox())
        {
            drawBitmap(m_resourceManager.getBoxBitmap(), column, row,
                    squareSize, canvas);
        }

        if ((row == board.getPlayerY()) && (column == board.getPlayerX()))
        {
            drawBitmap(m_resourceManager.getPlayerBitmap(),
                    column, row, squareSize, canvas);
        }
    }

    /**
     * Draw the given bitmap in the given position.
     *
     * @param column
     * @param row
     * @param squareSize
     * @param canvas
     */
    private void drawBitmap(Bitmap bitmap, int column, int row, int squareSize,
                Canvas canvas)
    {
        int squareLeft = column * squareSize;
        int squareTop = row * squareSize;
        Rect rect = new Rect(squareLeft, squareTop,
                squareLeft + squareSize - 1, squareTop + squareSize - 1);
        canvas.drawBitmap(bitmap, null, rect, null);
    }
}

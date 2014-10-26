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
 * A single Sokoban board square.
 * Because the square is read from a text file, each square is constructed
 * based on an encoded character.
 *
 * Encoded char meaning:</br>
 *
 * #     - Wall</br>
 * SPACE - Open floor-space</br>
 * @     - Player Start Point (on open floor-space)</br>
 * +     - Player Start Point (on a target spot)</br>
 * .     - Target Spot</br>
 * $     - Box (on open floor-space)</br>
 * *     - Box (on target spot)</br>
 *
 * @author Dedi Hirschfeld
 */
public class BoardSquare
{
    //
    // Bit masks for square contents. Not all combinations are legal,
    // of course.
    //

    /**
     * Mask for a wall in this square. If true, no other bit can be
     * turned on.
     */
    private final static byte IS_WALL = 0x01;

    /**
     * Mask for a box on this square.
     */
    private final static byte HAS_BOX = 0x02;

    /**
     * Mask for a target square.
     */
    private final static byte IS_TARGET = 0x04;

    /**
     * Mask for player starting point. At some point, we can get rid of this,
     * since the board square doesn't actually do anything with it but read it.
     */
    private final static byte IS_START_POINT = 0x08;

    /**
     *  Mask for the 'isInsideBoard' flag.
     */
    private final static byte IS_INSIDE_BOARD = 0x10;

    /**
     * A string of known characters, each in a position representing it's
     * byte value.
     */
    private final static String ENCODED_CHARS = " #$ . * @   +";


    //
    // Members.
    //

    private byte m_squareContents;

    //
    // Operations.
    //

    /**
     * Create a board square, by reading it from an encoded char. See above in
     * the class documentation for the encoding for the board square.
     *
     * @param encodedContents The encoded contents.
     */
    public BoardSquare(char encodedContents)
    {
        m_squareContents = contentByteFromChar(encodedContents);

    }

    /**
     * @return true if the given square is a walll square.
     */
    public boolean isWall()
    {
        return (m_squareContents & IS_WALL) != 0;
    }

    /**
     * @return true if the given square has a box on it.
     */
    public boolean hasBox()
    {
        return (m_squareContents & HAS_BOX) != 0;
    }

    /**
     * Put or remove a box from this square.
     *
     * @param isThere. True to put a box, false to remove it.
     */
    public void setBox(boolean isThere)
    {
        if (isThere)
        {
            m_squareContents |= HAS_BOX;
        }
        else
        {
            m_squareContents &= (~HAS_BOX);
        }
    }

    /**
     * Return the 'is_inside_board' flag. This is set to true for all squares
     *  that are inside the game board (that is, that are surounded by walls),
     *  and to false for all others.
     */
    public boolean isInsideBoard()
    {
        return (m_squareContents & IS_INSIDE_BOARD) != 0;
    }

    /**
     * Set the 'isInsideBoard' flag.
     */
    public void setIsInsideBoard(boolean isInside)
    {
        if (isInside)
        {
            m_squareContents |= IS_INSIDE_BOARD;
        }
        else
        {
            m_squareContents &= (~IS_INSIDE_BOARD);
        }

    }

    /**
     * @return true if the given square is a target square.
     */
    public boolean isTarget()
    {
        return (m_squareContents & IS_TARGET) != 0;
    }

    /**
     * @return true if the given square is the player's starting point.
     */
    public boolean isStartPoint()
    {
        return (m_squareContents & IS_START_POINT) != 0;
    }

    /**
     * Create a content byte from the given encoded char.
     *
     * @param encoded The encoded char
     * @return A byte representing the square content.
     */
    private byte contentByteFromChar(char encoded)
    {
        int charIndex = ENCODED_CHARS.indexOf(encoded);
        return (charIndex > 0 ? (byte)charIndex : 0);
    }

    /**
     * Get the square string representation. This will return the one-char
     * encoded square.
     */
    public char toChar()
    {
        return ENCODED_CHARS.charAt(m_squareContents);
    }
}

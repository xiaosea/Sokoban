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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * The resource manager for the Sokoban game. Currently, it simply manages the
 * image bitmaps.
 *
 *
 * @author dedi
 *
 */
public class GameResourceManager {

    //
    // Members.
    //

    /**
     * The android resource loader/manager to use for accessing resources.
     */
    private Resources m_resourceLoader;

    /**
     * Player bitmap;
     */
    private Bitmap m_playerBitmap;

    /**
     * Bitmap for box
     */
    private Bitmap m_boxBitmap;

    /**
     * Bitmap for box target;
     */
    private Bitmap m_targetBitmap;

    /**
     * Bitmap for the floor tile
     */
    private Bitmap m_tileBitmap;

    /**
     * Bitmap for the wall
     */
    private Bitmap m_wallBitmap;


    //
    // Operations.
    //

    /**
     * Create the game's resource manager.
     */
    public GameResourceManager(Resources resourceLoader)
    {
        m_resourceLoader = resourceLoader;
        loadBitmaps();
    }

    /**
     * Get the player bitmap.
     */
    public Bitmap getPlayerBitmap()
    {
        return m_playerBitmap;
    }

    /**
     * Get the target bitmap.
     * @return
     */
    public Bitmap getTargetBitmap()
    {
        return m_targetBitmap;
    }

    /**
     * Get the box bitmap.
     * @return
     */
    public Bitmap getBoxBitmap()
    {
        return m_boxBitmap;
    }

    /**
     * Get the tile bitmap.
     */
    public Bitmap getTileBitmap()
    {
        return m_tileBitmap;
    }

    /**
     * Get the wall bitmap.
     */
    public Bitmap getWallBitmap()
    {
        return m_wallBitmap;
    }

    /**
     * Load the needed bitmaps.
     * TODO: Resize when needed, instead of on every draw.
     */
    private void loadBitmaps()
    {
        m_playerBitmap = loadBitmap(R.drawable.man);
        m_boxBitmap = loadBitmap(R.drawable.box);
        m_targetBitmap = loadBitmap(R.drawable.ksok_goal);
        m_tileBitmap = loadBitmap(R.drawable.tile);
        m_wallBitmap = loadBitmap(R.drawable.wall);
    }

    /**
     * Load a bitmap from resource.
     * TODO: check whether we can use BitmapFactory instead.
     * @param resId The resource ID of the bitmap to load.
     * @return
     */
    private Bitmap loadBitmap(int resId)
    {
        Drawable sprite = m_resourceLoader.getDrawable(resId);
        int width = sprite.getIntrinsicWidth();
        int height = sprite.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        sprite.setBounds(0, 0, width, height);
        sprite.draw(canvas);
        return bitmap;
      }
}

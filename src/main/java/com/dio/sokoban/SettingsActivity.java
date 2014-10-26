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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * The timer settings dialog activity.
 * @author dedi
 */
public class SettingsActivity extends PreferenceActivity
{
    /**
     * Activity was created. Set the view according to the XML settings state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static boolean isSoundOn(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.PREF_SOUND_KEY), true);
    }
}

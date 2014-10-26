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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.HashMap;

/**
 * A dialog factory class. This class is written as a workaround to the horrible
 * way the Android API implements dialog creation. Instead of actually creating
 * them from the 'onCreateDialog' (and identifying them using a constant
 * integer), you can create them from anywhere in the activity by calling
 * 'doActivityDialog'. In order for this to work, the activity
 * onCreateDialog method must call the 'getPendingDialog' method of the
 * singleton factory object, and return the dialog it returns.
 * Additionally, this class implements some helper methods to create a,
 * messagebox, a yes-no dialog, and an 'ok-cancel' dialog.
 *
 * TODO: Take a look at this, and maybe use 'setOwnerActivity' method instead.
 * Otherwise, at least rename the thing.
 *
 * @author dedi
 *
 */
public class DialogFactory
{
    //
    // Members
    //

    /**
     * The one and only dialog factory object.
     */
    private final static DialogFactory c_dialogFactory = new DialogFactory();

    /**
     * A table of dialogs waiting to be associated with activities, keyed by
     * their IDs.
     */
    private HashMap<Integer, Dialog> m_pendingDialogsTable =
        new HashMap<Integer, Dialog>();

    /**
     * A button click listener that will dismiss the dialog.
     */
    private DialogInterface.OnClickListener m_dismissHandler;

    /**
     * A button click listener that will cancel the dialog.
     */
    private DialogInterface.OnClickListener m_cancelHandler;

    /**
     * The next available dialog ID.
     */
    private int m_nextDialogId = 0;


    //
    // Operations;
    //

    /**
     * Constructor is made private to prevent the creation of additional
     * methods.
     */
    private DialogFactory()
    {
    }

    /**
     * Get the single dialog factory object.
     */
    public static DialogFactory getInstance()
    {
        return c_dialogFactory;
    }

    /**
     * Do the given dialog as a dialog of the given activity. That is, associate
     * it with an ID, put it in the pending dialogs table, and call showDialog
     * on the activity to actually show it.
     */
    public void doActivityDialog(Activity activity, Dialog dialog)
    {
        // In the unlikely case that nextDialogId actually overlapped, find the
        // next available ID. This ignores the even-more-unlikely case that
        // there are currently 2^32 pending dialogs...
        while (m_pendingDialogsTable.containsKey(m_nextDialogId))
            m_nextDialogId++;

        int dialogId = m_nextDialogId++;

        m_pendingDialogsTable.put(dialogId, dialog);
        activity.showDialog(dialogId);
    }

    /**
     * Return the dialog with the given ID, waiting to be created. This method
     * must be called from the onCreateDialog method of the activity object.
     */
    public Dialog getPendingDialog(int dialogId)
    {
        return m_pendingDialogsTable.remove(dialogId);
    }

    /**
     * Create a message box associated with the given activity.
     *
     * @param activity The activity object
     * @param message The messagebox text.
     * @param buttonAction The action to take when the button is pressed
     * (could be null).
     */
    public void messageBox(Activity activity, CharSequence message,
            CharSequence okButtonCaption,
            final DialogInterface.OnClickListener buttonAction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        DialogInterface.OnClickListener okAction =
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (buttonAction != null)
                {
                    buttonAction.onClick(dialog, id);
                }
            }
        };
        builder.setNeutralButton(okButtonCaption, okAction);
        Dialog dialog = builder.create();
        doActivityDialog(activity, dialog);
    }

    /**
     * Create a message box associated with the given activity,
     * displaying the given view.
     *
     * @param activity The activity object
     * @param view The view to show.
     * @param buttonAction The action to take when the button is pressed
     * (could be null).
     */
    public void messageBoxFromURI(Activity activity, View view,
            CharSequence okButtonCaption,
            final DialogInterface.OnClickListener buttonAction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        DialogInterface.OnClickListener okAction =
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if (buttonAction != null)
                {
                    buttonAction.onClick(dialog, id);
                }
            }
        };
        builder.setNeutralButton(okButtonCaption, okAction);
        Dialog dialog = builder.create();
        doActivityDialog(activity, dialog);
    }

    /**
     * Create an 'Yes/No' messagebox associated with the given activity,
     * with the given actions.
     */
    public void yesNoMessageBox(Activity activity, CharSequence message,
            CharSequence yesButtonCaption,
            DialogInterface.OnClickListener yesAction,
            CharSequence noButtonCaption,
            DialogInterface.OnClickListener noAction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(yesButtonCaption, yesAction);
        builder.setNegativeButton(noButtonCaption, noAction);
        Dialog dialog = builder.create();
        doActivityDialog(activity, dialog);
    }

    /**
     * Create an activity exit confirmation dialog.
     */
    public void exitConfirmationBox(final Activity activity,
                                    CharSequence questionText,
                                    CharSequence yesButtonCaption,
                                    CharSequence noButtonCaption)
    {
        DialogInterface.OnClickListener yesAction =
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                   activity.finish();
              }
          };

          yesNoMessageBox(activity, questionText,
                  yesButtonCaption, yesAction,
                  noButtonCaption, getCancelHandler());
    }

    /**
     * get a button click listener that will dismiss the dialog.
     */
    public synchronized DialogInterface.OnClickListener getDismissHandler()
    {
        if (m_dismissHandler == null)
        {
            m_dismissHandler = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            };
        }
        return m_dismissHandler;
    }

    /**
     * get a button click listener that will cancel the dialog.
     */
    public synchronized DialogInterface.OnClickListener getCancelHandler()
    {
        if (m_cancelHandler == null)
        {
            m_cancelHandler = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            };
        }
        return m_cancelHandler;
    }

}

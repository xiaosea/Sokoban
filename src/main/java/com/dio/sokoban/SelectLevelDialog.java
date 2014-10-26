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

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * The level selection dialog.
 *
 * @author dedi
 */
public class SelectLevelDialog extends Dialog
    implements OnItemClickListener, ScrollLayout.OnSnapListener,PageControl.OnPageControlListener
{
    //
    // Members.
    //

    /**
     * The Sokoban activity this dialog is associated with.
     */
    private SokoGameActivity m_owner;

    /**
     * The level pageControl.
     */
    private PageControl m_PageControl;

    /**
     * The level scrollLayout.
     */
    private ScrollLayout m_ScrollLayout;

    //
    // Operations.
    //

    /**
     * Create a level selection dialog object, associated with a
     * Sokoban activity.
     */
    public SelectLevelDialog(SokoGameActivity owner)
    {
        super(owner);
        m_owner = owner;
        setOwnerActivity(owner);
    }


    /**
     * Actually create the dialog.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_level_dialog);
        setTitle(R.string.SELECT_LEVEL_DIALOG_TITLE);

        m_ScrollLayout = (ScrollLayout)findViewById(R.id.scrollLayout);
        m_ScrollLayout.setOnSnapListener(this);
        int pageSize = m_owner.getPageSize();
        int count = m_owner.getMaxLevel() / pageSize;
        if (count > 1) {
            m_PageControl = new PageControl(m_owner, count,
                PageControl.DEFAULT_POSITION);
            m_PageControl.setOnPageControlListener(this);
            LinearLayout pageControlLayout = (LinearLayout)findViewById(R.id.pageControlLayout);
            pageControlLayout.addView(m_PageControl);
          }

        int height = 0;
        for (int i = 0; i < count; i++) {
            LayoutInflater inflater = m_owner.getLayoutInflater();
            GridView gridView = (GridView)inflater.inflate(R.layout.gridview, null);
            gridView.setAdapter(new LevelAdapter(m_owner, i * pageSize + 1, i * pageSize + pageSize));
            gridView.setOnItemClickListener(this);
            m_ScrollLayout.addView(gridView);
            if (height == 0) {
                height = getTotalHeightOfGridView(gridView);
            }
		}
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        params.addRule(RelativeLayout.BELOW, R.id.pageControlLayout);
        m_ScrollLayout.setLayoutParams(params);
        
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.85f;
        getWindow().setAttributes(lp);
    }

    private int getTotalHeightOfGridView(GridView gridView) {
        int totalHeight = 0;
        Adapter adapter = gridView.getAdapter();
        if (adapter.getCount() > 0) {
            int num = gridView.getContext().getResources().getInteger(R.integer.NUM_COLUMNS_GRID_VIEW);
            View view = adapter.getView(0, null, gridView);
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight = (view.getMeasuredHeight() + gridView.getContext().getResources().getDimensionPixelSize(R.dimen.grid_view_spacing)) * (adapter.getCount() / num);
        }
        return totalHeight;
    }

    /**
     * Display a 'bad level selected' error message.
     */
//    private void doBadLevelError()
//    {
//        int maxLevel = m_owner.getMaxLevel();
//        String errMsg =
//            m_owner.getString(R.string.ERR_BAD_LEVEL_SELECTED, maxLevel);
//        String okButtonCaption = m_owner.getString(R.string.OK_BUTTON_CAPTION);
//        DialogFactory dialogFactory = DialogFactory.getInstance();
//        DialogInterface.OnClickListener dismissAction =
//            dialogFactory.getDismissHandler();
//        dialogFactory.messageBox(m_owner, errMsg,
//                                 okButtonCaption, dismissAction);
//    }
    
    class LevelAdapter extends BaseAdapter {
        private SokoGameActivity mSokoGameActivity;
        private List<Integer> mLevels;
        
        public LevelAdapter(SokoGameActivity sokoGameActivity, int start, int end) {
            mSokoGameActivity = sokoGameActivity;
            mLevels = new ArrayList<Integer>();
            for (int i = start; i <= end; i++) {
            	mLevels.add(i);
			}
        }
        
		@Override
		public int getCount() {
			return mLevels.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            int level = mLevels.get(position);
            if (convertView == null) {
                textView = new TextView(mSokoGameActivity);
                textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                textView.setPadding(8, 8, 8, 8);
                textView.setGravity(Gravity.CENTER);
            } else {
                textView = (TextView) convertView;
            }
            
            if (m_owner.getInvalLevels().contains(String.valueOf(level))) {
            	textView.setTag(level);
			}
			textView.setText(m_owner.getInvalLevels().contains(String.valueOf(level)) ? "x" : String.valueOf(level));
			textView.setBackgroundColor(level == mSokoGameActivity.getLevel() ? 0x77ff0000
					: mSokoGameActivity.getPassedLevels().contains(
							String.valueOf(level)) ? 0x00000000 : 0x775DA0E3);

            return textView;
		}
    	
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	String text = ((TextView)view).getText().toString();
    	int newLevel = "x".equals(text) ? Integer.parseInt(((TextView)view).getTag().toString()) : Integer.parseInt(text);
//        if (newLevel > 0 && newLevel <= m_owner.getMaxLevel())
            m_owner.setLevel(newLevel);
//        else
//            doBadLevelError();
        dismiss();
    }
    
    public void invalidate()
    {
    	if (m_ScrollLayout != null) {
    		for (int i = 0, count = m_ScrollLayout.getChildCount(); i < count; i++) {
        		((BaseAdapter)((GridView)m_ScrollLayout.getChildAt(i)).getAdapter()).notifyDataSetChanged();
			}
		}
    }


	@Override
	public void onPageControl(Object tag) {
		m_ScrollLayout.snapToScreen(Integer.parseInt(tag.toString()));
	}


	@Override
	public void onSnapComplete() {
	    if (m_PageControl != null) {
	    	m_PageControl.moveToPosition(m_ScrollLayout.getCurScreen());
	      }
	}
}

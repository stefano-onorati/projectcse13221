package ca.yorku.cse.mack.searchInterfaceExperiment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WordAdapter extends BaseAdapter
{
    String[] words;

    WordAdapter(String[] phrasesArg)
    {
        words = phrasesArg;
    }

    @Override
    public int getCount()
    {
        return words.length;
    }

    @Override
    public String getItem(int position)
    {
        return words[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = new TextView(parent.getContext());
            view.setPadding(6, 6, 6, 6);
            view.setBackgroundColor(Color.LTGRAY);
            ((TextView)view).setTextColor(Color.BLACK);
            ((TextView)view).setTextSize(24);
        }
        ((TextView)view).setText(words[position]);
        return view;
    }
}

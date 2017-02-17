package ca.yorku.cse.mack.searchInterfaceExperiment;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This, FastSearchListView, SimpleAdapter classes used from:
 * https://github.com/survivingwithandroid/Surviving-with-android/tree/master/ListView_SectionIndexer
 */

public class SimpleAdapter extends ArrayAdapter<String> implements SectionIndexer {

    private List<String> itemList;
    private String[] selectedItems;
    private int selectedItemIndex = 0;
    private Context context;
    private static String sections = "abcdefghijklmnopqrstuvwxyz";
    private int savedIndex = 0;
    private int totalClicks = 0;

    public SimpleAdapter(List<String> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        return itemList.size();
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public String getItem(int position) {
        return itemList.get(position);
    }

    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    public void setSelectedItems(String[] items) { this.selectedItems = items; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView text = (TextView) v.findViewById(android.R.id.text1);
        text.setText(itemList.get(position));

        v.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("SEARCHDEBUG", "CLICK");
                totalClicks++;
                if (itemList.get(position).equals(selectedItems[selectedItemIndex])) {
                    selectedItemIndex++;
                    Log.i("MYDEBUG", selectedItemIndex + "");
                    if (selectedItemIndex < selectedItems.length) {
                        Toast.makeText(context.getApplicationContext(), "CORRECT! Next item...", Toast.LENGTH_SHORT).show();
                        if (((SIEScroll)context).getActionBar() != null)
                            ((SIEScroll)context).getActionBar().
                                    setTitle("FIND: " + selectedItems[selectedItemIndex]);
                    } else {
                        Bundle b = new Bundle();
                        //b.putDouble("averageTime", (SIEScroll.timeElapse/selectedItems.length));
                        b.putDouble("averageTime", SIEScroll.timeElapse);
                        b.putDouble("efficiencyRate", ((double) selectedItems.length/ (double) totalClicks) * 100);
                        b.putDouble("searchRate", selectedItems.length / (SIEScroll.timeElapse / 60));
                        b.putString("initials", ((SIEScroll) context).getInitials());
                        b.putString("search", ((SIEScroll) context).getSearch());
                        b.putString("array", ((SIEScroll) context).getArray());
                        b.putString("conditionCode", ((SIEScroll) context).getConditionCode());
                        b.putString("participant", ((SIEScroll) context).getParticipant());
                        b.putString("group", ((SIEScroll) context).getGroup());
                        b.putString("session", ((SIEScroll) context).getSession());
                        Intent i = new Intent(context, SIEResults.class);
                        i.putExtras(b);
                        context.startActivity(i);
                        ((SIEScroll) context).finish();
                    }

                } else {
                    Toast.makeText(context.getApplicationContext(), "WRONG! Try again...", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return v;

    }

    @Override
    public int getPositionForSection(int section) {
        int index = savedIndex;

        for (int i=0; i < this.getCount(); i++) {
            String item = this.getItem(i).toLowerCase();
            if (item.charAt(0) == sections.charAt(section)) {
                index = i;
                savedIndex = index;
                break;
            }
        }

        return index;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        Log.d("ListView", "Get section");
        return 0;
    }

    @Override
    public Object[] getSections() {
        Log.d("ListView", "Get sections");
        String[] sectionsArr = new String[sections.length()];
        for (int i=0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);

        return sectionsArr;
    }
}
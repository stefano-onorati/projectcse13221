package ca.yorku.cse.mack.searchInterfaceExperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

/**
 * This, FastSearchListView, SimpleAdapter classes used from:
 * https://github.com/survivingwithandroid/Surviving-with-android/tree/master/ListView_SectionIndexer
 */

@SuppressWarnings("unused")
public class SIEScroll extends Activity {
    private FastSearchListView listView;
    private String search, array, conditionCode, initials, participant, group, session;
    public static double timeElapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout);

        Bundle b = getIntent().getExtras();
        search = b.getString("search");
        array = b.getString("array");
        conditionCode = b.getString("conditionCode");
        initials = b.getString("initials");
        participant = b.getString("participant");
        group = b.getString("group");
        session = b.getString("session");

        int arrayID = 0;
        timeElapse = 0.0;

        if (array.equals("Food"))
            arrayID = R.array.food;
        else if (array.equals("People"))
            arrayID = R.array.people;
        else if (array.equals("Countries"))
            arrayID = R.array.country;
        else if (array.equals("Movies"))
            arrayID = R.array.movie;
        else if (array.equals("Sports Teams"))
            arrayID = R.array.team;

        String[] a = getResources().getStringArray(arrayID);

        List<String> list = new ArrayList<String>();
        List<String> temp = new ArrayList<String>();
        for (int i = 0; i < a.length; i++) {
            list.add(a[i]);
            temp.add(a[i]);
        }

        Collections.sort(list);
        listView = (FastSearchListView) findViewById(R.id.listview);
        SimpleAdapter sa = new SimpleAdapter(list, this);
        listView.setAdapter(sa);

        String[] randomArray = new String[5]; //array of 5 randomly chosen strings
        Random r = new Random();
        //get five random items from a temporary array list
        //and delete that item to avoid duplicates
        for (int i = 0; i < randomArray.length; i++) {
            int position = r.nextInt(temp.size());
            randomArray[i] = temp.get(position);
            temp.remove(position);
        }
        sa.setSelectedItems(randomArray);
        setActionBarTitle("FIND: " + randomArray[sa.getSelectedItemIndex()]);
    }

    public void setActionBarTitle(String title) {
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }

    public String getArray() {return array; }

    public String getSearch() { return search; }

    public String getConditionCode() { return conditionCode; }

    public String getInitials() { return initials; }

    public String getParticipant() { return participant; }

    public String getGroup() { return group; }

    public String getSession() { return session; }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), SIESetup.class);
        startActivity(i);
    }
}


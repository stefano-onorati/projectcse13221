package ca.yorku.cse.mack.searchInterfaceExperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Source code from: http://www.thanhcs.com/2014/06/android-search-in-lisview.html
 */
public class SIEText extends Activity {

	EditText editsearch;
	ListView listView;
	private ArrayList<String> mItems, tempItems, searchedItems;
	private String search, array, conditionCode, initials, participant, group, session;
	private String[] selectedItems;
	private double[] times;
	private int totalClicks, selectedItemIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textlayout);
		
		editsearch = (EditText)findViewById(R.id.editText1);
		listView = (ListView)findViewById(R.id.listView1);

		Bundle b = getIntent().getExtras();
		search = b.getString("search");
		array = b.getString("array");
		conditionCode = b.getString("conditionCode");
		initials = b.getString("initials");
		participant = b.getString("participant");
		group = b.getString("group");
		session = b.getString("session");

		totalClicks = 0;
		selectedItemIndex = 0;

		int arrayID = 0;

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

		selectedItems = getResources().getStringArray(arrayID);
        mItems = new ArrayList<String>();
		tempItems = new ArrayList<String>();
		searchedItems = new ArrayList<String>();

		for (int i = 0; i < selectedItems.length; i++) {
			mItems.add(selectedItems[i]);
			tempItems.add(selectedItems[i]);
		}

		Collections.sort(mItems);
		searchedItems.addAll(mItems);

		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems));

		final String[] randomArray = new String[5]; //array of 5 randomly chosen strings
		Random r = new Random();
		//get five random items from a temporary array list
		//and delete that item to avoid duplicates
		for (int i = 0; i < randomArray.length; i++) {
			int position = r.nextInt(tempItems.size());
			randomArray[i] = tempItems.get(position);
			tempItems.remove(position);
		}
		setActionBarTitle("FIND: " + randomArray[selectedItemIndex]);

		times = new double[randomArray.length + 1];
		times[0] = SystemClock.elapsedRealtime()/1000.0;
        
        editsearch.addTextChangedListener(new TextWatcher() {
			//Event when changed word on EditTex
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textlength = editsearch.getText().length();
				searchedItems.clear();
				for (int i = 0; i < mItems.size(); i++) {
					if (textlength <= mItems.get(i).length()) {
						if (editsearch.getText().toString().equalsIgnoreCase(
								(String)
										mItems.get(i).subSequence(0,
												textlength))) {
							searchedItems.add(mItems.get(i));
						}
					}
				}
				listView.setAdapter(new ArrayAdapter<String>(SIEText.this, android.R.layout.simple_list_item_1, searchedItems));


			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Log.i("MYDEBUG", searchedItems.get(i) + "\t" + randomArray[selectedItemIndex]);
				totalClicks++;
				if (searchedItems.get(i).equals(randomArray[selectedItemIndex])) {
					editsearch.setText("");
					selectedItemIndex++;
					times[selectedItemIndex] = SystemClock.elapsedRealtime()/1000.0;
					Log.i("MYDEBUG", selectedItemIndex + "");
					if (selectedItemIndex < randomArray.length) {
						Toast.makeText(getApplicationContext(), "CORRECT! Next item...", Toast.LENGTH_SHORT).show();
						if (getActionBar() != null)
							getActionBar().setTitle("FIND: " + randomArray[selectedItemIndex]);
					} else {
						double[] timeBetween = new double[randomArray.length];
						double totalTime = 0.0;
						for (int j = 0; j < times.length-1; j++) {
							timeBetween[j] = times[j+1] - times[j];
							Log.i("MYDEBUG", "timeBetween[" + j +"] " + timeBetween[j]+ " seconds");
							totalTime += timeBetween[j];
						}
						Bundle b = new Bundle();
						//b.putDouble("averageTime", (totalTime / randomArray.length));
						b.putDouble("averageTime", totalTime );
						b.putDouble("efficiencyRate", ((double) randomArray.length / (double) totalClicks) * 100);
						b.putDouble("searchRate", randomArray.length / (totalTime / 60));
						b.putString("initials", initials);
						b.putString("search", search);
						b.putString("array", array);
						b.putString("conditionCode", conditionCode);
						b.putString("participant", participant);
						b.putString("group", group);
						b.putString("session", session);
						Intent intent = new Intent(getApplicationContext(), SIEResults.class);
						intent.putExtras(b);
						startActivity(intent);
						finish();
						Log.i("MYDEBUG", "SENT INTENT TO RESULTS");
					}

				} else {
					Toast.makeText(getApplicationContext(), "WRONG! Try again...", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void setActionBarTitle(String title) {
		if (getActionBar() != null) {
			getActionBar().setTitle(title);
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), SIESetup.class);
		startActivity(i);
	}
}

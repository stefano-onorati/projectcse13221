package ca.yorku.cse.mack.searchInterfaceExperiment;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Source code from: https://www.learn2crack.com/2014/01/android-custom-gridview.html
 */
public class SIEImage extends Activity {
	GridView grid;
	String[] web;
	TypedArray imageId;

	private String search, array, conditionCode, initials, participant, group, session;
	private double[] times;
	private int totalClicks, selectedItemIndex;
	private ArrayList<String> tempItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagelayout);

		Bundle b = getIntent().getExtras();
		search = b.getString("search");
		array = b.getString("array");
		conditionCode = b.getString("conditionCode");
		initials = b.getString("initials");
		participant = b.getString("participant");
		group = b.getString("group");
		session = b.getString("session");

		if (array.equals("Food")) {
			web = getResources().getStringArray(R.array.food);
			imageId = getResources().obtainTypedArray(R.array.foodDrawable);
		} else if (array.equals("People")) {
			web = getResources().getStringArray(R.array.people);
			imageId = getResources().obtainTypedArray(R.array.peopleDrawable);
		} else if (array.equals("Countries")) {
			web = getResources().getStringArray(R.array.country);
			imageId = getResources().obtainTypedArray(R.array.countryDrawable);
		} else if (array.equals("Movies")) {
			web = getResources().getStringArray(R.array.movie);
			imageId = getResources().obtainTypedArray(R.array.movieDrawable);
		} else if (array.equals("Sports Teams")) {
			web = getResources().getStringArray(R.array.team);
			imageId = getResources().obtainTypedArray(R.array.teamDrawable);
		}

		CustomGrid adapter = new CustomGrid(SIEImage.this, web, imageId);
		grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(adapter);

		final String[] randomArray = new String[5]; //array of 5 randomly chosen strings
		tempItems = new ArrayList<String>();
		times = new double[randomArray.length + 1];
		times[0] = SystemClock.elapsedRealtime()/1000.0;
		Log.i("MYDEBUG", times[0] + " = times[0]");
		totalClicks = 0;
		selectedItemIndex = 0;

		for (int i = 0; i < web.length; i++) {
			tempItems.add(web[i]);
		}

		Random r = new Random();
		//get five random items from a temporary array list
		//and delete that item to avoid duplicates
		for (int i = 0; i < randomArray.length; i++) {
			int position = r.nextInt(tempItems.size());
			randomArray[i] = tempItems.get(position);
			tempItems.remove(position);
		}
		setActionBarTitle("FIND: " + randomArray[selectedItemIndex]);

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Log.i("MYDEBUG", web[position] + "\t" + randomArray[selectedItemIndex]);
				Log.i("MYDEBUG", selectedItemIndex + "\t" + totalClicks);
				totalClicks++;
				if (web[position].equals(randomArray[selectedItemIndex])) {
					selectedItemIndex++;
					times[selectedItemIndex] = SystemClock.elapsedRealtime()/1000.0;
					Log.i("MYDEBUG", times[selectedItemIndex] + " = times["+ selectedItemIndex +"]");
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

package ca.yorku.cse.mack.searchInterfaceExperiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class SIEResults extends Activity
{
    final static String MYDEBUG = "MYDEBUG";
    private Button setup, exit;
    private TextView searchText, arrayText, timeText, effText, rateText, titleText;
    private String search, array, condition, initials, participant, group, session;
    private double averageTime, efficiencyRate, searchRate;

    /*
     *  Initialize variables for file storage of user data on phone
     */
    private static final String WORKING_DIRECTORY = "/SIEData/";
    private static final String APP = "SIE";
    private static final String SD2_HEADER = "App,Initials,Participant,Group,Session,Condition,Block,Search,Trial,TotalTime,Efficiency,SearchRate\n";
    private File dataDirectory, f1;

    private BufferedWriter sd2;
    private String sd2Leader;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        Log.i(MYDEBUG, "I'm called");

        Bundle b = getIntent().getExtras();
        averageTime = b.getDouble("averageTime");
        efficiencyRate = b.getDouble("efficiencyRate");
        searchRate = b.getDouble("searchRate");
        search = b.getString("search");
        array = b.getString("array");
        initials = b.getString("initials");
        condition = b.getString("conditionCode");
        participant = b.getString("participant");
        group = b.getString("group");
        session = b.getString("session");


        setup = (Button) findViewById(R.id.setup);
        exit = (Button) findViewById(R.id.exit);
        titleText = (TextView) findViewById(R.id.resultsTitle);
        searchText = (TextView) findViewById(R.id.searchTextView);
        arrayText = (TextView) findViewById(R.id.arrayTextView);
        timeText = (TextView) findViewById(R.id.timeTextView);
        effText = (TextView) findViewById(R.id.effTextView);
        rateText = (TextView) findViewById(R.id.rateTextView);

        titleText.setText("SIE RESULTS");
        searchText.setText(String.format("Search Interface = %s", search));
        arrayText.setText(String.format("Trial Array = %s", array));
       // timeText.setText(String.format("Time = %.2f s (mean/item)", averageTime));
        timeText.setText(String.format("Trial Time = %.2f s", averageTime));
        effText.setText(String.format("Accuracy Rate = %.1f", efficiencyRate) + "%");
        rateText.setText(String.format("Efficiency Rate = %.0f items/minute", searchRate));

        if (getActionBar() != null)
            getActionBar().hide();

        // make a working directory (if necessary) to store data files
        dataDirectory = new File(Environment.getExternalStorageDirectory() + WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + WORKING_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
        Log.i(MYDEBUG, "Working directory=" + dataDirectory);
        createFile();
    }

    public void onSetup(View v) {
        Intent i = new Intent(getApplicationContext(), SIESetup.class);
        startActivity(i);
        finish();
    }

    public void onExit(View v) {
        super.onDestroy();
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SIESetup.class);
        startActivity(intent);
        finish();
    }

    private void createFile() {
        /**
         * The following do-loop creates data files for output and a string sd2Leader to write to the sd2
         * output files.  Both the filenames and the sd2Leader are constructed by combining the setup parameters
         * so that the filenames and sd2Leader are unique and also reveal the conditions used for the block of input.
         *
         * The block code begins "B01" and is incremented on each loop iteration until an available
         * filename is found.  The goal, of course, is to ensure data files are not inadvertently overwritten.
         */
        int blockNumber = 0;
        do
        {
            ++blockNumber;
            String blockCode = String.format("B%02d", blockNumber);
            String baseFilename = String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s", APP,
                    initials,participant,group,session,blockCode,condition,search,array);
            f1 = new File(dataDirectory, baseFilename + ".txt");

            // also make a comma-delimited leader that will begin each data line written to the sd2 file
            sd2Leader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.1f,%.0f", APP,
                    initials,participant,group,session,blockCode,condition,search,array,averageTime,efficiencyRate,searchRate);
        } while (f1.exists());

        try
        {
            sd2 = new BufferedWriter(new FileWriter(f1));

            // output header in sd2 file
            sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            sd2.write(sd2Leader, 0, sd2Leader.length());
            sd2.flush();
        } catch (IOException e)
        {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();
        } // end file initialization
    }
}

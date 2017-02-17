package ca.yorku.cse.mack.searchInterfaceExperiment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * SoftKeyboardExperimentSetup - a class that implements a setup dialog for experimental applications on Android. <p>
 *
 * @author Scott MacKenzie, 2014-2016
 */
@SuppressWarnings("unused")
public class SIESetup extends Activity implements View.OnClickListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    /*
     * The following arrays are used to fill the spinners in the set up dialog. The first entries will be replaced by
     * corresponding values in the app's shared preferences, if any exist. In order for a value to exit as a shared
     * preference, the app must have been run at least once with the "Save" button tapped.
     */
    String[] sessionCode = {"S01", "S01", "S02", "S03", "S04", "S05"};
    String[] blockCode = {"(auto)"};
    String[] searchCode = {"Text", "Text", "Index Scroll", "Image"};
    String[] arrayCode = {"Sports Teams", "Food", "People", "Countries", "Movies", "Sports Teams"};
    String[] conditionCode = {"C01"};
    String[] participantCode = {"P01", "P01", "P02", "P03", "P04", "P05"};
    String[] groupCode = {"G01", "G01", "G02", "G03"};
    String initialsEdit = "";

    Button ok, save, exit;

    private Spinner spinBlockCode, spinSearchCode, spinArrayCode, spinConditionCode, spinParticipantCode,
        spinGroupCode, spinSessionCode;
    private EditText initialsCode;
    // end set up parameters

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setup);

        if (getActionBar() != null)
            getActionBar().hide();

        // get a reference to a SharedPreferences object (used to store, retrieve, and save setup parameters)
        sp = this.getPreferences(MODE_PRIVATE);

        // overwrite 1st entry from shared preferences, if corresponding value exits
        participantCode[0] = sp.getString("participantCode", participantCode[0]);
        sessionCode[0] = sp.getString("sessionCode", sessionCode[0]);
        // block code initialized in main activity (based on existing filenames)
        groupCode[0] = sp.getString("groupCode", groupCode[0]);
        conditionCode[0] = sp.getString("conditionCode", conditionCode[0]);
        searchCode[0] = sp.getString("searchCode", searchCode[0]);
        arrayCode[0] = sp.getString("arrayCode", arrayCode[0]);
        initialsEdit = sp.getString("initials", initialsEdit);

        // get references to widgets in setup dialog
        initialsCode = (EditText) findViewById(R.id.editInitials);
        spinSearchCode = (Spinner) findViewById(R.id.searchCodeGroup);
        spinArrayCode = (Spinner) findViewById(R.id.arrayCode);
        spinBlockCode = (Spinner)findViewById(R.id.spinBlockCode);
        spinConditionCode = (Spinner)findViewById(R.id.conditionCodeGroup);
        spinParticipantCode = (Spinner) findViewById(R.id.participantCodeGroup);
        spinSessionCode = (Spinner) findViewById(R.id.sessionCodeGroup);
        spinGroupCode = (Spinner) findViewById(R.id.groupCodeGroup);

        // get references to OK, SAVE, and EXIT buttons
        ok = (Button)findViewById(R.id.ok);
        exit = (Button)findViewById(R.id.exit);
        save = (Button) findViewById(R.id.save);

        // initialise spinner adapters

        ArrayAdapter<CharSequence> adapterSC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                searchCode);
        spinSearchCode.setAdapter(adapterSC);

        ArrayAdapter<CharSequence> adapterAC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                arrayCode);
        spinArrayCode.setAdapter(adapterAC);

        ArrayAdapter<CharSequence> adapterBC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                blockCode);
        spinBlockCode.setAdapter(adapterBC);

        ArrayAdapter<CharSequence> adapterCC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                conditionCode);
        spinConditionCode.setAdapter(adapterCC);

        ArrayAdapter<CharSequence> adapterPC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
            participantCode);
        spinParticipantCode.setAdapter(adapterPC);

        ArrayAdapter<CharSequence> adapterEC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                sessionCode);
        spinSessionCode.setAdapter(adapterEC);

        ArrayAdapter<CharSequence> adapterGC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                groupCode);
        spinGroupCode.setAdapter(adapterGC);

        initialsCode.setText(initialsEdit);
    }

    @Override
    public void onClick(View v)
    {
        if (v == ok)
        {
            // get user's choices
            String initials = initialsCode.getText().toString();
            String cond = conditionCode[spinConditionCode.getSelectedItemPosition()];
            String search = searchCode[spinSearchCode.getSelectedItemPosition()];
            String array = arrayCode[spinArrayCode.getSelectedItemPosition()];
            String participant = participantCode[spinParticipantCode.getSelectedItemPosition()];
            String group = groupCode[spinGroupCode.getSelectedItemPosition()];
            String session = sessionCode[spinSessionCode.getSelectedItemPosition()];

            // package the user's choices in a bundle
            Bundle b = new Bundle();
            // b.putString("blockCode", block);
            b.putString("initials", initials.toUpperCase());
            b.putString("search", search);
            b.putString("conditionCode", cond);
            b.putString("array", array);
            b.putString("participant", participant);
            b.putString("group", group);
            b.putString("session", session);

            // start experiment activity (sending the bundle with the user's choices)
            if (search.contains("Text")) {
                Intent i = new Intent(getApplicationContext(), SIEText.class);
                i.putExtras(b);
                startActivity(i);
            } else if (search.contains("Scroll")) {
                Intent i = new Intent(getApplicationContext(), SIEScroll.class);
                i.putExtras(b);
                startActivity(i);
            } else if (search.contains("Image")) {
                Intent i = new Intent(getApplicationContext(), SIEImage.class);
                i.putExtras(b);
                startActivity(i);
            }
            finish();

        } else if (v == save)
        {
            spe = sp.edit();
            spe.putString("participantCode", participantCode[spinParticipantCode.getSelectedItemPosition()]);
            spe.putString("sessionCode", sessionCode[spinSessionCode.getSelectedItemPosition()]);
            spe.putString("groupCode", groupCode[spinGroupCode.getSelectedItemPosition()]);
            spe.putString("conditionCode", conditionCode[spinConditionCode.getSelectedItemPosition()]);
            spe.putString("searchCode", searchCode[spinSearchCode.getSelectedItemPosition()]);
            spe.putString("arrayCode", arrayCode[spinArrayCode.getSelectedItemPosition()]);
            spe.putString("initials", initialsCode.getText().toString());
            spe.commit();
            Toast.makeText(getApplicationContext(), "Preferences saved!", Toast.LENGTH_SHORT).show();

        } else if (v == exit)
        {
            this.finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onDestroy();
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

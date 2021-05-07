package projects.medicationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static java.time.temporal.TemporalAdjusters.previous;
import static java.util.Calendar.SUNDAY;

public class MainActivity extends AppCompatActivity
{
    private final DBHelper db = new DBHelper(this);

    /**
     * Runs at start of activity, builds MainActivity
     *
     * @param savedInstanceState Stored instance of activity
     **************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Medication Schedule");

        LinearLayout scheduleLayout = findViewById(R.id.scheduleLayout);
        TextView noMeds = findViewById(R.id.noMeds);
        ScrollView scheduleScrollView = findViewById(R.id.scheduleScrollView);
        Spinner patientNames = findViewById(R.id.patientSpinner);

        // Exit if there are no patients in DB
        if (db.numberOfRows() == 0)
        {
            noMeds.setVisibility(View.VISIBLE);
            scheduleScrollView.setVisibility(View.GONE);
            patientNames.setVisibility(View.GONE);
            return;
        }

        ArrayList<Medication> medications = PatientUtils.medicationsForThisWeek(db);
        ArrayList<String> names = db.getPatients();

        // Change ME! to You for Adapter
        names.set(names.indexOf("ME!"), "You");

        // Load contents into spinner, or print results for only patient
        if (db.getPatients().size() <= 1)
        {
            patientNames.setVisibility(View.GONE);

            if (names.get(0).equals("You"))
                names.set(0, "ME!");

            CardCreator.createMedicationSchedule(medications, names.get(0), db, scheduleLayout);
        }
        else
        {
            patientNames.setVisibility(View.VISIBLE);

            ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
            patientNames.setAdapter(patientAdapter);

            // Select "You" by default
            if (names.contains("You"))
            {
                for (int i = 0; i < names.size(); i++)
                {
                    if (names.get(i).equals("You"))
                        patientNames.setSelection(i);
                }
            }

            patientNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    scheduleLayout.removeAllViews();

                    String name = adapterView.getSelectedItem().toString();

                    if (name.equals("You"))
                        name = "ME!";

                    CardCreator.createMedicationSchedule(medications, name, db, scheduleLayout);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }
    }

    /**
     * Creates option menu
     *
     * @param menu Menu containing selections for user
     **************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Instructions for restarting activity once returned to from other activity
     **************************************************************************/
    @Override
    protected void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /**
    *  Launches MyMedications.java when "My Medications" option is selected
    *
    *  @param item the "My Medications" menu option
    **************************************************************************/
    public void onMyMedicationsClick(MenuItem item)
    {
    }

    /**
     *  Launches AddMedication.java when "Add Medication" option is selected
     *
     *  @param item The "Add Medication" option
     **************************************************************************/
    public void onAddMedicationClick(MenuItem item)
    {
        Intent intent = new Intent(this, AddMedication.class);
        startActivity(intent);
    }

    /**
     *  Launches Settings.java when "Settings" option is selected
     *
     *  @param item The "Settings" menu option
     **************************************************************************/
    public void onSettingsClick(MenuItem item)
    {
    }
}
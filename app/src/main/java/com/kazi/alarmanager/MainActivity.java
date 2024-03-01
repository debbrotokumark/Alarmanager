package com.kazi.alarmanager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DatePicker datePicker;
    TimePicker timePicker;
    Button setAlarmButton;

    Button showDateTimeButton;

    SharedPreferences sharedPreferences;
    public static final String ALARM_PREFS = "AlarmPrefs";
    public static final String ALARM_SET = "AlarmSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        showDateTimeButton = findViewById(R.id.showDateTimeButton);


        sharedPreferences = getSharedPreferences(ALARM_PREFS, Context.MODE_PRIVATE);

        showDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDateTimeVisibility();
            }
        });

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
                toggleDateTimeVisibility();
            }
        });



        // Load saved date and time, if any
        long savedTime = sharedPreferences.getLong(ALARM_SET, 0);
        if (savedTime != 0) {
            // Convert saved time to Calendar object
            Calendar savedCalendar = Calendar.getInstance();
            savedCalendar.setTimeInMillis(savedTime);
            // Set DatePicker and TimePicker to saved values
            datePicker.init(savedCalendar.get(Calendar.YEAR),
                    savedCalendar.get(Calendar.MONTH),
                    savedCalendar.get(Calendar.DAY_OF_MONTH), null);
            timePicker.setHour(savedCalendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(savedCalendar.get(Calendar.MINUTE));
        }
    }




    private void toggleDateTimeVisibility() {
        int dateVisibility = datePicker.getVisibility();
        int timeVisibility = timePicker.getVisibility();

        if (dateVisibility == View.GONE && timeVisibility == View.GONE) {
            // Show date and time picker
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.VISIBLE);
            setAlarmButton.setVisibility(View.VISIBLE);
        } else {
            // Hide date and time picker
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
            setAlarmButton.setVisibility(View.GONE);
        }
    }

    private void setAlarm() {



        cancelExistingAlarms(); // Cancel existing alarms before setting a new one

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Save selected date and time using SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(ALARM_SET, calendar.getTimeInMillis());
        editor.apply();

        Toast.makeText(MainActivity.this, "Alarm set for " + day + "/" + (month + 1) + "/" + year + " at " + hour + ":" + minute, Toast.LENGTH_LONG).show();
    }

    private void cancelExistingAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

}

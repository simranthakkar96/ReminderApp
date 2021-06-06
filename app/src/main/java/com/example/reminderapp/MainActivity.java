package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity  implements RecyclerViewAdapter.ItemClickListener {

    RecyclerViewAdapter adapter;
    public String deleted_date = null;
    public String deleted_event = null;
    private EditText event, date;
    private FloatingActionButton fab;
    private Button btn_events,btn_completed;
    private BottomNavigationView bottomNavigationView;
    boolean state = false;
    int page = 1;
    TinyDB tinydb;
    BottomAppBar bottomAppBar;
    DatePickerDialog.OnDateSetListener dateSetListener;

    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> reminders = new ArrayList<>();
    ArrayList<String> completed_dates = new ArrayList<>();
    ArrayList<String> completed_events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tinydb = new TinyDB(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 30);

            Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long time = System.currentTimeMillis();
            long hours = 1000;

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        //Load data
        dates = tinydb.getListString("myDates");
        reminders = tinydb.getListString("myReminders");
        completed_dates = tinydb.getListString("myCompleted_dates");
        completed_events = tinydb.getListString("myCompleted_events");

        fab = findViewById(R.id.fab);
        btn_events = findViewById(R.id.btn_events);
        btn_completed = findViewById(R.id.btn_completed);
        date = findViewById(R.id.txt_date);
        event = findViewById(R.id.txt_event);
        bottomAppBar  = findViewById(R.id.bottomAppBar);


        //set animations
        YoYo.with(Techniques.SlideOutRight)
                .duration(0)
                .playOn(date);
        YoYo.with(Techniques.SlideOutRight)
                .duration(0)
                .playOn(event);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvReminders);
        RecyclerView recyclerViewC = findViewById(R.id.rvCompleted);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, dates,reminders);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        checkDate();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Toast.makeText(MainActivity.this, "YOLO", Toast.LENGTH_SHORT).show();
                        month = month + 1;
                        String date1 = day + "/" + month + "/" + year;
                        date.setText(date1);
                    }
                }, year, month, day);
                dialog.show();
            }
        });
//
//        btn_events.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (page == 2){
//
//                    YoYo.with(Techniques.SlideOutRight)
//                            .duration(700)
//                            .playOn(recyclerViewC);
//
//                    YoYo.with(Techniques.SlideInLeft)
//                            .duration(700)
//                            .playOn(recyclerView);
//                    page = 1;
//
//                }
//            }
//        });
//
//        btn_completed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (page == 1){
//
//                    YoYo.with(Techniques.SlideOutRight)
//                            .duration(700)
//                            .playOn(recyclerView);
//
//                    YoYo.with(Techniques.SlideInLeft)
//                            .duration(700)
//                            .playOn(recyclerViewC);
//                    page = 2;
//                }
//            }
//        });


        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack  = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    deleted_date = dates.get(position);
                    deleted_event = reminders.get(position);
                    reminders.remove(position);
                    dates.remove(position);
                    tinydb.putListString("myDates",dates);
                    tinydb.putListString("myReminders",reminders);
                    adapter.notifyDataSetChanged();

                    Snackbar.make(recyclerView, "Reminder Deleted", BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dates.add(position, deleted_date);
                            reminders.add(position, deleted_event);
                            adapter.notifyDataSetChanged();
                        }
                    }).show();
                }

                if (direction == ItemTouchHelper.RIGHT) {


                    deleted_date = dates.get(position);
                    deleted_event = reminders.get(position);
                    completed_dates.add(dates.get(position));
                    completed_events.add(reminders.get(position));
                    reminders.remove(position);
                    dates.remove(position);
                    tinydb.putListString("myCompleted_dates", completed_dates);
                    tinydb.putListString("myCompleted_events", completed_events);
                    adapter.notifyDataSetChanged();

                    Snackbar.make(recyclerView, "Reminder Completed", BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dates.add(position, deleted_date);
                            reminders.add(position, deleted_event);
                            adapter.notifyDataSetChanged();
                        }
                    }).show();

                }

            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent))
                            .addSwipeLeftActionIcon(R.drawable.ic_delete)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent))
                            .addSwipeRightActionIcon(R.drawable.ic_completed)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
         }
        };




        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!state) {

                    YoYo.with(Techniques.SlideOutLeft)
                            .duration(700)
                            .playOn(recyclerView);

                    YoYo.with(Techniques.SlideInRight)
                            .duration(700)
                            .playOn(date);

                    YoYo.with(Techniques.SlideInRight)
                            .duration(700)
                            .playOn(event);
                    state = true;
                }
                else{

                    YoYo.with(Techniques.SlideInLeft)
                            .duration(700)
                            .playOn(recyclerView);

                    YoYo.with(Techniques.SlideOutRight)
                            .duration(700)
                            .playOn(date);

                    YoYo.with(Techniques.SlideOutRight)
                            .duration(700)
                            .playOn(event);
                    state = false;

                    dates.add(date.getText().toString());
                    reminders.add(event.getText().toString());
                    tinydb.putListString("myDates",dates);
                    tinydb.putListString("myReminders",reminders);
                    date.setText("");
                    event.setText("");
                    checkDate();

                }

            }
        });

    }



    public void checkDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        String currentDate = sdf.format(new Date());
        String[] tod = currentDate.split("/");
        for(int i = 0; i < dates.size(); i++)
        {
            try {
                Date curr = sdf.parse(currentDate);
                Date usr = sdf.parse(dates.get(i));
                if(curr.compareTo(usr) > 0){
                    dates.set(i, currentDate);
                    tinydb.putListString("myDates",dates);
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


}
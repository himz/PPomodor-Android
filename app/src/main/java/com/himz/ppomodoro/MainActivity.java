package com.himz.ppomodoro;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
   /* Constants */
   private static final long INTERVAL = 1000;
   public static final String DEFAULT_CLOCK_TEXT = "00:00";

   /* Preference Variables */
   private static long pomodoroMinutes = 25;
   private static long breakMinutes = 5;
   private static long roundEndMinutes = 20;
   private static long pomodoroCountPerRound = 4;
   private static long breakCountPerRound = 4;

   /* UI Mappers */
   private Button btnStart;
   private Button btnStop;
   private TextView txtCountdownTimer;
   private TextView txtState;
   private TextView txtStateValue;
   private TextView txtPomodoroNumber;
   private TextView txtBreakNumber;
   private TextView txtStartTime;
   private TextView txtTomato[];


   /* Class member variables */
   private CounterClass pomodoroCounter;
   private CounterClass breakCounter;
   private long timeForSession;   /* In Milli Seconds*/
   private long interval;   /* In Milli Seconds*/
   private int currentPomodoroNumber;
   private int currentBreakNumber;
   private int currentRoundNumber;
   /* States:
   *   0: Start of the round (unaccounted time)
   *   1: Pomodoro in progress
   *   2: Break in progress
   */
   private int state;
   private int elapsedTimeInSeconds;

   void init() {
      timeForSession = convertMinutesToMillis(pomodoroMinutes);
      interval = INTERVAL;
      currentPomodoroNumber = 0;
      currentBreakNumber = 0;
      currentRoundNumber = 0;
      state = 0;
      txtTomato = new TextView[4];

      SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(this);
      SharedPreferences.Editor prefEditor = appSettings.edit();
      prefEditor.putString("UserName", "Guest123");
      prefEditor.putBoolean("PaidUser", false);
      prefEditor.putInt("PomodoroMinutes", 2);
      prefEditor.putInt("BreakMinutes", 2);
      prefEditor.putInt("RoundEndMinutes", 4);
      prefEditor.putInt("PomodoroCountPerRound", 4);
      prefEditor.putBoolean("AutoStartBreak", true);
      prefEditor.commit();
      String test = appSettings.getString("example_text", "Himz");
      Toast.makeText(this, (String)test, Toast.LENGTH_LONG).show();
      pomodoroMinutes = appSettings.getInt("PomodoroMinutes",25);
      breakMinutes = appSettings.getInt("BreakMinutes", 5);
      roundEndMinutes = appSettings.getInt("RoundEndMinutes", 20);
      pomodoroCountPerRound = appSettings.getInt("PomodoroCountPerRound", 4);
      breakCountPerRound = pomodoroCountPerRound;

   }

   long convertMinutesToMillis(long minutes) {
      return minutes * 60 * 1000;
   }

   void updateParams(int state) {
      if (state == 2) {
            /* It is a break */
         currentBreakNumber++;
         if (currentBreakNumber == breakCountPerRound) {
            timeForSession = convertMinutesToMillis(roundEndMinutes);
         } else {
            timeForSession = convertMinutesToMillis(breakMinutes);
         }
      } else if (state == 1) {
            /* It is a pomodoro*/
         if (currentBreakNumber == breakCountPerRound) {
                /* It is a new round */
            currentRoundNumber++;
            currentBreakNumber = 0;
            currentPomodoroNumber = 0;
         }
         currentPomodoroNumber++;
         timeForSession = convertMinutesToMillis(pomodoroMinutes);
      } else if (state == 0) {
            /* Signify state after every Break */
      }
   }


   void updateUI(int state) {
      if (state == 2) {
         txtTomato[currentPomodoroNumber - 1].setTextColor(Color.GREEN);
         txtStateValue.setText("BREAK TIME - Move, Drink water");
         btnStart.setVisibility(View.INVISIBLE);
         btnStop.setVisibility(View.VISIBLE);
      } else if (state == 1) {
         if (currentBreakNumber == 0 && currentPomodoroNumber == 1) {
            /* It is start of new Round, new Pomodoro */
            resetProgress();
         }
         btnStart.setVisibility(View.INVISIBLE);
         btnStop.setVisibility(View.VISIBLE);
         txtStateValue.setText("Focus on one task for 25 mins");
         txtTomato[currentPomodoroNumber - 1].setTextColor(Color.YELLOW);
      } else if (state == 0) {
         txtStateValue.setText("GetReady, Start a Pomodoro");
         btnStop.setVisibility(View.INVISIBLE);
         btnStart.setVisibility(View.VISIBLE);
      }
      txtState.setText(Integer.valueOf(state).toString());
      txtPomodoroNumber.setText(Integer.valueOf(currentPomodoroNumber).toString());
      txtBreakNumber.setText(Integer.valueOf(currentBreakNumber).toString());
      txtStartTime.setText(Long.valueOf(timeForSession / (60 * 1000)).toString());

   }

   void resetProgress() {
      for (TextView t : txtTomato) {
         t.setTypeface(null, Typeface.BOLD);
         t.setTextColor(Color.LTGRAY);
      }
   }
   @Override
   public void onBackPressed () {
      moveTaskToBack (true);
   }
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
               .setAction("Action", null).show();
         }
      });
      /* Initialize and setup the app */
      init();
      txtState = (TextView) this.findViewById(R.id.txtState);
      txtStateValue = (TextView) this.findViewById(R.id.txtStateValue);
      txtPomodoroNumber = (TextView) this.findViewById(R.id.txtPomodoroNumber);
      txtBreakNumber = (TextView) this.findViewById(R.id.txtBreakNumber);
      txtStartTime = (TextView) this.findViewById(R.id.txtStartTime);
      txtTomato[0] = (TextView) this.findViewById(R.id.txtTomato1);
      txtTomato[1] = (TextView) this.findViewById(R.id.txtTomato2);
      txtTomato[2] = (TextView) this.findViewById(R.id.txtTomato3);
      txtTomato[3] = (TextView) this.findViewById(R.id.txtTomato4);
      txtCountdownTimer = (TextView) this.findViewById(R.id.txtCountDown);

        /* Button Start code goes here */
      btnStart = (Button) this.findViewById(R.id.btnStart);
      btnStart.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (state == 0) {
               startPomodoro();
            }
            updateUI(state);
         }
      });

        /* Button Stop code goes here */
      btnStop = (Button) this.findViewById(R.id.btnStop);
      btnStop.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (state == 0) {
               /* Do Nothing*/
            } else if (state == 1) {
               /* End the current pomodoro, auto start the break */
               startBreak();
            } else if(state == 2) {
               /* state == 2 -- stop the break - start initial state */
               startIdleState();
            }
            updateUI(state);
         }
      });
      resetProgress();
      updateUI(state);
      pomodoroCounter = new CounterClass(timeForSession, interval);

   }
   void startIdleState() {
      pomodoroCounter.cancel();
      txtCountdownTimer.setText(DEFAULT_CLOCK_TEXT);
      state = 0;
      updateParams(state);
   }
   void startPomodoro() {
      state = 1;
      updateParams(state);
      pomodoroCounter = new CounterClass(timeForSession, interval);
      pomodoroCounter.start();
   }

   void startBreak() {
      pomodoroCounter.cancel();
      state = 2;
      updateParams(state);
      pomodoroCounter = new CounterClass(timeForSession, interval);
      pomodoroCounter.start();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         /*return true;*/
         moveTaskToBack (true);
         Intent i = new Intent(this, SettingsActivity.class);
         startActivity(i);
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   public class CounterClass extends CountDownTimer {



      /**
       * @param millisInFuture    The number of millis in the future from the call
       *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
       *                          is called.
       * @param countDownInterval The interval along the way to receive
       *                          {@link #onTick(long)} callbacks.
       */
      public CounterClass(long millisInFuture, long countDownInterval) {
         super(millisInFuture, countDownInterval);
      }

      @Override
      public void onTick(long millisUntilFinished) {
         long millis = millisUntilFinished;
         String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
         txtCountdownTimer.setText(hms);
      }

      @Override
      public void onFinish() {
         if (state == 1) {
            /* If it is a Pomodoro, autostart the break */
            startBreak();
            updateUI(state);
         } else if(state == 2) {
            startIdleState();
            updateUI(state);
         }
      }
   }
}

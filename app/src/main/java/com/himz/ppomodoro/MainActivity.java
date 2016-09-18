package com.himz.ppomodoro;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
   /* Constants */
   private static final long POMODOR0_MINUTES = 1;
   private static final long BREAK_MINUTES = 1;
   private static final long ROUND_END_MINUTES = 20;
   private static final long INTERVAL = 1000;
   private static final long POMODORO_COUNT_PER_ROUND = 4;
   private static final long BREAK_COUNT_PER_ROUND = 4;
   public static final String DEFAULT_CLOCK_TEXT = "00:00";

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
      timeForSession = convertMinutesToMillis(POMODOR0_MINUTES);
      interval = INTERVAL;
      currentPomodoroNumber = 0;
      currentBreakNumber = 0;
      currentRoundNumber = 0;
      state = 0;
      txtTomato = new TextView[4];
   }

   long convertMinutesToMillis(long minutes) {
      return minutes * 60 * 1000;
   }

   void updateParams(int state) {
      if (state == 2) {
            /* It is a break */
         currentBreakNumber++;
         if (currentBreakNumber == BREAK_COUNT_PER_ROUND) {
            timeForSession = convertMinutesToMillis(ROUND_END_MINUTES);
         } else {
            timeForSession = convertMinutesToMillis(BREAK_MINUTES);
         }
      } else if (state == 1) {
            /* It is a pomodoro*/
         if (currentBreakNumber == BREAK_COUNT_PER_ROUND) {
                /* It is a new round */
            currentRoundNumber++;
            currentBreakNumber = 0;
            currentPomodoroNumber = 0;
         }
         currentPomodoroNumber++;
         timeForSession = convertMinutesToMillis(POMODOR0_MINUTES);
      } else if (state == 0) {
            /* Signify state after every Break */
      }
   }


   void updateUI(int state) {
      if (state == 2) {
         txtTomato[currentPomodoroNumber - 1].setTextColor(Color.GREEN);
         txtStateValue.setText("BREAK TIME - Move, Drink water");
      } else if (state == 1) {
         if (currentBreakNumber == 0 && currentPomodoroNumber == 1) {
            /* It is start of new Round, new Pomodoro */
            resetProgress();
         }
         txtStateValue.setText("Focus on one task for 25 mins");
         txtTomato[currentPomodoroNumber - 1].setTextColor(Color.YELLOW);
      } else if (state == 0) {
         txtStateValue.setText("GetReady, Start a Pomodoro");
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

      resetProgress();


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
            } else if(state == 0 ) {
               /* state == 2 -- stop the break - start initial state */
               startIdleState();
            }
            updateUI(state);
         }
      });

      pomodoroCounter = new CounterClass(timeForSession, interval);
      txtCountdownTimer = (TextView) this.findViewById(R.id.txtCountDown);
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

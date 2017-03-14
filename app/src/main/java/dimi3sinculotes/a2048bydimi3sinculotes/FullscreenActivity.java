package dimi3sinculotes.a2048bydimi3sinculotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FullscreenActivity extends AppCompatActivity {
    // mio
    private TextView[][] refPanel = new TextView[4][4];
    private boolean winOnce = false;
    private SharedPreferences shared_preferences;
    private SharedPreferences.Editor shared_preferences_editor;

    //
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.imageView1);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        // mine
        setBoard();
        //spawn();
        loadData();
        Button b1;
        b1 = (Button)findViewById(R.id.repeat);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView vres = (TextView)findViewById(R.id.score);
                vres.setText("0");
                setBoard();
                spawn();
            }
        });

        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.setOnTouchListener(new OnSwipeTouchListener(this){

            public void onSwipeTop () {
                String lost1 = actionsPreMove();
                //moveTop
                for (int r = 3; r >= 0; r--) {
                    int empties = howManyEmpties('c', r);
                    switch (empties) {
                        case 0:
                            int m1 = Integer.valueOf(refPanel[0][r].getText().toString());
                            int m2 = Integer.valueOf(refPanel[1][r].getText().toString());
                            int m3 = Integer.valueOf(refPanel[2][r].getText().toString());
                            int m4 = Integer.valueOf(refPanel[3][r].getText().toString());

                            for (int i = 3; i >= 0; i--) {
                                refPanel[i][r].setText("");
                            }
                            // -2-2-4-4-
                            // -X-X-2-2-
                            // -X-2-2-X-
                            // -2-2-X-X-
                            // -2-4-8-16-
                            if ((m1 == m2) && (m3 == m4)) {
                                refPanel[0][r].setText(String.valueOf(m1 + m2));
                                refPanel[1][r].setText(String.valueOf(m3 + m4));
                            } else if (m1 == m2) {
                                refPanel[0][r].setText(String.valueOf(m1 + m2));
                                refPanel[1][r].setText(String.valueOf(m3));
                                refPanel[2][r].setText(String.valueOf(m4));
                            } else if (m2 == m3) {
                                refPanel[0][r].setText(String.valueOf(m1));
                                refPanel[1][r].setText(String.valueOf(m2 + m3));
                                refPanel[2][r].setText(String.valueOf(m4));
                            } else if (m3 == m4) {
                                refPanel[2][r].setText(String.valueOf(m3 + m4));
                                refPanel[1][r].setText(String.valueOf(m2));
                                refPanel[0][r].setText(String.valueOf(m1));
                            } else {
                                refPanel[0][r].setText(String.valueOf(m1));
                                refPanel[1][r].setText(String.valueOf(m2));
                                refPanel[2][r].setText(String.valueOf(m3));
                                refPanel[3][r].setText(String.valueOf(m4));
                            }

                            break;
                        case 1:
                            int n1 = 0;
                            int n2 = 0;
                            int n3 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[i][r])) {
                                    if (n1 == 0) {
                                        n1 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else if (n2 == 0) {
                                        n2 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else {
                                        n3 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    }
                                }
                            }
                            if (n1 == n2) {
                                int res = n1 + n2;
                                refPanel[1][r].setText(String.valueOf(n3));
                                refPanel[0][r].setText(String.valueOf(res));
                            } else if (n2 == n3) {
                                int res = n2 + n3;
                                refPanel[1][r].setText(String.valueOf(res));
                                refPanel[0][r].setText(String.valueOf(n1));
                            } else {
                                refPanel[0][r].setText(String.valueOf(n1));
                                refPanel[1][r].setText(String.valueOf(n2));
                                refPanel[2][r].setText(String.valueOf(n3));
                            }
                            break;

                        case 2:
                            int nn1 = 0;
                            int nn2 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[i][r])) {
                                    if (nn1 == 0) {
                                        nn1 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else {
                                        nn2 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    }
                                }
                            }
                            if (nn1 == nn2) {
                                int res = nn1 + nn2;
                                refPanel[0][r].setText(String.valueOf(res));
                            } else {
                                refPanel[0][r].setText(String.valueOf(nn1));
                                refPanel[1][r].setText(String.valueOf(nn2));
                            }
                            break;
                        case 3:
                            for (int i = 0; i < 4; i++) {

                                if (!sEmpty(refPanel[i][r])) {
                                    String s = refPanel[i][r].getText().toString();
                                    refPanel[i][r].setText("");
                                    refPanel[0][r].setText(s);
                                }
                            }
                            break;
                        case 4:
                            // Do nothing
                            break;
                    }
                }
                actionsPostMove(lost1);
            }

            public void onSwipeRight() {
                String lost1 = actionsPreMove();
                //moveRight
                for (int r = 0; r < 4; r++) {
                    int empties = howManyEmpties('r', r);
                    switch (empties) {
                        case 0:
                            int m1 = Integer.valueOf(refPanel[r][0].getText().toString());
                            int m2 = Integer.valueOf(refPanel[r][1].getText().toString());
                            int m3 = Integer.valueOf(refPanel[r][2].getText().toString());
                            int m4 = Integer.valueOf(refPanel[r][3].getText().toString());

                            for (int i = 0; i < 4; i++) {
                                refPanel[r][i].setText("");
                            }
                            // -2-2-4-4-
                            // -X-X-2-2-
                            // -X-2-2-X-
                            // -2-2-X-X-
                            // -2-4-8-16-
                            if ((m1 == m2) && (m3 == m4)) {
                                refPanel[r][2].setText(String.valueOf(m1 + m2));
                                refPanel[r][3].setText(String.valueOf(m3 + m4));
                            } else if (m3 == m4) {
                                refPanel[r][3].setText(String.valueOf(m3 + m4));
                                refPanel[r][2].setText(String.valueOf(m2));
                                refPanel[r][1].setText(String.valueOf(m1));
                            } else if (m2 == m3) {
                                refPanel[r][3].setText(String.valueOf(m4));
                                refPanel[r][2].setText(String.valueOf(m2 + m3));
                                refPanel[r][1].setText(String.valueOf(m1));
                            } else if (m1 == m2) {
                                refPanel[r][3].setText(String.valueOf(m4));
                                refPanel[r][2].setText(String.valueOf(m3));
                                refPanel[r][1].setText(String.valueOf(m1 + m2));
                            } else {
                                refPanel[r][3].setText(String.valueOf(m4));
                                refPanel[r][2].setText(String.valueOf(m3));
                                refPanel[r][1].setText(String.valueOf(m2));
                                refPanel[r][0].setText(String.valueOf(m1));
                            }

                            break;
                        case 1:
                            int n1 = 0;
                            int n2 = 0;
                            int n3 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[r][i])) {
                                    if (n1 == 0) {
                                        n1 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else if (n2 == 0) {
                                        n2 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else {
                                        n3 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    }
                                }
                            }
                            if (n2 == n3) {
                                int res = n2 + n3;
                                refPanel[r][2].setText(String.valueOf(n1));
                                refPanel[r][3].setText(String.valueOf(res));
                            } else if (n1 == n2) {
                                int res = n1 + n2;
                                refPanel[r][2].setText(String.valueOf(res));
                                refPanel[r][3].setText(String.valueOf(n3));
                            } else {
                                refPanel[r][1].setText(String.valueOf(n1));
                                refPanel[r][2].setText(String.valueOf(n2));
                                refPanel[r][3].setText(String.valueOf(n3));
                            }
                            break;

                        case 2:
                            int nn1 = 0;
                            int nn2 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[r][i])) {
                                    if (nn1 == 0) {
                                        nn1 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else {
                                        nn2 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    }
                                }
                            }
                            if (nn1 == nn2) {
                                int res = nn1 + nn2;
                                refPanel[r][3].setText(String.valueOf(res));
                            } else {
                                refPanel[r][3].setText(String.valueOf(nn2));
                                refPanel[r][2].setText(String.valueOf(nn1));
                            }
                            break;
                        case 3:
                            for (int i = 0; i < 4; i++) {
                                String s = new String();
                                if (!sEmpty(refPanel[r][i])) {
                                    s = refPanel[r][i].getText().toString();
                                    refPanel[r][i].setText("");
                                    refPanel[r][3].setText(s);
                                }
                            }
                            break;
                        case 4:
                            // Do nothing
                            break;
                    }
                }

                actionsPostMove(lost1);
            }

            public void onSwipeLeft() {
                String lost1 = actionsPreMove();
                //moveLeft
                for (int r = 3; r >= 0; r--) {
                    int empties = howManyEmpties('r', r);
                    switch (empties) {
                        case 0:
                            int m1 = Integer.valueOf(refPanel[r][0].getText().toString());
                            int m2 = Integer.valueOf(refPanel[r][1].getText().toString());
                            int m3 = Integer.valueOf(refPanel[r][2].getText().toString());
                            int m4 = Integer.valueOf(refPanel[r][3].getText().toString());

                            for (int i = 3; i >= 0; i--) {
                                refPanel[r][i].setText("");
                            }
                            // -2-2-4-4-
                            // -X-X-2-2-
                            // -X-2-2-X-
                            // -2-2-X-X-
                            // -2-4-8-16-
                            if ((m1 == m2) && (m3 == m4)) {
                                refPanel[r][0].setText(String.valueOf(m1 + m2));
                                refPanel[r][1].setText(String.valueOf(m3 + m4));
                            } else if (m1 == m2) {
                                refPanel[r][0].setText(String.valueOf(m1 + m2));
                                refPanel[r][1].setText(String.valueOf(m3));
                                refPanel[r][2].setText(String.valueOf(m4));
                            } else if (m2 == m3) {
                                refPanel[r][0].setText(String.valueOf(m1));
                                refPanel[r][1].setText(String.valueOf(m2 + m3));
                                refPanel[r][2].setText(String.valueOf(m4));
                            } else if (m3 == m4) {
                                refPanel[r][2].setText(String.valueOf(m3 + m4));
                                refPanel[r][1].setText(String.valueOf(m2));
                                refPanel[r][0].setText(String.valueOf(m1));
                            } else {
                                refPanel[r][0].setText(String.valueOf(m1));
                                refPanel[r][1].setText(String.valueOf(m2));
                                refPanel[r][2].setText(String.valueOf(m3));
                                refPanel[r][3].setText(String.valueOf(m4));
                            }

                            break;
                        case 1:
                            int n1 = 0;
                            int n2 = 0;
                            int n3 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[r][i])) {
                                    if (n1 == 0) {
                                        n1 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else if (n2 == 0) {
                                        n2 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else {
                                        n3 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    }
                                }
                            }
                            if (n1 == n2) {
                                int res = n1 + n2;
                                refPanel[r][1].setText(String.valueOf(n3));
                                refPanel[r][0].setText(String.valueOf(res));
                            } else if (n2 == n3) {
                                int res = n2 + n3;
                                refPanel[r][1].setText(String.valueOf(res));
                                refPanel[r][0].setText(String.valueOf(n1));
                            } else {
                                refPanel[r][0].setText(String.valueOf(n1));
                                refPanel[r][1].setText(String.valueOf(n2));
                                refPanel[r][2].setText(String.valueOf(n3));
                            }
                            break;

                        case 2:
                            int nn1 = 0;
                            int nn2 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[r][i])) {
                                    if (nn1 == 0) {
                                        nn1 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    } else {
                                        nn2 = Integer.parseInt(refPanel[r][i].getText().toString());
                                        refPanel[r][i].setText("");
                                    }
                                }
                            }
                            if (nn1 == nn2) {
                                int res = nn1 + nn2;
                                refPanel[r][0].setText(String.valueOf(res));
                            } else {
                                refPanel[r][0].setText(String.valueOf(nn1));
                                refPanel[r][1].setText(String.valueOf(nn2));
                            }
                            break;
                        case 3:
                            for (int i = 0; i < 4; i++) {
                                String s = new String();
                                if (!sEmpty(refPanel[r][i])) {
                                    s = refPanel[r][i].getText().toString();
                                    refPanel[r][i].setText("");
                                    refPanel[r][0].setText(s);
                                }
                            }
                            break;
                        case 4:
                            // Do nothing
                            break;
                    }
                }

                actionsPostMove(lost1);

            }

            public void onSwipeBottom() {
                String lost1 = actionsPreMove();
                //moveDown
                for (int r = 0; r < 4; r++) {
                    int empties = howManyEmpties('c', r);
                    switch (empties) {
                        case 0:
                            int m1 = Integer.valueOf(refPanel[0][r].getText().toString());
                            int m2 = Integer.valueOf(refPanel[1][r].getText().toString());
                            int m3 = Integer.valueOf(refPanel[2][r].getText().toString());
                            int m4 = Integer.valueOf(refPanel[3][r].getText().toString());

                            for (int i = 0; i < 4; i++) {
                                refPanel[i][r].setText("");
                            }
                            // -2-2-4-4-
                            // -X-X-2-2-
                            // -X-2-2-X-
                            // -2-2-X-X-
                            // -2-4-8-16-
                            if ((m1 == m2) && (m3 == m4)) {
                                refPanel[2][r].setText(String.valueOf(m1 + m2));
                                refPanel[3][r].setText(String.valueOf(m3 + m4));
                            } else if (m3 == m4) {
                                refPanel[3][r].setText(String.valueOf(m3 + m4));
                                refPanel[2][r].setText(String.valueOf(m2));
                                refPanel[1][r].setText(String.valueOf(m1));
                            } else if (m2 == m3) {
                                refPanel[3][r].setText(String.valueOf(m4));
                                refPanel[2][r].setText(String.valueOf(m2 + m3));
                                refPanel[1][r].setText(String.valueOf(m1));
                            } else if (m1 == m2) {
                                refPanel[3][r].setText(String.valueOf(m4));
                                refPanel[2][r].setText(String.valueOf(m3));
                                refPanel[1][r].setText(String.valueOf(m1 + m2));
                            } else {
                                refPanel[3][r].setText(String.valueOf(m4));
                                refPanel[2][r].setText(String.valueOf(m3));
                                refPanel[1][r].setText(String.valueOf(m2));
                                refPanel[0][r].setText(String.valueOf(m1));
                            }

                            break;
                        case 1:
                            int n1 = 0;
                            int n2 = 0;
                            int n3 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[i][r])) {
                                    if (n1 == 0) {
                                        n1 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else if (n2 == 0) {
                                        n2 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else {
                                        n3 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    }
                                }
                            }
                            if (n2 == n3) {
                                int res = n2 + n3;
                                refPanel[2][r].setText(String.valueOf(n1));
                                refPanel[3][r].setText(String.valueOf(res));
                            } else if (n1 == n2) {
                                int res = n1 + n2;
                                refPanel[2][r].setText(String.valueOf(res));
                                refPanel[3][r].setText(String.valueOf(n3));
                            } else {
                                refPanel[1][r].setText(String.valueOf(n1));
                                refPanel[2][r].setText(String.valueOf(n2));
                                refPanel[3][r].setText(String.valueOf(n3));
                            }
                            break;

                        case 2:
                            int nn1 = 0;
                            int nn2 = 0;
                            for (int i = 0; i < 4; i++) {
                                if (!sEmpty(refPanel[i][r])) {
                                    if (nn1 == 0) {
                                        nn1 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    } else {
                                        nn2 = Integer.parseInt(refPanel[i][r].getText().toString());
                                        refPanel[i][r].setText("");
                                    }
                                }
                            }
                            if (nn1 == nn2) {
                                int res = nn1 + nn2;
                                refPanel[3][r].setText(String.valueOf(res));
                            } else {
                                refPanel[3][r].setText(String.valueOf(nn2));
                                refPanel[2][r].setText(String.valueOf(nn1));
                            }
                            break;
                        case 3:
                            for (int i = 0; i < 4; i++) {
                                String s = new String();
                                if (!sEmpty(refPanel[i][r])) {
                                    s = refPanel[i][r].getText().toString();
                                    refPanel[i][r].setText("");
                                    refPanel[3][r].setText(s);
                                }
                            }
                            break;
                        case 4:
                            // Do nothing
                            break;
                    }
                }

                actionsPostMove(lost1);
            }


    });
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // mine
    public void loadData(){
        shared_preferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        String state = shared_preferences.getString("state", "");


        if(!state.equals("")) {

            String[] substates = state.split(",");

            TextView v = (TextView) findViewById(R.id.score);
            v.setText(String.valueOf(substates[0]));

            int k = 1;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    refPanel[i][j].setText(substates[k]);
                    colorWell(refPanel[i][j]);
                    lostGame();
                    k++;
                }
            }
        }else{
            spawn();
        }
    }
    public void saveData(){
        TextView v = (TextView)findViewById(R.id.score);
        String currentScore = v.getText().toString();
        String currentState = currentScore;

        shared_preferences_editor = shared_preferences.edit();
        shared_preferences_editor.clear();

        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                currentState = currentState + "," + refPanel[i][j].getText().toString();
            }
        }

        shared_preferences_editor.putString("state", currentState);
        shared_preferences_editor.commit();
    }
    public void setBoard(){
        refPanel[0][0] = (TextView)findViewById(R.id.textView0);
        refPanel[0][1] = (TextView)findViewById(R.id.textView1);
        refPanel[0][2] = (TextView)findViewById(R.id.textView2);
        refPanel[0][3] = (TextView)findViewById(R.id.textView3);
        refPanel[1][0] = (TextView)findViewById(R.id.textView4);
        refPanel[1][1] = (TextView)findViewById(R.id.textView5);
        refPanel[1][2] = (TextView)findViewById(R.id.textView6);
        refPanel[1][3] = (TextView)findViewById(R.id.textView7);
        refPanel[2][0] = (TextView)findViewById(R.id.textView8);
        refPanel[2][1] = (TextView)findViewById(R.id.textView9);
        refPanel[2][2] = (TextView)findViewById(R.id.textView10);
        refPanel[2][3] = (TextView)findViewById(R.id.textView11);
        refPanel[3][0] = (TextView)findViewById(R.id.textView12);
        refPanel[3][1] = (TextView)findViewById(R.id.textView13);
        refPanel[3][2] = (TextView)findViewById(R.id.textView14);
        refPanel[3][3] = (TextView)findViewById(R.id.textView15);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                refPanel[i][j].setBackgroundColor(Color.WHITE);
                refPanel[i][j].setText("");
            }
        }
    }
    private void colorWell(TextView textfi) {
        String s = textfi.getText().toString();
        switch (s) {
            case "":
                textfi.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case "2":
                textfi.setBackgroundColor(Color.parseColor("#A0A1F2"));
                break;
            case "4":
                textfi.setBackgroundColor(Color.parseColor("#A0A1F2"));
                break;
            case "8":
                textfi.setBackgroundColor(Color.parseColor("#FFDD69"));
                break;
            case "16":
                textfi.setBackgroundColor(Color.parseColor("#DAB848"));
                break;
            case "32":
                textfi.setBackgroundColor(Color.parseColor("#D06C20"));
                break;
            case "64":
                textfi.setBackgroundColor(Color.parseColor("#D22C02"));
                break;
            case "128":
                textfi.setBackgroundColor(Color.parseColor("#7EFF77"));
                break;
            case "256":
                textfi.setBackgroundColor(Color.parseColor("#7EFF77"));
                break;
            case "512":
                textfi.setBackgroundColor(Color.parseColor("#0BD200"));
                break;
            case "1024":
                textfi.setBackgroundColor(Color.parseColor("#00FF9E"));
                break;
            case "2048":
                textfi.setBackgroundColor(Color.parseColor("#EB00FF"));
                break;
            default:
                textfi.setBackgroundColor(Color.parseColor("#71694F"));
                break;
        }
    }
    private void spawn() {
        boolean cond1 = Math.random() < 0.5;
        boolean cond2 = Math.random() < 0.5;

        int x1 = (int) (Math.random() * 3);
        int x2 = (int) (Math.random() * 3);
        int y1 = (int) (Math.random() * 3);
        int y2 = (int) (Math.random() * 3);

        if (x1 == x2 && y1 == y2) {
            x1 = 2;
            x2 = 1;
        }

        if (cond1) {
            refPanel[x1][y1].setText("2");
        } else {
            refPanel[x1][y1].setText("4");
        }

        if (cond2) {
            refPanel[x2][y2].setText("4");
        } else {
            refPanel[x2][y2].setText("2");
        }

        colorWell(refPanel[x1][y1]);
        colorWell(refPanel[x2][y2]);

    }
    private void gameOver(boolean gover) {
        // Here goes the gameOver system
        boolean twothou = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                if (twothou == false) {
                    twothou = refPanel[i][j].getText().toString().equals("2048");
                }
            }
        }

        if (gover) {
            Toast.makeText(this, "You Lost :(", Toast.LENGTH_LONG).show();
            for(int i=0; i<4; i++){
                for(int j=0; j<4; j++){
                    refPanel[i][j].setBackgroundColor(Color.GRAY);
            }}
        }
        if (twothou && (!winOnce)) {
            Toast.makeText(this, "You won!! continue if you want", Toast.LENGTH_LONG).show();
            winOnce = true;
        }
    }
    private boolean sEmpty(TextView jtf) {
        return (jtf.getText().toString().equals(""));
    }
    private int howManyEmpties(char l, int i) {
        int t;
        if (l == 'r') {
            t = 0;
            for (int j = 0; j < 4; j++) {
                if (sEmpty(refPanel[i][j])) {
                    t++;
                }
            }
        } else {
            t = 0;
            for (int j = 0; j < 4; j++) {
                if (refPanel[j][i].getText().equals("")) {
                    t++;
                }
            }
        }
        return t;
    }
    private void newPiece() {
        int i = (int)(Math.random() * 4);
        int j = (int)(Math.random() * 4);

        while(!sEmpty(refPanel[i][j])){
            i = (int)(Math.random() * 4);
            j = (int)(Math.random() * 4);
        }
        if(Math.random() < 0.75){
            refPanel[i][j].setText("2");
        }else{
            refPanel[i][j].setText("4");
        }
        colorWell(refPanel[i][j]);
    }
    private boolean noEmpties() {
        boolean bool = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (bool){
                    bool = !(sEmpty(refPanel[i][j]));
                }
            }
        }
        return bool;
    }
    private void lostGame() {
        // Here goes the code when you DO LOSE your game
        boolean pair = false;

        if (noEmpties()){
            for (int i = 1; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if (!pair){
                        pair = (refPanel[i][j].getText().toString().equals(refPanel[i-1][j].getText().toString()))
                                || (refPanel[i][j].getText().toString().equals(refPanel[i][j-1].getText().toString()));
                    }
                }
            }

            if(!(pair)){
                if(!(refPanel[0][0].getText().toString().equals(refPanel[0][1].getText().toString())) &&
                        (!refPanel[0][0].getText().toString().equals(refPanel[1][0].getText().toString())) &&
                        (!refPanel[0][1].getText().toString().equals(refPanel[0][2].getText().toString())) &&
                        (!refPanel[0][2].getText().toString().equals(refPanel[0][3].getText().toString())) &&
                        (!refPanel[2][0].getText().toString().equals(refPanel[1][0].getText().toString())) &&
                        (!refPanel[3][0].getText().toString().equals(refPanel[2][0].getText().toString()))){

                    gameOver(true);
                }
            }}

    }
    private void toScore() {
        //Here goes the code which make you gain points
        TextView v = (TextView)findViewById(R.id.score);
        int i = Integer.parseInt(v.getText().toString());
        i++;
        v.setText(String.valueOf(i));

        saveData();
    }
    private String actionsPreMove(){
        String lost1 = new String();

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                lost1 = lost1 + "," + refPanel[j][k].getText();
            }
        }
        return lost1;
    }
    private void actionsPostMove(String lost1){
        String lost2 = new String();

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                lost2 = lost2 + "," + refPanel[j][k].getText();
            }
        }

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                colorWell(refPanel[j][k]);
            }
        }

        if (!lost1.equals(lost2)){
            newPiece();
            toScore();
        }else{
            lostGame();
        }
        gameOver(false);
    }
}

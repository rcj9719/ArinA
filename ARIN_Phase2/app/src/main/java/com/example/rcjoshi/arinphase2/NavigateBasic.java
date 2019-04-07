package com.example.rcjoshi.arinphase2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NavigateBasic extends AppCompatActivity implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int numSteps=0, limNumSteps=-1;

    private TextView mSrcMessage,mDestMessage,mNavMsg,mNumStepsMsg;
    private int mListenerRegistered=0;
    Button mStartNav,mStopNav;
    int mDestNum=0,mSrcNum=0;
    int mDestGroup=0,mSrcGroup=0;
    int mDir=0;
    int mStepsG1[]={25,15,24,14},mStepsG2[]={7,25,4,24,3,20},mStepsCross=7;
    int mAryPtrSrc,mAryPtrDest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_prev:
                    Intent mPrevIntent = new Intent(NavigateBasic.this, SourceIdentification.class);
                    startActivity(mPrevIntent);
                    finish();
                    return true;
                case R.id.bottom_navigation_steps:
                    Intent mGuideIntent = new Intent(NavigateBasic.this, MainActivity.class);
                    startActivity(mGuideIntent);
                    finish();
                    return true;
                case R.id.bottom_navigation_next:
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_basic);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        numSteps=0;
        sensorManager.registerListener(NavigateBasic.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mListenerRegistered=1;

        mSrcMessage = (TextView) findViewById(R.id.srcmessage);
        mDestMessage = (TextView) findViewById(R.id.destmessage);
        mNavMsg = (TextView) findViewById(R.id.navigationmsg);
        mNumStepsMsg = (TextView) findViewById(R.id.numstepsmsg);
        mStartNav = (Button) findViewById(R.id.startnavbtn);
        mStopNav = (Button) findViewById(R.id.stopnavbtn);

        mStartNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Navigation Started",Toast.LENGTH_SHORT).show();

                String mSavedSrc="Hello 000",mSavedDest="Hello 000";
                SharedPreferences sd=getSharedPreferences("data",Context.MODE_PRIVATE);

                mSavedSrc = sd.getString("sdSrc","");
                mSavedDest = sd.getString("sdDest","");

                Toast.makeText(getApplicationContext(),"Src: "+mSavedSrc
                        +"\nDestination: "+mSavedDest,Toast.LENGTH_SHORT).show();

                if (mSavedDest.equals("Washroom"))
                {   Toast.makeText(getApplicationContext(),"washroom selected",Toast.LENGTH_SHORT).show();
                    mDestNum = 108; mDestGroup=2;   }
                else if (mSavedDest.equals("Entrance"))
                {   mDestNum = 111; mDestGroup=2;   }
                else if (mSavedDest.equals("HOD Cabin"))
                {   mDestNum = 109; mDestGroup=2;   }
                else
                {
                    mDestMessage.setText("Destination selected: "+mSavedDest.substring(mSavedDest.length() - 3));
                    //Toast.makeText(getApplicationContext(),"Dest:"+mSavedDest.substring(mSavedDest.length() - 3),Toast.LENGTH_SHORT).show();
                    mDestNum = Integer.parseInt(mSavedDest.substring(mSavedDest.length() - 3));
                    if (mDestNum<=105)
                        mDestGroup=1;
                    else
                        mDestGroup=2;
                }

                if (mSavedSrc.equals("Washroom"))
                {   mSrcNum = 108; mSrcGroup=2;   }
                else if (mSavedSrc.equals("Entrance"))
                {   mSrcNum = 111; mSrcGroup=2;   }
                else if (mSavedSrc.equals("HOD Cabin"))
                {   mSrcNum = 109; mSrcGroup=2;   }
                else
                {
                    mSrcMessage.setText("Source selected: "+mSavedSrc.substring(mSavedSrc.length() - 3));
                    //Toast.makeText(getApplicationContext(),"Src:"+mSavedSrc.substring(mSavedSrc.length() - 3),Toast.LENGTH_SHORT).show();
                    mSrcNum = Integer.parseInt(mSavedSrc.substring(mSavedSrc.length() - 3));
                    //mSrcNum=0;
                    if (mSrcNum<=105)
                        mSrcGroup=1;
                    else
                        mSrcGroup=2;
                }

                mAryPtrSrc = mSrcNum-101;
                mAryPtrDest = mDestNum-101;
                if (mAryPtrSrc>3) mAryPtrSrc-=4;
                if (mAryPtrDest>3) mAryPtrDest-=4;

                if (mSrcGroup==mDestGroup)
                {
                    Toast.makeText(getApplicationContext(),"Same Group",Toast.LENGTH_SHORT).show();
                    if (mSrcGroup==1 && mSrcNum>mDestNum) {
                        mDir=-1;
                        for (int i=mAryPtrSrc-1; i>=mAryPtrDest; i+=mDir)
                        {
                            mNavMsg.setText("Inside the loop");
                            if (mListenerRegistered!=1) {
                                limNumSteps = mStepsG1[i];
                                //while (mStepsFlag==0){}
                                mNavMsg.setText(mStepsG1[i] + " steps towards Entrance");
                                Toast.makeText(getApplicationContext(), mStepsG1[i] +
                                        " steps towards Entrance", Toast.LENGTH_SHORT).show();
                                sensorManager.registerListener(NavigateBasic.this,
                                        accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
                            }
                            //else
                            //    i++;
                        }
                        //Toast.makeText(getApplicationContext(),"Direction: Towards Entrance",Toast.LENGTH_SHORT).show();
                    }
                    else if (mSrcGroup==1 && mSrcNum<mDestNum){
                        mDir=1;
                        for (int i=mAryPtrSrc; i<mAryPtrDest; i+=mDir)
                        {
                            Toast.makeText(getApplicationContext(),mStepsG1[i]+
                                    " steps towards 105",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getApplicationContext(),"Direction: Towards 105",Toast.LENGTH_SHORT).show();
                    }
                    else if (mSrcGroup==2 && mSrcNum<mDestNum){
                        mDir=1;
                        for (int i=mAryPtrSrc; i<mAryPtrDest; i+=mDir)
                        {
                            Toast.makeText(getApplicationContext(),mStepsG2[i]+
                                    " steps towards Entrance",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getApplicationContext(),"Direction: Towards Entrance",Toast.LENGTH_SHORT).show();
                    }
                    else if (mSrcGroup==2 && mSrcNum>mDestNum){
                        mDir=-1;
                        for (int i=mAryPtrSrc-1; i>=mAryPtrDest; i+=mDir)
                        {
                            Toast.makeText(getApplicationContext(),mStepsG2[i]+
                                    " steps towards 105",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(getApplicationContext(),"Direction: Towards 105",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Cross the Passage",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStopNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(NavigateBasic.this);
                mListenerRegistered=0;
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccelerometer(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        mNumStepsMsg.setText("Steps : " + numSteps);
        if (numSteps==limNumSteps) {
            mListenerRegistered = 0;
            sensorManager.unregisterListener(NavigateBasic.this);
            numSteps=0;
        }
    }
}
package com.rajankz.research.hci.gesture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.android.inputmethod.latin.LocaleUtils;
import com.android.inputmethod.latin.SettingsValues;
import com.android.inputmethod.latin.SubtypeSwitcher;
import com.google.code.microlog4android.*;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.appender.LogCatAppender;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.google.code.microlog4android.repository.DefaultLoggerRepository;
import android.view.View.OnClickListener;
import com.logger.HCILogger;

import com.android.inputmethod.latin.R;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**

 * Things we should definitely support:
 Common/normal punctuation: ? ! ' - " &
 Symbols used on the internet: @ # /
 Math/number symbols: $ %
 *
 */
public class StartActivity extends Activity implements OnClickListener, View.OnTouchListener {
    private static final String TAG = "StartActivity";

    EditText participantIdText;
    TextView phraseText;
    EditText phraseInput;
    private SettingsValues mSettingsValues;

    int index = 0;
    private static boolean firstClick = false;
    private static InputStream iStream;
    private static BufferedReader br;
    SharedPreferences sharedPrefs;

    int numOfSets;
    int numOfPhrases;
    String participantID;
    String onePhrase;

    final int NUM_SETS = 2;
    final int NUM_PHRASES = 20;

    enum TestType{Practice, Test};
    enum InputSet{Normal, Mixed};

    boolean enableGestures;
    boolean enableAlternateKeyboard;
    //boolean enableAutoCorrect;

    RadioGroup rGroup;
    String mTestType, mInputSet;

    private Resources mResources;
    private Random mRandom;

    private char[] mSymbolsArray = {'@','#','$','%','&','\'',',','?','!','\"'};
    private char[] mAlphabetsArray = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
      'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };
    private ArrayList<String> phraseList = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.enter);
        PropertyConfigurator.getConfigurator(this).configure();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        final Resources res = getResources();
        mResources = res;

        final LocaleUtils.RunInLocale<SettingsValues> job = new LocaleUtils.RunInLocale<SettingsValues>() {
            @Override
            protected SettingsValues job(Resources res) {
                return new SettingsValues(sharedPrefs, StartActivity.this);
            }
        };
        //mSettingsValues = job.runInLocale(mResources, mSubtypeSwitcher.getCurrentSubtypeLocale());
        mSettingsValues = job.runInLocale(mResources, Locale.getDefault());

        setDefaultValues();

        iStream = getResources().openRawResource(R.raw.phrases);
        br = new BufferedReader(new InputStreamReader(iStream));


        participantIdText = (EditText)findViewById(R.id.participantId);
        participantIdText.requestFocus();
        final Button startButton = (Button)findViewById(R.id.startButton);

        startButton.setEnabled(false);
        participantIdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0)
                    startButton.setEnabled(true);
                else
                    startButton.setEnabled(false);
            }
        });
        startButton.setOnClickListener(this);
    }

    private void setDefaultValues(){
        enableAlternateKeyboard =  mSettingsValues.mAlternateKeyboardEnabled;
        enableGestures = mSettingsValues.mGestureEnabled;

        CheckBox cbGestures = (CheckBox)findViewById(R.id.cbUseGestures);
        CheckBox cbAlternateKeyboard = (CheckBox)findViewById(R.id.cbUseAlternateKeyboard);
        cbGestures.setChecked(enableGestures);
        cbAlternateKeyboard.setChecked(enableAlternateKeyboard);


    }

    private void getAllValues(){
        participantID = ((EditText)findViewById(R.id.participantId)).getText().toString();

        try{
        numOfSets =  Integer.parseInt(((EditText)findViewById(R.id.numSets)).getText().toString());
        }catch(NumberFormatException nfe){numOfSets = NUM_SETS;}
        catch(NullPointerException npe){numOfSets = NUM_SETS;}

        try{
        numOfPhrases =  Integer.parseInt(((EditText)findViewById(R.id.numPhrases)).getText().toString());
        }catch(NumberFormatException nfe){numOfPhrases = NUM_PHRASES;}
        catch(NullPointerException npe){numOfPhrases = NUM_PHRASES;}

        rGroup = (RadioGroup)findViewById(R.id.radgrpTestType);
        if(rGroup.getCheckedRadioButtonId() == R.id.radio_practice)
            mTestType = TestType.Practice.toString();
        else
            mTestType = TestType.Test.toString();

        rGroup = (RadioGroup)findViewById(R.id.rGrpInputSet);
        if(rGroup.getCheckedRadioButtonId() == R.id.radio_normal)
            mInputSet = InputSet.Normal.toString();
        else
            mInputSet = InputSet.Mixed.toString();



    }

    public void onCheckboxClicked(View view){
         boolean checked = ((CheckBox) view).isChecked();
         SharedPreferences.Editor editor = sharedPrefs.edit();

         // Check which checkbox was clicked
         switch(view.getId()) {
             case R.id.cbUseGestures:
                 enableGestures = checked;
                 editor.putBoolean("gesturesPref",checked);
                 editor.commit();
                 break;
             case R.id.cbUseAlternateKeyboard:
                 enableAlternateKeyboard = checked;
                 editor.putBoolean("alternateKeyboardPref",checked);
                 editor.commit();
                 break;
         }
     }

    public void onRadioClicked(View view){
        //Toast.makeText(this.getApplicationContext(),""+view.toString(),10).show();
        //Log.d(TAG,view.toString());

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        /*
        final int action = ev.getAction();
        Log.i(TAG,"action="+action);
        */
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.startButton){
            getAllValues();
            HCILogger.getInstance().setLogFolderName(participantID);
            HCILogger.getInstance().logSessionStart(participantID, enableGestures, enableAlternateKeyboard, mTestType, mInputSet,numOfSets,numOfPhrases,Calendar.getInstance().getTimeInMillis());

            fillPhraseList();

            showPhraseScreen();
        }

    }

    public void fillPhraseList(){
        String text;
        try{
        while((text = br.readLine())!=null){
            phraseList.add(text);
        }
        }catch(IOException ioe){
            HCILogger.getInstance().error("Cannot Read from Phrases File");
            phraseText.setText("ERROR::Please contact the coordinator.");
        }
    }

    private void showPhraseScreen(){
        this.setContentView(R.layout.activity_main);
        phraseText = (TextView)findViewById(R.id.phraseText);
        phraseInput = (EditText)findViewById(R.id.editText);
        //phraseInput.setOnKeyListener(this);
        phraseInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if(keyCode == (EditorInfo.IME_ACTION_DONE)){// && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    HCILogger.getInstance().logSystemEvents("DONE_CLICK",Calendar.getInstance().getTimeInMillis());
                    if(!phraseText.getText().equals("") && (phraseInput.getText().length() < phraseText.getText().length()/2)){
                        return true;
                    }
                    loadPhrases();
                }
                return true;

                //Log.d(TAG, "onEditorAction");
                //TODO: This is where we handle the ENTER key
                //return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        loadPhrases();
    }

    private void showBreakScreen(){
        index = 0;
        if(--numOfSets == 0){
            this.setContentView(R.layout.end);
            HCILogger.getInstance().logSesionEnd(Calendar.getInstance().getTimeInMillis());
        }
        else{
            HCILogger.getInstance().logSessionBreak(Calendar.getInstance().getTimeInMillis());
            this.setContentView(R.layout.intermission);
        }
    }

    public void btnNextClick(View view){
        HCILogger.getInstance().logSystemEvents("NEXT_CLICK",Calendar.getInstance().getTimeInMillis());
        showPhraseScreen();
    }

    public void btnEndClick(View view){
        HCILogger.getInstance().logSystemEvents("END_CLICK",Calendar.getInstance().getTimeInMillis());
        HCILogger.getInstance().setLogFolderName("");
        this.setContentView(R.layout.enter);
    }

    private void loadPhrases()  {
        if(index++ == numOfPhrases)
            showBreakScreen();

        if(mInputSet.equals(InputSet.Normal.toString())){

            loadNoramlPhrases();
        }else{
            loadMixedPhrases();
        }

    }

    private void loadNoramlPhrases(){
            mRandom = new Random();
            int randomNum = mRandom.nextInt(phraseList.size());
            onePhrase = phraseList.get(randomNum);
            HCILogger.getInstance().logSystemEvents("PHRASE_LOADED:"+onePhrase,Calendar.getInstance().getTimeInMillis());
            phraseText.setText(onePhrase);
            phraseInput.setText("");
            phraseInput.requestFocus();
    }

    private void loadMixedPhrases(){
        onePhrase = getRandomPhrase();
        HCILogger.getInstance().logSystemEvents("PHRASE_LOADED:"+onePhrase,Calendar.getInstance().getTimeInMillis());
        phraseText.setText(onePhrase);
        phraseInput.setText("");
        phraseInput.requestFocus();
    }

    private String getRandomPhrase(){
        StringBuilder aPhrase = new StringBuilder(5);
        for(int i=0;i<5;i++){
            if(mRandom.nextBoolean())
                aPhrase.append(mSymbolsArray[mRandom.nextInt(mSymbolsArray.length)]);
            else
                aPhrase.append(mAlphabetsArray[mRandom.nextInt(mAlphabetsArray.length)]);
        }
        return aPhrase.toString();
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event){
        final int action = event.getAction();
        String actionStr;
        switch(action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{ actionStr = "HCI First Pointer Down"; break;}
            case MotionEvent.ACTION_UP:{ actionStr = "HCI First Pointer Up"; break;}
            case MotionEvent.ACTION_POINTER_DOWN:{ actionStr = "HCI Second Pointer Down"; break;}
            case MotionEvent.ACTION_POINTER_UP:{ actionStr = "HCI Second Pointer Up"; break;}
            default: {actionStr="ActionCode="+action;break;}
        }
        HCILogger.getInstance().debug(Calendar.getInstance().getTimeInMillis()+": HCI TouchEvent:"+actionStr+". x="+event.getX()+" y="+event.getY());
        return false;
    }
    */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        /*final int action = motionEvent.getAction();
        String actionStr;
        switch(action & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{ actionStr = "HCI First Pointer Down"; break;}
            case MotionEvent.ACTION_UP:{ actionStr = "HCI First Pointer Up"; break;}
            case MotionEvent.ACTION_POINTER_DOWN:{ actionStr = "HCI Second Pointer Down"; break;}
            case MotionEvent.ACTION_POINTER_UP:{ actionStr = "HCI Second Pointer Up"; break;}
            default: {actionStr="ActionCode="+action;break;}
        }
        HCILogger.getInstance().debug(Calendar.getInstance().getTimeInMillis()+": HCI TouchEvent:"+actionStr+". x="+motionEvent.getX()+" y="+motionEvent.getY());
        */
        return false;
    }


    @Override
    protected void onPause(){
        HCILogger.getInstance().setLogFolderName("");
        super.onPause();
    }

    @Override
    protected  void onResume(){
        HCILogger.getInstance().setLogFolderName(participantID);
        super.onResume();
    }

}

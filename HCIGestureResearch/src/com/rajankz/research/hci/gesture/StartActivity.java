package com.rajankz.research.hci.gesture;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.inputmethod.latin.LocaleUtils;
import com.android.inputmethod.latin.SettingsValues;
import com.google.code.microlog4android.config.PropertyConfigurator;
import android.view.View.OnClickListener;
import com.logger.HCILogger;

import com.android.inputmethod.latin.R;

import java.io.*;
import java.util.*;

/**

 * Things we should definitely support:
 Common/normal punctuation: ? ! ' - " &
 Symbols used on the internet: @ #
 Math/number symbols: $ %
 *
 */

public class StartActivity extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "StartActivity";

    EditText participantIdText;
    TextView phraseText;
    TextView txtNum;
    EditText phraseInput;
    private SettingsValues mSettingsValues;

    int index = 0;
    int dispIndex = 0;
    private static InputStream iStream;
    private static BufferedReader br;
    SharedPreferences sharedPrefs;

    int numOfSets;
    int numOfPhrases;

    int numPracticeBlock = 1;
    int numPracticeTrials = 20;
    int numMainTestBlock = 2;
    int numMainTestTrials = 20;

    String participantID;
    String onePhrase;

    final int NUM_SETS = 2;
    final int NUM_PHRASES = 20;



    enum TestType{Practice, MainTest};
    enum InputSet{Normal, Mixed};

    boolean enableGestures;
    boolean enableAlternateKeyboard;

    RadioGroup rGroup;
    String mTestType, mInputSet;

    private Resources mResources;
    private Random mRandom;

    private char[] mSymbolsArray = {'@','#','$','%','&','\'','-','?','!','\"'};
    private char[] mAlphabetsArray = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private ArrayList<String> phraseList = new ArrayList<String>();

    EditText etNumSets, etNumPhrases;
    RadioButton rbPractice, rbMainTest, rbNormal, rbMixed;
    RadioGroup rgTestType, rgInputSet;


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

        mSettingsValues = job.runInLocale(mResources, Locale.getDefault());
        setListeners();
        setDefaultValues();
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

    private void setListeners(){
        etNumSets = (EditText)findViewById(R.id.numSets);
        etNumPhrases = (EditText)findViewById(R.id.numPhrases);
        rbPractice = (RadioButton)findViewById(R.id.radio_practice);
        rbMainTest = (RadioButton)findViewById(R.id.radio_mainTest);
        rbNormal = (RadioButton)findViewById(R.id.radio_normal);
        rbMixed = (RadioButton)findViewById(R.id.radio_mixed);

        rgTestType = (RadioGroup)findViewById(R.id.radgrpTestType);
        rgInputSet = (RadioGroup)findViewById(R.id.rGrpInputSet);
        rgInputSet.setOnCheckedChangeListener(this);
        rgTestType.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        Log.i(TAG,"radio changed"+checkedId);
        switch(checkedId){
            case R.id.radio_practice:
                etNumSets.setText(""+numPracticeBlock);
                etNumPhrases.setText(""+numPracticeTrials);
                etNumSets.setEnabled(false);
                etNumPhrases.setEnabled(false);
                rgInputSet.check(rbNormal.getId());
                rbNormal.setEnabled(false);
                rbMixed.setEnabled(false);
                break;
            case R.id.radio_mainTest:
                etNumSets.setText(""+numMainTestBlock);
                etNumPhrases.setText(""+numMainTestTrials);
                etNumSets.setEnabled(true);
                etNumPhrases.setEnabled(true);
                rbNormal.setEnabled(true);
                rbMixed.setEnabled(true);
                break;
        }
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

        numOfSets = Integer.valueOf(((EditText)findViewById(R.id.numSets)).getText().toString());
        numOfPhrases = Integer.valueOf(((EditText)findViewById(R.id.numPhrases)).getText().toString());

        rGroup = (RadioGroup)findViewById(R.id.radgrpTestType);
        if(rGroup.getCheckedRadioButtonId() == R.id.radio_practice)  {
            mTestType = TestType.Practice.toString();
            iStream = getResources().openRawResource(R.raw.practice);
        }
        else if(rGroup.getCheckedRadioButtonId() == R.id.radio_mainTest)  {
            mTestType = TestType.MainTest.toString();
            iStream = getResources().openRawResource(R.raw.test);
        }

        br = new BufferedReader(new InputStreamReader(iStream));

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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.startButton){
            getAllValues();
            String testType = rgTestType.getCheckedRadioButtonId()==R.id.radio_practice?TestType.Practice.toString():TestType.MainTest.toString();
            String inputSet = rgInputSet.getCheckedRadioButtonId()==R.id.radio_normal?InputSet.Normal.toString():InputSet.Mixed.toString();
            if(testType.equalsIgnoreCase(TestType.Practice.toString()))
                inputSet = "";
            HCILogger.getInstance().setLogFolderName(participantID, testType, inputSet);
            HCILogger.getInstance().logSessionStart(participantID, enableGestures, enableAlternateKeyboard, mTestType, mInputSet,numOfSets,numOfPhrases,Calendar.getInstance().getTimeInMillis());

            fillPhraseList();

            showPhraseScreen();
        }



    }

    public void fillPhraseList(){
        String text;
        try{
        while((text = br.readLine())!=null){
            phraseList.add(text.toLowerCase());
        }
        }catch(IOException ioe){
            HCILogger.getInstance().error("Cannot Read from Phrases File");
            phraseText.setText("ERROR::Please contact the coordinator.");
        }
        Collections.shuffle(phraseList, new Random());
    }

    private void showPhraseScreen(){
        this.setContentView(R.layout.activity_main);
        phraseText = (TextView)findViewById(R.id.phraseText);
        txtNum = (TextView)findViewById(R.id.txtNum);

        phraseInput = (EditText)findViewById(R.id.editText);
        //phraseInput.setInputType(InputType.);
        //if mixed words then do not show suggestions
        if(mInputSet.equals(InputSet.Mixed.toString())){
            phraseInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //phraseInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            //phraseInput.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        }/* else{
            phraseInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        }   */


        phraseInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {

                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    HCILogger.getInstance().logSystemEvents("ENTER_DONE_CLICK",Calendar.getInstance().getTimeInMillis());
                    HCILogger.getInstance().logEnteredTextFinal(phraseInput.getText().toString(),Calendar.getInstance().getTimeInMillis());
                    if(!phraseText.getText().equals("") && (4*phraseInput.getText().length() < phraseText.getText().length()*3)){
                        return true;
                    }
                    loadPhrases();
                }

                return true;  //To change body of implemented methods use File | Settings | File Templates.
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
        finish();
        System.exit(0);
    }


    public void loadPhrases()  {
        if(index++ == numOfPhrases){
            showBreakScreen();
            return;
        }

        String paginate = "Phrase "+index+" of "+numOfPhrases;
        txtNum.setText(paginate);

        if(mInputSet.equals(InputSet.Normal.toString())){
            loadPhraseFromFile();
        }else{
            loadCreatedMixedString();
        }

        showKeyboard();

    }

    private void showKeyboard(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(phraseInput, 0);
    }

    private void loadPhraseFromFile(){
            //mRandom = new Random();
            //if(mTestType.equals(TestType.MainTest.toString())){dispIndex =  mRandom.nextInt(phraseList.size());}
            onePhrase = phraseList.get(dispIndex++);
            HCILogger.getInstance().logSystemEvents("NORMAL_PHRASE_LOADED:"+onePhrase,Calendar.getInstance().getTimeInMillis());
            phraseText.setText(onePhrase);
            phraseInput.setText("");
            phraseInput.requestFocus();
    }

    private void loadCreatedMixedString(){
        onePhrase = getRandomPhrase();
        HCILogger.getInstance().logSystemEvents("MIXED_PHRASE_LOADED:"+onePhrase,Calendar.getInstance().getTimeInMillis());
        phraseText.setText(onePhrase);
        phraseInput.setText("");
        phraseInput.requestFocus();
    }

    private String getRandomPhrase(){
        StringBuilder aPhrase = new StringBuilder(5);
        mRandom = new Random();
        for(int i=0;i<5;i++){
            if(mRandom.nextBoolean())
                aPhrase.append(mSymbolsArray[mRandom.nextInt(mSymbolsArray.length)]);
            else
                aPhrase.append(mAlphabetsArray[mRandom.nextInt(mAlphabetsArray.length)]);
        }
        return aPhrase.toString();
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

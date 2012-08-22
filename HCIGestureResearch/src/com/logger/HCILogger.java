package com.logger;

import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import com.android.inputmethod.keyboard.Keyboard;
import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.appender.LogCatAppender;
import com.google.code.microlog4android.format.PatternFormatter;
import com.google.code.microlog4android.repository.DefaultLoggerRepository;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class HCILogger {

    public static final HCILogger instance = new HCILogger();
    static String logFileName="";

    Logger rootLogger = DefaultLoggerRepository.INSTANCE.getRootLogger();
    FileAppender fileAppender = new FileAppender();
    LogCatAppender logCatAppender = new LogCatAppender();
    Logger logger = LoggerFactory.getLogger();
    boolean appenderSet = false;
    PatternFormatter patternFormatter;

    Calendar cal;
    Level level;
    String eventType;
    String text;
    String action;
    float x;
    float y;
    int eventPointerId, numOfPointers;
    String inputText;
    String tagName, participantId, sessionType, sessionSet;
    boolean gestureEnabled, alternateKeyboardEnabled, isTagDone;
    int numSets, numPhrases;
    long systemTimestamp, eventTimestamp;

    public enum PointerAction{DOWN, UP, MOVE};
    public enum TagName{SESSION,SET,PHRASE,KEY,GESTURE,STROKE,SYSTEM,TOUCH,SYMBOL};




    private HCILogger(){ }

    public static synchronized HCILogger getInstance(){return instance; }

    private void setAppender(){
        if(appenderSet)return;

        patternFormatter = new PatternFormatter();
        patternFormatter.setPattern("%m");

        fileAppender.setFileName(getLogFileName());
        fileAppender.setAppend(true);
        fileAppender.setFormatter(patternFormatter);

        rootLogger.addAppender( fileAppender );
        rootLogger.addAppender( logCatAppender );

        appenderSet = true;
    }

    private void removeFileAppender(){
        rootLogger.removeAppender(fileAppender);
    }

    public void logSessionStart(String participantId, boolean gestureEnabled, boolean alternateKeyboardEnabled, String sessionType,
        String sessionSet, int numSets, int numPhrases, long systemTimestamp){
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+TagName.SESSION);
        sb.append(" participantId="+participantId);
        sb.append(" gestures="+(gestureEnabled?"enabled":"disabled"));
        sb.append(" alternateKeyboard="+(alternateKeyboardEnabled?"enabled":"disabled"));
        sb.append(" type="+sessionType);
        sb.append(" set="+sessionSet);
        sb.append(" numOfSets="+numSets);
        sb.append(" numOfPhrases="+numPhrases);
        sb.append(" systemTimestamp="+systemTimestamp);
        sb.append(" >");
        info(sb.toString());
    }

    public void logTouchEvent(String event,int pointerId, float x, float y, long eventTimestamp, int totalPointers, long systemTimestamp){
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+TagName.TOUCH);
        sb.append(" event="+event);
        sb.append(" pointerId="+pointerId);
        sb.append(" x="+x);
        sb.append(" y="+y);
        sb.append(" eventTimestamp="+eventTimestamp);
        sb.append(" totalPointers="+totalPointers);
        sb.append(" systemTimestamp="+systemTimestamp);
        sb.append(" />");
        info(sb.toString());
    }

    public void logSesionEnd(long systemTimestamp){
         logTag(TagName.SESSION, false, true, "Session_End",systemTimestamp);
    }
    public void logStrokeStart(long systemTimestamp){
        logTag(TagName.STROKE, true, true, "Stroke_Start",systemTimestamp);
    }
    public void logSessionBreak(long systemTimestamp){
        logSystemEvents("SESSION_BREAK",systemTimestamp);
    }
    public void logStrokeEnd(long systemTimestamp){
        logTag(TagName.STROKE, false, true, "Stroke_End",systemTimestamp);
    }
    public void logGestureStart(long systemTimestamp){
        logTag(TagName.GESTURE,true, true, "Gesture_Start",systemTimestamp);
    }
    public void logGestureEnd(long systemTimestamp){
        logTag(TagName.GESTURE,false, true, "Gesture_End",systemTimestamp);
    }
    public void logKey(int keyCode, long systemTimestamp){
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+TagName.KEY);
        sb.append(" keyCode="+keyCode);
        sb.append(" printKey="+ Keyboard.printableCode(keyCode));
        sb.append(" systemTimestamp="+systemTimestamp);
        sb.append(" />");
        info(sb.toString());
    }
    public void logKeyXY(int keyCode, int x, int y, long systemTimestamp){
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+TagName.KEY);
        sb.append(" keyCode="+keyCode);
        sb.append(" printKey="+ Keyboard.printableCode(keyCode));
        sb.append(" x="+x);
        sb.append(" y="+y);
        sb.append(" systemTimestamp="+systemTimestamp);
        sb.append(" />");
        info(sb.toString());
    }
    public void logSymbol(char symbol, double score, long systemTimestamp){
        StringBuilder sb = new StringBuilder("");
        sb.append("<"+TagName.SYMBOL);
        sb.append(" symbol="+symbol);
        sb.append(" symbolCode="+Keyboard.getSymbolsCode(symbol));
        sb.append(" predictionScore="+score);
        sb.append(" systemTimestamp="+systemTimestamp);
        sb.append(" />");
        info(sb.toString());
    }

    private void logTag(TagName tagName, boolean tagOpening, boolean addSystemEvent, String systemEvent, long systemTimestamp){
        if(addSystemEvent)logSystemEvents(systemEvent,systemTimestamp);
        String tag = (tagOpening?"":"/")+tagName;
        info("<"+tag+">");
    }

    public void logSystemEvents(String eventType, long systemTimestamp){
        info("<"+TagName.SYSTEM+" event="+eventType+" systemTimestamp="+systemTimestamp+" />");
    }

    public void logEnteredTextFinal(String text,long systemTimestamp){
        info("<"+TagName.SYSTEM+" event=LogEnteredText"+" text="+text+" systemTimestamp="+systemTimestamp+" />");
    }

    public void debug(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.debug(log);
    }

    public void info(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.info(log);
    }

    public void error(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.error(log);
    }

    public void warn(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.warn(log);
    }

    public void fatal(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.fatal(log);
    }

    public void trace(String log){
        if(logFileName.equals(""))
            return;
        if(!appenderSet)setAppender();
        logger.trace(log);
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFolderName(String participantID) {
        HCILogger.getInstance();

        if(participantID == null)
            participantID = "";
        if(participantID.equals("")){
            rootLogger.removeAppender(fileAppender);
            return;
        }

        createFileStructure(participantID);

        if(!appenderSet)setAppender();
    }

    private static void createFileStructure(String participantID){

        String rootFolderString = "HCILogs"+File.separator+participantID;
        File rootFolder = new File(Environment.getExternalStorageDirectory()+ File.separator+rootFolderString);

        if(!rootFolder.exists())
            rootFolder.mkdirs();
        rootFolder.setWritable(true);

        int items = rootFolder.listFiles().length;
        String fileName =  participantID+(items+1)+".txt";
        logFileName =  Environment.getExternalStorageDirectory()+ File.separator+rootFolderString+File.separator+fileName;
        File logFile = new File(logFileName);
        if(!logFile.exists())
            try{
                logFile.createNewFile();
            } catch (IOException ioe) {
                Log.e("StartActivity", ioe.getMessage() + "\n" + ioe.getStackTrace());
            }
        logFile.setWritable(true);
        logFileName = rootFolderString+File.separator+fileName;


    }

}

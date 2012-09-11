import java.io.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: raj
 * Date: 9/6/12
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class HCILogConverter {

    String path;
    File file;
    File topDir;
    File xmlOutputDir;

    public void loadFile(String path){


    }

    private void preProcess(){
        xmlOutputDir = new File(topDir.getAbsolutePath()+"/xmls");
        if(!xmlOutputDir.exists() && !xmlOutputDir.mkdirs()){
            System.out.println("Unable to create xml directory");
            System.exit(-1);
        }

    }

    public void convertLog(){
        if(topDir==null)
            return;
        preProcess();
        File[] logFiles = topDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                if(fileName.substring(fileName.lastIndexOf(".")+1).equalsIgnoreCase("txt") ||
                        fileName.substring(fileName.lastIndexOf(".")+1).equalsIgnoreCase("xml"))
                    return true;
                else
                    return false;
            }
        });
        for(File oneLogFile:logFiles){
            convertOneLogFile(oneLogFile);
        }
    }

    private String handleSessionStartTag(String line){
       StringBuilder sb = new StringBuilder("");
        sb.append("<TextTest version=\"2.0.2\" ");
        String timeStamp = line.substring(line.indexOf("systemTimestamp=")+16);
        timeStamp = timeStamp.substring(0,timeStamp.indexOf(" "));
        long time=Long.parseLong(timeStamp);
        double timeD = Math.round(time)/1000.0;
        sb.append("time=\""+timeD+"\" ");
        Date date = new Date(time);
        sb.append("date=\""+date.toString()+"\">");
        return sb.toString();
    }

    private String handleSessionEndTag(String line){
        StringBuilder sb = new StringBuilder("");
        sb.append("</TextTest>");
        return sb.toString();
    }

    private String handleKeyTag(String line){
        StringBuilder sb = new StringBuilder("");
        sb.append("<entry ");
//        String ch=line.substring(line.indexOf("printKey='")+10,1);

        String timeStamp = line.substring(line.indexOf("systemTimestamp=")+16);
        timeStamp = timeStamp.substring(0,timeStamp.indexOf(" "));
        long time=Long.parseLong(timeStamp);
        double timeD = Math.round(time)/1000.0;
        sb.append("time=\""+timeD+"\" />");

        return "";
    }

    private String handleSystemTag(String line){
        StringBuilder sb = new StringBuilder("");
        String eventStr = line.substring(line.indexOf("event=")+6);
        eventStr = eventStr.substring(0,eventStr.indexOf(" "));
        if(!eventStr.contains(":"))
            return "";

        String event=eventStr.substring(0,eventStr.indexOf(":"));

        if(event.contains("MIXED_PHRASE_LOADED")){
            sb.append("<presented>");
            String phrase=eventStr.substring(eventStr.indexOf(":"));
            sb.append(phrase);
            sb.append("</presented>");
        }
        //if(event.contains("LogEnteredText"))


        return sb.toString();
    }

    private void parseDocument(File xmlFile) throws IOException, UnsupportedEncodingException {
        File newXMlFile = new File(xmlOutputDir+"/"+xmlFile.getName());
        boolean fileCreated = false;
        try{
        if(newXMlFile.exists())
            newXMlFile.delete();
        fileCreated = newXMlFile.createNewFile();
        }catch(Exception e){e.printStackTrace();}

        if(!fileCreated){
            System.out.println("Unable to create new xml file : "+newXMlFile.getName());
            System.exit(-1);
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(newXMlFile));
        out.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

        FileInputStream fis = new FileInputStream(xmlFile);
        InputStreamReader in = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(new FileReader(xmlFile));
        String line, text="";
        while((line = br.readLine())!= null){
            if(line.startsWith("<SESSION")){
                text = handleSessionStartTag(line);
            }else if(line.startsWith("<KEY")){
                text = handleKeyTag(line);
            }else if(line.startsWith("<SYSTEM")){
                text = handleSystemTag(line);
            }else if(line.startsWith("</SESSION"))
                text = handleSessionEndTag(line);
            if(text.equalsIgnoreCase("")) continue;
            out.write("\n" + text);
        }


        out.close();
        /*
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(file,
                    );
            sp.pa

        }catch(SAXException se) {
            se.printStackTrace();
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ie) {
            ie.printStackTrace();
        }

        */
    }

    public void convertOneLogFile(File oneLogFile){
        System.out.println("Working on : "+oneLogFile.getName());
        try{
        parseDocument(oneLogFile);
        }catch(Exception e){e.printStackTrace();}
    }

    public static void main(String[] args){

        HCILogConverter hcilc = new HCILogConverter();

        if(args.length < 1)
        {
            System.out.println("Not enough parameters");
            System.exit(-1);
        }

        hcilc.topDir = new File(args[0]);
        if(!(hcilc.topDir.exists() && hcilc.topDir.isDirectory())){
            System.out.println("Not a directory");
            System.exit(-1);
        }
        hcilc.convertLog();
    }
}

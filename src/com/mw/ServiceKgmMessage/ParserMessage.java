package com.mw.ServiceKgmMessage;

import android.util.Pair;
import com.mw.kgmspread.Event;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by mishaw on 30.07.14.
 */
public class ParserMessage {

    private XmlPullParser xml;
    private HashMap<Event, Pair<HashMap<Integer, String>, String>> dataTranslateMessage;
    public ParserMessage(XmlPullParser xml){
        this.xml = xml;
        dataTranslateMessage = new HashMap<Event, Pair<HashMap<Integer, String>, String>>();
    }

    public boolean parsing() {

        HashMap<String, HashMap<Integer, String>> arrayIndexToNumber = new HashMap<String, HashMap<Integer, String>>();
        int attributeCount;
        boolean farray = false;
        boolean fmessage = false;

        try {
            while(xml.getEventType() != XmlPullParser.END_DOCUMENT){
                switch (xml.getEventType()){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("config_message".equals(xml.getName()))
                            break;
                        else if ("array".equals(xml.getName())){
                            farray = true;
                            break;
                        } else if ("message".equals(xml.getName())){
                            fmessage = true;
                            break;
                        }

                        if (farray){
                            HashMap<Integer, String> indexToNumber = new HashMap<Integer, String>();
                            String arrayName= "";
                            String arrayNumber[];
                            attributeCount = xml.getAttributeCount();
                            for(int i=0; i<attributeCount; ++i){
                                if ("name".equals(xml.getAttributeName(i))){
                                    arrayName = xml.getAttributeValue(i);
                                } else if ("number".equals(xml.getAttributeName(i))){
                                    arrayNumber = xml.getAttributeValue(i).split(",");
                                    for (int n=0; n<arrayNumber.length; ++n){
                                        indexToNumber.put(n, arrayNumber[n]);
                                    }
                                }
                            }
                            arrayIndexToNumber.put(arrayName, indexToNumber);
                        } else if (fmessage){
                            String nameMessage = xml.getName();
                            String textMessage = "";
                            String arrayName = "";
                            attributeCount = xml.getAttributeCount();
                            for(int i=0; i<attributeCount; ++i){
                                if ("array".equals(xml.getAttributeName(i))){
                                    arrayName = xml.getAttributeValue(i);
                                } else if ("text".equals(xml.getAttributeName(i))){
                                    textMessage = xml.getAttributeValue(i);
                                }
                            }
                            if (arrayIndexToNumber.containsKey(arrayName))
                                dataTranslateMessage.put(Event.valueOf(nameMessage), new Pair<HashMap<Integer, String>, String>(arrayIndexToNumber.get(arrayName), textMessage));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( "config_message".equals(xml.getName()))
                            break;
                        else if ( "array".equals(xml.getName())){
                            farray = false;
                        } else if ( "message".equals(xml.getName())){
                            fmessage = false;
                        }
                        break;
                    default:
                        break;
                }
                xml.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public HashMap<Event, Pair<HashMap<Integer, String>, String>> getDataTranslateMessage(){
        return dataTranslateMessage;
    }

}
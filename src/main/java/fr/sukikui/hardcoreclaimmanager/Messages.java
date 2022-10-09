package fr.sukikui.hardcoreclaimmanager;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling all messages belonging to the plugin
 */
public class Messages {
    private static HashMap<String,String> messages = new HashMap<>();

    /**
     * Method to return the message value according to its name
     * @param messageName
     * @return the message with a given name
     */
    public static String getMessages(String messageName) {
        if (messages.size() == 0) {
            readMessages();
        }
        for (Map.Entry<String, String> set : messages.entrySet()) {
            if (set.getKey().equals(messageName)) {
                return set.getValue();
            }
        }
        return "";
    }

    /**
     * Utility method to load in memory all strings in strings.xml file
     */
    private static void readMessages() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(new XMLHandler());
            reader.parse(HardcoreClaimManager.getInstance().getDataFolder().getAbsolutePath() + "/strings.xml");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class that define the handler for the XML parser
     */
    private static class XMLHandler extends DefaultHandler {
        private String STRING = "string";
        private String currentAttribute = "";
        private String currentTag = "";

        public void startElement(String nameSpace, String localName, String qName, Attributes attr) {
            if (qName.equals(this.STRING)) {
                this.currentTag = qName;
                this.currentAttribute = attr.getValue("name");
            }
        }

        public void endElement(String nameSpace, String localName, String qName) {
            currentTag = "";
        }

        public void characters(char[] characters, int start, int length) {
            if (this.currentTag.equals(STRING) && !Character.isISOControl(characters[start])) {
                String message = new String(characters,start,length);
                messages.put(this.currentAttribute,message);
            }
        }
    }
}

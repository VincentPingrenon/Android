package com.example.journaldebord.util;

import android.util.Xml;

import com.example.journaldebord.indicateurs.BooleanSelector;
import com.example.journaldebord.indicateurs.DateSelector;
import com.example.journaldebord.indicateurs.HourSelector;
import com.example.journaldebord.indicateurs.ImageSelector;
import com.example.journaldebord.indicateurs.IntegerSelector;
import com.example.journaldebord.indicateurs.SatisfactionSelector;
import com.example.journaldebord.indicateurs.Selectors;
import com.example.journaldebord.indicateurs.TextSelector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLReader {

    public XMLReader() {

    }

    /**
     * Read the given xmlFile and return the XMLDefi linked to it
     *
     * @param xmlFile the xmlFile to be read
     * @return an XMLDefi
     */
    public static XMLDefi readXML(File xmlFile) {
        try (InputStream in = new FileInputStream(xmlFile)) {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(in, null);
            xmlPullParser.nextTag();
            return readFeed(xmlPullParser);
        } catch (XmlPullParserException | IOException e) {
            System.out.println(e.getCause());
        }
        return null;
    }

    private static XMLDefi readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        XMLDefi xmlDefi = null;
        String name = parser.getName();
        // Starts by looking for the entry tag
        if (name.equals("defi")) {
            xmlDefi = readDefi(parser);
        }
        return xmlDefi;
    }

    private static XMLDefi readDefi(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "defi");
        XMLDefi xml = new XMLDefi();
        xml.setName(parser.getAttributeValue("", "name"));
        xml.setBeginDate(parser.getAttributeValue("", "dateBegin"));
        xml.setEndDate(parser.getAttributeValue("", "dateEnd"));
        parser.nextTag();
        String name = parser.getName();
        if (name.equals("Selectors")) {
            xml.setSelectors(readSelectors(parser));
        } else {
            skip(parser);
        }
        return xml;
    }

    private static List<Selectors> readSelectors(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "Selectors");
        List<Selectors> selectors = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("selector")) {
                selectors.add(readSelector(parser));
            } else {
                skip(parser);
            }
        }
        return selectors;
    }

    private static Selectors readSelector(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "selector");
        Selectors selector = readType(parser);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                selector.setName(readName(parser));
            } else if (name.equals("day")) {
                selector = readDay(parser, selector);
            } else if (name.equals("position")) {
                selector.setPosition(readPosition(parser));
            } else {
                skip(parser);
            }
        }
        return selector;
    }

    private static Selectors readType(XmlPullParser parser) {
        Selectors selector = null;
        String type = parser.getAttributeValue("", "type");
        switch (type) {
            case "boolean":
                selector = new BooleanSelector();
                break;
            case "date":
                selector = new DateSelector();
                break;
            case "hour":
                selector = new HourSelector();
                break;
            case "image":
                selector = new ImageSelector();
                break;
            case "number":
                selector = new IntegerSelector();
                break;
            case "satisfaction":
                selector = new SatisfactionSelector();
                break;
            case "text":
                selector = new TextSelector();
                break;
        }
        return selector;
    }

    private static String readName(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, "", "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, "", "name");
        return name;
    }

    private static Selectors readDay(XmlPullParser parser, Selectors selector) throws XmlPullParserException, IOException {
        selector.setDate(parser.getAttributeValue("", "day"));
        parser.nextTag();
        if (parser.getName().equals("value")) {
            selector = setValue(readText(parser), selector);
        }
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, "", "day");
        return selector;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static int readPosition(XmlPullParser parser) throws IOException, XmlPullParserException {
        int pos;
        parser.require(XmlPullParser.START_TAG, "", "position");
        pos = Integer.valueOf(readText(parser));
        parser.require(XmlPullParser.END_TAG, "", "position");
        return pos;
    }

    @SuppressWarnings("unchecked")
    private static Selectors setValue(String valueToTransform, Selectors selector) {
        if (selector instanceof BooleanSelector) {
            selector.setValue(Boolean.valueOf(valueToTransform));
        } else if (selector instanceof DateSelector) {
            selector.setValue(valueToTransform);
        } else if (selector instanceof HourSelector) {
            selector.setValue(Long.valueOf(valueToTransform));
        } else if (selector instanceof ImageSelector) {
            selector.setValue(valueToTransform);
        } else if (selector instanceof IntegerSelector) {
            selector.setValue(Integer.valueOf(valueToTransform));
        } else if (selector instanceof SatisfactionSelector) {
            selector.setValue(Integer.valueOf(valueToTransform));
        } else if (selector instanceof TextSelector) {
            selector.setValue(valueToTransform);

        }
        return selector;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

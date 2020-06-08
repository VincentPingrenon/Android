package com.example.journaldebord.util;

import android.util.Xml;

import com.example.journaldebord.indicateurs.Selectors;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

public class XMLDefi implements Serializable {

    private static final long serialVersionUID = 6878364983674394167L;

    private String name;
    private String beginDate;
    private String endDate;
    private List<Selectors> selectors;

    public XMLDefi() {
    }

    public XMLDefi(String name, String beginDate, String endDate, List<Selectors> selectors){
        this.name = name;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.selectors = selectors;
    }

    public String generateXML(){
        XmlSerializer xS = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try{
            xS.startDocument("UTF-8", true);
            xS.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xS.setOutput(writer);
            xS.startTag("","defi");
            xS.attribute("","name",name);
            xS.attribute("","dateBegin",beginDate);
            xS.attribute("","dateEnd",endDate);
            xS.startTag("","Selectors");
            for (Selectors selector: selectors) {
                xS.startTag("","selector");
                xS.attribute("", "type", selector.getType());
                xS.startTag("", "name");
                xS.text(selector.getName());
                xS.endTag("", "name");
                xS.startTag("","day");
                xS.attribute("","day",selector.getDate());
                    xS.startTag("","value");
                        xS.text(selector.getValue() != null ? selector.getValue().toString(): "null");
                    xS.endTag("","value");
                xS.endTag("","day");
                xS.startTag("","position");
                xS.text(Integer.toString(selector.getPosition()));
                xS.endTag("","position");
                xS.endTag("","selector");
            }
            xS.endTag("","Selectors");
            xS.endTag("","defi");
            xS.endDocument();
        }catch(IOException io){

        }
        return writer.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Selectors> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Selectors> selectors) {
        this.selectors = selectors;
    }

}

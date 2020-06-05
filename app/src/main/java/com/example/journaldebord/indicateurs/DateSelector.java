package com.example.journaldebord.indicateurs;

import java.time.LocalDate;
import java.util.Date;

/**
 * This class the used to store a date selector as a class.
 * @author Vincent Pingrenon
 */
public class DateSelector extends Selectors<String> {
    /**
     * This is the constructor for the yes-no selector
     * @param id the id of the selector (new or reused)
     * @param position the position (1st .... to last in line)
     * @param value the value of the selector (timestamp here)
     */
    public DateSelector(int id, int position, String value, String date){
        setId(id);
        setName("date");
        setPosition(position);
        setValue(value);
        setDate(date);
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }
}

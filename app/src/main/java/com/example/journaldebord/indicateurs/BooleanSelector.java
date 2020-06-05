package com.example.journaldebord.indicateurs;

/**
 * This class the used to store a boolean selector as a class.
 * @author Vincent Pingrenon
 */
public class BooleanSelector extends Selectors<Boolean> {

    /**
     * This is the constructor for the yes-no selector
     * @param id the id of the selector (new or reused)
     * @param position the position (1st .... to last in line)
     * @param value the value of the selector (0 or 1 here)
     */
    public BooleanSelector(int id, int position, Boolean value, String date){
        setId(id);
        setName("boolean");
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
    public Boolean getValue() {
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
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }
}

package com.example.journaldebord.indicateurs;

/**
 * This class the used to store a boolean selector as a class.
 * @author Vincent Pingrenon
 */
public class BooleanSelector extends Selectors<Boolean> {

    public BooleanSelector() {
    }

    /**
     * This is the constructor for the yes-no selector
     * @param id the id of the selector (new or reused)
     * @param position the position (1st .... to last in line)
     * @param name The name of the component
     * @param value the value of the selector (0 or 1 here)
     */
    public BooleanSelector(int id, int position, String name, Boolean value, String date) {
        setId(id);
        setName(name);
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
    public String getType() {
        return "boolean";
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

    @Override
    public int compareTo(Object o) {
        Selectors compareToThis = (Selectors) o;
        if (position == compareToThis.getPosition()) return 0;
        if (position > compareToThis.getPosition()) return 1;
        return -1;
    }
}

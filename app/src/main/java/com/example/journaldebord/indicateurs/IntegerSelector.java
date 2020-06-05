package com.example.journaldebord.indicateurs;

/**
 * This class the used to store an integer selector as a class.
 * @author Vincent Pingrenon
 */
public class IntegerSelector extends Selectors<Number> {
    /**
     * This is the constructor for the yes-no selector
     * @param id the id of the selector (new or reused)
     * @param position the position (1st .... to last in line)
     * @param value the value of the selector (0 or 1 here)
     */
    public IntegerSelector(int id, int position, Number value, String date){
        setId(id);
        setName("number");
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
    public Number getValue() {
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
    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }
}

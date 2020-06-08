package com.example.journaldebord.indicateurs;

import java.io.Serializable;

public abstract class Selectors<T> implements Serializable, Comparable {

    private static final long serialVersionUID = -5084306426110879371L;

    protected int id;
    protected String name;
    protected int position;
    protected T value;
    protected String date;

    public abstract int getId();
    public abstract String getName();
    public abstract int getPosition();
    public abstract T getValue();
    public abstract String getDate();

    public abstract String getType();

    public abstract void setId(int id);
    public abstract void setName(String name);
    public abstract void setPosition(int position);
    public abstract void setValue(T value);
    public abstract void setDate(String date);

}

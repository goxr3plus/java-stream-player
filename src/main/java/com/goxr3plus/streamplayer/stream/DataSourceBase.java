package com.goxr3plus.streamplayer.stream;

import java.io.File;

public abstract class DataSourceBase implements DataSource {
    protected Object source;

    DataSourceBase(Object source) {
        this.source = source;
    }


    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "DataSource with " + source.toString();
    }

     @Override
     public boolean isFile() {
        return source instanceof File;
    }

}

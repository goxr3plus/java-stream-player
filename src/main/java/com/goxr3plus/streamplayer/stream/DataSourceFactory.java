package com.goxr3plus.streamplayer.stream;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class DataSourceFactory {

    private DataSourceFactory() {}

    public static DataSource newDataSource(Object source) throws OperationNotSupportedException {
        if (source instanceof File) {
            return new FileDataSource((File) source);
        }
        if (source instanceof URL) {
            return new UrlDataSource((URL) source);
        }
        if (source instanceof InputStream) {
            return new StreamDataSource((InputStream) source);
        }
        throw new OperationNotSupportedException();
    }
}

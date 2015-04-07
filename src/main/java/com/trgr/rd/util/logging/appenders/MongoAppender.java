package com.trgr.rd.util.logging.appenders;

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/22/12
 * Time: 2:18 PM
 */


import com.trgr.rd.util.logging.db.MongoAppenderHelper;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MongoAppender extends AppenderSkeleton {

    private String host = "localhost";
    private int port = 27017;
    private String db = "CP";
    private String collection = "logs";

    private MongoAppenderHelper helper = null;


    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }



    public boolean requiresLayout() {
        return true;
    }




    @Override
    protected void append(LoggingEvent event) {
        String message = this.layout.format(event);
        helper.append(message);
    }

    @Override
    public void activateOptions() {
       System.out.println("activating the appender on: " + host + ":" + port);
       helper = new  MongoAppenderHelper(host, port, db, collection);
    }

    @Override
    public void close() {
        System.out.println("closing the appender");
        helper.close();
    }


}
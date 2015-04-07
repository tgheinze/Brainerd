package org.heinze.util.logging.appenders;

import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 8/21/12
 * Time: 3:06 PM
 */
public class Ping {
    public static void main(String[] args) {
        System.out.println("Ping");



        Logger log2 = LoggerFactory.getLogger("MDB");
        log2.debug("Ping Mongo Appender 55sdfsdfsdf4444");
        log2.debug("Ping Mongo Appender 5sdfsfsfsfsdfsfsdfsfd4444444446");

        Logger log22 = LoggerFactory.getLogger(Ping.class);
        log22.debug("Ping Mongo Appender 5cccccccccccccccccccccccccccccsfsdfsfd4444444446");



        LogManager.shutdown();



    }
}

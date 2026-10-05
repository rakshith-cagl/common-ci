/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.utils.sql;

import org.apache.derby.client.am.SqlException;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author arthanarisamy
 */
public class OracleSqlFormatter extends SqlFormatter {

    /**
     * Format of Oracle date: 'YYYY-MM-DD HH24:MI:SS.#'
     */
    static final String YMD24 = "'YYYY-MM-DD HH24:MI:SS.#'";
    private static final String TO_DATE = "TO_DATE('";

    /**
     * Formats Calendar object into Oracle TO_DATE String.
     *
     * @param cal Calendar to be formatted
     * @return formatted TO_DATE function
     */
    private String format(Calendar cal) {
        return TO_DATE + new java.sql.Timestamp(cal.getTime().getTime()) + "'," + YMD24 + ")";
    }

    /**
     * Formats Date object into Oracle TO_DATE String.
     *
     * @param date Date to be formatted
     * @return formatted TO_DATE function
     */
    private String format(java.sql.Date date) {
        return TO_DATE + new java.sql.Timestamp(date.getTime()) + "'," + YMD24 + ")";
    }

    /**
     * Formats Time object into Oracle TO_DATE String.
     *
     * @param time Time to be formatted
     * @return formatted TO_DATE function
     */
    private String format(java.sql.Time time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new java.util.Date(time.getTime()));
        return TO_DATE + cal.get(Calendar.HOUR_OF_DAY) + ":" +
                cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "." +
                cal.get(Calendar.MILLISECOND) + "','HH24:MI:SS.#')";
    }

    /**
     * Formats Timestamp object into Oracle TO_DATE String.
     *
     * @param timestamp Timestamp to be formatted
     * @return formatted TO_DATE function
     */
    private String format(java.sql.Timestamp timestamp) {
        return TO_DATE + timestamp.toString() + "'," + YMD24 + ")";
    }


    /**
     * Formats object to an Oracle specific formatted function.
     *
     * @param o Object to be formatted.
     * @return formatted Oracle function or "NULL" if o is null.
     * @throws SqlException
     */
    public String format(Object o) throws SQLException {
        if (o == null) {
            return "NULL";
        }
        if (o instanceof Calendar calendar) {
            return format(calendar);
        }
        if (o instanceof Date date) {
            return format(date);
        }
        if (o instanceof Time time) {
            return format(time);
        }
        if (o instanceof Timestamp timestamp) {
            return format(timestamp);
        }
        //if object not in one of our overridden methods, send to super class
        return super.format(o);

    }
}

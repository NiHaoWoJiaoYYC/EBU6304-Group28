package org.bupt.persosnalfinance.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class User {
    private double[] lastQuarterAvg; // 上期花费
    private double[] thisQuarter;    // 本期花费

    // Getters 和 Setters
    public double[] getLastQuarterAvg() {
        return lastQuarterAvg;
    }

    public void setLastQuarterAvg(double[] lastQuarterAvg) {
        this.lastQuarterAvg = lastQuarterAvg;
    }

    public double[] getThisQuarter() {
        return thisQuarter;
    }

    public void setThisQuarter(double[] thisQuarter) {
        this.thisQuarter = thisQuarter;
    }






}
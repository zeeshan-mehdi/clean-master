package eu.faircode.netguard;

/*
    This file is part of NetGuard.


*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alim.cleanmaster.*;
public class ResourceRecord {
    public long Time;
    public String QName;
    public String AName;
    public String Resource;
    public int TTL;

    private static DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    public ResourceRecord() {
    }

    @Override
    public String toString() {
        return formatter.format(new Date(Time).getTime()) +
                " Q " + QName +
                " A " + AName +
                " R " + Resource +
                " TTL " + TTL +
                " " + formatter.format(new Date(Time + TTL * 1000L).getTime());
    }
}

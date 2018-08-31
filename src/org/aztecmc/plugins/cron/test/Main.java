package org.aztecmc.plugins.cron.test;

import org.aztecmc.plugins.cron.CronDef;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            //cron(args);
            testCrons();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCrons() throws Exception {
        List<Map<?,?>> crons = new ArrayList<Map<?,?>>() {{
            add(new HashMap<String, Object>()
            {{
                    put("name", "foo");
                    put("time", "T04:00");
                    put("period", "PT1H");
                    put("commands", Arrays.asList("FOO"));
                }});
        }};

        List<CronDef> cronDefs = CronDef.getFromConfig(
                crons, new LocalTime(0, 0, 0).toDateTimeToday(DateTimeZone.forID("America/New_York")),
                (s) -> System.out.println("LOG: " + s));

        for(CronDef cron : cronDefs) System.out.println("CRON: " + cron.name + "(" + cron.getNext() + ")");

        CronDef.runCrons(cronDefs,
                new LocalTime(1, 0, 0).toDateTimeToday(DateTimeZone.forID("America/New_York")),
                (s) -> System.out.println("CMD: " + s.getValue() + "(" + s.getKey().name + ")"));

        for(CronDef cron : cronDefs) System.out.println("CRON: " + cron.name + "(" + cron.getNext() + ")");
    }
}

package org.aztecmc.plugins.cron;

import org.aztecmc.plugins.cron.util.Pair;
import org.joda.time.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by NShadeIV on 4/1/2016.
 */
public class CronDef {
    private static final Comparator<CronDef> sortByNextAsc = (a, b) -> a.getNext().compareTo(b.getNext());

    public static List<CronDef> getFromConfig(String timezone, List<Map<?,?>> crons, Consumer<String> logger) {
        return getFromConfig(crons, DateTime.now(DateTimeZone.forID(timezone)), logger);
    }
    public static List<CronDef> getFromConfig(List<Map<?,?>> crons, ReadableInstant now, Consumer<String> logger) {
        List<CronDef> cronDefs = new ArrayList<>();
        if(crons == null)
            logger.accept("crons is null!");
        else if(crons.size() <= 0)
            logger.accept("crons is empty!");
        else {
            logger.accept("Adding crons (" + now + ")");
            for (Map<?, ?> cron : crons) {
                try {
                    CronDef cronDef = new CronDef();
                    cronDef.name = String.valueOf(cron.get("name"));
                    if(!"false".equalsIgnoreCase(String.valueOf(cron.get("enabled")))) {
                        cronDef.setInterval(now,
                                LocalTime.parse(String.valueOf(cron.get("time"))),
                                Period.parse(String.valueOf(cron.get("period"))));
                        List<?> commands = (List<?>)cron.get("commands");
                        for(Object command : commands) {
                            cronDef.commands.add(String.valueOf(command));
                        }
                        cronDefs.add(cronDef);
                        logger.accept("Adding cron: " + cronDef.name + "(" + cronDef.getNext() + ")");
                    }
                    else {
                        logger.accept("Disabled cron: " + cronDef.name);
                    }
                }
                catch(Exception e) {
                    StringWriter s = new StringWriter();
                    e.printStackTrace(new PrintWriter(s));
                    logger.accept(s.toString());
                }
            }
        }
        cronDefs.sort(sortByNextAsc);
        return cronDefs;
    }

    public static void runCrons(List<CronDef> crons, String timezone, Consumer<Pair<CronDef, String>> commandDispatcher) {
        runCrons(crons, DateTime.now(DateTimeZone.forID(timezone)), commandDispatcher);
    }
    public static void runCrons(List<CronDef> crons, ReadableInstant now, Consumer<Pair<CronDef, String>> commandDispatcher) {
        crons.sort(sortByNextAsc);
        for (CronDef cronDef : crons) {
            if (cronDef.getNext().isAfter(now))
                break;
            cronDef.advanceNext(now);
            for (String command : cronDef.commands)
                commandDispatcher.accept(new Pair<>(cronDef, command));
        }
    }


    public String name;
    private LocalTime time;
    private Period period;
    public final List<String> commands = new ArrayList<>();

    private LocalTime nextTime;
    private DateTime next;

    public void setInterval(ReadableInstant now, LocalTime time, Period period) {
        this.time = time;
        this.period = period;

        this.nextTime = time;
        this.next = time.toDateTime(now);
        advanceNext(now);
    }

    public DateTime getNext() {
        return next;
    }

    public void advanceNext(ReadableInstant now) {
        LocalTime nextTime = this.nextTime;
        DateTime next = this.next;
        while(!next.isAfter(now)) {
            nextTime = nextTime.plus(this.period);
            DateTime nextNext = nextTime.toDateTime(now);
            if(!nextNext.isAfter(next)) {
                nextTime = this.time;
                nextNext = nextTime.toDateTime(now).plusDays(1);
            }
            next = nextNext;
        }
        this.nextTime = nextTime;
        this.next = next;
    }
}

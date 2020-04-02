package jdk18;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author dataochen
 * @Description
 * @date: 2020/3/31 16:56
 */
public class Main {
    public static void main(String[] args) {
        timezones();

    }

    public static void fei() {
        List<Integer> integers = Arrays.asList(1, 1, 3, 4);
        Integer reduce = integers.stream().reduce(4, (x1, x2) -> x1 ^ x2);
        System.out.println(reduce);

    }

    public static void clock() {
        Clock clock = Clock.systemDefaultZone();
        Instant instant = clock.instant();
        Date from = Date.from(instant);
        System.out.println(clock);
        System.out.println(instant);
        System.out.println(from);
    }

    public static void timezones() {
        System.out.println(ZoneId.getAvailableZoneIds());
        ZoneId of = ZoneId.of("Asia/Shanghai");
        ZoneRules rules = of.getRules();
        System.out.println(rules);
    }
}

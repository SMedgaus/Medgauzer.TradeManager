package medgausapps.trademanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Sergey on 04.07.2017.
 */

public class DateUtils {

    public static final SimpleDateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat uiShortDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat uiLongDateFormat = new SimpleDateFormat("dd MMMM yyyy, EEE", Locale.getDefault());

    public final static String START_OF_MONTH = "start_of_month";
    public final static String END_OF_MONTH = "end_of_month";

    private final ArrayList<Integer> mDaysOff;

    /**
     * Days off must be days from Calendar.\n
     * For example, Calendar.Tuesday or Calendar.Sunday
     *
     * @param daysOff
     */
    public DateUtils(Integer... daysOff) {
        mDaysOff = new ArrayList<>();
        mDaysOff.addAll(Arrays.asList(daysOff));
    }

    /**
     * Calculates next date, regarding period
     *
     * @param startDate must be a workday (not Saturday or Sunday)
     * @param period
     * @return
     */
    public String calculateNextDate(String startDate, String period) {

        String[] dateInParts = startDate.split("-");
        int year = Integer.parseInt(dateInParts[0]);
        int month = Integer.parseInt(dateInParts[1]);
        int dayOfMonth = Integer.parseInt(dateInParts[2]);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth, 0, 0, 0);

        if (period.equals(START_OF_MONTH)) {
            calendar = nextStartOfMonth(calendar);
        }

        if (period.equals(END_OF_MONTH)) {
            calendar = nextEndOfMonth(calendar);
        }

        if (period.contains("|")) {
            calendar = nextWeekday(calendar, period);
        }

        return databaseDateFormat.format(calendar.getTime());
    }

    private Calendar nextWeekday(Calendar date, String period) {
        int numberOfWeeks = Integer.parseInt(period.substring(period.lastIndexOf('|') + 1));
        String[] weekdays = period.substring(0, period.lastIndexOf('|')).split("-");
        ArrayList<Integer> numWeekdays = new ArrayList<>(weekdays.length);
        for (int i = 0; i < weekdays.length; i++) {
            numWeekdays.add(Integer.parseInt(weekdays[i]));
        }

        int currentDayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        if (currentDayOfWeek == numWeekdays.get(numWeekdays.size() - 1)) {
            date.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
            date.set(Calendar.DAY_OF_WEEK, numWeekdays.get(0));
        } else {
            date.add(Calendar.DAY_OF_MONTH, 1);
            while (!numWeekdays.contains(date.get(Calendar.DAY_OF_WEEK))) {
                date.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        return date;
    }

    /**
     * Finds date in the start of the month. This day would be workday.
     *
     * @param c previous date
     * @return
     */

    private Calendar nextStartOfMonth(Calendar c) {

        Calendar startOfCurrentMonth = (Calendar) c.clone();
        startOfCurrentMonth.set(Calendar.DAY_OF_MONTH, startOfCurrentMonth.getActualMinimum(Calendar.DAY_OF_MONTH));
        while (isDayOff(startOfCurrentMonth)) {
            startOfCurrentMonth.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (c.get(Calendar.DAY_OF_MONTH) < startOfCurrentMonth.get(Calendar.DAY_OF_MONTH)) {
            return startOfCurrentMonth;
        } else {
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DAY_OF_MONTH, 1);

            while (isDayOff(c)) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            return c;
        }
    }

    /**
     * Finds date in the end of the month. This day would be workday
     *
     * @param c
     * @return
     */
    private Calendar nextEndOfMonth(Calendar c) {

        if (c.compareTo(getLastWorkday(c)) == 0 || c.after(getLastWorkday(c))) {
            c.add(Calendar.MONTH, 1);
        }

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        while (isDayOff(c)) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }

        return c;
    }

    /**
     * Finds the closest date to the specified dates in period
     *
     * @param c
     * @param period
     * @return
     */
    private Calendar nextDayInMonth(Calendar c, String period) {
        String[] days = period.substring(0, period.indexOf("inMonth")).split("-");
        Integer[] daysInMonth = new Integer[days.length];
        for (int i = 0; i < days.length; i++) {
            daysInMonth[i] = Integer.parseInt(days[i]);
        }
        int closestDay = findClosestDay(c.get(Calendar.DAY_OF_MONTH), daysInMonth);

        if (c.get(Calendar.DAY_OF_MONTH) >= daysInMonth[daysInMonth.length - 1]) {
            c.add(Calendar.MONTH, 1);
        }

        c.set(Calendar.DAY_OF_MONTH, closestDay);
        while (isDayOff(c)) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        return c;
    }

    private int findClosestDay(int currentDay, Integer[] possibleDays) {

        if (currentDay >= possibleDays[possibleDays.length - 1]) {
            currentDay = 0;
        }

        for (int i = 0; i < possibleDays.length; i++) {
            if (currentDay < possibleDays[i])
                return possibleDays[i];
        }

        return 0;
    }

    private Calendar getLastWorkday(final Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        while (isDayOff(c)) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        return c;
    }

    private boolean isDayOff(Calendar c) {
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return mDaysOff.contains(dayOfWeek);
    }

}

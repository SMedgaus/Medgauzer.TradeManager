package medgausapps.trademanager.preferences;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sergey on 15.07.2017.
 */

public class DaysConvertor {
    /**
     * Converts an array of week days to one number
     *
     * @param arrayOfDays an array of week days, where each day is taken from java.util.Calendar
     * @return number, where each digit is a particular week day, 0000001 - Sunday,
     * 1000010 - Monday and Saturday
     */
    public static Integer convertArrayToNumber(Integer[] arrayOfDays) {
        int result = 0;
        for (int day : arrayOfDays) {
            result += Math.pow(10, day - 1);
        }
        return result;
    }

    public static Integer[] convertNumberToArray(Integer weekDays) {
        ArrayList<Integer> arrayOfDays = new ArrayList<>();
        char[] days = Integer.toString(weekDays).toCharArray();
        int weekDay = Calendar.SUNDAY;
        for (int i = days.length - 1; i >= 0; i--) {
            if (days[i] == '1')
                arrayOfDays.add(weekDay);
            weekDay++;
        }
        Integer[] result = new Integer[arrayOfDays.size()];
        return arrayOfDays.toArray(result);
    }
}

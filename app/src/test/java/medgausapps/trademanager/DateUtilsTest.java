package medgausapps.trademanager;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by Sergey on 04.07.2017.
 */
public class DateUtilsTest {

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private DateUtils dateUtils = new DateUtils(Calendar.SATURDAY, Calendar.SUNDAY);

    @Test
    public void calculateStartOfMonth() throws Exception {

        assertTrue(testDatePrediction("2017-01-01", "2017-01-02", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-02", "2017-02-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-16", "2017-02-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-22", "2017-02-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-29", "2017-02-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-31", "2017-02-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-12-29", "2018-01-01", DateUtils.START_OF_MONTH));
        assertTrue(testDatePrediction("2017-07-02", "2017-07-03", DateUtils.START_OF_MONTH));

    }

    @Test
    public void calculateEndOfMonth() throws Exception {

        assertTrue(testDatePrediction("2017-01-01", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-02", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-12", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-14", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-27", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-28", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-29", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-30", "2017-01-31", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-01-31", "2017-02-28", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-03-31", "2017-04-28", DateUtils.END_OF_MONTH));
        assertTrue(testDatePrediction("2017-12-31", "2018-01-31", DateUtils.END_OF_MONTH));
    }

    @Test
    public void calculateWeekday() throws Exception {

        assertTrue(testDatePrediction("2017-05-01", "2017-05-08", Calendar.MONDAY + "|1"));
        assertTrue(testDatePrediction("2017-05-02", "2017-05-08", Calendar.MONDAY + "|1"));
        assertTrue(testDatePrediction("2017-05-07", "2017-05-08", Calendar.MONDAY + "|1"));
        assertTrue(testDatePrediction("2017-06-02", "2017-06-05", Calendar.MONDAY + "|1"));

        assertTrue(testDatePrediction("2017-05-01", "2017-05-22", Calendar.MONDAY + "|3"));
        assertTrue(testDatePrediction("2017-05-02", "2017-05-08", Calendar.MONDAY + "|3"));
        assertTrue(testDatePrediction("2017-05-07", "2017-05-08", Calendar.MONDAY + "|3"));
        assertTrue(testDatePrediction("2017-06-02", "2017-06-05", Calendar.MONDAY + "|3"));

        assertTrue(testDatePrediction("2017-05-01", "2017-05-05",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|1"));
        assertTrue(testDatePrediction("2017-05-02", "2017-05-05",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|1"));
        assertTrue(testDatePrediction("2017-05-05", "2017-05-08",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|1"));

        assertTrue(testDatePrediction("2017-05-01", "2017-05-05",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|2"));
        assertTrue(testDatePrediction("2017-05-02", "2017-05-05",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|2"));
        assertTrue(testDatePrediction("2017-05-05", "2017-05-15",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|2"));
        assertTrue(testDatePrediction("2017-05-13", "2017-05-15",
                Calendar.MONDAY + "-" + Calendar.FRIDAY + "|2"));

        assertTrue(testDatePrediction("2017-05-01", "2017-05-02",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));
        assertTrue(testDatePrediction("2017-05-02", "2017-05-04",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));
        assertTrue(testDatePrediction("2017-05-04", "2017-05-05",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));
        assertTrue(testDatePrediction("2017-05-05", "2017-05-30",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));
        assertTrue(testDatePrediction("2017-05-30", "2017-06-01",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));
        assertTrue(testDatePrediction("2017-05-17", "2017-05-18",
                Calendar.TUESDAY + "-" + Calendar.THURSDAY + "-" + Calendar.FRIDAY + "|4"));

    }


//    @Test
//    public void calculateDayInMonth() throws Exception {
//
//        assertTrue(testDatePrediction("2017-01-01", "2017-01-16", "15inMonth"));
//        assertTrue(testDatePrediction("2017-01-14", "2017-01-16", "15inMonth"));
//        assertTrue(testDatePrediction("2017-01-15", "2017-02-15", "15inMonth"));
//        assertTrue(testDatePrediction("2017-01-16", "2017-02-15", "15inMonth"));
//        assertTrue(testDatePrediction("2017-02-28", "2017-03-15", "15inMonth"));
//
//        assertTrue(testDatePrediction("2017-01-01", "2017-01-30", "28inMonth"));
//        assertTrue(testDatePrediction("2017-02-07", "2017-02-28", "28inMonth"));
//        assertTrue(testDatePrediction("2017-05-19", "2017-05-29", "28inMonth"));
//
//        assertTrue(testDatePrediction("2017-01-01", "2017-01-05", "5-15inMonth"));
//        assertTrue(testDatePrediction("2017-01-05", "2017-01-16", "5-15inMonth"));
//        assertTrue(testDatePrediction("2017-01-06", "2017-01-16", "5-15inMonth"));
//        assertTrue(testDatePrediction("2017-02-01", "2017-02-06", "5-15inMonth"));
//        assertTrue(testDatePrediction("2017-02-05", "2017-02-15", "5-15inMonth"));
//        assertTrue(testDatePrediction("2017-02-22", "2017-03-06", "5-15inMonth"));
//
//        assertTrue(testDatePrediction("2017-01-01", "2017-01-05", "1-5-15inMonth"));
//        assertTrue(testDatePrediction("2017-01-31", "2017-02-01", "1-5-15inMonth"));
//
//    }

    private boolean testDatePrediction(String currentDate, String expectedDate, String period) throws ParseException {
        String nextDay = dateUtils.calculateNextDate(currentDate, period);
        return nextDay.equals(expectedDate);
    }
}
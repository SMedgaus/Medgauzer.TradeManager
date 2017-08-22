package medgausapps.trademanager.preferences;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Sergey on 12.07.2017.
 */
public class WorkdaysPickerPreferenceTest {

    @Test
    public void testConvertArrayToNumber() {

        assertEquals((long) 0, (long) DaysConvertor.convertArrayToNumber(new Integer[]{}));
        assertEquals((long) 1, (long) DaysConvertor.convertArrayToNumber(new Integer[]{1}));
        assertEquals((long) 10, (long) DaysConvertor.convertArrayToNumber(new Integer[]{2}));
        assertEquals((long) 11, (long) DaysConvertor.convertArrayToNumber(new Integer[]{1,2}));
        assertEquals((long) 100, (long) DaysConvertor.convertArrayToNumber(new Integer[]{3}));
        assertEquals((long) 1000000, (long) DaysConvertor.convertArrayToNumber(new Integer[]{7}));
        assertEquals((long) 1000001, (long) DaysConvertor.convertArrayToNumber(new Integer[]{1,7}));
        assertEquals((long) 1111111, (long) DaysConvertor.convertArrayToNumber(new Integer[]{1,2,3,4,5,6,7}));

    }

    @Test
    public void testConvertNumberToArray() {
        assertArrayEquals(new Integer[]{}, DaysConvertor.convertNumberToArray(0));
        assertArrayEquals(new Integer[]{1}, DaysConvertor.convertNumberToArray(1));
        assertArrayEquals(new Integer[]{2}, DaysConvertor.convertNumberToArray(10));
        assertArrayEquals(new Integer[]{1,2}, DaysConvertor.convertNumberToArray(11));
        assertArrayEquals(new Integer[]{3}, DaysConvertor.convertNumberToArray(100));
        assertArrayEquals(new Integer[]{7}, DaysConvertor.convertNumberToArray(1000000));
        assertArrayEquals(new Integer[]{1,7}, DaysConvertor.convertNumberToArray(1000001));
        assertArrayEquals(new Integer[]{1,2,3,4,5,6,7}, DaysConvertor.convertNumberToArray(1111111));
    }

}
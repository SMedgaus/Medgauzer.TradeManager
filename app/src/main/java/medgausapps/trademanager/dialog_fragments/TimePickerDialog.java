package medgausapps.trademanager.dialog_fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Sergey on 17.07.2017.
 */

public class TimePickerDialog extends DialogFragment
        implements android.app.TimePickerDialog.OnTimeSetListener {

    public static final String INITIAL_PICKER_TIME = "INITIAL_PICKER_TIME";
    private OnTimeSetCustomListener mListener;

    public interface OnTimeSetCustomListener {
        void onTimeSet(int hours, int minutes, String fragmentTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialog.OnDateSetListener) {
            mListener = (OnTimeSetCustomListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, minute;
        Bundle args = getArguments();
        if (args != null) {
            String startTime = args.getString(INITIAL_PICKER_TIME);
            String[] startTimeParts = startTime.split(":");
            hour = Integer.parseInt(startTimeParts[0]);
            minute = Integer.parseInt(startTimeParts[1]);

        } else {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new android.app.TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimeSet(hourOfDay, minute, getTag());
    }
}

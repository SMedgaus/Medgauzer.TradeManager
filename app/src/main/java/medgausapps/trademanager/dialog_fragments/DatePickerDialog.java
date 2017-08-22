package medgausapps.trademanager.dialog_fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Sergey on 17.07.2017.
 */

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public static final String INITIAL_PICKER_DATE = "INITIAL_PICKER_DATE";
    private android.app.DatePickerDialog.OnDateSetListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof android.app.DatePickerDialog.OnDateSetListener) {
            mListener = (android.app.DatePickerDialog.OnDateSetListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        Bundle args = getArguments();
        if (args != null) {
            String startDate = args.getString(INITIAL_PICKER_DATE);
            String[] startDateParts = startDate.split("/");
            day = Integer.parseInt(startDateParts[0]);
            month = Integer.parseInt(startDateParts[1]);
            year = Integer.parseInt(startDateParts[2]);

        } else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        return new android.app.DatePickerDialog(getActivity(), this, year, month - 1, day);
    }

    public void setArguments(Bundle args, android.app.DatePickerDialog.OnDateSetListener callback) {
        setArguments(args);
        mListener = callback;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDateSet(view, year, month, day);
    }
}

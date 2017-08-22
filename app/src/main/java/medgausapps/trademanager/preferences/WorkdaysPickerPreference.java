package medgausapps.trademanager.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import medgausapps.trademanager.R;

import static medgausapps.trademanager.preferences.DaysConvertor.convertNumberToArray;

/**
 * Created by Sergey on 12.07.2017.
 */

public class WorkdaysPickerPreference extends Preference {

    private final static int DEFAULT_VALUE = 11111;
    private int mValue;
    private final String preferenceTitleStr;

    public WorkdaysPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_weekday_picker);

        final String xmlns="http://schemas.android.com/apk/res/android";
        preferenceTitleStr = attrs.getAttributeValue(xmlns, "title");
    }



    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView preferenceTitle = view.findViewById(R.id.preference_title);
        preferenceTitle.setText(preferenceTitleStr);

        Integer[] days = convertNumberToArray(mValue);

        Button btn = view.findViewById(R.id.monday_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Test!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            mValue = (Integer) defaultValue;
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.value = mValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        mValue = myState.value;
    }

    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }



}

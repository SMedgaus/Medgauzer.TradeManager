package medgausapps.trademanager;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import medgausapps.trademanager.database.DatabaseContract;

/**
 * Created by Sergey on 01.07.2017.
 */

public class ActualClientsListAdapter extends android.support.v4.widget.CursorAdapter {

    private ClientCallback mCallback;

    public ActualClientsListAdapter(Context context, Cursor c, int flags, ClientCallback callback) {
        super(context, c, flags);
        mCallback = callback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actual_clients_list_item, parent, false);

        ClientViewHolder viewHolder = new ClientViewHolder(root);
        root.setTag(viewHolder);

        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ClientViewHolder viewHolder = (ClientViewHolder) view.getTag();
        if (viewHolder != null) {
            viewHolder._id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Clients._ID));

            String clientName = cursor.getString(
                    cursor.getColumnIndex(DatabaseContract.Clients.JOINED_NAME));
            viewHolder.mClientName.setText(clientName);

            String workingTime = cursor.getString(
                    cursor.getColumnIndex(DatabaseContract.Clients.START_WORKING_TIME));
            workingTime += " - " + cursor.getString(
                    cursor.getColumnIndex(DatabaseContract.Clients.END_WORKING_TIME));
            viewHolder.mWorkingInterval.setText(workingTime);

            viewHolder.mRemindingDate = cursor.getString(
                    cursor.getColumnIndex(DatabaseContract.Clients.REMINDING_DATE));
            viewHolder.mPeriodicity = cursor.getString(
                    cursor.getColumnIndex(DatabaseContract.Clients.PERIODICITY));

            view.setId(viewHolder._id);
            int afterCallState = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Clients.AFTER_CALL_STATE));
            final int START_VIEW_ROTATION = 0, OK_VIEW_ROTATION = 135;
            if (afterCallState == DatabaseContract.Clients.CallState.OK) {
                viewHolder.mCallButton.setRotation(OK_VIEW_ROTATION);
            } else {
                viewHolder.mCallButton.setRotation(START_VIEW_ROTATION);
            }
            switch (afterCallState) {
                case DatabaseContract.Clients.CallState.UNDEFINED:
                case DatabaseContract.Clients.CallState.OK:
                    viewHolder.mCallButton.setBackgroundResource(R.drawable.call_button_shape_green);
                    break;
                case DatabaseContract.Clients.CallState.CALL_LATER:
                    viewHolder.mCallButton.setBackgroundResource(R.drawable.call_button_shape_yellow);
                    break;
                case DatabaseContract.Clients.CallState.NO_RESPONSE:
                    viewHolder.mCallButton.setBackgroundResource(R.drawable.call_button_shape_red);
                    break;
            }
        }
    }

    public interface ClientCallback {
        void onCallButtonClicked(long clientId);

        void onConfirmButtonClicked(long clientId, String remindingDate, String period, View rootView);

        void onPostponeButtonClicked(long clientId);
    }

    private class ClientViewHolder {
        int _id;
        String mRemindingDate, mPeriodicity;

        TextView mClientName, mWorkingInterval;
        ImageButton mCallButton;
        Button mPostponeButton, mConfirmButton;

        ClientViewHolder(final View rootView) {
            mClientName = rootView.findViewById(R.id.item_text_view);
            mWorkingInterval = rootView.findViewById(R.id.working_time_interval);

            mCallButton = rootView.findViewById(R.id.call_button);
            mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onCallButtonClicked(_id);
                }
            });

            mPostponeButton = rootView.findViewById(R.id.postpone_button);
            mPostponeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onPostponeButtonClicked(_id);
                }
            });

            mConfirmButton = rootView.findViewById(R.id.confirm_button);
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onConfirmButtonClicked(_id, mRemindingDate, mPeriodicity, rootView);
                }
            });
        }
    }

}

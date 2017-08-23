package medgausapps.trademanager.dialog_fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import medgausapps.trademanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallClientsListDialog extends DialogFragment {

    public static final String CONTACT_PHONES = "CONTACT_PHONES";
    public static final String CONTACT_NAMES = "CONTACT_NAMES";

    public CallClientsListDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            final ArrayList<String> contactPhones = args.getStringArrayList(CONTACT_PHONES);
            ArrayList<String> contactNames = args.getStringArrayList(CONTACT_NAMES);

            ArrayList<HashMap<String, String>> contactsList = new ArrayList<>();
            HashMap<String, String> values;

            final String TITLE = "Title", SUBTITLE = "subTitle";
            if (contactPhones != null && contactNames != null) {
                for (int i = 0; i < contactPhones.size(); i++) {
                    values = new HashMap<>();
                    if (!contactNames.get(i).isEmpty()) {
                        values.put(TITLE, contactNames.get(i));
                        values.put(SUBTITLE, contactPhones.get(i));
                    } else {
                        values.put(TITLE, contactPhones.get(i));
                    }
                    contactsList.add(values);
                }

                SimpleAdapter simpleContactsAdapter = new SimpleAdapter(getContext(),
                        contactsList, R.layout.client_contacts_list_item,
                        new String[]{TITLE, SUBTITLE}, new int[]{android.R.id.text1, android.R.id.text2});

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setAdapter(simpleContactsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int itemNum) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                                + contactPhones.get(itemNum)));
                        startActivity(intent);
                    }
                })
                        .setTitle(R.string.choose_contact);
                return builder.create();
            }
        }
        return new AlertDialog.Builder(getActivity()).create();
    }

}

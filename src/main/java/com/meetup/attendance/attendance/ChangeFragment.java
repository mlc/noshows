package com.meetup.attendance.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.meetup.attendance.http.RestService;
import com.meetup.attendance.http.Verb;
import com.meetup.attendance.rest.AttendanceRecord;
import com.meetup.attendance.rest.AttendanceRecord.Status;

import static com.google.common.base.Preconditions.checkArgument;

public class ChangeFragment extends DialogFragment implements DialogInterface.OnClickListener {
    static final Status[] STATI = Status.values();
    private Contract contract;

    public static interface Contract {
        void notifyChange(long memberId, Status newStatus);
    }

    private static class Adapter extends ArrayAdapter<Status> {
        public Adapter(Context context) {
            super(context, android.R.layout.simple_list_item_single_choice, STATI);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).ordinal();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    public static ChangeFragment create(AttendanceRecord record, String urlname, String eventId) {
        ChangeFragment cf = new ChangeFragment();
        Bundle args = new Bundle();
        args.putParcelable("record", record);
        args.putString("urlname", urlname);
        args.putString("event_id", eventId);
        cf.setArguments(args);
        return cf;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        checkArgument(activity instanceof Contract);
        contract = (Contract)activity;
    }

    @Override
    public void onDetach() {
        contract = null;
        super.onDetach();
    }

    public AttendanceRecord getRecord() {
        return getArguments().getParcelable("record");
    }

    public Uri getUri() {
        return AttendanceLoadFragment.getUri(getArguments().getString("urlname"),
                getArguments().getString("event_id"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setSingleChoiceItems(new Adapter(context), getRecord().status.ordinal(), this);
        return bld.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Bundle params = new Bundle();
        final long memberId = getRecord().member.id;
        final Status status = STATI[which];
        params.putLong("member", memberId);
        params.putParcelable("status", status);
        RestService.call(getActivity(), Verb.POST, getUri(), null, params);
        contract.notifyChange(memberId, status);

        dismiss();
    }
}
package com.schintha.nytimessearch.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.schintha.nytimessearch.R;
import com.schintha.nytimessearch.activities.SearchActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sc043016 on 7/28/16.
 */
public class FilterDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String SPORTS_DESK = "Sports";
    public static final String ARTS_DESK = "Arts";
    public static final String FASHION_DESK = "Fashion & Style";

    @Bind(R.id.etDate)
    EditText etDate;
    @Bind(R.id.tbSortOrder)
    ToggleButton tbSotOrder;
    @Bind(R.id.cbArts)
    CheckBox cbArts;
    @Bind(R.id.cbFashions) CheckBox cbFashions;
    @Bind(R.id.cbSports) CheckBox cbSports;
    @Bind(R.id.btnSave)
    Button btnSave;
    @Bind(R.id.btnClear) Button btnClear;
    @Bind(R.id.ibCalendar)
    ImageButton ibCalendar;

    SearchActivity parentActivity;
    public FilterDialog() {

    }

    public static FilterDialog newInstance(String title) {
        FilterDialog frag = new FilterDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentActivity = (SearchActivity) getActivity();
        return inflater.inflate(R.layout.fragment_filter_settings, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tbSotOrder.isChecked()) {
                    parentActivity.sortOrder = getString(R.string.oldest);
                } else {
                    parentActivity.sortOrder = getString(R.string.newest);
                }
                parentActivity.beginDate = etDate.getText().toString().replace("/", "");
                parentActivity.newsDesk.clear();
                parentActivity.newsDesk.add(cbSports.isChecked() ? ("\"" + SPORTS_DESK + "\"") : "");
                parentActivity.newsDesk.add(cbArts.isChecked() ? ("\"" + ARTS_DESK + "\"") : "");
                parentActivity.newsDesk.add(cbFashions.isChecked() ? ("\"" + FASHION_DESK + "\"") : "");
                dismiss();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDate.setText("");
                tbSotOrder.setChecked(false);
                cbArts.setChecked(false);
                cbFashions.setChecked(false);
                cbSports.setChecked(false);
            }
        });

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(parentActivity, FilterDialog.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar c  = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        etDate.setText(sdf.format(c.getTime()));
    }
}

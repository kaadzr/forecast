package com.globus_ltd.forecast_sber_test.presentation.ui.forecast;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.widget.FilterQueryProvider;

import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.globus_ltd.forecast_sber_test.R;

import java.util.ArrayList;
import java.util.List;

class SuggestionAdapter extends SimpleCursorAdapter {
    private static final String COLUMN_LOCATION_NAME = "location_name";
    private static final String[] columns = {COLUMN_LOCATION_NAME};
    private static final String[] cursorColumns = {BaseColumns._ID, COLUMN_LOCATION_NAME};
    private static final int[] viewIds = {R.id.text1};
    private List<String> data;

    SuggestionAdapter(Context context) {
        super(context, R.layout.list_item, null,
                columns, viewIds, -1);
        setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (data == null || data.isEmpty() || constraint == null) {
                    return null;
                }
                List<String> filteredData = new ArrayList<>();
                for (String string : data) {
                    if (string.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        filteredData.add(string);
                    }
                }
                return createCursor(filteredData);
            }
        });
    }

    public void setData(List<String> data) {
        this.data = data;
        Cursor cursor = createCursor(data);
        swapCursor(cursor);
    }

    public List<String> getData() {
        setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return null;
            }
        });
        return data;
    }

    private Cursor createCursor(List<String> data) {
        MatrixCursor cursor = new MatrixCursor(cursorColumns);
        int counter = 0;
        for (String name : data) {
            cursor.addRow(new Object[]{counter, name});
            counter++;
        }
        return cursor;
    }

    @Nullable
    static String getLocationFromCursor(Cursor cursor, int position) {
        if (cursor == null) {
            return null;
        }
        int index = cursor.getColumnIndex(COLUMN_LOCATION_NAME);
        cursor.moveToPosition(position);
        return cursor.getString(index);
    }
}

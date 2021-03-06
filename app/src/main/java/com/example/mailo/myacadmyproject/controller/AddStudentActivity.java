package com.example.mailo.myacadmyproject.controller;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.R.layout.*;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mailo.myacadmyproject.R;
import com.example.mailo.myacadmyproject.model.backend.AcademyContract;
import com.example.mailo.myacadmyproject.model.entities.Year;

public class AddStudentActivity extends Activity implements View.OnClickListener {

    private EditText IdEditText;
    private EditText NameEditText;
    private EditText PhoneEditText;

    private Spinner yearSpinner;
    private Button addStudentButton;
    private ListView itemListView;
    private TextView loadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        findViews();
    }

    @Override
    public void onClick(View v) {
        if (v == addStudentButton) {
            addStudent();
        }
    }


    private void findViews() {
        IdEditText = (EditText) findViewById(R.id.IdEditText);
        NameEditText = (EditText) findViewById(R.id.NameEditText);
        PhoneEditText = (EditText) findViewById(R.id.PhoneEditText);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        addStudentButton = (Button) findViewById(R.id.addStudentButton);

        addStudentButton.setOnClickListener(this);

        yearSpinner.setAdapter(new ArrayAdapter<Year>(this, android.R.layout.simple_list_item_1, Year.values()));

        itemListView = (ListView) findViewById(R.id.ItemListView);
        loadingTextView = (TextView) findViewById(R.id.loadingTextView);

        final Uri uri = AcademyContract.Student.STUDENT_URI;
        updateItemList(this, uri);
    }

    private void updateItemList(final Context context, final Uri uri) {

        new AsyncTask<Void, Void, Cursor>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                itemListView.setVisibility(View.INVISIBLE);
                loadingTextView.setVisibility(View.VISIBLE);
            }

            @Override
            protected Cursor doInBackground(Void... params) {
                return getContentResolver().query(uri, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                CursorAdapter adapter = new CursorAdapter(context, cursor) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        TextView tv = new TextView(context);
                        return tv;
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        TextView tv = (TextView) view;
                        tv.setText("[" + cursor.getString(0) + "]  " + cursor.getString(1));
                    }
                };

                itemListView.setAdapter(adapter);
                itemListView.setVisibility(View.VISIBLE);
                loadingTextView.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void addStudent() {

        final Uri uri = AcademyContract.Student.STUDENT_URI;// Uri.parse("content://com.oshri.academy/students/");
        final ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(AcademyContract.Student.ID, Integer.valueOf(this.IdEditText.getText().toString()));
        }catch(Exception e) {}

        contentValues.put(AcademyContract.Student.NAME, this.NameEditText.getText().toString());
        contentValues.put(AcademyContract.Student.PHONE, this.PhoneEditText.getText().toString());

        String year = ((Year) yearSpinner.getSelectedItem()).name();
        contentValues.put(AcademyContract.Student.YEAR, year);

new AsyncTask<Void, Void, Uri>() {
    @Override
    protected void onPostExecute(Uri uriResult) {
        super.onPostExecute(uriResult);

        long id = ContentUris.parseId(uriResult);
        if(id>0)
            Toast.makeText(getBaseContext(),"insert id: " + id,Toast.LENGTH_LONG).show();

        updateItemList(AddStudentActivity.this, uri);
    }

    @Override
    protected Uri doInBackground(Void... params) {
       return getContentResolver().insert(uri, contentValues);
    }
}.execute();
    }
}

package com.example.me495.lbstest;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText editText1;
    AutoCompleteTextView autoCompleteTextView;
    Button button;
    SuggestionSearch suggestionSearch;
    ArrayAdapter<String> sugAdapter;

    OnGetSuggestionResultListener suggestionResultListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            Log.d("getSuggestionResult","ok");
            if (res == null || res.getAllSuggestions() == null) {
                Log.d("Result","null");
                return;
            }
            sugAdapter.clear();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    sugAdapter.add(info.key);
                    Log.d("info",info.key);
                }
            }
            //sugAdapter.notifyDataSetChanged();
            autoCompleteTextView.showDropDown();
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("afterTextChange","ok");
            suggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(autoCompleteTextView.getText().toString())
                    .city(editText1.getText().toString()));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editText1 = (EditText) findViewById(R.id.city);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.address);
        button = (Button) findViewById(R.id.submit_address);
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(sugAdapter);
        autoCompleteTextView.addTextChangedListener(textWatcher);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Log.d("address_text", String.valueOf(autoCompleteTextView.getText()));
                intent.putExtra("city", String.valueOf(editText1.getText()));
                if(String.valueOf(autoCompleteTextView.getText()).equals("")) {
                    intent.putExtra("address", String.valueOf(editText1.getText()));
                }
                else {
                    intent.putExtra("address", String.valueOf(autoCompleteTextView.getText()));
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

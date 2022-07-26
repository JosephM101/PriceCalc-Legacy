package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TextPromptDialog extends AppCompatActivity {
    EditText editText;
    Button button_ok;
    Button button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_prompt_dialog);

        Intent i = getIntent();
        setTitle(i.getStringExtra("title")); // Set title

        editText = findViewById(R.id.textprompt_entry);
        editText.setHint(i.getStringExtra("hint")); // Set EditText hint

        button_ok = findViewById(R.id.button_textprompt_ok);
        button_ok.setOnClickListener(view -> {
            Intent data = new Intent();
            Log.d("PROMPT", editText.getText().toString());
            data.putExtra("text", editText.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        });

        if (i.hasExtra("text")) {
            editText.setText(i.getStringExtra("text"));
        }

        button_cancel = findViewById(R.id.button_textprompt_cancel);
        button_cancel.setOnClickListener(view -> {
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            finish();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                UpdateButtons();
            }
        });

        UpdateButtons();
    }

    void UpdateButtons() {
        if(editText.getText().length() < 1) {
            button_ok.setEnabled(false);
        } else {
            button_ok.setEnabled(true);
        }
    }
}
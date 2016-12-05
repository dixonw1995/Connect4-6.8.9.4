package com.example.daft.connect4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static com.example.daft.connect4.R.id.radio_sound0;
import static com.example.daft.connect4.R.id.radio_sound1;

/**
 * Created by shukfunwong on 4/12/2016.
 */

public class SettingActivity extends AppCompatActivity {
    private RadioGroup soundGroup;
    private RadioButton soundButton;
    private Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        addListenerOnButton();
        soundGroup = (RadioGroup) findViewById(R.id.radio_sound);
        if (GameActivity.isSoundOff) {
            soundGroup.check(R.id.radio_sound1);
        }
        else {
            soundGroup.check(R.id.radio_sound0);
        }
    }

    public void addListenerOnButton() {

        soundGroup = (RadioGroup) findViewById(R.id.radio_sound);
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = soundGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                soundButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(SettingActivity.this,
                        soundButton.getText(), Toast.LENGTH_SHORT).show();

                if(((RadioButton)findViewById(radio_sound1)).isChecked()){
                    GameActivity.isSoundOff = true;
                }else if(((RadioButton)findViewById(radio_sound0)).isChecked()) {
                    GameActivity.isSoundOff = false;
                }

                finish();
            }

        });
    }

}

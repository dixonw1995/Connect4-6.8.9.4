package com.example.daft.connect4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by shukfunwong on 4/12/2016.
 */

public class HelpActivity extends AppCompatActivity {
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        backButton = (Button)findViewById(R.id.backButton);

        backButton.setOnClickListener(new Button.OnClickListener(){
            @Override
                public void onClick(View arg0) {
                    finish();
                }
        });

    }
}

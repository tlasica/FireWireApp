package pl.tlasica.firewireapp.play;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pl.tlasica.firewireapp.R;

public class LevelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
    }

    public void onClose(View view) {
        finish();
    }
}

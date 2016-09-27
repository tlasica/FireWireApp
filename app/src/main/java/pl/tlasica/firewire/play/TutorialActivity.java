package pl.tlasica.firewire.play;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pl.tlasica.firewire.R;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
    }

    public void onClose(View view) {
        finish();
    }
}

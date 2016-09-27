package pl.tlasica.firewire;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;

public class BasicActivity extends AppCompatActivity {

    protected void setButtonFontBold(int id) {
        setButtonFont(id, Font.ROBOTO_BOLD);
    }

    protected void setButtonFontBoldItalic(int id) {
        setButtonFont(id, Font.ROBOTO_BOLD_ITALIC);
    }

    protected void setButtonFont(int id) {
        setButtonFont(id, Font.ROBOTO_REGULAR);
    }

    protected void setTextFontBold(int id) {
        setTextFont(id, Font.ROBOTO_BOLD);
    }

    protected void setTextFontBoldItalic(int id) {
        setTextFont(id, Font.ROBOTO_BOLD_ITALIC);
    }

    protected void setTextFont(int id) {
        setTextFont(id, Font.ROBOTO_REGULAR);
    }

    private void setButtonFont(int id, String fontName) {
        Button bttn = (Button)findViewById(id);
        bttn.setTypeface(Typer.set(getApplicationContext()).getFont(fontName));
    }

    private void setTextFont(int id, String fontName ) {
        TextView text = (TextView)findViewById(id);
        text.setTypeface(Typer.set(getApplicationContext()).getFont(fontName));
    }

}

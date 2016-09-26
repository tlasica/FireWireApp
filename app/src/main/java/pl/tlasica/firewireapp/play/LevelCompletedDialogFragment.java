package pl.tlasica.firewireapp.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import pl.tlasica.firewireapp.PlayActivity;
import pl.tlasica.firewireapp.R;
import pl.tlasica.firewireapp.model.LevelPlay;

/**
 * Dialog to show points after completed level
 * TODO: make it more visually attractive
 */
public class LevelCompletedDialogFragment extends DialogFragment {

    public  GameLoopStatistics gameStats;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Good job!");
        builder.setMessage(this.buildMsg());
        builder.setIcon(R.drawable.shockcircle);

        builder.setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });

        builder.setNegativeButton("PLAY AGAIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // play next level
                PlayActivity ply = (PlayActivity)getActivity();
                ply.repeatLevel();
            }
        });
        builder.setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // play next level
                PlayActivity ply = (PlayActivity)getActivity();
                ply.nextLevel();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String buildMsg() {
        int points = PointCalculator.points(gameStats, LevelPlay.current().board);
        String msg = String.format("Level completed in %02.1fs\n" +
                "LEVEL POINTS: %d" +
                "\n",
                gameStats.durationSec(),
                points);
        return msg;
    }
}

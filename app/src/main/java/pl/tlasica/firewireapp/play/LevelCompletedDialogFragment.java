package pl.tlasica.firewireapp.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;

import pl.tlasica.firewireapp.PlayActivity;
import pl.tlasica.firewireapp.model.LevelPlay;

/**
 * Dialog to show points after completed level
 * TODO: make it more visually attractive
 */
public class LevelCompletedDialogFragment extends DialogFragment {

    public  GameLoopStatistics gameStats;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Good job!");
        builder.setMessage(this.buildMsg());
        builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // play next level
                PlayActivity ply = (PlayActivity)getActivity();
                ply.nextLevel();
            }
        });
        builder.setNegativeButton("MENU", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String buildMsg() {
        int points = PointCalculator.points(gameStats, LevelPlay.current().board);
        String msg = String.format("Level completed in %02.1fs\n\n" +
                "Connectors used: %d\n" +
                "Extra moves: %d\n" +
                "\n" +
                "TOTAL POINTS: %d",
                gameStats.durationSec(),
                gameStats.numPlace - gameStats.numRemove,
                gameStats.numRotate + gameStats.numMove,
                points);
        return msg;
    }
}

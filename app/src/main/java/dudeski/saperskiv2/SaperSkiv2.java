package dudeski.saperskiv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class SaperSkiv2 extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saper_skiv2);

        View newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);

        View continueButton = findViewById(R.id.continue_game_button);
        continueButton.setOnClickListener(this);

        View highscoresButton = findViewById(R.id.high_scores_button);
        highscoresButton.setOnClickListener(this);

        View rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(this);

        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.new_game_button:
                openNewGameDialog();
                break;
            case R.id.continue_game_button:
                break;
            case R.id.high_scores_button:
                break;
            case R.id.rules_button:
                Intent i = new Intent(this, Rules.class);
                startActivity(i);
                break;
            case R.id.exit_button:
                finish(); //zakończenie aplikacji
                break;
        }
    }

    private void openNewGameDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.difficulty_title)
                .setItems(R.array.difficulty,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoginterface, int i)
                            {
                                startNewGame(i);
                            }
                        }).show();
    }

    private void startNewGame(int i)
    {
        Intent intent = new Intent(this, Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY,i);
        startActivity(intent);
    }
}

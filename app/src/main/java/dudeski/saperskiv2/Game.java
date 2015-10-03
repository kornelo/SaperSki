package dudeski.saperskiv2;

/**
 * Created by Dude on 2015-05-06.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import java.util.Random;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;


public class Game extends Activity
{
    public static final String KEY_DIFFICULTY = "dudeski.saperski.difficulty";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    private int totalRows;
    private int totalCols;
    private int totalMines;

    private int easyRows = 9;
    private int easyColumns = 9;
    private int easyMines = 10;

    private int mediumRows = 16;
    private int mediumColumns = 16;
    private int mediumMines = 40;

    private int hardRows = 30;
    private int hardColumns = 16;
    private int hardMines = 99;

    private int tileWH = 13;
    private int tilePadding = 5;

    private Tile tiles[][];
    private TableLayout mineField;

    private boolean timerStarted = false;
    private boolean minesSet = false;
    private ImageButton btnSmiley;

    private Handler timer = new Handler();
    private int secondsPassed = 0;
    public TextView timerText;
    public TextView mineCount;

    public int correctFlags;
    public int totalCoveredTiles;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        timerText = (TextView) this.findViewById(R.id.Timer);
        mineCount = (TextView) this.findViewById(R.id.MineCount);
        mineField = (TableLayout)findViewById(R.id.MineField);
        int diff = getIntent().getIntExtra(KEY_DIFFICULTY,DIFFICULTY_EASY);
        createGameBoard(diff);
        showGameBoard();

    }



    public void showGameBoard()
    {
        //for every row
        for(int row=0;row<totalRows;row++)
        {
            //create a new table row
            TableRow tableRow = new TableRow(this);
            //set the height and width of the row
            tableRow.setLayoutParams(new LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));

            //for every column
            for(int col=0;col<totalCols;col++)
            {
                //set the width and height of the tile
                tiles[row][col].setLayoutParams(new LayoutParams(tileWH * tilePadding,  tileWH * tilePadding));
                //add some padding to the tile
                tiles[row][col].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
                //add the tile to the table row
                tableRow.addView(tiles[row][col]);
            }
            //add the row to the minefield layout
            mineField.addView(tableRow, new TableLayout.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));
        }

    }

    public void createGameBoard(int diff)
    {

        //set total rows and columns based on the difficulty
        totalRows = easyRows;
        totalCols = easyColumns;
        totalMines = easyMines;
        switch(diff)
        {
            case 0:
                break;
            case 1:
                totalRows = mediumRows;
                totalCols = mediumColumns;
                totalMines = mediumMines;
                tileWH = 10;
                tilePadding=4;
                break;
            case 2:
                totalRows = hardRows;
                totalCols = hardColumns;
                totalMines = hardMines;
                tileWH = 11;
                tilePadding=3;
                break;
        }

        totalCoveredTiles = totalRows*totalCols;

        //setup the tiles array
        tiles = new Tile[totalRows][totalCols];

        for(int row = 0; row < totalRows;row++)
        {
            for(int col = 0; col < totalCols;col++)
            {
                //create a tile
                tiles[row][col] = new Tile(this);
                //set the tile defaults
                tiles[row][col].setDefaults();

                final int curRow = row;
                final int curCol = col;

                //add a click listener
                tiles[row][col].setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {


                         if(!timerStarted)
                        {
                            timerStarted = true;
                            startTimer();
                        }
                        if(!minesSet)
                        {
                            minesSet = true;
                            setupMineField(curRow,curCol);
                        }

                        if(!tiles[curRow][curCol].isFlag())
                        {
                            if(tiles[curRow][curCol].isMine())
                            {
                                loseGame();
                            }
                            else
                            {

                                uncoverTiles(curRow,curCol);

                            }
                            if(checkWonGame())
                            {
                            winGame();
                            }

                        }
                    }
                });

                //add a long click listener
                tiles[row][col].setOnLongClickListener(new OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        if(tiles[curRow][curCol].isCovered())
                        {
                            if(tiles[curRow][curCol].isEmpty())
                            {
                                tiles[curRow][curCol].setFlag();
                                totalMines--;
                                setMinesText(totalMines);
                                if(tiles[curRow][curCol].isMine())
                                    correctFlags++;
                            }
                            else
                            {
                                tiles[curRow][curCol].setEmpty();
                                totalMines++;
                                setMinesText(totalMines);
                            }
                        }
                            return true;
                    }
                });
            }
        }
        setMinesText(totalMines);

        btnSmiley = new ImageButton(this);
        btnSmiley.setBackgroundResource(R.drawable.smile);
        addRestartListener(diff);

    }

    public void setupMineField(int row, int col) {
        Random random = new Random();
        int mineRow;
        int mineCol;
        for (int i = 0; i < totalMines; i++) {
            mineRow = random.nextInt(totalRows);
            mineCol = random.nextInt(totalCols);

            if (mineRow == row && mineCol == col) //clicked tile
            {
                i--;
            } else if (tiles[mineRow][mineCol].isMine()) //already a mine
            {
                i--;
            } else {
                //plant a new mine
                tiles[mineRow][mineCol].plantMine();
                //go one row and col back
                int startRow = mineRow - 1;
                int startCol = mineCol - 1;
                //check 3 rows across and 3 down
                int checkRows = 3;
                int checkCols = 3;
                if (startRow < 0) //if it is on the first row
                {
                    startRow = 0;
                    checkRows = 2;
                }
                else if (startRow + 3 > totalRows) //if it is on the last row
                    checkRows = 2;

                if (startCol < 0)
                {
                    startCol = 0;
                    checkCols = 2;
                }
                else if (startCol + 3 > totalCols) //if it is on the last row
                    checkCols = 2;

                for (int j = startRow; j < startRow + checkRows; j++) //3 rows across
                {
                    for (int k = startCol; k < startCol + checkCols; k++) //3 rows down
                    {
                        if (!tiles[j][k].isMine()) //if it isn't a mine
                            tiles[j][k].updateSurroundingMineCount();
                    }
                }
            }
        }
    }

    public void winGame()
    {
        btnSmiley.setBackgroundResource(R.drawable.win);
        Toast.makeText(Game.this,"You WIN!", Toast.LENGTH_SHORT).show();
        endGame();
    }
    public boolean checkWonGame()
    {
        if(totalCoveredTiles == totalMines || correctFlags == totalMines)
            return true;
        else
        return false;
    }
    public void loseGame()
    {
        btnSmiley.setBackgroundResource(R.drawable.lost);
        endGame();
    }

    public void endGame()
    {
        stopTimer();
        for(int i=0;i<totalRows;i++)
        {
            for(int j=0;j<totalCols;j++)
            {
                //if the tile is covered
                if(tiles[i][j].isCovered())
                {
                    //if there is no flag or mine
                    if(!tiles[i][j].isFlag() && !tiles[i][j].isMine())
                    {
                        tiles[i][j].openTile();
                    }
                    //if there is a mine but no flag
                    else if(tiles[i][j].isMine() && !tiles[i][j].isFlag())
                    {
                        tiles[i][j].openTile();
                    }

                }
            }
        }
    }


    private void addRestartListener(final int diff)
    {
        btnSmiley = (ImageButton) findViewById(R.id.Smiley);
        btnSmiley.setBackgroundResource(R.drawable.smile);
        btnSmiley.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                createGameBoard(diff);
                showGameBoard();
                Toast.makeText(Game.this,"Restart!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void resetGame()
    {

        btnSmiley.setBackgroundResource(R.drawable.smile);

        // remove the table rows from the minefield table layout
        mineField.removeAllViews();

        // reset variables
        timerStarted = false;
        minesSet = false;
        stopTimer();
        secondsPassed = 0;
        timerText.setText("000");

    }

    public void startTimer()
    {
        if(secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimer);
            timer.postDelayed(updateTimer, 1000);
        }
    }

    public void stopTimer()
    {
        timer.removeCallbacks(updateTimer);
    }

    private Runnable updateTimer = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            String curTime = Integer.toString(secondsPassed);
            //update the text view
            if (secondsPassed < 10)
            {
                timerText.setText("00" + curTime);
            }
            else if (secondsPassed < 100)
            {
                timerText.setText("0" + curTime);
            }
            else
            {
                timerText.setText(curTime);
            }
            timer.postAtTime(this, currentMilliseconds);
            //run again in 1 second
            timer.postDelayed(updateTimer, 1000);
        }
    };

    public void uncoverTiles(int row, int col)
    {
        //if the tile is a mine, or a flag return
        if(tiles[row][col].isMine() || tiles[row][col].isFlag())
            return;

        tiles[row][col].openTile();
        if(totalCoveredTiles>0)
            totalCoveredTiles--;

        if(tiles[row][col].getNoSurroundingMines() > 0)
            return;

        //go one row and col back
        int startRow = row-1;
        int startCol = col-1;
        //check 3 rows across and 3 down
        int checkRows = 3;
        int checkCols = 3;
        if(startRow < 0) //if it is on the first row
        {
            startRow = 0;
            checkRows = 2;
        }
        else if(startRow+3 > totalRows) //if it is on the last row
            checkRows = 2;

        if(startCol < 0)
        {
            startCol = 0;
            checkCols = 2;
        }
        else if(startCol+3 > totalCols) //if it is on the last column
            checkCols = 2;

        for(int i=startRow;i<startRow+checkRows;i++) //3 or 2 rows across
        {
            for(int j=startCol;j<startCol+checkCols;j++) //3 or 2 rows down
            {
                if(tiles[i][j].isCovered())
                    uncoverTiles(i,j);
            }
        }
    }
    public void setMinesText(int totalMines)
    {
        String minesText = Integer.toString(totalMines);
        if(totalMines >= 0)
        if(totalMines >= 10)
        mineCount.setText("0"+minesText);
        else mineCount.setText("00"+minesText);

    }
}

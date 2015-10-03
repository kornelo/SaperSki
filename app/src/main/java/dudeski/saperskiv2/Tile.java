package dudeski.saperskiv2;

/**
 * Created by Dude on 2015-05-06.
 */

import android.content.Context;
import  android.util.AttributeSet;
import android.widget.Button;

public class Tile extends Button
{
    private boolean isMine;
    private boolean isFlag;
    private boolean isQuestionMark;
    private boolean isCovered;
    private boolean isEmpty;
    private int noSurroundingMines;

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
    }

    public void setDefaults()
    {
        isMine = false;
        isFlag = false;
        isQuestionMark = false;
        isCovered = true;
        noSurroundingMines = 0;
        isEmpty = true;

        this.setBackgroundResource(R.drawable.tile);
    }

    public void setMine(boolean mine)
    {

    }

    public void setFlag()
    {
        isFlag = true;
        setFlagIcon();
    }

    public void setQuestionMark()
    {
        isFlag = false;
        isQuestionMark = true;
        setQuestionMarkIcon();
    }

    public void setEmpty()
    {
        isFlag = false;
        setCoveredIcon();
    }

    public boolean setUncovered()
    {
       return isCovered = false;
    }

    public void updateSurroundingMineCount()
    {
        noSurroundingMines++;
        String img =  "mines"+noSurroundingMines;
        int drawableId = getResources().getIdentifier(img, "drawable", "dudeski.saperskv2i");

    }

    public  void openTile()
    {
        if(!isCovered)return;

        setUncovered();
        if(this.isMine())
            triggerMine();
        else
            showNumber();
    }

    //set the tile as a mine
    public void plantMine()
    {
        isMine = true;

    }

    public void triggerMine()
    {
        this.setBackgroundResource(R.drawable.boom);
    }

    public boolean isMine()
    {
    return isMine;
    }

    public boolean isFlag()
    {
    return isFlag;
    }

    public boolean isEmpty() {return isEmpty;}

    public boolean isQuestionMark(){return isQuestionMark;}

    public int getNoSurroundingMines()
    {
        return noSurroundingMines;
    }
    public boolean isCovered()
    {
        return isCovered;
    }

    //show the number icon
    public void showNumber()
    {
        String img = "mines"+noSurroundingMines;
        int drawableId = getResources().getIdentifier(img,"drawable","dudeski.saperskiv2");
        this.setBackgroundResource(drawableId);
    }

    public void setFlagIcon()
    {
        isEmpty = false;
        this.setBackgroundResource(R.drawable.flag);
    }

    public void setQuestionMarkIcon()
    {
        this.setBackgroundResource(R.drawable.quest);
    }

    public void setCoveredIcon()
    {
        this.setBackgroundResource(R.drawable.tile);
    }
}

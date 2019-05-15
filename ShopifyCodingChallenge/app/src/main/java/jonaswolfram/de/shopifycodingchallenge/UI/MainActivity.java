package jonaswolfram.de.shopifycodingchallenge.UI;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import jonaswolfram.de.shopifycodingchallenge.BusinessLogic.Grid;
import jonaswolfram.de.shopifycodingchallenge.R;

public class MainActivity extends Activity {

    private final String LOG = "MainActivity";
    private Grid grid;
    private Drawable drawableYellow;
    private Drawable drawableGray;
    private Drawable drawableRed;
    private Drawable drawableGreen;
    private int timesClicked;
    private int firstPositionClicked;
    private int selectedWordsCounter;
    private int allWordsCounter;
    private View firstViewClicked;
    private TextView textViewWordCounter;
    private TextView textViewTitle;
    private GridView gridView;
    private Button replayButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWordCounter = findViewById(R.id.textViewWordCounter);
        textViewTitle = findViewById(R.id.textViewTitle);
        gridView = findViewById(R.id.gridView);
        replayButton = findViewById(R.id.button);

        drawableYellow = getDrawable(R.drawable.grid_view_item_background_yellow);
        drawableGray = getDrawable(R.drawable.grid_view_item_background_gray);
        drawableRed = getDrawable(R.drawable.grid_view_item_background_red);
        drawableGreen = getDrawable(R.drawable.grid_view_item_background_green);

        /*
        grid.printCurrentField("background");
        grid.printCurrentField("foreground");
        */

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWordSearch();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //the click should only be handled when the game is not over yet
                if (selectedWordsCounter != allWordsCounter) {
                    if (view.isEnabled()) {
                        giveUserFeedback(view, position);
                    } else {
                        Toast.makeText(getApplicationContext(), "You cannot select this letter anymore!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (savedInstanceState == null) {
            startWordSearch();
        }
        else {
            restorePreviousState(savedInstanceState);
        }
    }

    private void giveUserFeedback(View view, int position) {
        Log.i(LOG, "The user clicked on the position " + position);
        timesClicked++;
        //after every second click a selection can be made
        if (timesClicked % 2 == 0) {
            int wordFoundCode = grid.checkIfWordWasFound(firstPositionClicked, position);
            final int[] charIndexes = grid.getCharIndexes();
            switch (wordFoundCode) {
                case -2:
                    Toast.makeText(getApplicationContext(), "Words can only be vertical, or horizontal. Try again!", Toast.LENGTH_SHORT).show();
                    firstViewClicked.setBackground(drawableGray);
                    break;
                case -1:
                    Toast.makeText(getApplicationContext(), "You are not allowed to do the same selection two times in a row", Toast.LENGTH_SHORT).show();
                    firstViewClicked.setBackground(drawableGray);
                    break;
                    //the selection was not correct
                case 0:
                    boolean greenCellHasBeenCrossed = false;
                    for (int charIndex : charIndexes) {
                        if (gridView.getChildAt(charIndex).getBackground() == drawableGreen) {
                            greenCellHasBeenCrossed = true;
                        }
                    }
                    if (greenCellHasBeenCrossed) {
                        Toast.makeText(getApplicationContext(), "This is not a CROSSWORD!", Toast.LENGTH_SHORT).show();
                        firstViewClicked.setBackground(drawableGray);
                    } else {
                        for (int charIndex : charIndexes) {
                            gridView.getChildAt(charIndex).setBackground(drawableRed);
                        }
                        //used to keep the colour red for the selected cells for one second. After that it is restored to previous state
                        new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] objects) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                for (int charIndex : charIndexes) {
                                    gridView.getChildAt(charIndex).setBackground(drawableGray);
                                }
                            }
                        }.execute();
                    }
                    break;
                    //the selection was correct
                case 1:
                    selectedWordsCounter++;
                    //win condition
                    if (selectedWordsCounter == allWordsCounter) {
                        textViewTitle.setText("You won. \n Congratulations!");
                        textViewWordCounter.setVisibility(View.GONE);
                        replayButton.setVisibility(View.VISIBLE);
                    }
                    textViewWordCounter.setText(allWordsCounter - selectedWordsCounter + " words remaining");
                    for (int charIndex : charIndexes) {
                        gridView.getChildAt(charIndex).setBackground(drawableGreen);
                        //a word cannot be clicked again after correct selection
                        gridView.getChildAt(charIndex).setEnabled(false);
                    }
                    break;
            }
        } else {
            firstPositionClicked = position;
            firstViewClicked = view;
            firstViewClicked.setBackground(drawableYellow);
        }
    }

    private void startWordSearch() {
        timesClicked = 0;
        firstPositionClicked = 0;
        selectedWordsCounter = 0;

        grid = new Grid();
        allWordsCounter = grid.getWords().length;
        grid.populateFieldsWithRandomChars();
        grid.populateFieldsWithGivenWords();

        textViewWordCounter.setVisibility(View.VISIBLE);
        textViewWordCounter.setText(allWordsCounter - selectedWordsCounter + " words remaining");
        textViewTitle.setText("Try to find all " + allWordsCounter + " words!");
        replayButton.setVisibility(View.GONE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.grid_view_item, grid.getCurrentGrid());
        gridView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequenceArrayList("currentGridViewData", grid.getCurrentGrid());
        outState.putInt("timesClicked", timesClicked);
        outState.putInt("firsPositionClicked", firstPositionClicked);
        outState.putInt("selectedWordsCounter", selectedWordsCounter);
        outState.putStringArray("words", grid.getWords());
    }

    private void restorePreviousState(Bundle savedInstanceState){
        timesClicked = savedInstanceState.getInt("timesClicked");
        firstPositionClicked = savedInstanceState.getInt("firsPositionClicked");
        selectedWordsCounter = savedInstanceState.getInt("selectedWordsCounter");

        grid = new Grid();
        allWordsCounter = grid.getWords().length;

        if (selectedWordsCounter == allWordsCounter) {
            textViewTitle.setText("You won. \n Congratulations!");
            textViewWordCounter.setVisibility(View.GONE);
            replayButton.setVisibility(View.VISIBLE);
        }
        else {
            textViewTitle.setText("Try to find all " + allWordsCounter + " words!");
            textViewWordCounter.setText(allWordsCounter - selectedWordsCounter + " words remaining");
        }

        ArrayList currentGridViewData = savedInstanceState.getCharSequenceArrayList("currentGridViewData");
        grid.setWords(savedInstanceState.getStringArray("words"));
        grid.setCurrentGrid(currentGridViewData);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.grid_view_item, currentGridViewData);
        gridView.setAdapter(arrayAdapter);
    }
}

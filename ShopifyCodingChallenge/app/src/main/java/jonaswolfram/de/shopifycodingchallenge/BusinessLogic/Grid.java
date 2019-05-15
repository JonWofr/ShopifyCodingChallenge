package jonaswolfram.de.shopifycodingchallenge.BusinessLogic;

import android.util.Log;

import java.util.ArrayList;

public class Grid {
    private String LOG = "Grid";

    private String[] words = {"OBJECTIVEC", "SWIFT", "KOTLIN", "VARIABLE", "JAVA", "MOBILE", "CODE"};
    private final Rules rules = new Rules();
    //the backgroundGrid is necessary to make a quick check for words, which already occupy the randomPosition, possible
    private char[] backgroundGrid = new char[100];
    private char[] foregroundGrid = new char[100];
    private int[] charIndexes;
    private boolean selectionTowardsTopOrBottom;

    public void populateFieldsWithRandomChars() {
        for (int i = 0; i < 100; i++) {
            char randomChar = (char) (65 + (int) (Math.random() * 26));
            backgroundGrid[i] = randomChar;
            foregroundGrid[i] = randomChar;
        }
    }

    public void populateFieldsWithGivenWords() {
        for (String word : words) {
            int wordLength = word.length();
            int randomPosition;
            boolean[] validDirections;

            while (true) {
                randomPosition = (int) (Math.random() * 100);
                Log.d(LOG, "Selected position is " + randomPosition + " for the word " + word);
                validDirections = rules.determineValidDirections(randomPosition, wordLength, backgroundGrid);
                boolean hasMinimumOneValidDirection = false;
                for (boolean direction : validDirections) {

                    if (direction){
                        hasMinimumOneValidDirection = true;
                        break;
                    }
                }
                if (hasMinimumOneValidDirection) {
                    break;
                }
                else {
                    Log.d(LOG, "No direction possible. Picking new number..");
                }
            }

            for (int i = 0; i < validDirections.length; i++){
                Log.d(LOG, "Direction " + i + " is " + validDirections[i]);
            }

            while (true) {
                int randomDirection = (int) (Math.random() * 4);
                Log.d(LOG, "Selected direction is " + randomDirection + " for the word " + word);
                if (validDirections[randomDirection]) {
                    setWordIntoCells(word, wordLength, randomPosition, randomDirection);
                    break;
                }
            }
        }
    }

    private void setWordIntoCells(String word, int wordLength, int randomPosition, int randomDirection){
        for (int i = 0; i < wordLength; i++) {
            //0 = top, 1 = right, 2 = bottom, 3 = left
            switch (randomDirection) {
                case 0:
                    backgroundGrid[randomPosition - i * 10] = (char) 43;
                    foregroundGrid[randomPosition - i * 10] = word.charAt(i);
                    break;
                case 1:
                    backgroundGrid[randomPosition + i] = (char) 43;
                    foregroundGrid[randomPosition + i] = word.charAt(i);
                    break;
                case 2:
                    backgroundGrid[randomPosition + i * 10] = (char) 43;
                    foregroundGrid[randomPosition + i * 10] = word.charAt(i);
                    break;
                case 3:
                    backgroundGrid[randomPosition - i] = (char) 43;
                    foregroundGrid[randomPosition - i] = word.charAt(i);
                    break;
            }
        }
    }

    public int checkIfWordWasFound (int firstPositionClicked, int secondPositionClicked){
        if (rules.checkIfDiagonalWasSelected(firstPositionClicked, secondPositionClicked)){
            return -2;
        }
        //notify the user that he pressed on the same cell two times in a row
        else if (firstPositionClicked == secondPositionClicked){
            return -1;
        }
        else {
            int deviation = determineDeviation(firstPositionClicked, secondPositionClicked);
            Log.d(LOG, "The distance between " + firstPositionClicked + " and " + secondPositionClicked + " is " + deviation);
            //needed to color indexes later on
            charIndexes = new int[deviation];
            String selectedChars = "";
            for (int i = 0; i < deviation; i++){
                int charIndex;
                if (selectionTowardsTopOrBottom) {
                    //selection towards the top
                    if (firstPositionClicked > secondPositionClicked) {
                        charIndex = firstPositionClicked - i * 10;
                    }
                    //selection towards the bottom
                    else {
                        charIndex = firstPositionClicked + i * 10;
                    }
                }
                else {
                    //selection towards the left
                    if (firstPositionClicked > secondPositionClicked) {
                        charIndex = firstPositionClicked - i;
                    }
                    //selection towards the right
                    else {
                        charIndex = firstPositionClicked + i;
                    }
                }
                selectedChars += foregroundGrid[charIndex];
                charIndexes[i] = charIndex;
            }
            Log.d(LOG, "The selected chars include " + selectedChars);
            for (int i = 0; i < words.length; i++){

                if (words[i].equals(selectedChars)){
                    Log.d(LOG, "The selected chars did match");
                    //To ensure that every word can only be selected once
                    words[i] = "";
                    return 1;
                }
            }
            Log.d(LOG, "The selected chars did not match any word");
            return 0;
        }
    }

    private int determineDeviation (int firstPositionClicked, int secondPositionClicked){
        int deviation;

        if (firstPositionClicked % 10 == secondPositionClicked % 10){
            selectionTowardsTopOrBottom = true;
            // + 1, because you cannot select the same position twice
            deviation = Math.abs(firstPositionClicked - secondPositionClicked) / 10 + 1;
        }
        else {
            selectionTowardsTopOrBottom = false;
            // + 1, because you cannot select the same position twice
            deviation = Math.abs(firstPositionClicked - secondPositionClicked) + 1;
        }
        return deviation;
    }

    //For debugging
    /*
    public void printCurrentField(String fieldType){
        String currentFieldAsString = "\n";
        for (int i = 0; i < 10; i++){
            for (int x = 0; x < 10; x++){
                switch (fieldType){
                    case "foreground":
                        currentFieldAsString += foregroundGrid[i * 10 + x] + " ";
                        break;
                    case "background":
                        currentFieldAsString += backgroundGrid[i * 10 + x] + " ";
                        break;
                }
                if (x == 9){
                    currentFieldAsString += "\n";
                }
            }
        }
        Log.d(LOG, currentFieldAsString);
    }
    */


    public ArrayList getCurrentGrid() {
        ArrayList currentGrid = new ArrayList();
                for (char specificChar : foregroundGrid){
                    currentGrid.add(specificChar);
                }
        return currentGrid;
    }

    public void setCurrentGrid(ArrayList currentGrid){
        for (int i = 0; i < 100; i++) {
            foregroundGrid[i] = currentGrid.get(i).toString().charAt(0);
        }
    }

    public int[] getCharIndexes (){
        return charIndexes;
    }

    public String[] getWords(){
        return words;
    }

    public void setWords(String[] words){
        this.words = words;
    }

}

package jonaswolfram.de.shopifycodingchallenge.BusinessLogic;

class Rules {

    private int randomPosition;
    private int wordLength;
    private char[] backgroundField;
    private final char charSignallingWordOccupation = (char) 43;

    boolean[] determineValidDirections (int randomPosition, int wordLength, char[] backgroundField){
        this.randomPosition = randomPosition;
        this.wordLength = wordLength;
        this.backgroundField = backgroundField;
        boolean[] validDirections = new boolean[4];

        validDirections[0] = checkDirectionTop();
        validDirections[1] = checkDirectionRight();
        validDirections[2] = checkDirectionBottom();
        validDirections[3] = checkDirectionLeft();
        return validDirections;
    }

    private boolean checkDirectionTop (){
        for (int i = 0; i < wordLength; i++){
            int index = randomPosition - i * 10;
            if (index < 0) {
                return false;
            }
            if (backgroundField[index] == charSignallingWordOccupation){
                return false;
            }
        }
        return true;
    }

    private boolean checkDirectionRight (){
        for (int i = 0; i < wordLength; i++){
            int index = randomPosition + i;
            if (index % 10 == 0 && i != 0) {
                return false;
            }
            if (index > 99){
                return false;
            }
            if (backgroundField[index] == charSignallingWordOccupation){
                return false;
            }
        }
        return true;
    }

    private boolean checkDirectionBottom (){
        for (int i = 0; i < wordLength; i++){
            int index = randomPosition + i * 10;
            if (index > 99) {
                return false;
            }
            if (backgroundField[index] == charSignallingWordOccupation){
                return false;
            }
        }
        return true;
    }

    private boolean checkDirectionLeft (){
        for (int i = 0; i < wordLength; i++){
            int index = randomPosition - i;
            if (index % 10 == 9 && i != 0) {
                return false;
            }
            if (index < 0){
                return false;
            }
            if (backgroundField[index] == charSignallingWordOccupation){
                return false;
            }
        }
        return true;
    }

    boolean checkIfDiagonalWasSelected (int firstPositionClicked, int secondPositionClicked){
        String firstPositionClickedAsString = String.valueOf(firstPositionClicked);
        String secondPositionClickedAsString = String.valueOf(secondPositionClicked);
        if (firstPositionClicked % 10 == secondPositionClicked % 10){
            return false;
        }
        else if (firstPositionClickedAsString.length() == 1 && secondPositionClickedAsString.length() == 1){
            return false;
        }
        else if (firstPositionClickedAsString.substring(0, 1).equals(secondPositionClickedAsString.substring(0, 1)) && firstPositionClickedAsString.length() == 2 && secondPositionClickedAsString.length() == 2){
            return false;
        }
        else {
            return true;
        }
    }

}

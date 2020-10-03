package hangman;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {
        EvilHangmanGame myGame = new EvilHangmanGame();
        File theDictionary = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);

        boolean dictExceptionThrown = false;
        Scanner inputLine = new Scanner(System.in);

        try {
            myGame.startGame(theDictionary, wordLength);
            myGame.setNumGuesses(Integer.parseInt(args[2]));

        } catch (IOException | EmptyDictionaryException e) {
            e.printStackTrace();
            dictExceptionThrown = true;
        }


        if(!dictExceptionThrown) {
            while (myGame.getNumGuesses() > 0) {
                System.out.println("You have " + myGame.getNumGuesses() + " guesses left.");
                System.out.print("Used letters:");
                myGame.printGuessedLetters();
                System.out.println("Word: " + myGame.getCurrentGuessKey());
                System.out.print("Enter Guess: ");

                char newGuess = inputLine.next().charAt(0);

                if (!Character.isLetter(newGuess)) {
                    System.out.println("Invalid input\n");
                    continue;
                }

                try {
                    myGame.makeGuess(newGuess);
                } catch (GuessAlreadyMadeException e) {
                    System.out.println("You already used that letter\n");
                    continue;
                }

                if(!myGame.getCurrentGuessKey().contains("-")) { break; }
            }

            if(myGame.getCurrentGuessKey().contains("-"))
                System.out.println("You lose!");
            else
                System.out.println("You win!");
            System.out.println("The word was " + myGame.getMySet().first());
        }
    }
}

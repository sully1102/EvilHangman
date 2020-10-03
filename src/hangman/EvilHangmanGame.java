package hangman;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.lang.StringBuilder;



public class EvilHangmanGame implements IEvilHangmanGame {

    private TreeSet<String> mySet;
    private SortedSet<Character> guessedLetters;
    private String currentGuessKey;
    private int numGuesses;

    public EvilHangmanGame() {
        mySet = new TreeSet<String>();
        guessedLetters = new TreeSet<Character>();
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        if(wordLength < 2) { throw new EmptyDictionaryException(); }

        mySet.clear();
        guessedLetters.clear();
        setNumGuesses(0);

        Scanner myReader = new Scanner(dictionary);

        while (myReader.hasNext()) {
            String word = myReader.next();
            if (word.length() == wordLength) {
                mySet.add(word.toLowerCase());
            }
        }
        myReader.close();

        if(mySet.isEmpty()) { throw new EmptyDictionaryException(); }

        char[] chars = new char[wordLength];
        Arrays.fill(chars, '-');
        setCurrentGuessKey(new String(chars));
    }

    @Override
    public TreeSet<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);

        if(guessedLetters.contains(guess)) { throw new GuessAlreadyMadeException(); }

        else {
            Map<String, TreeSet<String>> theMap = new HashMap<String, TreeSet<String>>();
            TreeSet<String> potentialKeys = new TreeSet<String>();

            for(String currentWord : mySet) {                               //populate theMap
                String currentPattern = makePattern(guess, currentWord);
                if(!theMap.containsKey(currentPattern)) {
                    theMap.put(currentPattern, new TreeSet<String>());
                }
                theMap.get(currentPattern).add(currentWord);
            }

            int maxSize = 0;
            for(String key : theMap.keySet()) {               //find the max size of the sets
                int individualSize = theMap.get(key).size();
                if(individualSize > maxSize)
                    maxSize = individualSize;
            }
            for(String key : theMap.keySet()) {               //populate the potentialKey Set
                if(theMap.get(key).size() == maxSize)
                    potentialKeys.add(key);
            }

            if(potentialKeys.size() > 1) {                       //get lowest frequency count
                int lowestFrequencyCount = getCurrentGuessKey().length();
                for(String key : potentialKeys) {
                    if(lowestFrequencyCount > getFrequencyCount(guess,key))
                        lowestFrequencyCount = getFrequencyCount(guess,key);
                }
                TreeSet<String> deletionSet = new TreeSet<String>();
                for(String key : potentialKeys) {
                    if(getFrequencyCount(guess,key) != lowestFrequencyCount)
                        deletionSet.add(key);
                }
                potentialKeys.removeAll(deletionSet);
            }

            if(potentialKeys.size() > 1) {                            //get rightmost letters
                for(int i = getCurrentGuessKey().length()-1; i >= 0; i--) {
                    boolean letterFound = false;
                    for(String key : potentialKeys) {
                        if(key.charAt(i) == guess)
                            letterFound = true;
                    }
                    TreeSet<String> deletionSet = new TreeSet<String>();
                    if(letterFound) {
                        for (String key : potentialKeys) {
                            if(key.charAt(i) != guess)
                                deletionSet.add(key);
                        }
                    }
                    potentialKeys.removeAll(deletionSet);
                    if(potentialKeys.size() == 1)
                        break;
                }
            }

            String newKey = potentialKeys.first();
            int freCount = getFrequencyCount(guess,newKey);
            changeTheSet(theMap.get(newKey));
            setCurrentGuessKey(newKey);

            if(freCount != 0) {
                if(freCount == 1)
                    System.out.println("Yes, there is 1 " + guess + "\n");
                else
                    System.out.println("Yes, there are " + freCount + " " + guess + "\'s\n");
            }
            else {
                System.out.println("Sorry, there are no " + guess + "\'s\n");
                numGuesses--;
            }
            guessedLetters.add(guess);
        }

        return mySet;
    }

    public int getFrequencyCount(char guess, String key) {
        int frequencyCount = 0;
        for(int i = 0; i < key.length(); i++) {
            if(key.charAt(i) == guess)
                frequencyCount++;
        }
        return frequencyCount;
    }

    public String makePattern(char guess, String currentWord) {
        StringBuilder pattern =  new StringBuilder();
        for(int i = 0; i < currentWord.length(); i++) {
            if(currentGuessKey.charAt(i) == '-') {
                if (currentWord.charAt(i) == guess)
                    pattern.append(guess);
                else
                    pattern.append('-');
            }
            else
                pattern.append(currentGuessKey.charAt(i));
        }
        return pattern.toString();
    }

    @Override
    public SortedSet<Character> getGuessedLetters() { return guessedLetters; }

    public String getCurrentGuessKey() { return currentGuessKey; }

    public void setCurrentGuessKey(String newString) { this.currentGuessKey = newString; }

    public TreeSet<String> getMySet() { return mySet; }

    public void changeTheSet(TreeSet<String> newSet) {
        mySet.clear();
        mySet.addAll(newSet);
    }

    public int getNumGuesses() { return numGuesses; }
    public void setNumGuesses(int numGuesses) { this.numGuesses = numGuesses; }

    public void printGuessedLetters() {
        for(Character letter : guessedLetters) {
            System.out.print(" " + letter);
        }
        System.out.println();
    }
}
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class FindWord {

	public static ArrayList<String> dictionaryForward;
	public static ArrayList<String> dictionaryBackward;

	public static String DICTPATH = "C://Users//STEVEN-PC//Documents//dictionary.txt";

	// static HashMap foundWords = new HashMap();
	static TreeSet<FoundWord> foundWords = new TreeSet(new NodeComparator());

	public FindWord() {
		ScrabbleAI sa = new ScrabbleAI();

		dictionaryForward = new ArrayList<String>();
		dictionaryBackward = new ArrayList<String>();

		dictionaryForward = storeDictionary(dictionaryForward);
		dictionaryBackward = findDictionaryBackwards(dictionaryForward, dictionaryBackward);
	}
	
	public void find(String letters, String full, int index, int otherIndex, int horivert) {
		// maximize starting string
		int indexStart = index;
		while (indexStart - 1 >= 0)
			if (full.charAt(indexStart - 1) == 0)
				break;
			else
				indexStart--;

		int indexEnd = index;
		while (indexEnd + 1 < 15)
			if (full.charAt(indexEnd + 1) == 0)
				break;
			else
				indexEnd++;

		String start = full.substring(indexStart, indexEnd + 1);

		StringBuilder str = new StringBuilder(start);
		String newstr = str.reverse().toString();
		
		// traverse backwards
		if (wordSearch(dictionaryBackward, newstr, true))
			findWords(letters, start, full, indexStart, indexEnd, otherIndex, horivert, false, true);

		// traverse forwards
		if (wordSearch(dictionaryForward, newstr, true))
			findWords(letters, start, full, indexStart, indexEnd, otherIndex, horivert, true, true);
	}

	public void findWords(String letters, String start, String full, int indexStart, int indexEnd, int otherIndex,
			int horivert, boolean ifForward, boolean ifFirst) {
		System.out.println(start);
		
		String curr = "";
		String aLetters;
		boolean ifDFS = false;

		int iStart = indexStart;
		int iEnd = indexEnd;

		// check if letter for current space already exists
		// if it does,no need to use letters
		if (ifForward) {

			// if have space to expand forward
			if (indexEnd + 1 >= full.length())
				return;
			// if existing character in next index of full
			else if (full.charAt(indexEnd + 1) != 0) {
				curr = start + full.charAt(indexEnd + 1);
				aLetters = letters;

				// if string sufficiently expanded, check if string is a word
				if (!expandable(full, indexStart, indexEnd + 1))
					checkDic(curr, iStart, otherIndex, horivert);

				// continue searching!
				search(aLetters, curr, full, iStart, iEnd + 1, otherIndex, horivert);
			} else {
				ifDFS = true;
			}
			// reverse searching - same as above but backwards
		} else { // !ifForward
			if (indexStart - 1 < 0)
				return;
			else if (full.charAt(indexStart - 1) != 0) {
				curr = full.charAt(indexStart - 1) + start;
				aLetters = letters;
				if (!expandable(full, indexStart - 1, indexEnd))
					checkDic(curr, iStart - 1, otherIndex, horivert);
				search(aLetters, curr, full, iStart - 1, iEnd, otherIndex, horivert);

			} else {
				ifDFS = true;
			}
		}

		// dfs each letter
		for (int i = 0; i < letters.length() && ifDFS; i++) {
			if (letters.charAt(i) != '+') { // if not a blank tile
				iStart = indexStart;
				iEnd = indexEnd;

				String temp = letters.substring(0, i) + letters.substring(i + 1);
				aLetters = temp;

				int putVal = 0;
				if (ifForward) { // put forwards
					putVal = indexEnd + 1;
					if (full.charAt(indexEnd + 1) == 0) {
						curr = start + letters.charAt(i);
						iEnd++;
					}
				} else { // put backwards
					putVal = indexStart - 1;
					if (full.charAt(indexStart - 1) == 0)
						curr = letters.charAt(i) + start;
					iStart--;
				}

				boolean part = false;
				if (!expandable(full, iStart, iEnd))
					part = checkDic(curr, iStart, otherIndex, horivert);

				// if first letter addition to word; also check orthogonal
				// direction
				if (part && ifFirst) {
					int ops = Math.abs(horivert - 1);
					String aFull = getFull(putVal, ops);
					search(aLetters, "" + letters.charAt(i), aFull, otherIndex, otherIndex, putVal, ops);
					part = false;
				}

				// continue search if word
				search(aLetters, curr, full, iStart, iEnd, otherIndex, horivert);

			} else { // a blank tile
				for (char j = 'A'; j <= 'Z'; j++) {

					iStart = indexStart;
					iEnd = indexEnd;

					String temp = letters.substring(0, i) + letters.substring(i + 1);
					aLetters = temp;

					int putVal = 0;
					if (ifForward) { // put forwards
						putVal = indexEnd + 1;
						if (full.charAt(indexEnd + 1) == 0) {
							curr = start + letters.charAt(i);
							iEnd++;
						}
					} else { // put backwards
						putVal = indexStart - 1;
						if (full.charAt(indexStart - 1) == 0)
							curr = letters.charAt(i) + start;
						iStart--;
					}

					boolean part = false;
					if (!expandable(full, iStart, iEnd))
						part = checkDic(curr, iStart, otherIndex, horivert);

					if (part && ifFirst) {
						int ops = Math.abs(horivert - 1);
						String aFull = getFull(putVal, ops);
						search(aLetters, "" + letters.charAt(i), aFull, otherIndex, otherIndex, putVal, ops);
						part = false;
					}

					// continue search if word
					search(aLetters, curr, full, iStart, iEnd, otherIndex, horivert);
				}
			}
		}

	}

	// check if additional characters in full can be added
	public boolean expandable(String full, int indexStart, int indexEnd) {
		if ((indexStart - 1 >= 0 && full.charAt(indexStart - 1) != 0)
				|| (indexEnd + 1 < 15 && full.charAt(indexEnd + 1) != 0))
			return true;
		else
			return false;
	}

	// adds word to foundWords with orientation if word exists
	public boolean checkDic(String curr, int startIndex, int otherIndex, int horivert) {
		boolean checkWord = false;
		// checks if current letters are a word
		checkWord = wordSearch(dictionaryForward, curr, false);
		
		if (checkWord) {

			// stored data that determines the orientation of the word
			int x;
			int y;

			if (horivert == 0) {
				y = startIndex;
				x = otherIndex;
			} else {
				y = otherIndex;
				x = startIndex;
			}

			FoundWord word = new FoundWord(x, y, curr, horivert);

			// tests if word is valid on scrabble board
			boolean success = word.getPointValue();
			if (success)
				foundWords.add(word);

		}
		return checkWord;
	}

	// continue searching given letters in the main col/row
	public void search(String aLetters, String curr, String full, int indexStart, int indexEnd, int otherIndex,
			int horivert) {
		// continue searching if dictionary contains word that contains curr at
		// beginning
		if (wordSearch(dictionaryForward, curr, true))
			findWords(aLetters, curr, full, indexStart, indexEnd, otherIndex, horivert, true, false);

		StringBuilder str = new StringBuilder(curr);
		String newstr = str.reverse().toString();

		// search reverse direction as well, since backwards words valid
		if (wordSearch(dictionaryBackward, newstr, true)) {
			findWords(aLetters, curr, full, indexStart, indexEnd, otherIndex, horivert, false, false);
		}
	}

	// basic binary search for checking if word exists
	public static boolean wordSearch(ArrayList<String> dictionary, String word, boolean aTest) {

		int dicLength = dictionary.size();
		int wordLength = word.length();

		int min = 0;
		int max = dicLength;

		boolean isWord = false;
		boolean testDup1 = false;
		boolean testDup2 = false;

		int checkWord;

		while ((max - min) > 1) {
			if (dictionary.get(max - 1).equals(word) || dictionary.get(min).equals(word))
				return true;

			// halve the sample size
			int currIndex = min + ((max - min) / 2);

			// do modified search if words similar, but one is longer
			if (testDup1 || testDup2) {
				if (!checkWordExtra(dictionary, currIndex, word) && !checkWordSmall(dictionary, currIndex, word))
					return false;
				else
					return true;
			} else
				checkWord = currIndex;

			// set current word to current middle
			String currWord = dictionary.get(checkWord);
			int currWordLen = currWord.length();

			int index = 0;

			// return if current word is same as search word
			if (currWord.equals(word))
				return true;

			while ((index <= wordLength) && isWord == false) {
				// CRUCIAL - DIFFERENT RETURN WHEN aTest IS TRUE:
				// TRUE WHEN THERE IS A WORD THAT EXISTS THAT CONTAINS
				// PASSED WORD: E.G. TRUE WHEN WORD: FA ; CURRWORD: FATE
				if (index == wordLength && currWord.contains(word) && aTest)
					return true;

				// if search word in dictionary is contained but shorter
				if (index == (wordLength)) {
					testDup1 = true;
					break;
				}

				// if current word is contained but shorter
				if (index == currWordLen) {
					testDup2 = true;
					break;
				}

				// gets ascii value of current index for both words
				int wordLetterIndex = findLetterIndex(word.charAt(index));
				int dicLetterIndex = findLetterIndex(currWord.charAt(index));

				// if matching index, keep searching through letters
				if (wordLetterIndex == dicLetterIndex)
					index++;

				// change search parameters if characters differ
				else if (wordLetterIndex < dicLetterIndex) {
					max = checkWord;
					break;
				} else if (dicLetterIndex < wordLetterIndex) {
					min = checkWord;
					break;
				}
			}
		}
		return false;
	}

	// loop through all words that contain word until possibly reach one that
	// equals exactly
	public static boolean checkWordExtra(ArrayList<String> dictionary, int index, String word) {
		while (dictionary.get(index).contains(word)) {
			if (index - 1 < 0)
				break;

			if (dictionary.get(index).equals(word)) {
				return true;
			} else
				index--;
		}
		return false;
	}

	// loop forwards through all words that contain word until one that equals
	// exactly
	public static boolean checkWordSmall(ArrayList<String> dictionary, int index, String word) {
		while (dictionary.get(index).contains(word)) {
			if (index + 1 >= dictionary.size())
				break;
			if (dictionary.get(index).equals(word))
				return true;
			else
				index++;
		}
		return false;
	}

	// gets dictionary from file and adds values to arraylist
	public ArrayList<String> storeDictionary(ArrayList<String> dictionary) {

		try {
			FileReader reader = new FileReader(DICTPATH);
			BufferedReader textReader = new BufferedReader(reader);

			String line;
			while ((line = textReader.readLine()) != null) {
				line = line.toUpperCase();
				dictionary.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return dictionary;
	}

	// reverses dictionary and adds to arraylist
	public ArrayList<String> findDictionaryBackwards(ArrayList<String> dictionary, ArrayList<String> dicBack) {
		for (int i = 0; i < dictionary.size(); i++) {
			StringBuilder str = new StringBuilder(dictionary.get(i));
			String output = str.reverse().toString();
			dicBack.add(output);
		}

		String[] aDic = new String[dictionary.size()];
		aDic = dicBack.toArray(aDic);
		Arrays.sort(aDic);

		ArrayList<String> ret = new ArrayList<String>(Arrays.asList(aDic));
		return ret;
	}

	// gets full column/row
	public static String getFull(int index, int horizvert) {
		if (horizvert == 0) { // gets horizontal string
			return new String(ScrabblePanel.currentBoard[index]);
		} else { // gets vertical string{
			String vert = "";
			for (int k = 0; k < 15; k++)
				vert += ScrabblePanel.currentBoard[k][index];
			return vert;
		}

	}

	// ACCESSOR METHODS

	public TreeSet<FoundWord> getFoundWords() {
		return foundWords;
	}

	// UTILITY METHODS

	// comparator for TreeSet - removes duplicate strings
	public static class NodeComparator implements Comparator<FoundWord> {
		public int compare(FoundWord first, FoundWord second) {
			if (first.getWord().equals(second.getWord()))
				return 0;
			else // not equals
				return 1;
		}
	}

	// resets found words to empty
	public void resetFoundWords() {
		foundWords = new TreeSet(new NodeComparator());
	}

	// returns ascii value of character - 96 (to start from 0)
	public static int findLetterIndex(char character) {
		return ((int) character - 96);
	}
}

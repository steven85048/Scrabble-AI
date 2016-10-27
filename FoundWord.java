
public class FoundWord {
	private int x;
	private int y;
	private int horivert;
	private int pointValue;
	private String word;
	private String hashString;
	
	public FoundWord(int x, int y, String word, int horivert) {
		this.x = x;
		this.y = y;
		this.word = word;
		this.horivert = horivert;
		getPointValue();
		hashString = x + y + horivert + pointValue + word;
		
		System.out.println(toString());
	}

	// traverse through word to obtain point values; add connected words
	// returns false if connected word does not exist
	public boolean getPointValue() {
		int startIndex = 0;
		int oppositeIndex = 0;

		switch (horivert) {
		case 0: // horizontal
			startIndex = y;
			oppositeIndex = x;
			break;
		case 1: // vertical
			startIndex = x;
			oppositeIndex = y;
			break;
		}
		// multipliers
		boolean doubleWord = false;
		boolean tripleWord = false;

		int points = 0;

		
		for (int i = 0; i < word.length(); i++) {
			int expandedPoints = 0;

			int curr = i + startIndex;

			int multiplier = 0;
			char currentChar = 0;

			if (horivert == 0) {
				currentChar = word.charAt(i);
				multiplier = ScrabblePanel.multipliers[oppositeIndex][curr];
			} else if (horivert == 1) {
				currentChar = word.charAt(i);
				multiplier = ScrabblePanel.multipliers[curr][oppositeIndex];
			}

			// String containing the perpendicular line of letters
			String perpLine = FindWord.getFull(curr, Math.abs(horivert - 1));
			perpLine = perpLine.substring(0, oppositeIndex) + currentChar + perpLine.substring(oppositeIndex+1);

			int currCharVal = (Integer) ScrabblePanel.values.get(currentChar);

			// Expand the word to get all letters
			if (canExpand(perpLine, oppositeIndex)) {
				// get fully expanded word
				String expandedWord = getWord(perpLine, oppositeIndex);
				// word not valid if an expanded version word does not exist
				if (!FindWord.wordSearch(FindWord.dictionaryForward, expandedWord, false)){
					return false;
				}

				expandedPoints += (findPoints(expandedWord) - currCharVal);

				// if current character already exists on board, don't add
				// multiplier
				if (!(currentChar == 0))
					multiplier = 0;
			}

			// apply various modifiers based on tiles on multipliers
			switch (multiplier) {
			case 0:
				break;
			case 1:
				tripleWord = true;
				break;
			case 2:
				doubleWord = true;
				break;
			case 3:
				currCharVal *= 3;
				break;
			case 4:
				currCharVal *= 2;
				break;
			}

			points += currCharVal;
			points += expandedPoints;
		}

		// apply multipliers at end
		if (tripleWord)
			points *= 3;
		else if (doubleWord)
			points *= 2;

		pointValue = points;

		return true;
	}

	// gets point value of the string given value chart
	public static int findPoints(String word) {
		int points = 0;
		for (int i = 0; i < word.length(); i++)
			points += (Integer) ScrabblePanel.values.get(word.charAt(i));
		return points;
	}

	// expands the line at index curr until maximum letters reached
	public static String getWord(String line, int curr) {

		int min = curr;
		while (min >= 0) {
			if (line.charAt(min) == 0 || min <= 0)
				break;
			else
				min--;
		}

		int max = curr;
		while (max < 15) {
			if (line.charAt(max) == 0 || max == 14)
				break;
			else
				max++;
		}

		int leftBound = 0;
		if (min == 0)
			leftBound = 0;
		else
			leftBound = min + 1;

		String aReturn = line.substring(leftBound, max);

		return aReturn;
	}

	// checks if possible to keep expanding in either direction
	public boolean canExpand(String full, int startIndex) {
		if ((startIndex + 1 < 15 && full.charAt(startIndex + 1) != 0) || (startIndex - 1 >= 0 && full.charAt(startIndex - 1) != 0))
			return true;

		return false;
	}

	// accessor methods

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	// getter methods

	public int getHorivert() {
		return horivert;
	}

	public int getPoints() {
		return pointValue;
	}

	public String getWord() {
		return word;
	}

	public String getHashString(){
		return hashString;
	}
	
	// utility methods

	public String toString() {
		return x + "        " + y + "       " + word + "        " + horivert + "       " + pointValue;
	}

}

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScrabblePanel extends JPanel {
	private int PANEL_WIDTH = 15;
	private int PANEL_HEIGHT = 15;
	private int NUM_LETTERS = 7;

	public static int[][] multipliers = new int[15][15];
	public static HashMap values = new HashMap();
	public static char[][] currentBoard;
	public static char[][] savedBoard;

	private PriorityQueue foundWordsPQ;
	private ArrayList<FoundWord> foundWords;
	private HashMap<String, FoundWord> mapDuplicate;
	
	private FoundWord currentWord;
	private String letters;

	private JTextField[][] textFields;
	private JTextField[] letterFields;

	private JPanel mainPanel;

	private JPanel leftPanel;
	private JPanel rightPanel;

	private JPanel boardPanel;
	private JPanel letterPanel;
	private JPanel dataPanel;

	private JButton wordsButton;
	private JButton updateButton;

	private JList list;

	FindWord fw;

	public ScrabblePanel() {
		fw = new FindWord();

		textFields = new JTextField[PANEL_WIDTH][PANEL_HEIGHT];
		letterFields = new JTextField[NUM_LETTERS];

		resetBoard();

		mainPanel = new JPanel(new GridLayout(1, 2));
		mainPanel.setPreferredSize(new Dimension(ScrabbleApplet.APPLET_WIDTH, ScrabbleApplet.APPLET_HEIGHT - 20));

		// LEFT SIDE COMPONENTS

		leftPanel = new JPanel(new BorderLayout());

		boardPanel = new JPanel(new GridLayout(PANEL_HEIGHT, PANEL_WIDTH));
		letterPanel = new JPanel(new GridLayout(1, NUM_LETTERS));
		wordsButton = new JButton("Find Words");

		boardPanel.setPreferredSize(new Dimension(500, 500));
		letterPanel.setPreferredSize(new Dimension(500, 33));

		// populate grid of text fields
		for (int i = 0; i < PANEL_WIDTH; i++) {
			for (int j = 0; j < PANEL_HEIGHT; j++) {
				JTextField tf = new JTextField();
				boardPanel.add(tf);
				textFields[i][j] = tf;
			}
		}

		// populate grid of user letters
		for (int i = 0; i < NUM_LETTERS; i++) {
			JTextField tf = new JTextField();
			letterPanel.add(tf);
			letterFields[i] = tf;
		}

		JPanel bottomHalf = new JPanel(new BorderLayout());
		bottomHalf.add(letterPanel, BorderLayout.NORTH);
		bottomHalf.add(wordsButton, BorderLayout.SOUTH);

		leftPanel.add(boardPanel, BorderLayout.NORTH);
		leftPanel.add(bottomHalf, BorderLayout.SOUTH);

		// RIGHT SIDE COMPONENTS

		rightPanel = new JPanel(new BorderLayout());

		dataPanel = new JPanel(new BorderLayout());
		dataPanel.setPreferredSize(new Dimension(500, 600));

		updateButton = new JButton("Update");

		list = new JList();
		dataPanel.add(list, BorderLayout.CENTER);

		rightPanel.add(dataPanel, BorderLayout.CENTER);
		rightPanel.add(updateButton, BorderLayout.SOUTH);

		// ADD TO MAIN PANEL

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);

		add(mainPanel);

		// ADD ACTION LISTENERS
		wordsButton.addActionListener(new WordsListener());
		updateButton.addActionListener(new UpdateListener());

		// OTHER STUFF
		initializeMultipliers();
		initializeValues();
	}

	// initialization methods

	public void initializeMultipliers() {
		// Triple word = 1
		multipliers[0][3] = 1;
		multipliers[0][11] = 1;
		multipliers[3][0] = 1;
		multipliers[3][14] = 1;
		multipliers[11][14] = 1;
		multipliers[11][0] = 1;
		multipliers[14][11] = 1;
		multipliers[14][3] = 1;

		// Double word = 2
		multipliers[1][5] = 2;
		multipliers[1][9] = 2;
		multipliers[3][7] = 2;
		multipliers[5][1] = 2;
		multipliers[5][13] = 2;
		multipliers[7][3] = 2;
		multipliers[7][11] = 2;
		multipliers[9][13] = 2;
		multipliers[9][1] = 2;
		multipliers[11][7] = 2;
		multipliers[13][5] = 2;
		multipliers[13][9] = 2;

		// Triple letter = 3
		multipliers[0][6] = 3;
		multipliers[0][8] = 3;
		multipliers[3][3] = 3;
		multipliers[3][11] = 3;
		multipliers[5][5] = 3;
		multipliers[5][9] = 3;
		multipliers[6][0] = 3;
		multipliers[6][14] = 3;
		multipliers[8][14] = 3;
		multipliers[8][0] = 3;
		multipliers[9][9] = 3;
		multipliers[9][5] = 3;
		multipliers[11][11] = 3;
		multipliers[11][3] = 3;
		multipliers[14][8] = 3;
		multipliers[14][6] = 3;

		// Double letter = 4
		multipliers[1][2] = 4;
		multipliers[1][12] = 4;
		multipliers[2][1] = 4;
		multipliers[2][4] = 4;
		multipliers[2][13] = 4;
		multipliers[2][10] = 4;
		multipliers[4][2] = 4;
		multipliers[4][6] = 4;
		multipliers[4][12] = 4;
		multipliers[4][8] = 4;
		multipliers[6][4] = 4;
		multipliers[6][10] = 4;
		multipliers[8][10] = 4;
		multipliers[8][4] = 4;
		multipliers[10][8] = 4;
		multipliers[10][12] = 4;
		multipliers[10][6] = 4;
		multipliers[10][2] = 4;
		multipliers[12][10] = 4;
		multipliers[12][13] = 4;
		multipliers[12][4] = 4;
		multipliers[12][1] = 4;
		multipliers[13][12] = 4;
		multipliers[13][2] = 4;
	}

	public void initializeValues() {
		values.put('A', 1);
		values.put('B', 4);
		values.put('C', 4);
		values.put('D', 2);
		values.put('E', 1);
		values.put('F', 4);
		values.put('G', 3);
		values.put('H', 3);
		values.put('I', 1);
		values.put('J', 10);
		values.put('K', 5);
		values.put('L', 2);
		values.put('M', 4);
		values.put('N', 2);
		values.put('O', 1);
		values.put('P', 4);
		values.put('Q', 10);
		values.put('R', 1);
		values.put('S', 1);
		values.put('T', 1);
		values.put('U', 2);
		values.put('V', 5);
		values.put('W', 4);
		values.put('X', 8);
		values.put('Y', 3);
		values.put('Z', 10);
	}

	// listener for getWords button
	private class WordsListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// get FoundWord objects for each position
			resetBoard();
			getBoardData();

			for (int i = 0; i < currentBoard.length; i++) {
				for (int j = 0; j < currentBoard[0].length; j++) {
					if (currentBoard[i][j] != 0) {
						System.out.println("CHECK INDEX: " + i + "   " + j);
						
						// words for horizontal and vertical directions
						TreeSet<FoundWord> horizWords = new TreeSet<FoundWord>();
						TreeSet<FoundWord> vertWords = new TreeSet<FoundWord>();

						// traverse in horizontal direction
						String horiz = FindWord.getFull(i, 0);
						horiz.toUpperCase();
						fw.find(letters, horiz, j, i, 0);
						horizWords = fw.getFoundWords();
						fw.resetFoundWords();

						// traverse in vertical direction
						String vert = FindWord.getFull(j, 1);
						vert.toUpperCase();
						fw.find(letters, vert, i, j, 1);
						vertWords = fw.getFoundWords();
						fw.resetFoundWords();

						addTreeSetToFound(horizWords);
						addTreeSetToFound(vertWords);
					}
				}
			}

			// transfer priority queue data to arraylist
			movePriorityQueueToArrayList();
			updateJList();

			// save current user inputted board
			savedBoard = clone2DArray(currentBoard);
		}
	}

	// listener for update button
	private class UpdateListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {	
			// resets board to user input
			resetTextFields();
			
			char[][] selectedBoard = clone2DArray(savedBoard);
			int indexSelected = list.getSelectedIndex();
			FoundWord found = foundWords.get(indexSelected);

			int horivert = found.getHorivert();
			String word = found.getWord();
			int x = found.getX();
			int y = found.getY();
			
			int startIndex = 0;
			int otherIndex = 0;
			
			for (int i = 0; i < word.length(); i++) {
				switch (horivert) {
				// horizontal
				case 0:
					startIndex = y + i;
					otherIndex = x;
					
					selectedBoard[otherIndex][startIndex] = word.charAt(i);
					break;
				// vertical
				case 1:
					startIndex = x + i;
					otherIndex = y;
					
					selectedBoard[startIndex][otherIndex] = word.charAt(i);
					break;
				}
			}
			
			updateTextFields(selectedBoard);
		}
	}

	// displays data on text fields
	public void updateTextFields(char[][] data){
		for (int i = 0 ; i < data.length;i ++){
			for (int j = 0 ; j < data[0].length; j++){
				textFields[i][j].setText("" + data[i][j]);
			}
		}
	}
	
	// resets text field to user data
	public void resetTextFields(){
		updateTextFields(savedBoard);
	}
	
	// updates jlist according to foundWords
	public void updateJList() {
		// call updateUI() for the JList object

		// initialize JList
		DefaultListModel model = new DefaultListModel();

		// adds athlete's strings to model
		for (int i = 0; i < foundWords.size(); i++)
			model.addElement(foundWords.get(i).toString());

		// sets model and refreshes ui
		list.setModel(model);
		list.updateUI();

	}

	// comparator

	// sorts data based on point values so higher point value words first
	private class PointComparator implements Comparator<FoundWord> {

		public int compare(FoundWord first, FoundWord second) {
			return second.getPoints() - first.getPoints();
		}

	}

	// utility methods

	// transfers treeset data into larger priority queue sorted by point value
	public void addTreeSetToFound(TreeSet<FoundWord> words) {
		for (FoundWord word : words) {
			// add to PriorityQueue with modified
			foundWordsPQ.add(word);
			mapDuplicate.put(word.getHashString(), word);
		}
	}

	// transfers priority queue data into array list by polling top
	public void movePriorityQueueToArrayList() {
		while (!foundWordsPQ.isEmpty()) {
			FoundWord curr = (FoundWord) foundWordsPQ.poll();
			if (mapDuplicate.containsKey(curr.getHashString()))
				foundWords.add(curr);
		}
	}

	// loops through textFields to get data from board
	public void getBoardData() {
		for (int i = 0; i < textFields.length; i++) {
			for (int j = 0; j < textFields[0].length; j++) {
				String text = textFields[i][j].getText();
				if (!(text.equals(""))) {
					text.toUpperCase();
					char currChar = text.charAt(0);
					currentBoard[i][j] = Character.toUpperCase(currChar);
				}
			}
		}

		letters = "";
		for (int i = 0; i < letterFields.length; i++) {
			String text = letterFields[i].getText();
			if (!text.equals(""))
				letters += Character.toUpperCase(text.charAt(0));
		}
	}

	// resets data stored
	public void resetBoard() {
		foundWordsPQ = new PriorityQueue(new PointComparator());
		foundWords = new ArrayList<FoundWord>();
		mapDuplicate = new HashMap<String, FoundWord>();
		currentBoard = new char[15][15];
	}

	// clone 2D array 
	public char[][] clone2DArray(char[][] arr){
		char[][] newArr = new char[arr.length][arr[0].length];
		for (int i = 0 ; i < arr.length; i++)
			newArr[i] = arr[i].clone();
		
		return newArr;
	}
}

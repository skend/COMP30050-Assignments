package poker;

import java.util.ArrayList;

public class PlayingCard {

	public static void main(String[] args) {
		ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>();
		
		char[] suits = { PlayingCard.HEARTS, PlayingCard.DIAMONDS,
						PlayingCard.CLUBS, PlayingCard.SPADES };
		
		int suitTrack = 0;
		
		// create each card in the deck and add them to the deck[]
		for (int i = 0; i < 4; i++) {
			char currentSuit = suits[suitTrack++];
			for (int j = 2; j < 11; j++) {
				deck.add(new PlayingCard("" + j, currentSuit, j, j));
			}
			deck.add(new PlayingCard("J", currentSuit, 11, 11));
			deck.add(new PlayingCard("Q", currentSuit, 12, 12));
			deck.add(new PlayingCard("K", currentSuit, 13, 13));
			deck.add(new PlayingCard("A", currentSuit, 1, 14));
		}
		
		for (int i = 0; i < 52; i++) {
			System.out.println(deck.get(i).toString());
		}
	}
	
	public static final char DIAMONDS = 'D';
	public static final char HEARTS = 'H';
	public static final char SPADES = 'S';
	public static final char CLUBS = 'C';
	
	public PlayingCard(String type, char suit, int faceValue, int gameValue) {
		this.type = type;
		this.suit = suit;
		this.faceValue = faceValue;
		this.gameValue = gameValue;
	}
	
	public String toString() {
		return "" + type + suit;
	}
	
	public int getFaceValue() { return faceValue; }
	public int getGameValue() { return gameValue; }
	public int getSuit() { return suit; }
	
	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;
	
}

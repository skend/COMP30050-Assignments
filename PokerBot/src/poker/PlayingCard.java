package poker;

import java.util.ArrayList;

public class PlayingCard {

	public static void main(String[] args) {
		ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>();

		char[] suits = { PlayingCard.HEARTS, PlayingCard.DIAMONDS, PlayingCard.CLUBS, PlayingCard.SPADES };

		int suitTrack = 0;

		// Create each card in the deck and add them to the deck[].
		for (int i = 0; i < DeckOfCards.NUMBER_OF_SUITS; i++) {
			char currentSuit = suits[suitTrack++];
			for (int j = TWO_GAME_VALUE; j <= TEN_GAME_VALUE; j++) {
				deck.add(new PlayingCard("" + j, currentSuit, j, j));
			}
			deck.add(new PlayingCard("J", currentSuit, JACK_GAME_VALUE, JACK_GAME_VALUE));
			deck.add(new PlayingCard("Q", currentSuit, QUEEN_GAME_VALUE, QUEEN_GAME_VALUE));
			deck.add(new PlayingCard("K", currentSuit, KING_GAME_VALUE, KING_GAME_VALUE));
			deck.add(new PlayingCard("A", currentSuit, 1, ACE_GAME_VALUE));
		}

		for (int i = 0; i < DeckOfCards.DECK_SIZE; i++) {
			System.out.println(deck.get(i).toString());
		}
	}
	
	// Constants are ALWAYS better than magic numbers no matter what :^)
	public static final char DIAMONDS = 'D';
	public static final char HEARTS = 'H';
	public static final char SPADES = 'S';
	public static final char CLUBS = 'C';
	
	public static final int TWO_GAME_VALUE = 2;
	public static final int THREE_GAME_VALUE = 3;
	public static final int FOUR_GAME_VALUE = 4;
	public static final int FIVE_GAME_VALUE = 5;
	public static final int SIX_GAME_VALUE = 6;
	public static final int SEVEN_GAME_VALUE = 7;
	public static final int EIGHT_GAME_VALUE = 8;
	public static final int NINE_GAME_VALUE = 9;
	public static final int TEN_GAME_VALUE = 10;
	public static final int JACK_GAME_VALUE = 11;
	public static final int QUEEN_GAME_VALUE = 12;
	public static final int KING_GAME_VALUE = 13;
	public static final int ACE_GAME_VALUE = 14;

	public PlayingCard(String type, char suit, int faceValue, int gameValue) {
		this.type = type;
		this.suit = suit;
		this.faceValue = faceValue;
		this.gameValue = gameValue;
	}

	public String toString() {
		return "" + type + suit;
	}

	public int getFaceValue() {
		return faceValue;
	}

	public int getGameValue() {
		return gameValue;
	}

	public char getSuit() {
		return suit;
	}

	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;

}

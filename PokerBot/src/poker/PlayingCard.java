package poker;

public class PlayingCard {

	public static void main(String[] args) {
		PlayingCard[] deck = new PlayingCard[52];
		
		char[] suits = { PlayingCard.HEARTS, PlayingCard.DIAMONDS,
						PlayingCard.CLUBS, PlayingCard.SPADES };
		
		int deckCount = 0;
		int suitTrack = 0;
		
		// create each card in the deck and add them to the deck[]
		for (int i = 0; i < 4; i++) {
			char currentSuit = suits[suitTrack++];
			for (int j = 2; j < 11; j++) {
				deck[deckCount++] = new PlayingCard("" + j, currentSuit, j, j);
			}
			deck[deckCount++] = new PlayingCard("J", currentSuit, 11, 11);
			deck[deckCount++] = new PlayingCard("Q", currentSuit, 12, 12);
			deck[deckCount++] = new PlayingCard("K", currentSuit, 13, 13);
			deck[deckCount++] = new PlayingCard("A", currentSuit, 1, 14);
		}
		
		for (int i = 0; i < 52; i++) {
			System.out.println(deck[i].toString());
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
	
	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;
	
}

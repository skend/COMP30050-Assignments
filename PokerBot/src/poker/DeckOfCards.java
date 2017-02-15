package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DeckOfCards {

	public static void main(String[] args) {
		DeckOfCards test = new DeckOfCards();

		// Deal 52 cards and return them.
		for (int i = 0; i < 52; i++) {
			PlayingCard card = test.dealNext();
			System.out.println(card);
			test.returnCard(card);
		}

		// Attempt to deal 5 more cards. This should print 5 lines of 'null'.
		System.out.println("\nDealt all the cards and returned them. Testing by dealing 5 more cards.");
		for (int i = 0; i < 5; i++) {
			PlayingCard card1 = test.dealNext();
			System.out.println(card1);
		}

		// Reset the deck and attempt to deal 5 more cards. This should print 5
		// lines of valid cards.
		System.out.println("\nResetting the deck and trying to deal 5 more cards.");
		test.reset();
		for (int i = 0; i < 5; i++) {
			PlayingCard card1 = test.dealNext();
			System.out.println(card1);
		}
	}

	public DeckOfCards() {
		reset();
	}

	// Recreate the deck and shuffle it.
	public void reset() {
		// make the deck thread safe
		deck = Collections.synchronizedList(new ArrayList<PlayingCard>());

		char[] suits = { PlayingCard.HEARTS, PlayingCard.DIAMONDS, PlayingCard.CLUBS, PlayingCard.SPADES };

		numCardsDealt = 0;
		int suitTrack = 0;

		// Create each card in the deck and add them to the deck ArrayList.
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

		shuffle();
	}

	// Shuffle the deck by swapping cards 10000 times.
	public void shuffle() {
		Random rand = new Random();

		for (int i = 0; i < 10000; i++) {
			int rand1 = rand.nextInt((51 - 0) + 1) + 0;
			int rand2 = rand.nextInt((51 - 0) + 1) + 0;
			Collections.swap(this.deck, rand1, rand2);
		}
	}

	// Remove the card from the top of the deck and return it (to a player).
	public PlayingCard dealNext() {
		if (numCardsDealt >= 52)
			return null;

		PlayingCard card = null;

		if (deck.size() > 0)
			card = deck.get(0);
		else
			return null;

		deck.remove(0);
		numCardsDealt++;

		return card;
	}

	// Return a card (from a hand/discarded) to the deck.
	public void returnCard(PlayingCard card) {
		deck.add(card);
	}

	private int numCardsDealt;
	private List<PlayingCard> deck;
}

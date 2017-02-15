package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HandOfCards {

	private ArrayList<PlayingCard> hand;
	private static final int HAND_SIZE = 5;
	private DeckOfCards deck;

	public HandOfCards(DeckOfCards d) {
		this.deck = d;
		hand = new ArrayList<PlayingCard>();
		deal();
	}

	public DeckOfCards getDeck() {
		return deck;
	}

	/*
	 * Deals cards to the hand and stores them in the private ArrayList hand.
	 * After dealing the cards it sorts them by calling sort().
	 */
	private void deal() {
		for (int i = 0; i < HAND_SIZE; i++) {
			hand.add(deck.dealNext());
		}

		sort();
	}

	/*
	 * Sorts the cards in the hand putting the highest game value card in the
	 * first position and the lowest game value card in the last position.
	 */
	private void sort() {
		Collections.sort(hand, new Comparator<PlayingCard>() {
			public int compare(PlayingCard c1, PlayingCard c2) {
				return c2.getGameValue() - c1.getGameValue();
			}
		});
	}

	/*
	 * Helper function used to determine whether a hand forms a 'run' meaning
	 * that the highest card is one above the second highest card and the second
	 * highest card is one above the third highest card and so on for all cards
	 * in the hand.
	 */
	private boolean isRun() {
		boolean lowValueRun = false;
		if (hand.get(0).getGameValue() == 14 && hand.get(4).getGameValue() == 2)
			lowValueRun = true;

		if (lowValueRun) {
			return ((hand.get(1).getGameValue() - hand.get(2).getGameValue())
					* (hand.get(2).getGameValue() - hand.get(3).getGameValue())
					* (hand.get(3).getGameValue() - hand.get(4).getGameValue()) == 1)
					&& hand.get(4).getGameValue() == 2;
		} else {
			return ((hand.get(0).getGameValue() - hand.get(1).getGameValue())
					* (hand.get(1).getGameValue() - hand.get(2).getGameValue())
					* (hand.get(2).getGameValue() - hand.get(3).getGameValue())
					* (hand.get(3).getGameValue() - hand.get(4).getGameValue()) == 1);
		}
	}

	// Determines whether all the cards in the hand are of the same suit.
	private boolean sameSuit() {
		return ((hand.get(0).getSuit() == hand.get(1).getSuit()) 
				&& (hand.get(0).getSuit() == hand.get(2).getSuit())
				&& (hand.get(0).getSuit() == hand.get(3).getSuit())
				&& (hand.get(0).getSuit() == hand.get(4).getSuit()));
	}

	// Determines whether the hand contains one pair.
	public boolean isOnePair() {
		return ((hand.get(0).getGameValue() == hand.get(1).getGameValue())
				|| (hand.get(1).getGameValue() == hand.get(2).getGameValue())
				|| (hand.get(2).getGameValue() == hand.get(3).getGameValue())
				|| (hand.get(3).getGameValue() == hand.get(4).getGameValue())) 
				&& !isFourOfAKind() && !isThreeOfAKind() && !isTwoPair() && !isFullHouse();
	}

	// Determines whether the hand is a straight.
	public boolean isStraight() {
		return isRun() && !sameSuit();
	}

	// Determines whether the hand is a high hand.
	public boolean isHighHand() {
		return !isFourOfAKind() && !isThreeOfAKind() && !isTwoPair() && !isOnePair() 
				&& !isStraight() && !sameSuit() && !isFullHouse();
	}

	// Determines whether the hand is a flush.
	public boolean isFlush() {
		return !isRun() && sameSuit();
	}

	// Determines whether the hand is a straight flush (meaning the cards are in
	// order and t.
	public boolean isStraightFlush() {
		return isRun() && sameSuit() && !isRoyalFlush();
	}

	/*
	 * Determines whether the hand is a full house (meaning it has three of the
	 * same value cards, and one pair of same value cards).
	 */
	public boolean isFullHouse() {
		boolean pair = false;
		boolean threeOfAKind = false;

		if ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(0).getGameValue() != hand.get(2).getGameValue())
				|| ((hand.get(3).getGameValue() == hand.get(4).getGameValue()
				&& hand.get(2).getGameValue() != hand.get(3).getGameValue()))) {
			pair = true;
		}

		if ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(0).getGameValue() == hand.get(2).getGameValue())
				|| ((hand.get(2).getGameValue() == hand.get(3).getGameValue()
				&& hand.get(2).getGameValue() == hand.get(4).getGameValue()))) {
			threeOfAKind = true;
		}

		return pair && threeOfAKind;
	}

	// Determines whether the hand contains two pairs of same value cards.
	public boolean isTwoPair() {
		boolean pair1 = false;
		boolean pair2 = false;

		if ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				|| hand.get(1).getGameValue() == hand.get(2).getGameValue())) {
			pair1 = true;
		}

		if ((hand.get(2).getGameValue() == hand.get(3).getGameValue()
				|| hand.get(3).getGameValue() == hand.get(4).getGameValue())) {
			pair2 = true;
		}

		if (hand.get(0).getGameValue() == hand.get(2).getGameValue()
				|| hand.get(2).getGameValue() == hand.get(4).getGameValue())
			return false;

		return pair1 && pair2;
	}

	// Determines whether the hand contains three of the same value card.
	public boolean isThreeOfAKind() {
		return ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(1).getGameValue() == hand.get(2).getGameValue()
				&& hand.get(0).getGameValue() != hand.get(3).getGameValue())
				|| (hand.get(1).getGameValue() == hand.get(2).getGameValue()
				&& hand.get(2).getGameValue() == hand.get(3).getGameValue()
				&& hand.get(1).getGameValue() != hand.get(4).getGameValue())
				|| (hand.get(2).getGameValue() == hand.get(3).getGameValue()
				&& hand.get(3).getGameValue() == hand.get(4).getGameValue()
				&& hand.get(1).getGameValue() != hand.get(4).getGameValue()))
				&& !isFullHouse() && !isFourOfAKind();
	}

	// Determines whether the hand contains four of the same value card.
	public boolean isFourOfAKind() {
		return ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(2).getGameValue() == hand.get(3).getGameValue()
				&& (hand.get(0).getGameValue() == hand.get(3).getGameValue())
				|| (hand.get(1).getGameValue() == hand.get(2).getGameValue()
				&& hand.get(3).getGameValue() == hand.get(4).getGameValue()
				&& hand.get(1).getGameValue() == hand.get(4).getGameValue())))
				&& !isTwoPair();
	}

	/*
	 * Determines whether the hand is a royal flush (meaning the cards are in
	 * order and of the same suit AND that the highest value card is an ace).
	 */
	public boolean isRoyalFlush() {
		return sameSuit() && isRun() && hand.get(0).getGameValue() == 14;
	}

	public static void main(String[] args) {
		DeckOfCards d = new DeckOfCards();

		// Sort the cards into separate lists based on suit.
		ArrayList<PlayingCard> hearts = new ArrayList<>();
		ArrayList<PlayingCard> spades = new ArrayList<>();
		ArrayList<PlayingCard> diamonds = new ArrayList<>();
		ArrayList<PlayingCard> clubs = new ArrayList<>();
		for (int i = 0; i < 52; i++) {
			PlayingCard card = d.dealNext();
			switch (card.getSuit()) {
			case 'H':
				hearts.add(card);
				break;
			case 'S':
				spades.add(card);
				break;
			case 'D':
				diamonds.add(card);
				break;
			case 'C':
				clubs.add(card);
				break;
			}
		}

		// Order these suit lists from lowest to highest game value card.
		Collections.sort(hearts, new Comparator<PlayingCard>() {
			public int compare(PlayingCard c1, PlayingCard c2) {
				return c1.getGameValue() - c2.getGameValue();
			}
		});

		Collections.sort(spades, new Comparator<PlayingCard>() {
			public int compare(PlayingCard c1, PlayingCard c2) {
				return c1.getGameValue() - c2.getGameValue();
			}
		});

		Collections.sort(diamonds, new Comparator<PlayingCard>() {
			public int compare(PlayingCard c1, PlayingCard c2) {
				return c1.getGameValue() - c2.getGameValue();
			}
		});

		Collections.sort(clubs, new Comparator<PlayingCard>() {
			public int compare(PlayingCard c1, PlayingCard c2) {
				return c1.getGameValue() - c2.getGameValue();
			}
		});

		/*
		 * Create hands of cards from these lists that correspond to real poker
		 * hands. Store these hands in their own lists.
		 */
		ArrayList<PlayingCard> royalFlush = new ArrayList<PlayingCard>();
		for (int i = 8; i < 13; i++) {
			royalFlush.add(spades.get(i));
		}

		ArrayList<PlayingCard> straightFlush = new ArrayList<PlayingCard>();
		for (int i = 0; i < 5; i++) {
			straightFlush.add(spades.get(i));
		}

		ArrayList<PlayingCard> fourOfAKind = new ArrayList<PlayingCard>();
		fourOfAKind.add(hearts.get(0));
		fourOfAKind.add(spades.get(4));
		fourOfAKind.add(clubs.get(4));
		fourOfAKind.add(diamonds.get(4));
		fourOfAKind.add(hearts.get(4));

		ArrayList<PlayingCard> threeOfAKind = new ArrayList<PlayingCard>();
		threeOfAKind.add(spades.get(4));
		threeOfAKind.add(clubs.get(4));
		threeOfAKind.add(diamonds.get(4));
		threeOfAKind.add(clubs.get(8));
		threeOfAKind.add(diamonds.get(10));

		ArrayList<PlayingCard> twoPair = new ArrayList<PlayingCard>();
		twoPair.add(spades.get(4));
		twoPair.add(clubs.get(4));
		twoPair.add(spades.get(9));
		twoPair.add(clubs.get(9));
		twoPair.add(clubs.get(12));

		ArrayList<PlayingCard> fullHouse = new ArrayList<PlayingCard>();
		fullHouse.add(clubs.get(4));
		fullHouse.add(diamonds.get(4));
		fullHouse.add(spades.get(8));
		fullHouse.add(clubs.get(8));
		fullHouse.add(spades.get(8));

		ArrayList<PlayingCard> flush = new ArrayList<PlayingCard>();
		flush.add(spades.get(0));
		flush.add(spades.get(8));
		flush.add(spades.get(10));
		flush.add(spades.get(2));
		flush.add(spades.get(4));

		ArrayList<PlayingCard> straight = new ArrayList<PlayingCard>();
		straight.add(clubs.get(0));
		straight.add(diamonds.get(1));
		straight.add(spades.get(2));
		straight.add(hearts.get(3));
		straight.add(clubs.get(4));

		ArrayList<PlayingCard> pair = new ArrayList<PlayingCard>();
		pair.add(clubs.get(0));
		pair.add(diamonds.get(0));
		pair.add(spades.get(2));
		pair.add(hearts.get(6));
		pair.add(clubs.get(10));

		d.reset();

		/*
		 * Determine whether isRoyalFlush() returns true for a royal flush hand
		 * and make sure it is not mistaken for other hands.
		 */
		HandOfCards royalFlushHand = new HandOfCards(d);
		royalFlushHand.hand.clear();
		for (PlayingCard card : royalFlush)
			royalFlushHand.hand.add(card);
		royalFlushHand.sort();
		if (royalFlushHand.isRoyalFlush())
			System.out.println("Royal Flush - Success");
		if (royalFlushHand.isFlush())
			System.out.println("Royal Flush - Failed");
		if (royalFlushHand.isStraight())
			System.out.println("Royal Flush - Failed");
		if (royalFlushHand.isStraightFlush())
			System.out.println("Royal Flush - Failed");

		/*
		 * Determine whether isFourOfAKind() returns true for a four of a kind
		 * hand and make sure it is not mistaken for other hands.
		 */
		HandOfCards fourOfAKindHand = new HandOfCards(d);
		fourOfAKindHand.hand.clear();
		for (PlayingCard card : fourOfAKind)
			fourOfAKindHand.hand.add(card);
		fourOfAKindHand.sort();
		if (fourOfAKindHand.isFourOfAKind())
			System.out.println("Four of a Kind - Success");
		if (fourOfAKindHand.isThreeOfAKind())
			System.out.println("Four of a Kind - Failed 1");
		if (fourOfAKindHand.isTwoPair())
			System.out.println("Four of a Kind - Failed");
		if (fourOfAKindHand.isOnePair())
			System.out.println("Four of a Kind - Failed");
		if (fourOfAKindHand.isFullHouse())
			System.out.println("Four of a Kind - Failed");

		/*
		 * Determine whether isThreeOfAKind() returns true for a three of a kind
		 * hand and make sure it is not mistaken for other hands.
		 */
		HandOfCards threeOfAKindHand = new HandOfCards(d);
		threeOfAKindHand.hand.clear();
		for (PlayingCard card : threeOfAKind)
			threeOfAKindHand.hand.add(card);
		threeOfAKindHand.sort();
		if (threeOfAKindHand.isThreeOfAKind())
			System.out.println("Three of a Kind - Success");
		if (threeOfAKindHand.isFourOfAKind())
			System.out.println("Three of a Kind - Failed");
		if (threeOfAKindHand.isTwoPair())
			System.out.println("Three of a Kind - Failed");
		if (threeOfAKindHand.isOnePair())
			System.out.println("Three of a Kind - Failed");
		if (threeOfAKindHand.isFullHouse())
			System.out.println("Three of a Kind - Failed");

		/*
		 * Determine whether isTwoPair() returns true for a two pair hand and
		 * make sure it is not mistaken for other hands.
		 */
		HandOfCards twoPairHand = new HandOfCards(d);
		twoPairHand.hand.clear();
		for (PlayingCard card : twoPair)
			twoPairHand.hand.add(card);
		twoPairHand.sort();
		if (twoPairHand.isTwoPair())
			System.out.println("Two Pair - Success");
		if (twoPairHand.isFourOfAKind())
			System.out.println("Two Pair - Failed");
		if (twoPairHand.isThreeOfAKind())
			System.out.println("Two Pair - Failed");
		if (twoPairHand.isOnePair())
			System.out.println("Two Pair - Failed");
		if (twoPairHand.isFullHouse())
			System.out.println("Two Pair - Failed");

		/*
		 * Determine whether isPair() returns true for a pair hand and make sure
		 * it is not mistaken for other hands.
		 */
		HandOfCards pairHand = new HandOfCards(d);
		pairHand.hand.clear();
		for (PlayingCard card : pair)
			pairHand.hand.add(card);
		pairHand.sort();
		if (pairHand.isOnePair())
			System.out.println("Pair - Success");
		if (pairHand.isFourOfAKind())
			System.out.println("Pair - Failed");
		if (pairHand.isThreeOfAKind())
			System.out.println("Pair - Failed");
		if (pairHand.isTwoPair())
			System.out.println("Pair - Failed");
		if (pairHand.isFullHouse())
			System.out.println("Two Pair - Failed");

		/*
		 * Determine whether isStraightFlush() returns true for a straight flush
		 * and make sure it is not mistaken for other hands.
		 */
		HandOfCards straightFlushHand = new HandOfCards(d);
		straightFlushHand.hand.clear();
		for (PlayingCard card : straightFlush)
			straightFlushHand.hand.add(card);
		straightFlushHand.sort();
		if (straightFlushHand.isFlush())
			System.out.println("Straight flush - Failed");
		if (straightFlushHand.isStraight())
			System.out.println("Straight flush - Failed");
		if (straightFlushHand.isStraightFlush())
			System.out.println("Straight flush - Success");
		if (straightFlushHand.isRoyalFlush())
			System.out.println("Straight flush - Failed");

		/*
		 * Determine whether isFullHouse() returns true for a full house and
		 * make sure it is not mistaken for other hands.
		 */
		HandOfCards fullHouseHand = new HandOfCards(d);
		fullHouseHand.hand.clear();
		for (PlayingCard card : fullHouse)
			fullHouseHand.hand.add(card);
		fullHouseHand.sort();
		if (fullHouseHand.isFullHouse())
			System.out.println("Full house - Success");
		if (fullHouseHand.isFourOfAKind())
			System.out.println("Full house - Failed");
		if (fullHouseHand.isThreeOfAKind())
			System.out.println("Full house - Failed");
		if (fullHouseHand.isTwoPair())
			System.out.println("Full house - Failed");
		if (fullHouseHand.isOnePair())
			System.out.println("Full house - Failed");

		/*
		 * Determine whether isFlush() returns true for a flush and make sure it
		 * is not mistaken for other hands.
		 */
		HandOfCards flushHand = new HandOfCards(d);
		flushHand.hand.clear();
		for (PlayingCard card : flush)
			flushHand.hand.add(card);
		flushHand.sort();
		if (flushHand.isFlush())
			System.out.println("Flush - Success");
		if (flushHand.isStraight())
			System.out.println("Flush - Failed");
		if (flushHand.isStraightFlush())
			System.out.println("Flush - Failed");
		if (flushHand.isRoyalFlush())
			System.out.println("Flush - Failed");

		/*
		 * Determine whether isStraight() returns true for a straight and make
		 * sure it is not mistaken for other hands.
		 */
		HandOfCards straightHand = new HandOfCards(d);
		straightHand.hand.clear();
		for (PlayingCard card : straight)
			straightHand.hand.add(card);
		straightHand.sort();
		if (straightHand.isStraight())
			System.out.println("Straight - Success");
		if (straightHand.isFlush())
			System.out.println("Straight - Failed");
		if (straightHand.isStraightFlush())
			System.out.println("Straight - Failed");
		if (straightHand.isRoyalFlush())
			System.out.println("Straight - Failed");

		System.out.println("\n");
		d.reset();

		HandOfCards randomHand = new HandOfCards(d);
		for (int i = 0; i < HAND_SIZE; i++) {
			System.out.println(randomHand.hand.get(i));
		}
		System.out.println("Pair: " + randomHand.isOnePair());
		System.out.println("Two Pair: " + randomHand.isTwoPair());
		System.out.println("Three of a kind: " + randomHand.isThreeOfAKind());
		System.out.println("Four of a kind: " + randomHand.isFourOfAKind());
		System.out.println("Flush: " + randomHand.isFlush());
		System.out.println("Straight Flush: " + randomHand.isStraightFlush());
		System.out.println("Straight: " + randomHand.isStraight());
		System.out.println("Royal Flush: " + randomHand.isRoyalFlush());
		System.out.println("Full house: " + randomHand.isFullHouse());
		System.out.println("High hand: " + randomHand.isHighHand());
	}
}
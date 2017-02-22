package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HandOfCards {
	
	/*
	 * The maximum value a hand subtracted from the previous tier's default value
	 * is approximately 570,000. The default value intervals are 1,000,000 in order
	 * to clearly separate each type of hand which makes testing easier. 
	 */
	private static final int HIGH_CARD_DEFAULT = 0;
	private static final int ONE_PAIR_DEFAULT = 1000000;
	private static final int TWO_PAIR_DEFAULT = 2000000;
	private static final int THREE_OF_A_KIND_DEFAULT = 3000000;
	private static final int STRAIGHT_DEFAULT = 4000000;
	private static final int FLUSH_DEFAULT = 5000000;
	private static final int FULL_HOUSE_DEFAULT = 6000000;
	private static final int FOUR_OF_A_KIND_DEFAULT = 7000000;
	private static final int STRAIGHT_FLUSH_DEFAULT = 8000000;
	private static final int ROYAL_FLUSH_DEFAULT = 9000000;

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
		return ((hand.get(0).getSuit() == hand.get(1).getSuit()) && (hand.get(0).getSuit() == hand.get(2).getSuit())
				&& (hand.get(0).getSuit() == hand.get(3).getSuit())
				&& (hand.get(0).getSuit() == hand.get(4).getSuit()));
	}

	public int getGameValue() {
		// Weight the highest card over the next highest card and so on.
		if (isHighHand()) {
			int handValue = HandOfCards.HIGH_CARD_DEFAULT;
			handValue += Math.pow(hand.get(0).getGameValue(), 5);
			handValue += Math.pow(hand.get(1).getGameValue(), 4);
			handValue += Math.pow(hand.get(2).getGameValue(), 3);
			handValue += Math.pow(hand.get(3).getGameValue(), 2);
			handValue += hand.get(0).getGameValue();
			return handValue;
		}

		// Weight the pair, then high card, then next highest card and so on.
		if (isOnePair()) {
			// Find the index where the pair begins in the hand.
			// Initialise the hand value at the one pair default value.
			int handValue = HandOfCards.ONE_PAIR_DEFAULT;
			int pairIndex = findPairStartIndex(0);

			// Since the hand is sorted, the positions of the high cards are known.
			if (pairIndex == 0) {
				handValue += Math.pow(hand.get(1).getGameValue(), 3);
				handValue += Math.pow(hand.get(2).getGameValue(), 2);
				handValue += hand.get(3).getGameValue();
			} else if (pairIndex == 1) {
				handValue += Math.pow(hand.get(0).getGameValue(), 3);
				handValue += Math.pow(hand.get(3).getGameValue(), 2);
				handValue += hand.get(4).getGameValue();
			} else if (pairIndex == 2) {
				handValue += Math.pow(hand.get(0).getGameValue(), 3);
				handValue += Math.pow(hand.get(1).getGameValue(), 2);
				handValue += hand.get(4).getGameValue();
			} else if (pairIndex == 3) {
				handValue += Math.pow(hand.get(0).getGameValue(), 3);
				handValue += Math.pow(hand.get(1).getGameValue(), 2);
				handValue += hand.get(2).getGameValue();
			}

			handValue += Math.pow(hand.get(pairIndex).getGameValue(), 5);
			return handValue;
		}

		// Weight the highest value pair, then the other pair, then the kicker (remaining card).
		if (isTwoPair()) {
			// Initialise the hand value to the two pair default value.
			// Find the indexes of each pair in the hand.
			int handValue = HandOfCards.TWO_PAIR_DEFAULT;
			int index1 = findPairStartIndex(0);
			int index2 = findPairStartIndex(index1 + 1);

			// Add the value of the kicker to the hand value.
			if (index1 == 0 && index2 == 3) {
				handValue += hand.get(2).getGameValue();
			} else if (index1 == 1 && index2 == 3) {
				handValue += hand.get(0).getGameValue();
			} else if (index1 == 0 && index2 == 2) {
				handValue += hand.get(4).getGameValue();
			}
			
			// Add the value of the greatest pair to the power of 4.
			// Add the value of the second greatest pair to the power of 3.
			handValue += Math.pow(hand.get(index1).getGameValue(), 4);
			handValue += Math.pow(hand.get(index2).getGameValue(), 3);
			return handValue;
		}
		
		// Adds the card value of the three of a kind to the default value.
		if (isThreeOfAKind()) {
			int index = findPairStartIndex(0);
			return hand.get(index).getGameValue() + HandOfCards.THREE_OF_A_KIND_DEFAULT;
		}
		
		// Adds the card value of the four of a kind to the default value.
		if (isFourOfAKind()) {
			int index = findPairStartIndex(0);
			return hand.get(index).getGameValue() + HandOfCards.FOUR_OF_A_KIND_DEFAULT;
		}
		
		// Get the highest value card in the straight and add it to the default value.
		if (isStraight()) {
			int handValue = 0;
			
			// If the hand contains an ace - determine whether it is an ace low or ace high.
			// If it is an ace low, then get the next highest card in the hand.
			if (hand.get(0).getGameValue() == 14) {
				if (hand.get(4).getGameValue() == 2) {
					handValue = 4;
				} else {
					handValue = 14;
				}
			} else {
				handValue = hand.get(0).getGameValue();
			}

			return handValue + HandOfCards.STRAIGHT_DEFAULT;
		}
		
		// The same principle as high hand. 
		if (isFlush()) {
			int handValue = HandOfCards.FLUSH_DEFAULT;
			handValue += Math.pow(hand.get(0).getGameValue(), 5);
			handValue += Math.pow(hand.get(1).getGameValue(), 4);
			handValue += Math.pow(hand.get(2).getGameValue(), 3);
			handValue += Math.pow(hand.get(3).getGameValue(), 2);
			handValue += hand.get(0).getGameValue();
			return handValue;
		}
		
		// Weight the three of a kind over the two pair. 
		if (isFullHouse()) {
			boolean threeOfAKindAtStart = false;
			int handValue = FULL_HOUSE_DEFAULT;

			if (hand.get(0).getGameValue() == hand.get(2).getGameValue()) {
				threeOfAKindAtStart = true;
			}

			if (threeOfAKindAtStart) {
				handValue += Math.pow(hand.get(0).getGameValue(), 3) + Math.pow(hand.get(3).getGameValue(), 2);
			} else {
				handValue += Math.pow(hand.get(2).getGameValue(), 3) + Math.pow(hand.get(0).getGameValue(), 2);
			}

			return handValue;
		}
		
		// Add the highest card in the straight to the default value.
		if (isStraightFlush()) {
			boolean containsAce = false;
			int handValue = 0;

			if (hand.get(0).getGameValue() == 14) {
				containsAce = true;
			}

			if (containsAce) {
				if (hand.get(4).getGameValue() == 2) {
					handValue = 4;
				} else {
					handValue = 14;
				}
			} else {
				handValue = hand.get(0).getGameValue();
			}

			return handValue + HandOfCards.STRAIGHT_FLUSH_DEFAULT;
		}
		
		// All royal flushes are the same (disregarding suit) so just add 10,000 to the default value.
		if (isRoyalFlush()) {
			return HandOfCards.ROYAL_FLUSH_DEFAULT + 10000;
		}

		return 0;
	}

	private int findPairStartIndex(int offset) {
		for (int i = offset; i < HAND_SIZE - 1; i++) {
			if (hand.get(i).getGameValue() == hand.get(i + 1).getGameValue()) {
				return i;
			}
		}

		return 0;
	}

	// Determines whether the hand contains one pair.
	public boolean isOnePair() {
		return ((hand.get(0).getGameValue() == hand.get(1).getGameValue())
				|| (hand.get(1).getGameValue() == hand.get(2).getGameValue())
				|| (hand.get(2).getGameValue() == hand.get(3).getGameValue())
				|| (hand.get(3).getGameValue() == hand.get(4).getGameValue())) && !isFourOfAKind() && !isThreeOfAKind()
				&& !isTwoPair() && !isFullHouse();
	}

	// Determines whether the hand is a straight.
	public boolean isStraight() {
		return isRun() && !sameSuit();
	}

	// Determines whether the hand is a high hand.
	public boolean isHighHand() {
		return !isFourOfAKind() && !isThreeOfAKind() && !isTwoPair() && !isOnePair() && !isStraight() && !sameSuit()
				&& !isFullHouse();
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

	public String toString() {
		String str = "";
		for (int i = 0; i < HAND_SIZE; i++) {
			str += hand.get(i).toString() + " ";
		}
		return str;
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

		d.reset();
		System.out.println();

		HandOfCards randomHand = new HandOfCards(d);
		for (int i = 0; i < HAND_SIZE; i++) {
			System.out.println(randomHand.hand.get(i));
		}
		System.out.println("");
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

		// Display a break between boolean function testing and handValue
		// testing.
		System.out.println();
		for (int i = 0; i < 6; i++) {
			System.out.println("-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
		}
		System.out.println();
		System.out.println("\n----------- Hand Value Testing -----------\n");

		
		// High hands will be tested against other high hands.
		// Hand3 should have the highest value, followed by hand1, with hand2
		// having the lowest value.
		System.out.println("------------------------------------------");
		System.out.println("High Card Tests");
		System.out.println("Proposition: Hand3 > Hand1 > Hand2");
		System.out.println("------------------------------------------");
		d.reset();
		// hand1: AS KH 4H 3C 2D
		HandOfCards hand1 = new HandOfCards(d);
		hand1.hand.clear();
		hand1.hand.add(spades.get(12));
		hand1.hand.add(hearts.get(11));
		hand1.hand.add(hearts.get(2));
		hand1.hand.add(clubs.get(1));
		hand1.hand.add(diamonds.get(0));
		System.out.println("Hand1: " + hand1.toString() + "\nValue:" + hand1.getGameValue() + "\n");

		// hand2: AS QH JH 10C 9D
		HandOfCards hand2 = new HandOfCards(d);
		hand2.hand.clear();
		hand2.hand.add(spades.get(12));
		hand2.hand.add(hearts.get(10));
		hand2.hand.add(hearts.get(9));
		hand2.hand.add(clubs.get(8));
		hand2.hand.add(diamonds.get(7));
		System.out.println("Hand2: " + hand2.toString() + "\nValue:" + hand2.getGameValue() + "\n");

		// hand3: AS KH 8H 3C 2D
		HandOfCards hand3 = new HandOfCards(d);
		hand3.hand.clear();
		hand3.hand.add(spades.get(12));
		hand3.hand.add(hearts.get(11));
		hand3.hand.add(hearts.get(6));
		hand3.hand.add(clubs.get(1));
		hand3.hand.add(diamonds.get(0));
		System.out.println("Hand3: " + hand3.toString() + "\nValue:" + hand3.getGameValue() + "\n");

		
		// Test hands with one pair. Hand5 should be the best hand, followed by
		// hand4 followed by hand6.
		System.out.println("------------------------------------------");
		System.out.println("One Pair Tests");
		System.out.println("Proposition: Hand5 > Hand4 > Hand6");
		System.out.println("------------------------------------------");
		d.reset();
		// hand4: JH 10C 6S 2C 2D
		HandOfCards hand4 = new HandOfCards(d);
		hand4.hand.clear();
		hand4.hand.add(hearts.get(9));
		hand4.hand.add(clubs.get(8));
		hand4.hand.add(spades.get(4));
		hand4.hand.add(clubs.get(0));
		hand4.hand.add(diamonds.get(0));

		hand4.sort();
		System.out.println("Hand4: " + hand4.toString() + "\nValue:" + hand4.getGameValue() + "\n");

		// hand5: JH 6S 3C 3D 2C
		HandOfCards hand5 = new HandOfCards(d);
		hand5.hand.clear();
		hand5.hand.add(hearts.get(9));
		hand5.hand.add(spades.get(4));
		hand5.hand.add(clubs.get(1));
		hand5.hand.add(diamonds.get(1));
		hand5.hand.add(clubs.get(0));
		hand5.sort();
		System.out.println("Hand5: " + hand5.toString() + "\nValue:" + hand5.getGameValue() + "\n");

		// hand6: JH 9C 6S 2C 2D
		HandOfCards hand6 = new HandOfCards(d);
		hand6.hand.clear();
		hand6.hand.add(hearts.get(9));
		hand6.hand.add(clubs.get(7));
		hand6.hand.add(spades.get(4));
		hand6.hand.add(clubs.get(0));
		hand6.hand.add(diamonds.get(0));
		hand6.sort();
		System.out.println("Hand6: " + hand6.toString() + "\nValue:" + hand6.getGameValue() + "\n");

		
		// Test hands with two pairs. Hand7 should be worth more than hand8.
		System.out.println("------------------------------------------");
		System.out.println("Two Pair Tests");
		System.out.println("Proposition: Hand7 > Hand8");
		System.out.println("------------------------------------------");
		d.reset();
		// hand7: JH JC 6S 2C 2D
		HandOfCards hand7 = new HandOfCards(d);
		hand7.hand.clear();
		hand7.hand.add(hearts.get(9));
		hand7.hand.add(clubs.get(9));
		hand7.hand.add(spades.get(4));
		hand7.hand.add(clubs.get(0));
		hand7.hand.add(diamonds.get(0));
		hand7.sort();
		System.out.println("Hand7: " + hand7.toString() + "\nValue:" + hand7.getGameValue() + "\n");

		// hand8: JH JC 5S 2C 2D
		HandOfCards hand8 = new HandOfCards(d);
		hand8.hand.clear();
		hand8.hand.add(hearts.get(9));
		hand8.hand.add(clubs.get(9));
		hand8.hand.add(spades.get(3));
		hand8.hand.add(clubs.get(0));
		hand8.hand.add(diamonds.get(0));
		hand8.sort();
		System.out.println("Hand8: " + hand8.toString() + "\nValue:" + hand8.getGameValue() + "\n");

		
		// Test hands with three of a kind. Hand9 should be worth more than hand10.
		System.out.println("------------------------------------------");
		System.out.println("Three of a Kind Tests");
		System.out.println("Proposition: Hand9 > Hand10");
		System.out.println("------------------------------------------");
		d.reset();
		// hand9: JH JC JS 3C 2D
		HandOfCards hand9 = new HandOfCards(d);
		hand9.hand.clear();
		hand9.hand.add(hearts.get(9));
		hand9.hand.add(clubs.get(9));
		hand9.hand.add(spades.get(9));
		hand9.hand.add(clubs.get(1));
		hand9.hand.add(diamonds.get(0));
		hand9.sort();
		System.out.println("Hand9: " + hand9.toString() + "\nValue:" + hand9.getGameValue() + "\n");

		// hand10: 10H 10C 10S 9C 8D
		HandOfCards hand10 = new HandOfCards(d);
		hand10.hand.clear();
		hand10.hand.add(hearts.get(8));
		hand10.hand.add(clubs.get(8));
		hand10.hand.add(spades.get(8));
		hand10.hand.add(clubs.get(7));
		hand10.hand.add(diamonds.get(6));
		hand10.sort();
		System.out.println("Hand10: " + hand10.toString() + "\nValue:" + hand10.getGameValue() + "\n");

		
		// Test hands with four of a kind. Hand11 should be worth more than hand12.
		System.out.println("------------------------------------------");
		System.out.println("Four of a Kind Tests");
		System.out.println("Proposition: Hand11 > Hand12");
		System.out.println("------------------------------------------");
		d.reset();
		// hand11: JH JC JS JD 2D
		HandOfCards hand11 = new HandOfCards(d);
		hand11.hand.clear();
		hand11.hand.add(hearts.get(9));
		hand11.hand.add(clubs.get(9));
		hand11.hand.add(spades.get(9));
		hand11.hand.add(diamonds.get(9));
		hand11.hand.add(diamonds.get(0));
		hand11.sort();
		System.out.println("Hand11: " + hand11.toString() + "\nValue:" + hand11.getGameValue() + "\n");

		// hand12: 10H 10C 10S 10D AD
		HandOfCards hand12 = new HandOfCards(d);
		hand12.hand.clear();
		hand12.hand.add(hearts.get(8));
		hand12.hand.add(clubs.get(8));
		hand12.hand.add(spades.get(8));
		hand12.hand.add(diamonds.get(8));
		hand12.hand.add(diamonds.get(12));
		hand12.sort();
		System.out.println("Hand12: " + hand12.toString() + "\nValue:" + hand12.getGameValue() + "\n");

		
		// Test hands with a straight. Hand13 should be worth more than hand14.
		System.out.println("------------------------------------------");
		System.out.println("Straight Tests");
		System.out.println("Proposition: Hand13 > Hand14");
		System.out.println("------------------------------------------");
		d.reset();
		// hand13: JH 10C 9S 8D 7D
		HandOfCards hand13 = new HandOfCards(d);
		hand13.hand.clear();
		hand13.hand.add(hearts.get(9));
		hand13.hand.add(clubs.get(8));
		hand13.hand.add(spades.get(7));
		hand13.hand.add(diamonds.get(6));
		hand13.hand.add(diamonds.get(5));
		hand13.sort();
		System.out.println("Hand13: " + hand13.toString() + "\nValue:" + hand13.getGameValue() + "\n");

		// hand14: 10H 9C 8S 7C 6D
		HandOfCards hand14 = new HandOfCards(d);
		hand14.hand.clear();
		hand14.hand.add(hearts.get(8));
		hand14.hand.add(clubs.get(7));
		hand14.hand.add(spades.get(6));
		hand14.hand.add(clubs.get(5));
		hand14.hand.add(diamonds.get(4));
		hand14.sort();
		System.out.println("Hand14: " + hand14.toString() + "\nValue:" + hand14.getGameValue() + "\n");

		
		// Test hands with a flush. Hand16 should be worth more than hand15.
		System.out.println("------------------------------------------");
		System.out.println("Flush Tests");
		System.out.println("Proposition: Hand16 > Hand15");
		System.out.println("------------------------------------------");
		d.reset();
		// hand15: JH 2H 5H 3H 9H
		HandOfCards hand15 = new HandOfCards(d);
		hand15.hand.clear();
		hand15.hand.add(hearts.get(9));
		hand15.hand.add(hearts.get(0));
		hand15.hand.add(hearts.get(3));
		hand15.hand.add(hearts.get(1));
		hand15.hand.add(hearts.get(7));
		hand15.sort();
		System.out.println("Hand15: " + hand15.toString() + "\nValue:" + hand15.getGameValue() + "\n");

		// hand16: QC 9C 3C 2C 6C
		HandOfCards hand16 = new HandOfCards(d);
		hand16.hand.clear();
		hand16.hand.add(clubs.get(10));
		hand16.hand.add(clubs.get(7));
		hand16.hand.add(clubs.get(1));
		hand16.hand.add(clubs.get(0));
		hand16.hand.add(clubs.get(4));
		hand16.sort();
		System.out.println("Hand16: " + hand16.toString() + "\nValue:" + hand16.getGameValue() + "\n");

		
		// Test hands with a full house. Hand17 should be worth more than hand18
		System.out.println("------------------------------------------");
		System.out.println("Full House Tests");
		System.out.println("Proposition: Hand17 > Hand18");
		System.out.println("------------------------------------------");
		d.reset();
		// hand17: JH JC JS 10C 10D
		HandOfCards hand17 = new HandOfCards(d);
		hand17.hand.clear();
		hand17.hand.add(hearts.get(9));
		hand17.hand.add(clubs.get(9));
		hand17.hand.add(spades.get(9));
		hand17.hand.add(clubs.get(8));
		hand17.hand.add(diamonds.get(8));
		hand17.sort();
		System.out.println("Hand17: " + hand17.toString() + "\nValue:" + hand17.getGameValue() + "\n");

		// hand18: 10H 10C 10S AC AD
		HandOfCards hand18 = new HandOfCards(d);
		hand18.hand.clear();
		hand18.hand.add(hearts.get(8));
		hand18.hand.add(clubs.get(8));
		hand18.hand.add(spades.get(8));
		hand18.hand.add(clubs.get(12));
		hand18.hand.add(diamonds.get(12));
		hand18.sort();
		System.out.println("Hand18: " + hand18.toString() + "\nValue:" + hand18.getGameValue() + "\n");

		
		// Test hands with a straight flush. Hand19 should be worth more than hand20
		System.out.println("------------------------------------------");
		System.out.println("Straight Flush Tests");
		System.out.println("Proposition: Hand19 > Hand20");
		System.out.println("------------------------------------------");
		d.reset();
		// hand19: JH 10H 9H 8H 7H
		HandOfCards hand19 = new HandOfCards(d);
		hand19.hand.clear();
		hand19.hand.add(hearts.get(9));
		hand19.hand.add(hearts.get(8));
		hand19.hand.add(hearts.get(7));
		hand19.hand.add(hearts.get(6));
		hand19.hand.add(hearts.get(5));
		hand19.sort();
		System.out.println("Hand19: " + hand19.toString() + "\nValue:" + hand19.getGameValue() + "\n");

		// hand20: 10H 9H 8H 7H 6H
		HandOfCards hand20 = new HandOfCards(d);
		hand20.hand.clear();
		hand20.hand.add(hearts.get(8));
		hand20.hand.add(hearts.get(7));
		hand20.hand.add(hearts.get(6));
		hand20.hand.add(hearts.get(5));
		hand20.hand.add(hearts.get(4));
		hand20.sort();
		System.out.println("Hand20: " + hand20.toString() + "\nValue:" + hand20.getGameValue() + "\n");

		
		// Test hands with a straight flush. Hand21 should be worth more than all previous hands.
		System.out.println("------------------------------------------");
		System.out.println("Royal Flush Tests");
		System.out.println("Proposition: Hand21 > ALL");
		System.out.println("------------------------------------------");
		d.reset();
		// hand21: AH KH QH JH 10H
		HandOfCards hand21 = new HandOfCards(d);
		hand21.hand.clear();
		hand21.hand.add(hearts.get(12));
		hand21.hand.add(hearts.get(11));
		hand21.hand.add(hearts.get(10));
		hand21.hand.add(hearts.get(9));
		hand21.hand.add(hearts.get(8));
		hand21.sort();
		System.out.println("Hand21: " + hand21.toString() + "\nValue:" + hand21.getGameValue() + "\n");
	}
}
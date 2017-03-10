package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HandOfCards {

	/*
	 * The maximum value a hand can be, subtracted from the previous tier's
	 * default value, is approximately 570,000. Since this isn't a round number,
	 * I have set the default value intervals at 1,000,000 in order to avoid
	 * interval values such as 1,970,000. Having round numbers makes it much
	 * clearer which tier a hand belongs to.
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
	public static final int HAND_SIZE = 5;
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
		if (hand.get(0).getGameValue() == PlayingCard.ACE_GAME_VALUE
				&& hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE)
			lowValueRun = true;

		if (lowValueRun) {
			return ((hand.get(1).getGameValue() - hand.get(2).getGameValue())
					* (hand.get(2).getGameValue() - hand.get(3).getGameValue())
					* (hand.get(3).getGameValue() - hand.get(4).getGameValue()) == 1)
					&& hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE;
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

	private int getHighHandValue() {
		int handValue = HandOfCards.HIGH_CARD_DEFAULT;
		handValue += Math.pow(hand.get(0).getGameValue(), 5);
		handValue += Math.pow(hand.get(1).getGameValue(), 4);
		handValue += Math.pow(hand.get(2).getGameValue(), 3);
		handValue += Math.pow(hand.get(3).getGameValue(), 2);
		handValue += hand.get(0).getGameValue();
		return handValue;
	}

	// Weight the pair, then high card, then next highest card and so on.
	private int getOnePairValue() {
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

	// Weight the highest value pair, then the other pair, then the kicker
	// (remaining card).
	private int getTwoPairValue() {
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

	// Get the highest value card in the straight and add it to the default
	// value.
	private int getStraightValue() {
		int handValue = 0;

		// If the hand contains an ace - determine whether it is an ace low
		// or ace high.
		// If it is an ace low, then get the next highest card in the hand.
		if (hand.get(0).getGameValue() == PlayingCard.ACE_GAME_VALUE) {
			if (hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE) {
				handValue = 4;
			} else {
				handValue = 14;
			}
		} else {
			handValue = hand.get(0).getGameValue();
		}

		return handValue + HandOfCards.STRAIGHT_DEFAULT;
	}

	// Weight the three of a kind over the two pair.
	private int getFullHouseValue() {
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
	private int getStraightFlushValue() {
		boolean containsAce = false;
		int handValue = 0;

		if (hand.get(0).getGameValue() == PlayingCard.ACE_GAME_VALUE) {
			containsAce = true;
		}

		if (containsAce) {
			if (hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE) {
				handValue = 4;
			} else {
				handValue = 14;
			}
		} else {
			handValue = hand.get(0).getGameValue();
		}

		return handValue + HandOfCards.STRAIGHT_FLUSH_DEFAULT;
	}

	public int getGameValue() {
		// Weight the highest card over the next highest card and so on.
		if (isHighHand()) {
			return getHighHandValue();
		}

		if (isOnePair()) {
			return getOnePairValue();
		}

		if (isTwoPair()) {
			return getTwoPairValue();
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

		if (isStraight()) {
			return getStraightValue();
		}

		// The same principle as high hand.
		if (isFlush()) {
			return HandOfCards.FLUSH_DEFAULT + getHighHandValue();
		}

		if (isFullHouse()) {
			return getFullHouseValue();
		}

		if (isStraightFlush()) {
			return getStraightFlushValue();
		}

		// All royal flushes are the same (disregarding suit) so just add 10,000
		// to the default value.
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
		if ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(2).getGameValue() == hand.get(3).getGameValue())) {
			return hand.get(0).getGameValue() != hand.get(2).getGameValue();
		}
		

		if ((hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(3).getGameValue() == hand.get(4).getGameValue())) {
			return hand.get(0).getGameValue() != hand.get(3).getGameValue();
		}

		if (hand.get(1).getGameValue() == hand.get(2).getGameValue()
				&& hand.get(3).getGameValue() == hand.get(4).getGameValue())
			return hand.get(1).getGameValue() != hand.get(3).getGameValue();

		return false;
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
		return sameSuit() && isRun() && hand.get(0).getGameValue() == PlayingCard.ACE_GAME_VALUE;
	}

	private int findCardIndexInHandByGameValue(int gameValue, int offset) {
		for (int i = offset; i < HAND_SIZE; i++) {
			if (hand.get(i).getGameValue() == gameValue) {
				return i;
			}
		}
		return -1;
	}

	private boolean handContainsCardByGameValue(int gameValue) {
		if (gameValue < 2 || gameValue > 14)
			return false;
		for (int i = 0; i < HAND_SIZE; i++) {
			if (hand.get(i).getGameValue() == gameValue) {
				return true;
			}
		}
		return false;
	}

	// Determine how close a hand is to becoming a straight.
	private int determineProximityToStraight() {
		// A hand which is not a straight, is at most 4 cards away from becoming
		// a straight.
		final int PROXIMITY_TO_STRAIGHT = 4;
		int proximity = PROXIMITY_TO_STRAIGHT;
		boolean containsAce = false;
		int lastCardValue = hand.get(0).getGameValue();

		if (lastCardValue == PlayingCard.ACE_GAME_VALUE)
			containsAce = true;

		// Edge case: determines if a hand is close to an ace low straight.
		if (containsAce) {
			if (hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE
					&& hand.get(3).getGameValue() == PlayingCard.THREE_GAME_VALUE
					&& hand.get(2).getGameValue() == PlayingCard.FOUR_GAME_VALUE) {
				return 1;
			} else if (hand.get(4).getGameValue() == PlayingCard.TWO_GAME_VALUE
					&& hand.get(3).getGameValue() == PlayingCard.FOUR_GAME_VALUE
					&& hand.get(2).getGameValue() == PlayingCard.FIVE_GAME_VALUE) {
				return 1;
			} else if (hand.get(4).getGameValue() == PlayingCard.THREE_GAME_VALUE
					&& hand.get(3).getGameValue() == PlayingCard.FOUR_GAME_VALUE
					&& hand.get(2).getGameValue() == PlayingCard.FIVE_GAME_VALUE) {
				return 1;
			}
		}

		for (int i = 1; i < HAND_SIZE; i++) {
			if (findCardIndexInHandByGameValue(lastCardValue - 1, i) != -1) {
				proximity--;
			} else {
				if (findCardIndexInHandByGameValue(lastCardValue - 2, i) != -1) {
					proximity--;
					if (proximity <= 2)
						break;
				}
			}
			if (lastCardValue - hand.get(i).getGameValue() > 3) {
				if (proximity <= 2)
					break;
				else {
					proximity++;
					if (proximity > PROXIMITY_TO_STRAIGHT)
						proximity = PROXIMITY_TO_STRAIGHT;
				}
			}
			lastCardValue = hand.get(i).getGameValue();
		}

		// Check for 3 off straight.
		if (proximity > 2) {
			for (int i = 0; i < HAND_SIZE; i++) {
				for (int a = -4; a < 5; a++) {
					if (handContainsCardByGameValue(hand.get(i).getGameValue() + a))
						return 3;
				}
			}
		}

		return proximity;
	}

	// Count the number of times a suit occurs in a hand.
	// If the suit occurs 3 or more times then return a string of two
	// characters, otherwise return null.
	// The first character represents the suit and the second character is a
	// number representing the number of times that suit occurs in the hand.
	private String countSuitFrequency() {
		int heartsFrequency = 0;
		int diamondsFrequency = 0;
		int clubsFrequency = 0;
		int spadesFrequency = 0;
		for (int i = 0; i < HAND_SIZE; i++) {
			switch (hand.get(i).getSuit()) {
			case PlayingCard.DIAMONDS:
				diamondsFrequency++;
				break;
			case PlayingCard.HEARTS:
				heartsFrequency++;
				break;
			case PlayingCard.SPADES:
				spadesFrequency++;
				break;
			case PlayingCard.CLUBS:
				clubsFrequency++;
				break;
			}
		}

		if (diamondsFrequency == HAND_SIZE - 1 || diamondsFrequency == HAND_SIZE - 2
				|| diamondsFrequency == HAND_SIZE - 3) {
			return "" + PlayingCard.DIAMONDS + diamondsFrequency;
		} else if (heartsFrequency == HAND_SIZE - 1 || heartsFrequency == HAND_SIZE - 2
				|| heartsFrequency == HAND_SIZE - 3) {
			return "" + PlayingCard.HEARTS + heartsFrequency;
		} else if (clubsFrequency == HAND_SIZE - 1 || clubsFrequency == HAND_SIZE - 2
				|| clubsFrequency == HAND_SIZE - 3) {
			return "" + PlayingCard.CLUBS + clubsFrequency;
		} else if (spadesFrequency == HAND_SIZE - 1 || spadesFrequency == HAND_SIZE - 2
				|| spadesFrequency == HAND_SIZE - 3) {
			return "" + PlayingCard.SPADES + spadesFrequency;
		} else
			return null;
	}

	// Find which cards should be discarded in a broken straight.
	// If the broken straight is one off a straight then the problem card should
	// receive the oneOffProbability.
	// If the broken straight is two off a straight then the problem card should
	// receive the twoOffProbability.
	// A broken straight which is three off a straight is too unlikely to be
	// obtained by trading in 3 cards.
	private int findProblemCardsInBrokenStraight(int cardPosition, int oneOffProbability, int twoOffProbability) {
		int straightProximity = determineProximityToStraight();
		if (straightProximity <= 2) {
			if (straightProximity == 1) {
				int lastCardValue = hand.get(0).getGameValue();

				int problemCardIndex = -1;
				for (int i = 0; i < HAND_SIZE - 1; i++) {
					if (!(findCardIndexInHandByGameValue(lastCardValue - 1, i + 1) != -1
							|| findCardIndexInHandByGameValue(lastCardValue - 2, i + 1) != -1)) {
						problemCardIndex = i + 1;
						break;
					}
					lastCardValue = hand.get(i + 1).getGameValue();
				}
				if (problemCardIndex == cardPosition)
					return oneOffProbability;
				else
					return 0;
			} else if (straightProximity == 2) {
				int firstHalfOfHandDifference = hand.get(0).getGameValue() - hand.get(1).getGameValue()
						+ hand.get(1).getGameValue() - hand.get(2).getGameValue();
				int secondHalfOfHandDifference = hand.get(2).getGameValue() - hand.get(3).getGameValue()
						+ hand.get(3).getGameValue() - hand.get(4).getGameValue();

				if (firstHalfOfHandDifference > secondHalfOfHandDifference) {
					if (cardPosition == 0 || cardPosition == 1) {
						return twoOffProbability;
					} else
						return 0;
				} else {
					if (cardPosition == 3 || cardPosition == 4) {
						return twoOffProbability;
					} else
						return 0;
				}
			} else
				return 0;
		} else
			return 0;
	}

	// Find the cards in a high hand which should be discarded in order to
	// potentially improve ones hand.
	private int getHighHandDiscardProbability(int cardPosition) {
		// High hand doesn't calculate odds of getting a better hand since the
		// player should always attempt to obtain one.
		String suitFrequency = countSuitFrequency();
		int straightProximity = determineProximityToStraight();

		// Check if it is possible to obtain a flush from the current hand. If
		// so then determine which
		// cards should be discarded. Return a non-zero for these cards and 0
		// for the remaining cards.
		if (suitFrequency != null) {
			if (suitFrequency.equals("D4")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.DIAMONDS)
					return 100;
				else
					return 0;
			} else if (suitFrequency.equals("D3")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.DIAMONDS)
					return 50;
				else
					return 0;
			} else if (suitFrequency.equals("D2") && straightProximity > 2) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.DIAMONDS)
					return 33;
				else
					return 0;
			}

			if (suitFrequency.equals("C4")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.CLUBS)
					return 100;
				else
					return 0;
			} else if (suitFrequency.equals("C3")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.CLUBS)
					return 50;
				else
					return 0;
			} else if (suitFrequency.equals("C2") && straightProximity > 2) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.CLUBS)
					return 33;
				else
					return 0;
			}

			if (suitFrequency.equals("S4")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.SPADES)
					return 100;
				else
					return 0;
			} else if (suitFrequency.equals("S3")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.SPADES)
					return 50;
				else
					return 0;
			} else if (suitFrequency.equals("S2") && straightProximity > 2) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.SPADES)
					return 33;
				else
					return 0;
			}

			if (suitFrequency.equals("H4")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.HEARTS)
					return 100;
				else
					return 0;
			} else if (suitFrequency.equals("H3")) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.HEARTS)
					return 50;
				else
					return 0;
			} else if (suitFrequency.equals("H2") && straightProximity > 2) {
				if (hand.get(cardPosition).getSuit() != PlayingCard.HEARTS)
					return 33;
				else
					return 0;
			}
		}

		// Determine whether it is possible to obtain a straight from the
		// current hand. If it is possible
		// find the cards which should be discarded. Return a non-zero for these
		// cards and zero for the other cards.
		if (straightProximity <= 2) {
			// Not using calculations because a player with a high hand should
			// 'always' go for something better.
			return findProblemCardsInBrokenStraight(cardPosition, 80, 50);
		} else {
			// If the hand is not close to a straight or a flush then discard
			// the two lowest cards.
			if (cardPosition == 3)
				return 50;
			if (cardPosition == 4)
				return 50;
		}

		return 0;
	}

	// Method to find which cards should be discarded (if any) if the hand is a
	// one pair.
	// The one pair is quite a common hand to obtain or be dealt (~50% chance)
	// so if the hand
	// is close to a flush or straight then attempt to acquire one of these
	// hands (even if it
	// means discarding the pair). If the one pair cannot be upgraded to a flush
	// or straight
	// then discard the two lowest value non-pair cards.
	private int getOnePairDiscardProbability(int cardPosition) {
		String suitFrequency = countSuitFrequency();
		
		if (suitFrequency.charAt(1) == 4 && hand.get(cardPosition).getSuit() == suitFrequency.charAt(0)) {
			return 100;
		}
		else if (suitFrequency.charAt(1) == 4) {
			return 0;
		}
		else {
			// If the one pair is not close to a straight/flush then discard the two lowest non-pair cards.
			int index = findPairStartIndex(0);

			if (index == 0) {
				if (cardPosition == 4) {
					return 75;
				} else if (cardPosition == 3) {
					return 50;
				} else if (cardPosition == 2 && hand.get(2).getGameValue() < 8) {
					// If the highest value card in your hand is less than average
					// discard that too.
					return 100;
				} else {
					return 0;
				}
			} else if (index == 1) {
				if (cardPosition == 4) {
					return 75;
				} else if (cardPosition == 3) {
					return 50;
				} else if (cardPosition == 0 && hand.get(2).getGameValue() < 8) {
					// If the highest value card in your hand is less than average
					// discard that too.
					return 100;
				} else {
					return 0;
				}
			} else if (index == 2) {
				if (cardPosition == 4) {
					return 75;
				} else if (cardPosition == 1) {
					return 50;
				} else if (cardPosition == 0 && hand.get(2).getGameValue() < 8) {
					// If the highest value card in your hand is less than average
					// discard that too.
					return 100;
				} else {
					return 0;
				}
			} else if (index == 3) {
				if (cardPosition == 2) {
					return 75;
				} else if (cardPosition == 1) {
					return 50;
				} else if (cardPosition == 0 && hand.get(2).getGameValue() < 8) {
					// If the highest value card in your hand is less than average
					// discard that too.
					return 100;
				} else {
					return 0;
				}
			} else
				return 0;
		}
	}

	// Determine which cards should be discarded in a two pair.
	private int getTwoPairDiscardProbability(int cardPosition) {
		int index1 = findPairStartIndex(0);
		int index2 = findPairStartIndex(index1 + 1);

		// Find the non-pair card and discard it.
		if (cardPosition != index1 && cardPosition != index1 + 1 && cardPosition != index2
				&& cardPosition != index2 + 1) {
			return 100;
		} else
			return 0;
	}

	// Determine which cards should be discarded for a three of a kind hand.
	public int getThreeOfAKindDiscardProbability(int cardPosition) {
		// Find where in the hand the three of a kind is located.
		boolean start, middle, end;
		start = middle = end = false;

		if (hand.get(0).getGameValue() == hand.get(1).getGameValue()
				&& hand.get(1).getGameValue() == hand.get(2).getGameValue()) {
			start = true;
		} else if (hand.get(1).getGameValue() == hand.get(2).getGameValue()
				&& hand.get(2).getGameValue() == hand.get(3).getGameValue()) {
			middle = true;
		} else if (hand.get(2).getGameValue() == hand.get(3).getGameValue()
				&& hand.get(3).getGameValue() == hand.get(4).getGameValue()) {
			end = true;
		}

		// Discard the non-pair cards. This cannot ruin the three of a kind but
		// can only improve the hand by getting a four of a kind or full house.
		if (start && (cardPosition == 3 || cardPosition == 4)) {
			return 100;
		} else if (middle && (cardPosition == 0 || cardPosition == 4)) {
			return 100;
		} else if (end && (cardPosition == 0 || cardPosition == 1)) {
			return 100;
		} else {
			return 0;
		}
	}

	// Determine which cards (if any) should be discarded in a four of a kind
	// hand.
	private int getFourOfAKindDiscardProbability(int cardPosition) {
		// Discard the non-pair card always. Not discarding a card could appear
		// suspicious.
		if (hand.get(0).getGameValue() != hand.get(1).getGameValue() && cardPosition == 0) {
			return 100;
		} else if (hand.get(3).getGameValue() != hand.get(4).getGameValue() && cardPosition == 4) {
			return 100;
		} else
			return 0;
	}

	public int getDiscardProbability(int cardPosition) {
		if (isHighHand()) {
			return getHighHandDiscardProbability(cardPosition);
		}

		else if (isOnePair()) {
			return getOnePairDiscardProbability(cardPosition);
		}

		else if (isTwoPair()) {
			return getTwoPairDiscardProbability(cardPosition);
		}

		else if (isThreeOfAKind()) {
			return getThreeOfAKindDiscardProbability(cardPosition);
		}

		else if (isStraight()) {
			// The probability of a player holding a straight in 5 card draw is
			// 0.76%.
			// The odds of two player receiving this hand in a single turn is
			// highly unlikely.
			// Therefore I think it best that the bot/player discards nothing.

			return 0;
		}

		else if (isFlush()) {
			// The probability of a player holding a flush in 5 card draw is
			// 0.367%.
			// The odds of two player receiving this hand in a single turn is
			// highly unlikely.
			// Therefore I think it best that the bot/player discards nothing.

			return 0;
		}

		else if (isFullHouse()) {
			// The probability of a player holding a full house in 5 card draw
			// is 0.17%.
			// The odds of two player receiving this hand in a single turn is
			// highly unlikely.
			// Therefore I think it best that the bot/player discards nothing.

			return 0;
		}

		else if (isFourOfAKind()) {
			return getFourOfAKindDiscardProbability(cardPosition);
		}

		else if (isStraightFlush()) {
			// The probability of a player holding a straight flush in 5 card
			// draw is 0.0256%.
			// The odds of two player receiving this hand in a single turn is
			// highly unlikely.
			// Therefore I think it best that the bot/player discards nothing.

			return 0;
		}

		else if (isRoyalFlush()) {
			// There is no better hand than this so obviously discard no cards.

			return 0;
		}

		else
			return 0;
	}
	
	// Return the number of cards discarded at the beginning of a round. 
	// This function also handles discarding and adding new cards.
	public int discard() {
		int cardsToDiscard = 0;
		ArrayList<Integer> cardIndicesToDiscard = new ArrayList<Integer>();

		// Find which cards have a discardProbability greater than 1. 
		// Add their positions in the hand to an ArrayList cardIndicesToDiscard.
		// Start from the highest index in the ArrayList to avoid out of bounds exceptions.
		for (int i = HAND_SIZE - 1; i >= 0; i--) {
			if (cardsToDiscard == 3)
				break;
			if (getDiscardProbability(i) > 0) {
				cardIndicesToDiscard.add(i);
				cardsToDiscard++;
			}
		}
		
		// Return each card marked in the cardIndicesToDiscard ArrayList to the deck.
		for (int i = 0; i < cardsToDiscard; i++) {
			PlayingCard p = hand.get(cardIndicesToDiscard.get(i));
			hand.remove(p);
			deck.returnCard(p);
		}
		
		// Deal a new card to the hand.
		for (int i = 0; i < cardsToDiscard; i++) {
			hand.add(deck.dealNext());
		}
		
		// Resort the hand after adding new cards.
		sort();
		
		return cardsToDiscard;
	}

	// Returns a string with each card in the hand separated by a space.
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
		for (int i = 0; i < DeckOfCards.DECK_SIZE; i++) {
			PlayingCard card = d.dealNext();
			switch (card.getSuit()) {
			case PlayingCard.HEARTS:
				hearts.add(card);
				break;
			case PlayingCard.SPADES:
				spades.add(card);
				break;
			case PlayingCard.DIAMONDS:
				diamonds.add(card);
				break;
			case PlayingCard.CLUBS:
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
		for (int i = 8; i < DeckOfCards.DECK_SIZE / DeckOfCards.NUMBER_OF_SUITS; i++) {
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

		// Test hands with three of a kind. Hand9 should be worth more than
		// hand10.
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

		// Test hands with four of a kind. Hand11 should be worth more than
		// hand12.
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

		// Test hands with a straight flush. Hand19 should be worth more than
		// hand20
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

		// Test hands with a straight flush. Hand21 should be worth more than
		// all previous hands.
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

		// Display a break between handValue testing and discard testing.
		System.out.println();
		for (int i = 0; i < 6; i++) {
			System.out.println("-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-");
		}
		System.out.println();
		System.out.println("\n----------- Discard Testing -----------\n");

		System.out.println("Broken ace high straight off by 1");
		System.out.println("Card\tDiscard Probability");
		ArrayList<Integer> probs = new ArrayList<Integer>();
		d.reset();
		HandOfCards handDiscard1 = new HandOfCards(d);
		handDiscard1.hand.clear();
		handDiscard1.hand.add(spades.get(12));
		handDiscard1.hand.add(hearts.get(11));
		handDiscard1.hand.add(diamonds.get(2));
		handDiscard1.hand.add(hearts.get(9));
		handDiscard1.hand.add(clubs.get(8));
		handDiscard1.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard1.getDiscardProbability(i));
			System.out.println(handDiscard1.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken ace low straight off by 1");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard2 = new HandOfCards(d);
		handDiscard2.hand.clear();
		handDiscard2.hand.add(spades.get(12));
		handDiscard2.hand.add(hearts.get(0));
		handDiscard2.hand.add(diamonds.get(2));
		handDiscard2.hand.add(hearts.get(1));
		handDiscard2.hand.add(clubs.get(8));
		handDiscard2.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard2.getDiscardProbability(i));
			System.out.println(handDiscard2.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken low straight off by 2");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard3 = new HandOfCards(d);
		handDiscard3.hand.clear();
		handDiscard3.hand.add(spades.get(10));
		handDiscard3.hand.add(hearts.get(7));
		handDiscard3.hand.add(diamonds.get(3));
		handDiscard3.hand.add(hearts.get(1));
		handDiscard3.hand.add(clubs.get(0));
		handDiscard3.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard3.getDiscardProbability(i));
			System.out.println(handDiscard3.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken high straight off by 2");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard4 = new HandOfCards(d);
		handDiscard4.hand.clear();
		handDiscard4.hand.add(spades.get(11));
		handDiscard4.hand.add(hearts.get(9));
		handDiscard4.hand.add(diamonds.get(7));
		handDiscard4.hand.add(hearts.get(3));
		handDiscard4.hand.add(clubs.get(0));
		handDiscard4.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard4.getDiscardProbability(i));
			System.out.println(handDiscard4.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken flush off by 1");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard5 = new HandOfCards(d);
		handDiscard5.hand.clear();
		handDiscard5.hand.add(spades.get(11));
		handDiscard5.hand.add(spades.get(9));
		handDiscard5.hand.add(spades.get(7));
		handDiscard5.hand.add(spades.get(3));
		handDiscard5.hand.add(clubs.get(0));
		handDiscard5.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard5.getDiscardProbability(i));
			System.out.println(handDiscard5.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken flush off by 2");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard6 = new HandOfCards(d);
		handDiscard6.hand.clear();
		handDiscard6.hand.add(spades.get(11));
		handDiscard6.hand.add(spades.get(9));
		handDiscard6.hand.add(spades.get(7));
		handDiscard6.hand.add(hearts.get(3));
		handDiscard6.hand.add(clubs.get(0));
		handDiscard6.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard6.getDiscardProbability(i));
			System.out.println(handDiscard6.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Broken straight/flush off by 3. Should prioritise flush.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard7 = new HandOfCards(d);
		handDiscard7.hand.clear();
		handDiscard7.hand.add(diamonds.get(11));
		handDiscard7.hand.add(spades.get(9));
		handDiscard7.hand.add(spades.get(1));
		handDiscard7.hand.add(hearts.get(6));
		handDiscard7.hand.add(clubs.get(0));
		handDiscard7.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard7.getDiscardProbability(i));
			System.out.println(handDiscard7.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("One Pair - occasionally discard the 2 least valuable, non-pair cards.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard8 = new HandOfCards(d);
		handDiscard8.hand.clear();
		handDiscard8.hand.add(diamonds.get(11));
		handDiscard8.hand.add(spades.get(9));
		handDiscard8.hand.add(spades.get(1));
		handDiscard8.hand.add(hearts.get(0));
		handDiscard8.hand.add(clubs.get(0));
		handDiscard8.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard8.getDiscardProbability(i));
			System.out.println(handDiscard8.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("One Pair - close to a straight");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard9 = new HandOfCards(d);
		handDiscard9.hand.clear();
		handDiscard9.hand.add(diamonds.get(4));
		handDiscard9.hand.add(spades.get(3));
		handDiscard9.hand.add(spades.get(2));
		handDiscard9.hand.add(hearts.get(1));
		handDiscard9.hand.add(clubs.get(1));
		handDiscard9.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard9.getDiscardProbability(i));
			System.out.println(handDiscard9.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Two Pair. If the kicker is below average then discard it.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard10 = new HandOfCards(d);
		handDiscard10.hand.clear();
		handDiscard10.hand.add(diamonds.get(11));
		handDiscard10.hand.add(spades.get(11));
		handDiscard10.hand.add(spades.get(1));
		handDiscard10.hand.add(hearts.get(0));
		handDiscard10.hand.add(clubs.get(0));
		handDiscard10.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard10.getDiscardProbability(i));
			System.out.println(handDiscard10.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Two Pair. If the kicker is below average then discard it.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard11 = new HandOfCards(d);
		handDiscard11.hand.clear();
		handDiscard11.hand.add(diamonds.get(11));
		handDiscard11.hand.add(spades.get(1));
		handDiscard11.hand.add(spades.get(1));
		handDiscard11.hand.add(hearts.get(0));
		handDiscard11.hand.add(clubs.get(0));
		handDiscard11.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard11.getDiscardProbability(i));
			System.out.println(handDiscard11.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Three of a Kind. Occasionally discard remaining cards.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard12 = new HandOfCards(d);
		handDiscard12.hand.clear();
		handDiscard12.hand.add(diamonds.get(11));
		handDiscard12.hand.add(spades.get(1));
		handDiscard12.hand.add(spades.get(9));
		handDiscard12.hand.add(hearts.get(1));
		handDiscard12.hand.add(clubs.get(1));
		handDiscard12.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard12.getDiscardProbability(i));
			System.out.println(handDiscard12.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Four of a Kind. Occasionally discard remaining card.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard13 = new HandOfCards(d);
		handDiscard13.hand.clear();
		handDiscard13.hand.add(diamonds.get(1));
		handDiscard13.hand.add(spades.get(1));
		handDiscard13.hand.add(spades.get(9));
		handDiscard13.hand.add(hearts.get(1));
		handDiscard13.hand.add(clubs.get(1));
		handDiscard13.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard13.getDiscardProbability(i));
			System.out.println(handDiscard13.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Straight.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard14 = new HandOfCards(d);
		handDiscard14.hand.clear();
		handDiscard14.hand.add(diamonds.get(1));
		handDiscard14.hand.add(spades.get(2));
		handDiscard14.hand.add(spades.get(3));
		handDiscard14.hand.add(hearts.get(4));
		handDiscard14.hand.add(clubs.get(5));
		handDiscard14.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard14.getDiscardProbability(i));
			System.out.println(handDiscard14.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Flush.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard15 = new HandOfCards(d);
		handDiscard15.hand.clear();
		handDiscard15.hand.add(diamonds.get(1));
		handDiscard15.hand.add(diamonds.get(4));
		handDiscard15.hand.add(diamonds.get(8));
		handDiscard15.hand.add(diamonds.get(12));
		handDiscard15.hand.add(diamonds.get(0));
		handDiscard15.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard15.getDiscardProbability(i));
			System.out.println(handDiscard15.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Full House.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard16 = new HandOfCards(d);
		handDiscard16.hand.clear();
		handDiscard16.hand.add(diamonds.get(1));
		handDiscard16.hand.add(spades.get(1));
		handDiscard16.hand.add(clubs.get(1));
		handDiscard16.hand.add(hearts.get(9));
		handDiscard16.hand.add(diamonds.get(9));
		handDiscard16.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard16.getDiscardProbability(i));
			System.out.println(handDiscard16.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Straight Flush.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard17 = new HandOfCards(d);
		handDiscard17.hand.clear();
		handDiscard17.hand.add(diamonds.get(1));
		handDiscard17.hand.add(diamonds.get(2));
		handDiscard17.hand.add(diamonds.get(3));
		handDiscard17.hand.add(diamonds.get(4));
		handDiscard17.hand.add(diamonds.get(5));
		handDiscard17.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard17.getDiscardProbability(i));
			System.out.println(handDiscard17.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Royal Flush.");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard18 = new HandOfCards(d);
		handDiscard18.hand.clear();
		handDiscard18.hand.add(diamonds.get(12));
		handDiscard18.hand.add(diamonds.get(11));
		handDiscard18.hand.add(diamonds.get(10));
		handDiscard18.hand.add(diamonds.get(9));
		handDiscard18.hand.add(diamonds.get(8));
		handDiscard18.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard18.getDiscardProbability(i));
			System.out.println(handDiscard18.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Three off Flush");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard19 = new HandOfCards(d);
		handDiscard19.hand.clear();
		handDiscard19.hand.add(diamonds.get(12));
		handDiscard19.hand.add(diamonds.get(10));
		handDiscard19.hand.add(hearts.get(6));
		handDiscard19.hand.add(clubs.get(3));
		handDiscard19.hand.add(spades.get(0));
		handDiscard19.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard19.getDiscardProbability(i));
			System.out.println(handDiscard19.hand.get(i) + "\t" + probs.get(i));
		}

		System.out.println("\n");
		System.out.println("Weird high hand");
		System.out.println("Card\tDiscard Probability");
		probs.clear();
		d.reset();
		HandOfCards handDiscard20 = new HandOfCards(d);
		handDiscard20.hand.clear();
		handDiscard20.hand.add(diamonds.get(8));
		handDiscard20.hand.add(diamonds.get(7));
		handDiscard20.hand.add(hearts.get(7));
		handDiscard20.hand.add(clubs.get(7));
		handDiscard20.hand.add(spades.get(5));
		handDiscard20.sort();
		for (int i = 0; i < HAND_SIZE; i++) {
			probs.add(handDiscard20.getDiscardProbability(i));
			System.out.println(handDiscard20.hand.get(i) + "\t" + probs.get(i));
		}
	}
}
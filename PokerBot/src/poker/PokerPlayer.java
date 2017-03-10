package poker;

public class PokerPlayer {
	
	// The discard function is in the HandOfCards class. I feel it belongs in that class
	// more than it belongs in this one. It is also easier to implement in that class.

	public static void main(String[] args) {
		DeckOfCards d = new DeckOfCards();
		PokerPlayer p = new PokerPlayer(d);
		boolean highHand = false, onePair = false, twoPair = false, threeOfAKind = false;
		
		/*
		 * The content of the hands is not being tested anymore. This simply checks that
		 * cards are discarded safely and replaced with another card from the deck. High hand,
		 * one pair, two pair and three of a kind are the only types of hands tested because for
		 * the other hands no cards are discarded.
		 */
		for (int i = 0; i < 10; i++) {
			System.out.println("\n------------------------------");
			while (!(highHand && onePair && twoPair && threeOfAKind)) {
				p.dealHand();
				
				if (!highHand && p.hand.isHighHand()) {
					System.out.println("\nHigh Hand");
					System.out.println("Before: " + p.hand);
					System.out.println("Discard " + p.hand.discard() + " cards.");
					System.out.println("After: " + p.hand);
					highHand = true;
					continue;
				}
				
				if (!onePair && p.hand.isOnePair()) {
					System.out.println("\nOne Pair");
					System.out.println("Before: " + p.hand);
					System.out.println("Discard " + p.hand.discard() + " cards.");
					System.out.println("After: " + p.hand);
					onePair = true;
					continue;
				}
				
				if (!twoPair && p.hand.isTwoPair()) {
					System.out.println("\nTwo Pair");
					System.out.println("Before: " + p.hand);
					System.out.println("Discard " + p.hand.discard() + " cards.");
					System.out.println("After: " + p.hand);
					twoPair = true;
					continue;
				}
				
				if (!threeOfAKind && p.hand.isThreeOfAKind()) {
					System.out.println("\nThree of a Kind");
					System.out.println("Before: " + p.hand);
					System.out.println("Discard " + p.hand.discard() + " cards.");
					System.out.println("After: " + p.hand);
					threeOfAKind = true;
					continue;
				}
				
				d.reset();
			}
			System.out.println("------------------------------");
			highHand = false;
			onePair = false;
			twoPair = false;
			threeOfAKind = false;
		}
	}
	
	public PokerPlayer(DeckOfCards deck) {
		this.deck = deck;
	}
	
	// Deal a new hand for the player. Deal at round start, not on player creation.
	public void dealHand() {
		this.hand = new HandOfCards(deck);
	}
	
	private DeckOfCards deck;
	private HandOfCards hand;
}

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Card {
    private String rank;
    private String suit;
    private int value;

    public Card(String rank, String suit, int value) {
        this.rank = rank;
        this.suit = suit;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class Player {
    private String name;
    private ArrayList<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public int calculateHandValue() {
        int totalValue = 0;
        int numAces = 0;
        for (Card card : hand) {
            totalValue += card.getValue();
            if (card.getValue() == 11) {
                numAces++;
            }
        }
        while (totalValue > 21 && numAces > 0) {
            totalValue -= 10;
            numAces--;
        }
        return totalValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("'s hand:\n");
        for (Card card : hand) {
            builder.append(card.toString()).append("\n");
        }
        builder.append("Total hand value: ").append(calculateHandValue()).append("\n");
        return builder.toString();
    }
}

public class Blackjack {
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    private static final String[] SUITS = {"Clubs", "Diamonds", "Hearts", "Spades"};
    private static final int[] VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
    private static final Random RANDOM = new Random();
    private static final Scanner SCANNER = new Scanner(System.in);

    private static ArrayList<Card> deck;

    public static void main(String[] args) {
        System.out.println("Welcome to Blackjack!");
        System.out.println("The goal is to get as close to 21 without going over.");
        System.out.println("Each player will take turns to draw cards.");
        System.out.println("If your total goes over 21, you bust!");
        System.out.println("Let's begin!\n");

        Player player1 = new Player(getPlayerName());
        Player player2 = new Player(getPlayerName());
        Player dealer = new Player("Dealer");

        initializeDeck();
        dealInitialCards(player1, player2, dealer);

        playPlayerTurn(player1);
        playPlayerTurn(player2);

        if (player1.calculateHandValue() <= 21 || player2.calculateHandValue() <= 21) {
            playDealerTurn(dealer);
        }

        determineWinner(player1, player2, dealer);

        SCANNER.close();
    }

    private static String getPlayerName() {
        System.out.print("Enter player's name: ");
        return SCANNER.nextLine();
    }

    private static void initializeDeck() {
        deck = new ArrayList<>();
        for (String rank : RANKS) {
            for (String suit : SUITS) {
                int value = VALUES[getIndex(rank)];
                deck.add(new Card(rank, suit, value));
            }
        }
    }

    private static int getIndex(String rank) {
        switch (rank) {
            case "Jack":
                return 9;
            case "Queen":
                return 10;
            case "King":
                return 11;
            case "Ace":
                return 12;
            default:
                return Integer.parseInt(rank) - 2;
        }
    }

    private static void dealInitialCards(Player player1, Player player2, Player dealer) {
        deal(player1);
        deal(player1);
        deal(player2);
        deal(player2);
        deal(dealer);
        deal(dealer);

        System.out.println(player1);
        System.out.println(player2);
        System.out.println(dealer);
    }

    private static void deal(Player player) {
        int randomIndex = RANDOM.nextInt(deck.size());
        Card drawnCard = deck.remove(randomIndex);
        player.addCard(drawnCard);
    }

    private static void playPlayerTurn(Player player) {
        System.out.println(player.getName() + "'s turn:");
        int handValue = player.calculateHandValue();
        System.out.println(player);
        while (handValue <= 21 && hitOrStay(player)) {
            deal(player);
            System.out.println(player);
            handValue = player.calculateHandValue();
            if (handValue > 21) {
                System.out.println(player.getName() + " busts!");
                return;
            }
            System.out.println(player.getName() + ", Hit or Stay? (h/s): ");
        }
    }       

    private static boolean hitOrStay(Player player) {
        System.out.print(player.getName() + ", Hit or Stay? (h/s): ");
        String choice = SCANNER.nextLine().trim().toLowerCase();
        return choice.equals("h");
    }

    private static void playDealerTurn(Player dealer) {
        System.out.println("\nDealer's turn:");
        int dealerHandValue = dealer.calculateHandValue();
        while (dealerHandValue < 17) {
            deal(dealer);
            System.out.println(dealer);
            dealerHandValue = dealer.calculateHandValue();
        }
        System.out.println("Dealer's turn ends.");
    }

    private static void determineWinner(Player player1, Player player2, Player dealer) {
        int player1Value = player1.calculateHandValue();
        int player2Value = player2.calculateHandValue();
        int dealerValue = dealer.calculateHandValue();

        if ((player1Value <= 21 && player1Value == player2Value) || (player2Value <= 21 && player2Value == dealerValue)) {
            System.out.println("It's a tie!");
        } else if (player1Value > 21 && player2Value > 21) {
            System.out.println("Both players bust! Dealer wins.");
        } else if (dealerValue > 21) {
            if (player1Value <= 21 && player2Value <= 21) {
                System.out.println("Dealer busts! Both players win.");
            } else if (player1Value <= 21) {
                System.out.println("Dealer busts! " + player1.getName() + " wins.");
            } else if (player2Value <= 21) {
                System.out.println("Dealer busts! " + player2.getName() + " wins.");
            }
        } else {
            if (player1Value <= 21 && (player1Value > player2Value || player2Value > 21)) {
                System.out.println(player1.getName() + " wins!");
            } else if (player2Value <= 21 && (player2Value > player1Value || player1Value > 21)) {
                System.out.println(player2.getName() + " wins!");
            } else if (dealerValue > player1Value && dealerValue > player2Value) {
                System.out.println("Dealer wins.");
            } else if (dealerValue == player1Value && dealerValue == player2Value) {
                System.out.println("It's a tie!");
            } else if (dealerValue > player1Value && dealerValue > player2Value) {
                System.out.println("Dealer wins.");
            } else if (dealerValue > player1Value && dealerValue <= 21 && dealerValue <= player2Value) {
                System.out.println(player1.getName() + " loses to dealer.");
            } else if (dealerValue > player2Value && dealerValue <= 21 && dealerValue <= player1Value) {
                System.out.println(player2.getName() + " loses to dealer.");
            }
        }
    }
}
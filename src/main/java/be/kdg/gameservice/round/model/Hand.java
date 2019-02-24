package be.kdg.gameservice.round.model;

import be.kdg.gameservice.card.Card;
import be.kdg.gameservice.card.Rank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Hand implements Comparable<Hand> {
    private HandType handType;
    private List<Card> cards;
    private List<Integer> cardRankValue;

    private final static String ranks = "23456789TJQKA";

    public Hand(HandType handType, List<Card> cards) {
        this.cards = cards;
        this.handType = handType;
        this.sortCardRanks();
    }

    public void sortCards(List<Integer> cards) {
        // Declare and Initialize an array
        //int[] array = input;

        List<Integer> array = cards;

        Map<Integer, Integer> map = new HashMap<>();
        List<Integer> outputArray = new ArrayList<>();

        // Assign elements and their count in the list and map
        for (int current : array) {
            int count = map.getOrDefault(current, 0);
            map.put(current, count + 1);
            outputArray.add(current);
        }

        // Compare the map by value
        SortComparator comp = new SortComparator(map);

        // Sort the map using Collections CLass
        Collections.sort(outputArray, comp);

        // Final Output
        for (Integer i : outputArray) {
            System.out.print(i + " ");
        }
    }

    public void sortCardRanks() {
        List<String> cardRanks = cards.stream().map(c -> c.getType().getRank().getName()).collect(Collectors.toList());
        cardRankValue = cardRanks.stream().map(r -> ranks.indexOf(r)).collect(Collectors.toList());
        this.sortCards(cardRankValue);
    }


    @Override
    public int compareTo(Hand that) {
        int handTypeCompare = Integer.compare(this.handType.getScore(), that.handType.getScore());

        if(handTypeCompare == 0) {
            if(this.cards.size() == 5 && that.cards.size() == 5) {
                for(int i=0; i<this.cardRankValue.size(); i++) {
                    int cardCompare = this.cardRankValue.get(i).compareTo(that.cardRankValue.get(i));
                    if(cardCompare != 0) {
                        return cardCompare;
                    }
                }
            }
            return 0;
        }

        return handTypeCompare;
    }
}

package org.joshdb.ThinDFA;

import org.joshdb.ThinDFA;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses US Currency
 */
public class USCurrencyDFA extends ThinDFA
{

    private static final int numStates = 16;
    //Trove's crazy fast primitive hashmaps have numerous performance benefits
    //and as a bonus return 0 on failed lookup, which coincidentally is alway the state
    //we want to be in upon getting a character that does not map to some state transition
    /**
     * The data structure representing the states between which the Deterministic Finite Automaton
     * transitions as it is fed characters
     */
    private static final TCharIntHashMap[] states = new TCharIntHashMap[numStates];

    static
    {
        states[0] = new TCharIntHashMap();
        states[0].put('$', 3);
        states[0].put('U', 1);
        states[0].put('u', 1);

        states[1] = new TCharIntHashMap();
        states[1].put('S', 2);
        states[1].put('s', 2);

        states[2] = new TCharIntHashMap();
        states[2].put('D', 3);
        states[2].put('d', 3);
        //states[3].put('N', 4); //USN - US Next Day. It is ISO 4217, but ¯\_(ツ)_/¯
        //states[3].put('n', 4);

        initStateFromCharRange(states, 3, '1', '9', 5);
        states[3].put(' ', 4);

        initStateFromCharRange(states, 4, '1', '9', 5);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 5, -2);
        setRangeTransition(states, 5, '0', '9', 6);
        states[5].put(',', 8);
        states[5].put('.', 12);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 6, -2);
        setRangeTransition(states, 6, '0', '9', 7);
        states[6].put(',', 8);
        states[6].put('.', 12);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 7, -2);
        setRangeTransition(states, 7, '0', '9', 15);
        states[7].put(',', 8);
        states[7].put('.', 12);


        initStateFromCharRange(states, 8, '0', '9', 9);

        initStateFromCharRange(states, 9, '0', '9', 10);

        initStateFromCharRange(states, 10, '0', '9', 11);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 11, -2);
        states[11].put(',', 8);
        states[11].put('.', 12);

        initStateFromCharRange(states, 12, '0', '9', 13);

        initStateFromCharRange(states, 13, '0', '9', 14);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 14, -2);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 15, -2);
        setRangeTransition(states, 15, '0', '9', 15);
        states[15].put('.', 12);
    }

    public USCurrencyDFA()
    {
        super(states);
    }
}
package org.joshdb.ThinDFA;

import org.joshdb.ThinDFA;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses Social Security Numbers
 */
public class SSNDFA extends ThinDFA
{

    private static final int numStates = 12;
    //Trove's crazy fast primitive hashmaps have numerous performance benefits
    //and as a bonus return 0 on failed lookup, which coincidentally is alway the state
    //we want to be in upon getting a character that does not map to some state transition
    /**
     * The data structure representing the states between which the Deterministic Finite Automaton
     * transitions as it is fed characters
     */
    private static final TCharIntHashMap[] states = new TCharIntHashMap[numStates];

    //private Integer currentState = 0;

    static
    {
        initStateFromCharRange(states, 0, '0', '9', 1);
        initStateFromCharRange(states, 1, '0', '9', 2);
        initStateFromCharRange(states, 2, '0', '9', 3);


        states[3] = new TCharIntHashMap();
        //TODO whatever separators we'll accept for SSNs go here
        states[3].put('-', 4);


        initStateFromCharRange(states, 4, '0', '9', 5);
        initStateFromCharRange(states, 5, '0', '9', 6);

        states[6] = new TCharIntHashMap();
        //TODO whatever separators we'll accept for SSNs go here
        states[6].put('-', 7);


        initStateFromCharRange(states, 7, '0', '9', 8);
        initStateFromCharRange(states, 8, '0', '9', 9);
        initStateFromCharRange(states, 9, '0', '9', 10);
        initStateFromCharRange(states, 10, '0', '9', 11);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 11, -2);
    }

    public SSNDFA()
    {
        super(states);
    }


}

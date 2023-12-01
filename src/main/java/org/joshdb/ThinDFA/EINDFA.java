package org.joshdb.ThinDFA;

import org.joshdb.ThinDFA;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses Employer Identification Numbers
 */
public class EINDFA extends ThinDFA
{

    private static final int numStates = 20;
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
        //TODO skip 1-10 so that 0 goes to 10 1 to 11 etc to make this a little more understandable
        states[0] = new TCharIntHashMap();
        states[0].put('0', 1);
        states[0].put('1', 2);
        states[0].put('2', 3);
        states[0].put('3', 4);
        states[0].put('4', 5);
        states[0].put('5', 6);
        states[0].put('6', 7);
        states[0].put('7', 8);
        states[0].put('8', 9);
        states[0].put('9', 10);

        initStateFromCharRange(states, 1, '1', '6', 11);
        initStateFromCharRange(states, 2, '0', '6', 11);
        initStateFromCharRange(states, 3, '0', '7', 11);
        initStateFromCharRange(states, 4, '0', '9', 11);
        initStateFromCharRange(states, 5, '0', '8', 11);
        initStateFromCharRange(states, 6, '0', '9', 11);
        initStateFromCharRange(states, 7, '0', '8', 11);
        initStateFromCharRange(states, 8, '1', '7', 11);
        initStateFromCharRange(states, 9, '0', '8', 11);
        initStateFromCharRange(states, 10, '0', '5', 11);
        states[10].put('8', 11);
        states[10].put('9', 11);

        initStateFromCharRange(states, 11, '0', '9', 13);
        states[11].put('-', 12);

        //todo this transition seems like it can get rolled into the loop just below
        initStateFromCharRange(states, 12, '0', '9', 13);

        //todo it looks like the 18->19 '0'-'9' transition is entirely unused
        //we should probably get rid of that
        for (int i = 13; i < 18; i++)
        {
            initStateFromCharRange(states, i, '0', '9', i + 1);
        }

        initStateFromCharRange(states, 18, '0', '9', 19);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 19, -2);
    }

    public EINDFA()
    {
        super(states);
    }
}

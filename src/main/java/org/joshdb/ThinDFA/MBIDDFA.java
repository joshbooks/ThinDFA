package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses Social Security Numbers
 */
public class MBIDDFA extends ThinDFA
{
    //A medicare beneficiary ID is just a social security number with one of the following suffixes
    //String [] suffixes = new String[] {"A", "B1", "B2", "B3", "B4", "B5", "B6", "BY", "C1", "C2", "C3", "C4", "C5", "C6", "C7",
    // "C8", "C9", "D", "D1", "D2", "D3", "D4", "D5", "D6", "E", "E1", "E2", "E3", "E4", "E5", "F", "F1", "F2", "F3", "F4", "F5",
    // "F6", "HA", "HB", "HC", "M", "M1", "T", "TA", "TB", "W", "W1", "W2", "W3", "W4", "W5", "W6", "WA"};


    //todo math it up nice and find the new numStates
    private static final int numStates = 22;

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

        states[11] = new TCharIntHashMap();

        //A
        states[11].put('A', numStates-1);

        //B1-B6
        states[11].put('B', 12);

        states[12] = new TCharIntHashMap();

        for (char i = '1'; i < '7'; i++)
        {
            states[12].put((i), numStates-1);
        }

        //BY
        states[12].put('Y', numStates-1);

        //C1-C9
        states[11].put('C', 13);
        states[13] = new TCharIntHashMap();

        for (char i = '1'; i <= '9'; i++)
        {
            states[13].put(i, numStates-1);
        }

        //D
        states[11].put('D', 14);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 14, -2);

        //D1-D6
        for (char i = '1'; i < '7'; i++)
        {
            states[14].put(i, numStates-1);
        }

        //E
        states[11].put('E', 15);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 15, -2);

        //E1-E5
        for (char i = '1'; i < '6'; i++)
        {
            states[15].put(i, numStates-1);
        }

        //F
        states[11].put('F', 16);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 16, -2);

        //F1-F6
        for (char i = '1'; i < '7'; i++)
        {
            states[16].put(i, numStates-1);
        }

        //HA-HC
        states[11].put('H', 17);
        states[17] = new TCharIntHashMap();

        for (char i = 'A'; i < 'D'; i++)
        {
            states[17].put(i, numStates-1);
        }

        //M
        states[11].put('M', 18);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 18, -2);

        //M1
        states[18].put('1', numStates-1);

        //T
        states[11].put('T', 19);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 19, -2);

        //TA-TB
        for (char i = 'A'; i < 'C'; i++)
        {
            states[19].put(i, numStates-1);
        }

        //W
        states[11].put('W', 20);
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 20, -2);

        //W1-6
        for (char i = '1'; i < '7'; i++)
        {
            states[20].put((i), numStates-1);
        }

        //WA
        states[20].put('A', numStates-1);

        //space at the end
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, numStates-1, -2);

    }

    public MBIDDFA()
    {
        super(states);
    }


}

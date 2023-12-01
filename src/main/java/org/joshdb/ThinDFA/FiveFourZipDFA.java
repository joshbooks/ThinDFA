package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * Created by josh.hight on 8/30/17.
 */
public class FiveFourZipDFA extends ThinDFA
{
    private static final int NUM_STATES = 11;
    private static final TCharIntHashMap[] states = new TCharIntHashMap[NUM_STATES];


    static
    {
        initStateFromCharRange(states, 0, '0', '9', 1);
        initStateFromCharRange(states, 1, '0', '9', 2);
        initStateFromCharRange(states, 2, '0', '9', 3);
        initStateFromCharRange(states, 3, '0', '9', 4);
        initStateFromCharRange(states, 4, '0', '9', 5);

        states[5] = new TCharIntHashMap();
        //TODO whatever separators we'll accept for Zip codes go here
        states[5].put('-', 6);

        initStateFromCharRange(states, 6, '0', '9', 7);
        initStateFromCharRange(states, 7, '0', '9', 8);
        initStateFromCharRange(states, 8, '0', '9', 9);
        initStateFromCharRange(states, 9, '0', '9', 10);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 10, -2);
    }





    public FiveFourZipDFA()
    {
        super(states);
    }
}

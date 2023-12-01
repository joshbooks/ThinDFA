package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * Created by josh.hight on 9/8/17.
 */
public class PRNDFA extends ThinDFA
{
    private static final int NUM_STATES = 9;
    private static TCharIntHashMap[] states = new TCharIntHashMap[NUM_STATES];

    static
    {
        initStateFromCharRange(states, 0, 'A', 'Z', 1);
        initStateFromCharRange(states, 1, 'A', 'Z', 2);

        initStateFromCharRange(states, 2, '0', '9', 3);
        initStateFromCharRange(states, 3, '0', '9', 4);
        initStateFromCharRange(states, 4, '0', '9', 5);
        initStateFromCharRange(states, 5, '0', '9', 6);
        initStateFromCharRange(states, 6, '0', '9', 7);
        initStateFromCharRange(states, 7, '0', '9', 8);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 8, -2);

    }

    public PRNDFA()
    {
        super(states);
    }
}

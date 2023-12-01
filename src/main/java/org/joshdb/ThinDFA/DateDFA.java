package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * Created by josh.hight on 8/30/17.
 */
public class DateDFA extends ThinDFA
{
    private static final int NUM_STATES = 13;
    private static final TCharIntHashMap[] states = new TCharIntHashMap[NUM_STATES];

    static
    {
        initStateFromCharRange(states, 0, '0', '9', 1);
        initStateFromCharRange(states, 1, '0', '9', 2);



        //beginning of possible year
        initStateFromCharRange(states, 2, '0', '9', 3);
        initStateFromCharRange(states, 3, '0', '9', 4);

        //separators if it's not a year
        states[2].put('/', 5);
        states[2].put('-', 5);
        states[2].put('.', 5);

        //separators for the end of the possible year

        states[4] = new TCharIntHashMap();
        states[4].put('/', 5);
        states[4].put('-', 5);
        states[4].put('.', 5);

        initStateFromCharRange(states, 5, '0', '9', 6);
        initStateFromCharRange(states, 6, '0', '9', 7);

        //separators
        states[7] = new TCharIntHashMap();
        states[7].put('/', 8);
        states[7].put('-', 8);
        states[7].put('.', 8);

        initStateFromCharRange(states, 8, '0', '9', 9);
        initStateFromCharRange(states, 9, '0', '9', 10);

        //if two digits then wordbreak it was a month/day/whatever and we're done and it is totally a date
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 10, -2);
        //if three digits we need one more for this to be a real date
        setRangeTransition(states, 10, '0', '9', 11);
        //if we got the last one we just need to wait for the final word break
        initStateFromCharRange(states, 11, '0', '9', 12);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 12, -2);
    }


    public DateDFA()
    {
        super(states);
    }

}

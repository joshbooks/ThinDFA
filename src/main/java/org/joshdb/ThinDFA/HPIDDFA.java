package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses Health Provider Identification Numbers
 */
public class HPIDDFA extends ThinDFA
{

    private static final int numStates = 160;
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
        states[0].put('0', 19);
        states[0].put('1', 10);
        states[0].put('2', 11);
        states[0].put('3', 12);
        states[0].put('4', 13);
        states[0].put('5', 14);
        states[0].put('6', 15);
        states[0].put('7', 16);
        states[0].put('8', 17);
        states[0].put('9', 18);


        for (int i = 10; i < 90; )
        {
            if ((i / 10) % 2 == 1)
            {
                int end = i + 10;
                for (; i < end; i++)
                {
                    states[i] = new TCharIntHashMap();
                    for (char j = '0'; j <= '9'; j++)
                    {
                        states[i].put(j, /*the next set of modulus states*/((i - (i % 10)) + 10) +
                        /*The particular modulus state within that set*/(((i % 10) + (j - '0')) % 10));
                    }
                }
            }

            if ((i / 10) % 2 == 0)
            {
                int end = i + 10;
                for (; i < end; i++)
                {
                    states[i] = new TCharIntHashMap();
                    states[i].put('0', ((i - (i % 10)) + 10) + (i % 10));
                    states[i].put('1', ((i - (i % 10)) + 10) + ((i + 2) % 10));
                    states[i].put('2', ((i - (i % 10)) + 10) + ((i + 4) % 10));
                    states[i].put('3', ((i - (i % 10)) + 10) + ((i + 6) % 10));
                    states[i].put('4', ((i - (i % 10)) + 10) + ((i + 8) % 10));
                    states[i].put('5', ((i - (i % 10)) + 10) + ((i + 1) % 10));
                    states[i].put('6', ((i - (i % 10)) + 10) + ((i + 3) % 10));
                    states[i].put('7', ((i - (i % 10)) + 10) + ((i + 5) % 10));
                    states[i].put('8', ((i - (i % 10)) + 10) + ((i + 7) % 10));
                    states[i].put('9', ((i - (i % 10)) + 10) + ((i + 9) % 10));
                }
            }

            //[-]*
            if (((i - 10) / 10) % 4 == 0)
            {
                for (int j = i - 10; j < i; j++)
                {
                    states[j].put('-', j);
                }
            }
        }

        for (int i = 90; i < 100; i++)
        {
            states[i] = new TCharIntHashMap();
        }

        //only transition to success state if check digit
        //results in a modulus of 0
        states[90].put('4', -2);
        states[91].put('3', -2);
        states[92].put('2', -2);
        states[93].put('1', -2);
        states[94].put('0', -2);
        states[95].put('9', -2);
        states[96].put('8', -2);
        states[97].put('7', -2);
        states[98].put('6', -2);
        states[99].put('5', -2);

        //if we want to support 15-digit credit cards we can just add this line:
        //setComplementTransition(states, digits, 150, -2);
    }

    public HPIDDFA()
    {
        super(states);
    }
}

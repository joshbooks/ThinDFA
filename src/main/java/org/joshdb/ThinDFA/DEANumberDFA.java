package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;

/**
 * Created by josh.hight on 8/30/17.
 */
public class DEANumberDFA extends ThinDFA
{
    private static final int NUM_STATES = 71;
    private static final TCharIntHashMap[] states = new TCharIntHashMap[NUM_STATES];

    static
    {
        //first letter
        states[0] = new TCharIntHashMap();
        char[] allowableFirstChars = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'P', 'R', 'S', 'T', 'U', 'X'};

        for (char i : allowableFirstChars)
        {
            states[0].put(i, 1);
        }

        //second letter or 9
        initStateFromCharRange(states, 1, 'A', 'Z', 2);
        setRangeTransition(states, 1, 'a', 'z', 2);
        states[1].put('9', 2);

        //first digit
        states[2] = new TCharIntHashMap();

        //skipping 7 states to make the number work out all pretty
        states[2].put('0', 10);
        states[2].put('1', 11);
        states[2].put('2', 12);
        states[2].put('3', 13);
        states[2].put('4', 14);
        states[2].put('5', 15);
        states[2].put('6', 16);
        states[2].put('7', 17);
        states[2].put('8', 18);
        states[2].put('9', 19);

        //5 digits after the first digit
        for (int i = 1; i < 6; i++)
        {
            //for each digit we have 10 checksum states
            for (int j = 0; j <=9; j++)
            {
                //j is the current checksum state, so 12 means that the first digit we saw was 2
                int digitStateNumber = (i * 10) + j;

                states[digitStateNumber] = new TCharIntHashMap();

                //and for each checksum state there are 10 digits we could see
                //and still be looking at a credit card
                for (int k = 0; k <= 9; k++)
                {
                    int toMod, toState, modInc;

                    //the amount the current mod state needs to be incremented
                    if (i % 2 == 0)
                    {
                        modInc = k;
                    }
                    else
                    {
                        modInc = k * 2;
                    }

                    if (modInc >= 10)
                    {
                        modInc = ( /*(modInc / 10) + */(modInc % 10) );
                    }


                    toMod = j + modInc;

                    if (toMod >= 10)
                    {
                        toMod = ( /*(toMod / 10) +  */(toMod % 10));
                    }

                    toState = ((i + 1) * 10) + toMod;

                    states[digitStateNumber].put((char) ('0' + k), toState);
                }
            }
        }

        //check digit, just make sure the sum works out
        states[60] = new TCharIntHashMap();
        states[60].put('0', 70);

        states[61] = new TCharIntHashMap();
        states[61].put('1', 70);

        states[62] = new TCharIntHashMap();
        states[62].put('2', 70);

        states[63] = new TCharIntHashMap();
        states[63].put('3', 70);

        states[64] = new TCharIntHashMap();
        states[64].put('4', 70);

        states[65] = new TCharIntHashMap();
        states[65].put('5', 70);

        states[66] = new TCharIntHashMap();
        states[66].put('6', 70);

        states[67] = new TCharIntHashMap();
        states[67].put('7', 70);

        states[68] = new TCharIntHashMap();
        states[68].put('8', 70);

        states[69] = new TCharIntHashMap();
        states[69].put('9', 70);

        //and once we've seen the final digit and it's a valid thing then we wait for a word break
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, 70, -2);
    }

    public DEANumberDFA()
    {
        super(states);
    }
}

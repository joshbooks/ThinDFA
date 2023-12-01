package org.joshdb.ThinDFA;

import org.joshdb.ThinDFA;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses email addresses
 */
public class EmailDFA extends ThinDFA
{
    private static final int numStates = 30;
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
        //initStateFromCharRange(states, 0, 'a', 'z', 1);
        states[0] = new TCharIntHashMap(65000);
        setRangeTransition(states, 0, 'a', 'z', 1);

        setRangeTransition(states, 0, 'A', 'Z', 1);
        setRangeTransition(states, 0, '0', '9', 1);
        //unicode characters
        setRangeTransition(states, 0, '\u0080', '\uffff', 1);

        //todo make sure this list of special characters is right
        for (char i : "!#$%&'*+-/=?\\\\^_`{|}~".toCharArray())
        {
            states[0].put(i, 1);
        }

        //initStateFromCharRange(states, 1, 'a', 'z', 1);
        states[1] = new TCharIntHashMap(65000);
        setRangeTransition(states, 1, 'a', 'z', 1);

        setRangeTransition(states, 1, 'A', 'Z', 1);
        setRangeTransition(states, 1, '0', '9', 1);
        //unicode characters
        setRangeTransition(states, 1, '\u0080', '\uffff', 1);

        //todo make sure this list of special characters is right
        for (char i : "!#$%&'*+-/=?\\\\^_`{|}~".toCharArray())
        {
            states[1].put(i, 1);
        }

        states[1].put('@', 2);

        states[2] = new TCharIntHashMap(65000);

        //HERE we have the bracketed ip address email format
        //in all it's hideous glory
        states[2].put('[', 3);


        int j = 3;

        while (j < 20)
        {
            //3,9,15
            states[j] = new TCharIntHashMap();
            states[j].put('0', j + 1);
            states[j].put('1', j + 1);
            states[j].put('2', j + 4);

            setRangeTransition(states, j, '3', '9', j + 2);

            j++;
            //4,10,16
            initStateFromCharRange(states, j, '0', '9', j + 1);
            states[j].put('.', j + 5);

            j++;
            //5,11,17
            initStateFromCharRange(states, j, '0', '9', j + 1);
            states[j].put('.', j + 4);

            j++;
            //6,12,18
            states[j] = new TCharIntHashMap();
            states[j].put('.', j + 3);

            j++;
            //7,13,19
            initStateFromCharRange(states, j, '0', '4', j - 2);
            states[j].put('5', j + 1);
            states[j].put('.', j + 2);


            j++;
            //8,14,20
            initStateFromCharRange(states, j, '0', '5', j - 2);
            states[j].put('.', j + 1);
            j++;

        }

        //21
        states[j] = new TCharIntHashMap();
        states[j].put('1', j + 1);
        states[j].put('2', j + 4);

        setRangeTransition(states, j, '3', '9', j + 2);

        j++;
        //22
        initStateFromCharRange(states, j, '0', '9', j + 1);
        states[j].put(']', -2);

        j++;
        //23
        initStateFromCharRange(states, j, '0', '9', j + 1);
        states[j].put(']', -2);

        j++;
        //24
        states[j] = new TCharIntHashMap();
        states[j].put(']', -2);

        j++;
        //25
        initStateFromCharRange(states, j, '0', '4', j - 2);
        states[j].put('5', j + 1);
        states[j].put(']', -2);


        j++;
        //26
        initStateFromCharRange(states, j, '0', '5', j - 2);
        states[j].put(']', -2);


        //it's ok, it's all over now


        //psych, here's the domain name part
        setRangeTransition(states, 2, 'a', 'z', 27);
        setRangeTransition(states, 2, 'A', 'Z', 27);
        setRangeTransition(states, 2, '0', '9', 27);
        //unicode characters
        setRangeTransition(states, 2, '\u0080', '\uffff', 27);

        states[27] = new TCharIntHashMap(65535);
        for (char i = '\u0000'; i < Character.MAX_VALUE; i++)
        {
            if (i >= '0' && i <= '9')
            {
                states[27].put(i, 27);
            }
            else if (i >= 'A' && i <= 'Z')
            {
                states[27].put(i, 27);
            }
            else if (i >= 'a' && i <= 'z')
            {
                states[27].put(i, 27);
            }
            else if (i == '-')
            {
                states[27].put(i, 28);
            }
            else if (i == '.')
            {
                states[27].put(i, 29);
            }
            else if (i >= '\u0080' && i <= '\ufffe')
            {
                states[27].put(i, 27);
            }
            else
            {
                states[27].put(i, -2);
            }
        }

        states[29] = new TCharIntHashMap(65000);
        setRangeTransition(states, 29, 'a', 'z', 27);
        setRangeTransition(states, 29, 'A', 'Z', 27);
        setRangeTransition(states, 29, '0', '9', 27);
        //unicode characters
        setRangeTransition(states, 29, '\u0080', '\uffff', 27);

        states[28] = new TCharIntHashMap(65000);
        setRangeTransition(states, 28, 'a', 'z', 27);
        setRangeTransition(states, 28, 'A', 'Z', 27);
        setRangeTransition(states, 28, '0', '9', 27);
        //unicode characters
        setRangeTransition(states, 28, '\u0080', '\uffff', 27);
        states[28].put('-', 28);
    }

    public EmailDFA()
    {
        super(states);
    }
}

package org.joshdb.ThinDFA;

import org.joshdb.ThinDFA;
import gnu.trove.map.hash.TCharIntHashMap;

/**
 * A Class representing a Deterministic Finite Automaton that parses IPv4 addresses
 */
public class IPDFA extends ThinDFA
{

    private static final int numStates = 24;
    //Trove's crazy fast primitive hashmaps have numerous performance benefits
    //and as a bonus return 0 on failed lookup, which coincidentally is always the state
    //we want to be in upon getting a character that does not map to some state transition
    /**
     * The data structure representing the states between which the Deterministic Finite Automaton
     * transitions as it is fed characters
     */
    private static final TCharIntHashMap[] states = new TCharIntHashMap[numStates];

    static
    {

        int j = 0;

        while (j < 17)
        {
            //0,6,12
            states[j] = new TCharIntHashMap();
            states[j].put('0', j + 1);
            states[j].put('1', j + 1);
            states[j].put('2', j + 4);

            setRangeTransition(states, j, '3', '9', j + 2);

            j++;
            //1,7,13
            initStateFromCharRange(states, j, '0', '9', j + 1);
            states[j].put('.', j + 5);

            j++;
            //2,8,14
            initStateFromCharRange(states, j, '0', '9', j + 1);
            states[j].put('.', j + 4);

            j++;
            //3,9,15
            states[j] = new TCharIntHashMap();
            states[j].put('.', j + 3);

            j++;
            //4,10,16
            initStateFromCharRange(states, j, '0', '4', j - 2);
            states[j].put('5', j + 1);
            states[j].put('.', j + 2);


            j++;
            //5,11,17
            initStateFromCharRange(states, j, '0', '5', j - 2);
            states[j].put('.', j + 1);
            j++;

        }

        //18
        states[j] = new TCharIntHashMap();
        states[j].put('0', j + 1);
        states[j].put('1', j + 1);
        states[j].put('2', j + 4);

        setRangeTransition(states, j, '3', '9', j + 2);

        j++;
        //19
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, j, -2);
        setRangeTransition(states, j,'0', '9', j + 1);

        j++;
        //20
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, j, -2);
        setRangeTransition(states, j, '0', '9', j + 1);


        j++;
        //21
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, j, -2);


        j++;
        //22
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, j, -2);
        states[j].put('5', j + 1);
        setRangeTransition(states, j, '0', '4', j - 2);


        j++;
        //23
        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, j, -2);
        setRangeTransition(states, j, '0', '5', j-2);

    }

    public IPDFA()
    {
        super(states);
    }


}

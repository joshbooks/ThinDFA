package org.joshdb.ThinDFA;

import gnu.trove.map.hash.TCharIntHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Joshua Hight
 *         A DFA that accepts credit card numbers which pass the luhn check
 *         https://en.wikipedia.org/wiki/Luhn_algorithm
 */
public class LuhnCCDFA extends ThinDFA
{

    // The idea is that each state represents a modulus, and the transition to the
    // next state consists of adding the current modulus to the one corresponding
    // to the current state modified by character encountered (either the number
    // represented by a numeric character or special values every other state according to the luhn algorithm )
    private static class LuhnVarSet
    {
        private final int minDigits;
        private final int maxDigits;
        //todo why does this need to be an arraylist?
        private final ArrayList<String> prefixes;
        private LuhnVarSet(int min, int max, ArrayList<String> prefList)
        {
            minDigits = min;
            maxDigits = max;
            prefixes = new ArrayList<>(prefList);
            prefixes.sort(String::compareTo);
        }
        @Override
        public boolean equals(Object obj)
        {
            if (obj == null || !(obj instanceof LuhnVarSet))
            {
                return false;
            }
            LuhnVarSet other = (LuhnVarSet)obj;
            if (other.minDigits != minDigits || other.maxDigits != maxDigits)
            {
                return false;
            }
            else if (other.prefixes.size() != prefixes.size())
            {
                return false;
            }
            for (int i = 0; i < prefixes.size(); i++)
            {
                if (!prefixes.get(i).equals(other.prefixes.get(i)))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = Integer.toString(minDigits).hashCode() ^  Integer.toString(maxDigits).hashCode();
            for (String prefix : prefixes)
            {
                hash ^= prefix.hashCode();
            }
            return hash;
        }

    }

    private static final HashMap<LuhnVarSet, TCharIntHashMap[]> statesRegistry = new HashMap<>();

    private static int AddPrefixChar(char c, ArrayList<TCharIntHashMap> states, int curState, int maxState)
    {
        if (curState > states.size())
        {
            throw new IndexOutOfBoundsException("State we're in is somehow out of bounds...");
        }
        if (curState == states.size())
        {
            TCharIntHashMap state = new TCharIntHashMap();
            state.put(c, ++maxState);
            states.add(curState, state);
            return maxState;
        }
        else if (states.get(curState).contains(c))
        {
            return states.get(curState).get(c);
        }
        else
        {
            states.get(curState).put(c, ++maxState);
            return maxState;
        }
    }
    //TODO add support for implicit prefixes so that HPIDDFA becomes something like LuhnDFA(10, null, "80840")
    private static int AddPrefix(String prefix, ArrayList<TCharIntHashMap> states, int curState,
                                 int maxState, int parity, TIntIntHashMap finalStates) throws NumberFormatException
    {
        final int prefixParity = prefix.length() % 2;
        int localMax = maxState;
        int modulus = 0;
        for (int i = 0; i < prefix.length() - 1; i++)
        {
            curState = AddPrefixChar(prefix.charAt(i), states, curState, localMax);
            if (curState > localMax)
            {
                localMax = curState;
            }

            int digit = Integer.parseInt(prefix.substring(i, i + 1));
            if (parity == (i % 2)) // double
            {
                digit = (digit * 2)/10 + (digit * 2) % 10;
            }

            modulus = (modulus + digit) % 10;
        }
        int finalDigit = Integer.parseInt(prefix.substring(prefix.length() - 1, prefix.length()));
        int curMod = finalDigit;
        if (parity != prefixParity)
        {
            curMod = (finalDigit * 2)/10 + (finalDigit * 2) % 10;
        }
        modulus = (modulus + curMod) % 10;

        TCharIntHashMap finalPrefixState = new TCharIntHashMap();
        states.add(localMax, finalPrefixState);

        // notion here is that finalDigit and modulus will both be >=0, <10, so we can get
        // some extra information by just tacking it onto the end of the other values
        finalStates.put(10 * curState + finalDigit, 10 * prefix.length() + modulus);

        return localMax;
    }

    private static void ExcludePrefix(String prefix, TCharIntHashMap[] states)
    {
        int currentState = 0;
        int i;
        for (i = 0; i < prefix.length() - 1; i++)
        {
            if (!states[currentState].contains(prefix.charAt(i)))
            {
                return;
            }
            currentState = states[currentState].get(prefix.charAt(i));
        }

        if (states[currentState].contains(prefix.charAt(i)))
        {
            states[currentState].put(prefix.charAt(i), 0);
        }
    }

    private static TCharIntHashMap[] SetUpDfa(int numDigits, ArrayList<String> prefix, ArrayList<String> exclude)
            throws NumberFormatException
    {
        final int parity = numDigits % 2;
        prefix.sort(String::compareTo);
        ArrayList<TCharIntHashMap> prefixStates = new ArrayList<>(160);
        TIntIntHashMap finalStates = new TIntIntHashMap();
        int maxState = 0;
        int minPrefixDigits = prefix.isEmpty() ? 0 : Integer.MAX_VALUE;
        for (String p : prefix)
        {
            if (p.length() < minPrefixDigits) {
                minPrefixDigits = p.length();
            }
            maxState = AddPrefix(p, prefixStates, 0, maxState, parity, finalStates);

            if (p.length() > numDigits)
            {
                throw new NumberFormatException("prefix can't be longer than length!");
            }

        }

        int startState = prefix.isEmpty() ? 0 : (prefixStates.size() / 10 + 1) * 10;
        final int numStates = startState + (numDigits - minPrefixDigits) * 10;
        // represents the lowest state DFA should be in after prefix; so 0 for no prefix

        for (int key : finalStates.keys())
        {
            int value = finalStates.get(key);
            int length = value / 10;
            int modulus = value % 10;
            int finalDigit = key % 10;
            int fromState = (key / 10);
            int toState = startState + (length - minPrefixDigits) * 10 + modulus;
            char digit = (finalDigit + "").charAt(0);
            TCharIntHashMap state = prefixStates.get(fromState);
            state.put(digit, toState);
        }

        final TCharIntHashMap[] states = new TCharIntHashMap[numStates + 1];
        prefixStates.toArray(states);

        int currMod = 0;

        states[startState] = new TCharIntHashMap();

        if (minPrefixDigits == 0)
        {
            if (parity == 1) {
                states[startState].put('0', ((currMod) % 10)+ startState + 10);
                states[startState].put('1', ((currMod + 1) % 10)+ startState + 10);
                states[startState].put('2', ((currMod + 2) % 10)+ startState + 10);
                states[startState].put('3', ((currMod + 3) % 10)+ startState + 10);
                states[startState].put('4', ((currMod + 4) % 10)+ startState + 10);
                states[startState].put('5', ((currMod + 5) % 10)+ startState + 10);
                states[startState].put('6', ((currMod + 6) % 10)+ startState + 10);
                states[startState].put('7', ((currMod + 7) % 10)+ startState + 10);
                states[startState].put('8', ((currMod + 8) % 10)+ startState + 10);
                states[startState].put('9', ((currMod + 9) % 10)+ startState + 10);
            }
            else
            {
                states[startState].put('0', ((currMod) % 10)+ startState + 10);
                states[startState].put('1', ((currMod + 2) % 10)+ startState + 10);
                states[startState].put('2', ((currMod + 4) % 10)+ startState + 10);
                states[startState].put('3', ((currMod + 6) % 10)+ startState + 10);
                states[startState].put('4', ((currMod + 8) % 10)+ startState + 10);
                states[startState].put('5', ((currMod + 1) % 10)+ startState + 10);
                states[startState].put('6', ((currMod + 3) % 10)+ startState + 10);
                states[startState].put('7', ((currMod + 5) % 10)+ startState + 10);
                states[startState].put('8', ((currMod + 7) % 10)+ startState + 10);
                states[startState].put('9', ((currMod + 9) % 10)+ startState + 10);
            }

            // lowest state we can be in is now 10, and we have precomputed first digit
            startState += 10;
            minPrefixDigits += 1;
        }

        for (int i = startState; i < (numStates - 10); )
        {

            int end = i + 10;
            int currentParity = ((i - startState)/10 + minPrefixDigits) % 2;
            if (currentParity != parity) {
                for (; i < end; i++) {
                    int mod = i % 10;
                    states[i] = new TCharIntHashMap();
                    for (char j = '0'; j <= '9'; j++) {
                        states[i].put(j, /*the next set of modulus states*/((i - mod) + 10) +
                        /*The particular modulus state within that set*/((mod + (j - '0')) % 10));
                    }
                }
            }
            else
            {
                for (; i < end; i++) {
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

            if ((minPrefixDigits + (i - startState - 10) / 10) % 4 == 0)
            {
                for (int j = i - 10; j < i; j++)
                {
                    states[j].put('-', j);
                }
            }
        }

        for (int i = (numStates - 10); i < numStates; i++)
        {
            states[i] = new TCharIntHashMap();
        }

        //only transition to pre-success state if check digit
        //results in a modulus of 0
        states[numStates - 10].put('0', numStates);
        states[numStates - 9].put('9', numStates);
        states[numStates - 8].put('8', numStates);
        states[numStates - 7].put('7', numStates);
        states[numStates - 6].put('6', numStates);
        states[numStates - 5].put('5', numStates);
        states[numStates - 4].put('4', numStates);
        states[numStates - 3].put('3', numStates);
        states[numStates - 2].put('2', numStates);
        states[numStates - 1].put('1', numStates);

        initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, numStates, -2);

        if (exclude != null)
        {
            for (String exc : exclude)
            {
                ExcludePrefix(exc, states);
            }

        }

        return states;
    }

    // since we can only support parity, we simply take the parity of the min digits
    private static synchronized TCharIntHashMap[] SetUpDfa(int min, int max, ArrayList<String> prefix,
                                                           ArrayList<String> exclude) throws NumberFormatException
    {
        final LuhnVarSet varSet = new LuhnVarSet(min, max, prefix);
        if (statesRegistry.containsKey(varSet))
        {
            return statesRegistry.get(varSet);
        }

        if (min == max)
        {
            TCharIntHashMap[] states = SetUpDfa(max, prefix, exclude);
            statesRegistry.put(varSet, states);
            return states;
        }
        if (min > max)
        {
            throw new NumberFormatException("Min digits can't be greater than max!");
        }
        int intParity = min % 2;
        int correctedMax = max;
        if ((correctedMax % 2) != intParity)
        {
            correctedMax--;
        }
        TCharIntHashMap[] states = SetUpDfa(correctedMax, prefix, exclude);
        int numStates = states.length - 1; // subtracting one from the accept state
        for (int i = 2; i <= (max - min); i += 2)
        {
            int fromState = numStates - i * 10;
            initStateFromWordbreakTransition(states, Character.DECIMAL_DIGIT_NUMBER, fromState, -2);
        }
        statesRegistry.put(varSet, states);
        return states;
    }


    public LuhnCCDFA() throws NumberFormatException, IndexOutOfBoundsException { this(16); }

    public LuhnCCDFA(int numDigits) throws NumberFormatException, IndexOutOfBoundsException
    {
        this(numDigits, new ArrayList<>());
    }

    public LuhnCCDFA(int numDigits, ArrayList<String> prefixes) throws NumberFormatException, IndexOutOfBoundsException
    {
        super(SetUpDfa(numDigits, numDigits, prefixes, null));
    }

    public LuhnCCDFA(int minDigits, int maxDigits, ArrayList<String> prefixes)
            throws NumberFormatException, IndexOutOfBoundsException
    {
        super(SetUpDfa(minDigits, maxDigits, prefixes, null));
    }

    public LuhnCCDFA(int minDigits, int maxDigits, ArrayList<String> prefixes, ArrayList<String> excludePrefixes)
            throws NumberFormatException, IndexOutOfBoundsException
    {
        super(SetUpDfa(minDigits, maxDigits, prefixes, excludePrefixes));
    }
}

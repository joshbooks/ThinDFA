package org.joshdb.ThinDFA;

import com.google.gson.*;
import gnu.trove.map.hash.TCharIntHashMap;
import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;
import org.jetbrains.annotations.NotNull;


/**
 * The abstract class from which to extend classes representing Deterministic Finite Automata
 */
public class ThinDFA
{

    static final char [] digits = new char[10];
    static
    {
        for (char i = '0'; i <= '9'; i++)
        {
            digits[i - '0'] = i;
        }
    }

    private static final TCharSet universe = new TCharHashSet(Character.MAX_VALUE);
    static
    {
        for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++)
        {
            universe.add(i);
        }
    }

    //set up a GsonBuilder that knows how to handle TCharIntHashMaps
    private static final GsonBuilder jsonMungerBuilder = new GsonBuilder();
    static
    {
        jsonMungerBuilder.registerTypeAdapter(TCharIntHashMap.class, (JsonSerializer<TCharIntHashMap>)
                (src, typeOfSrc, context) ->
                {
                    JsonObject metaDict = new JsonObject();
                    metaDict.add("noEntryValue", new JsonPrimitive(src.getNoEntryValue()));

                    JsonObject dict = new JsonObject();

                    metaDict.add("dictionary", dict);

                    src.forEachEntry((a, b) ->
                    {
                        dict.add(""+a, new JsonPrimitive(b));
                        return true;
                    });

                    return metaDict;
                }
        );

        jsonMungerBuilder.registerTypeAdapter(TCharIntHashMap.class, (JsonDeserializer<TCharIntHashMap>)
                (json, typeOfT, context) ->
                {
                    JsonObject metaObject = json.getAsJsonObject();
                    JsonObject object = metaObject.get("dictionary").getAsJsonObject();
                    int noEntryValue = metaObject.get("noEntryValue").getAsInt();

                    //JsonObject object = json.getAsJsonObject();


                    TCharIntHashMap hash = new TCharIntHashMap(object.entrySet().size(), .5f, '\0', noEntryValue);

                    object.entrySet().forEach(i -> hash.put(i.getKey().charAt(0), i.getValue().getAsInt()));

                    return hash;
                }
        );

    }
    //Use the TCharIntHashMap aware GsonBuilder to build a Gson object
    private static final Gson jsonMunger;
    static
    {
        jsonMunger = jsonMungerBuilder.create();
    }


    /**
     * This Field should ALWAYS be a pointer to a static final
     * class specific array, since these have a tendency to be large,
     * are stateless, and get accessed frequently as part of a critical
     * pathway within this application. This is the data structure representing
     * the states between which a Deterministic Finite Automaton
     * transitions as it is fed characters.
     */
    private final TCharIntHashMap[] states;

    /**
     * The integer value corresponding to the current state of the DFA<br>
     * 0 must be the initial state<br>
     * -2 is successful match
     */
    private int currentState = 0;

    ThinDFA(TCharIntHashMap[] states)
    {
        this.states = states;
    }

    static void initStateFromCharRange(TCharIntHashMap[] states, int stateNumber, char fromChar, char toChar, int
            toState)
    {
        states[stateNumber] = new TCharIntHashMap();

        setRangeTransition(states, stateNumber, fromChar, toChar, toState);
    }

    static void setRangeTransition(TCharIntHashMap[] states, int stateNumber, char fromChar, char toChar, int toState)
    {
        for (char i = fromChar; i <= toChar && i < Character.MAX_VALUE; i++)
        {
            states[stateNumber].put(i, toState);
        }
    }

    @Deprecated
    static void setComplementTransition(TCharIntHashMap[] states, char[] not, int stateNumber, int toState)
    {
        for (char i : complement(not))
        {
            states[stateNumber].put(i, toState);
        }
    }

    /**
     *
     * @param states
     * @param charType
     * @param stateNumber
     * @param toState
     */
    static void initStateFromWordbreakTransition(TCharIntHashMap[] states, byte charType, int stateNumber, int toState)
    {
        states[stateNumber] = stateWithWordbreakTransition(charType, toState);
    }

    /**
     * creates a {@link TCharIntHashMap} that will transition to toState if it encounters a wordbreak transition.
     * The Official Unicode Rules for word breaks can be found here http://www.unicode.org/reports/tr29/tr29-21.html
     * @param charType the type of character preceding the word break, this determines what characters are considered
     * @param toState
     * @return
     */
    static TCharIntHashMap stateWithWordbreakTransition(byte charType, int toState)
    {
        TCharSet badChars = new TCharHashSet();

        switch (charType)
        {
            case Character.DECIMAL_DIGIT_NUMBER:
            {
                //TODO get the number of states for each of these cases and make an initFromWordBreakTransition function
                for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++)
                {
                    int type = Character.getType((int)i);
                    if ((type == Character.DECIMAL_DIGIT_NUMBER) || (type == Character.UPPERCASE_LETTER) ||
                            (type == Character.LOWERCASE_LETTER) || (type == Character.TITLECASE_LETTER) ||
                            (type == Character.MODIFIER_LETTER) || (type == Character.OTHER_LETTER) ||
                            (type == Character.LETTER_NUMBER) || (type == Character.CONNECTOR_PUNCTUATION))
                    {
                        badChars.add(i);
                    }
                }

                break;
            }
            default:
                break;
        }

        return stateWithComplementTransition(badChars, toState);
    }

    /**
     * Returns a {@link TCharIntHashMap} that will transition to 0 given one of the {@code excludedChars} but will otherwise return {@code defaultState}
     * @param excludedChars the characters for which this states should transition back to 0
     * @param defaultState the state which this state should transition to given any character that is not in {@code excludedChars}
     * @return the generated state
     */
    static TCharIntHashMap stateWithComplementTransition(TCharSet excludedChars, int defaultState)
    {
        TCharIntHashMap mapWithDefault = stateWithDefaultTransition(defaultState, excludedChars.size());

        excludedChars.forEach(excludedChar ->
        {
            mapWithDefault.put(excludedChar, 0);
            return true;
        });

        return mapWithDefault;
    }

    /**
     * Generates a state that will return the given state number by default instead of 0
     * @param defaultTransitionToState the state to which this state should transition to by default
     * @return a {@link TCharIntHashMap} to be inserted into an array to be used as a states field in a {@link ThinDFA}
     */
    @NotNull
    static TCharIntHashMap stateWithDefaultTransition(int defaultTransitionToState, int initialSize)
    {
        return new TCharIntHashMap(initialSize, .5f, '\0', defaultTransitionToState);
    }


    /**
     * Updates the current state of the automata by repeatedly getting the state transition
     * corresponding to the next input character in the hashmaps that represent the current state
     * at any given time
     *
     * @param next an a string whose characters shall be treated as the next pieces of input
     * @return the number of times the DFA transitioned into a success state while parsing the input
     */
    public long update(String next)
    {
        long count = 0;

        for (int i = 0; i < next.length(); i++)
        {
            if (update(next.charAt(i)))
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Updates the current state of the automata by getting the state transition
     * corresponding to the input character in the hashmap that represents the current state
     *
     * @param next a character that shall be treated as the next piece of input
     * @return whether or not this character resulted in the DFA transitioning into the success state
     */
    public boolean update(char next)
    {
        //grab current state before transition
        //grab char
        currentState = states[currentState].get(next);
        //grab state after transition

        if (currentState == -2)
        {
            currentState = 0;
            return true;
        }

        return false;
    }

    /**
     * Updates the current state of the automaton by repeatedly getting the state transition
     * corresponding to the next input character in the hashmaps that represent the current state
     * at any given time
     *
     * @param next an array of characters that shall be treated as the next pieces of input
     * @param off The offset at which to begin reading the array
     * @param len the number of bytes from the array to read
     * @return the number of times the DFA transitioned into a success state while parsing the input
     */
    public long update(char[] next, int off, int len)
    {
        long retVal = 0;
        for (int i = off; i < len; i++)
        {
            currentState = states[currentState].get(next[i]);

            if (currentState == -2)
            {
                currentState = 0;
                retVal++;
            }
        }
        return retVal;
    }


    private static char[] complement(char[] of)
    {
        TCharSet stephen = new TCharHashSet(universe);
        stephen.removeAll(of);
        return stephen.toArray();
    }

    public static String toDot(ThinDFA dfa)
    {
        StringBuilder fullDFA = new StringBuilder("digraph DFA {\n");

        for (int i = 0; i < dfa.states.length; i++)
        {
            TCharIntHashMap node = dfa.states[i];

            if (node == null)
            {
                continue;
            }

            final int nodeId = i;

            node.forEachEntry((a, b) ->
            {
                fullDFA.append(nodeId).append("->").append(b).append("[label=\"");

                if (Character.isLetterOrDigit(a))
                {
                    fullDFA.append(a);
                }
                else
                {
                    fullDFA.append("\\u").append(Integer.toHexString(a | 0x10000).substring(1));
                }


                fullDFA.append("\"];");
                return true;
            });
        }

        fullDFA.append("\n}");

        return fullDFA.toString();
    }

    public String toDot()
    {
        return toDot(this);
    }


    public static ThinDFA fromJson(String json) throws JsonSyntaxException
    {
        //custom deserializer means we should definitely not try to get
        //the Gson in ApplicationUtilities to do this stuff
        return new ThinDFA(jsonMunger.fromJson(json, TCharIntHashMap[].class));
    }

    public static String toJson(ThinDFA dfa)
    {
        return jsonMunger.toJson(dfa.states);
    }

    public String toJson()
    {
        return ThinDFA.toJson(this);
    }

    public boolean jsonEquals(String json)
    {
        ThinDFA otherDfa = fromJson(json);
        return this.equals(otherDfa);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj.getClass() != this.getClass())
        {
            return false;
        }

        ThinDFA other = (ThinDFA)obj;
        if (other.states.length != this.states.length)
        {
            return false;
        }

        for (int i = 0; i < other.states.length; i++)
        {
            if (this.states[i] != null && other.states[i] != null)
            {
                if (!other.states[i].equals(this.states[i]))
                {
                    return false;
                }
            }
            else if (!(this.states[i] == null && other.states[i] == null))
            {
                return false;
            }
        }

        return true;
    }
}

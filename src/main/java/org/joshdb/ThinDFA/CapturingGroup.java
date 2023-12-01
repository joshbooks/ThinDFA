package org.joshdb.ThinDFA;

import gnu.trove.set.hash.TIntHashSet;

import java.security.MessageDigest;
import java.util.Collection;

/**
 * Created by josh.hight on 11/18/16.
 */
abstract class CapturingGroup
{
    /**
     * A MessageDigest object that is used to hash the entities
     * this CapturingGroup is intended to hash
     */
    private final MessageDigest m_hasher;

    /**
     * An array of TIntHashSets such that m_states[fromState] is always a
     * valid operation that returns an initialized TIntHashSet and
     * the contains(toState) method on that hash set indicates whether or
     * not to hash a character that resulted in the state transition
     * from fromState to toState
     */
    private final TIntHashSet[] m_states;

    CapturingGroup(MessageDigest md, TIntHashSet[] states)
    {
        m_hasher = md;
        m_states = states;
    }

    byte[] update(int fromState, char encountered, int toState)
    {
        if (toState == 0)
        {
            m_hasher.reset();
            return null;
        }

        if (m_states[fromState].contains(toState))
        {
            //most significant byte, then least
            m_hasher.update((byte)((encountered>>8)&0xf));
            m_hasher.update((byte)(encountered&0xf));
        }

        if (toState == -2)
        {
            return m_hasher.digest();
        }

        return null;
    }

    static void initState(TIntHashSet[] states, int state)
    {
        states[state] = new TIntHashSet();
    }

    static void initState(TIntHashSet[] states, int state, int initialCapacity)
    {
        states[state] = new TIntHashSet(initialCapacity);
    }

    static void setStateValues(TIntHashSet[] states, int state, Collection<Integer> toStates)
    {
        states[state].addAll(toStates);
    }

    static void initStateFromValues(TIntHashSet[] states, int state, Collection<Integer> toStates)
    {
        initState(states, state, toStates.size());
        setStateValues(states, state, toStates);
    }

}

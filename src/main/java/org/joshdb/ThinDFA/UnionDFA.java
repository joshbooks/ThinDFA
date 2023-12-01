package org.joshdb.ThinDFA;

import gnu.trove.list.array.TCharArrayList;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharIntHashMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.PatternSyntaxException;

/**
 * Created by josh.hight on 9/15/17.
 */
public class UnionDFA extends ThinDFA
{
    /**
     * A registry of previously constructed DFAs that we keep around so we don't have to construct them again
     */
    private static final HashMap<String[], TCharIntHashMap[]> unionRegistry = new HashMap<>();

    /**
     * A helper method to return an existing DFA if we've already constructed one, otherwise to
     * construct one and cache it in the registry for future use
     * @param addends the list of words that should be added to the trie before a dfa is generated from it
     * @return a TCharIntHashMap[] that can be used as the states field of a {@link ThinDFA}
     * @throws IncompatibleRegexException if the provided words cannot be combined as a DFA
     */
    private static synchronized TCharIntHashMap[] setUpDfa(String[] addends) throws IncompatibleRegexException
    {
        if (addends == null || addends.length == 0)
        {
            throw new PatternSyntaxException("Cannot have an empty union", "", 0);
        }

        TCharIntHashMap[] states = unionRegistry.get(addends);

        if (states == null)
        {
            states = generateUnionDfa(addends);
            unionRegistry.put(addends, states);
        }

        return states;
    }

    /**
     * Helper function that handles generating the prefix tree and constructing a dfa from it
     * @param addends the list of words that should be added to the trie before a dfa is generated from it
     * @return a TCharIntHashMap[] that can be used as the states field of a {@link ThinDFA}
     * @throws IncompatibleRegexException if the provided words cannot be combined as a DFA
     */
    private static TCharIntHashMap[] generateUnionDfa(String[] addends) throws IncompatibleRegexException
    {
        UnionTrie trie = new UnionTrie();

        trie.addWords(addends);

        //todo minimize dfa
        //while not technically necessarily minimal, a conceptually neat optimization would be to start at the end and zip up identical paths
        //just as the union trie starts at the beginning to  zip up identical paths.
        return trie.constructDfa();
    }

    private static class UnionDigraph
    {
        final UnionTrie.UnionTrieNode rootNode;

        /**
         * build a UnionDigraph out of a UnionTrie
         * @param organized the UnionTrie this Digraph will be based upon
         */
        private UnionDigraph(UnionTrie organized)
        {
            //todo maybe do a deep copy instead clobbering the shit out of the UnionTrie this is based on
            rootNode = organized.rootNode;

            //this is the imaginary state that is an epsilon transition away from all the leaf nodes in a UnionTrie
            UnionTrie.UnionTrieNode acceptState = new UnionTrie.UnionTrieNode();

            ArrayList<UnionTrie.UnionTrieNode> leafNodes = rootNode.getSubtreeLeafNodes();

            //so lets connect all the parents of the leaf nodes directly to the accept state and factor out the epsilon transitions
            //all in one go
            for (UnionTrie.UnionTrieNode node : leafNodes)
            {
                //each node should have exactly one parent at this point
                UnionTrie.UnionTrieNode parent = node.parents.get(0);
                parent.children.put(node.letters.get(0), acceptState);
                acceptState.parents.add(parent);
                acceptState.letters.add(node.letters.get(0));
            }


            //so we want to find all possible paths from all parents of the accept state to the accept state as well as the string associated with each path
            //and then we can deduplicate parents that share ALL the same strings

            HashMap<Set<String>, UnionTrie.UnionTrieNode> parentForInputLanguage = new HashMap<>();

            for (int i = 0; i < acceptState.parents.size(); i++)
            {
                Set<String> inputLanguageForParent = getInputLanguage(acceptState.parents.get(i), acceptState);
                UnionTrie.UnionTrieNode functionallyIdenticalParent = parentForInputLanguage.get(inputLanguageForParent);

                if (functionallyIdenticalParent == null)
                {
                    parentForInputLanguage.put(inputLanguageForParent, acceptState.parents.get(i));
                }
                else
                {
                    
                }

            }





        }


        private static Set<String> getInputLanguage(UnionTrie.UnionTrieNode forNode, UnionTrie.UnionTrieNode acceptState)
        {
            Set<String> inputLanguage = new HashSet<>();

            getInputLanguageForSubtree(forNode, acceptState, "", inputLanguage);

            return inputLanguage;
        }

        private static void getInputLanguageForSubtree(UnionTrie.UnionTrieNode startPoint, UnionTrie.UnionTrieNode acceptState, String stringSoFar, Set<String> inputLanguage)
        {
            startPoint.children.forEachEntry((letter, node) ->
            {
                String newString = stringSoFar + letter;

                if (node == acceptState)
                {
                    inputLanguage.add(newString);
                }
                else
                {
                    getInputLanguageForSubtree(node, acceptState, newString, inputLanguage);
                }

                return true;
            });

        }
    }

    /**
     * A prefix tree. we start with a root node that contains no letters, and then we have one node per possible character following it.
     * The reasoning behind my use of this data structure is that it inherently eliminates common prefixes, which is what's required
     * in order to implement the union or "|" regular expression operator with DFAs.
     */
    private static class UnionTrie
    {
        UnionTrieNode rootNode = new UnionTrieNode();

        /**
         * Calls addWord for each word in the provided list
         * @param addends a list of words to be added to this UnionTrie
         * @throws IncompatibleRegexException if the provided words cannot be combined as a DFA
         */
        void addWords(String[] addends) throws IncompatibleRegexException
        {
            for (String word : addends)
            {
                addWord(word);
            }
        }

        @SuppressWarnings("unused")
        void printTree()
        {
            rootNode.printSubtree(0, '\0');

        }

        /**
         * Adds the specified word to the prefix trie
         * @param word a word to be added to the prefix tree
         * @throws IncompatibleRegexException if the provided word cannot be added to this trie and result in a valid trie
         */
        void addWord(String word) throws IncompatibleRegexException
        {
            UnionTrieNode node = rootNode;

            UnionTrieNode canExtend = rootNode;

            for (int i = 0; i < word.length(); i++)
            {
                //this means we're about to turn a leaf node we didn't create into a branch node,
                //which is going to clobber a transition to the success state which means this union 
                // is not allowable and we should throw an exception
                if (node.isLeaf() && !(node == canExtend))
                {
                    throw new IncompatibleRegexException();
                }

                char letter = word.charAt(i);

                UnionTrieNode childForLetter = node.getChild(letter);

                if (childForLetter == null)
                {
                    node = node.addChild(letter, node);
                    canExtend = node;
                }
                else
                {
                    node = childForLetter;
                }
            }

            //this is not a good thing
            //it means that there's a bit of the regex that
            // we're going to miss
            if (!node.isLeaf())
            {
                throw new IncompatibleRegexException();
            }
        }


        /**
         * @return the number of edges between the root node and the most distant leaf node
         */
        int maxDepth()
        {
            return rootNode.subTreeMaxDepth(0);
        }


        ArrayList<UnionTrieNode> getLeafNodes()
        {
            return rootNode.getSubtreeLeafNodes();
        }

        /**
         * size function
         * @return the number of nodes in this trie
         */
        int size()
        {
            AtomicInteger size = new AtomicInteger(0);

            addToSize(rootNode, size);

            //get rid of the bogus root node
            return size.get() - 1;
        }

        /**
         * A helper function to help with the recursion required for size
         * @param node the node with which to begin out recursion
         * @param size the {@link AtomicInteger} to update to reflect the extent of the trie covered by this stack depth and lower
         *             (or higher or however you like to think about it)
         */
        private void addToSize(UnionTrieNode node, AtomicInteger size)
        {
            size.getAndIncrement();

            node.children.forEachValue(value ->
            {
                if (value.isLeaf())
                {
                    size.getAndIncrement();
                }
                else
                {
                    addToSize(value, size);
                }

                return true;
            });
        }

        /**
         * Construct a DFA from this prefix tree
         * @return a TCharIntHasMap[] to be used as the states field of a {@link ThinDFA}
         */
        TCharIntHashMap[] constructDfa()
        {
            TCharIntHashMap[] states = new TCharIntHashMap[size()];

            constructDfa(rootNode, states, 0);

            return states;
        }

        /**
         * A helper function so we can do the recursion magic required to actually construct a DFA from a trie
         * @param trieNode the node at which to begin our traversal
         * @param states the TCharIntHashMap[] to update with the state transitions extracted from the trie nodes
         * @param currentIndex the current working index of {@code states}
         * @return the first free index of {@code states}
         */
        private static int constructDfa(UnionTrieNode trieNode, TCharIntHashMap[] states, final int currentIndex)
        {
            //ok, so I think we just have to kep track of the current index (so pass a new atomicinteger(currentIndex.get()))
            //as well as the first open index, so we should return an int that we check in the calling stack
            states[currentIndex] = new TCharIntHashMap();
            AtomicInteger firstFree = new AtomicInteger(currentIndex + 1);

            trieNode.children.forEachEntry((letter, child) ->
            {
                if (child.isLeaf())
                {
                    states[currentIndex].put(letter, -2);
                }
                else
                {
                    states[currentIndex].put(letter, firstFree.get());
                    firstFree.set(constructDfa(child, states, firstFree.get()));
                }

                //keep iterating
                return true;
            });

            return firstFree.get();
        }

        //ooh the bottom up trie minimization could actually work, we only need the one parent per child relationship to be true at the very beginning
        //after that we can make any sort of weirded out graph we like since we don't need that relationship to be true after we make a dfa out of it
        //this will mean maintaining a boolean field indicating whether or not the trie has already been minimized and that is ok. and let's make the minimize
        //function return the trie so we can do a sweet chained call

        /**
         * A single node of a {@link UnionTrie}
         */
        private static class UnionTrieNode
        {
            private TCharObjectHashMap<UnionTrieNode> children = new TCharObjectHashMap<>();

            /**
             * this should have exactly one entry in while this is still as trie with one parent per child
             * but once things get freaky and we let children have multiple parents this will have more entries
             */
            ArrayList<UnionTrieNode> parents = new ArrayList<>();
            //private char letter;
            TCharArrayList letters = new TCharArrayList();

            /**
             * Returns the child node corresponding to the character provided
             * @param childFor the letter to which a returned child ought correspond
             * @return a {@link UnionTrieNode} for the given character or null if one does not exist
             */
            UnionTrieNode getChild(char childFor)
            {
                return children.get(childFor);
            }

            /**
             * Add a child for a given character
             * @param letter the character for which to add a child
             * @return the child that was added
             */
            UnionTrieNode addChild(char letter, UnionTrieNode parent)
            {
                UnionTrieNode child = new UnionTrieNode();
                child.parents.add(parent);
//                child.letter = letter;
                letters.add(letter);
                children.put(letter, child);
                return child;
            }

            /**
             *
             * @return false if this node has any children, true if it has none
             */
            boolean isLeaf()
            {
                return children.isEmpty();
            }

            /**
             *
             * @return the number of edges between this node and the most distant leaf node
             * @param depth the depth of this node as determined by prior recursive calls
             */
            int subTreeMaxDepth(int depth)
            {
                AtomicInteger maxDepth = new AtomicInteger(depth);

                children.forEachValue(node ->
                {
                    int nodeDepth = depth+1;

                    if (!isLeaf())
                    {
                        node.subTreeMaxDepth(depth + 1);
                    }

                    if (nodeDepth > maxDepth.get())
                    {
                        maxDepth.set(nodeDepth);
                    }

                    return true;
                });

                return maxDepth.get();
            }

            void printSubtree(int depth, char letter)
            {
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < depth; i++)
                {
                    builder.append('|');
                }

                builder.append(letter);


                System.out.println(builder.toString());

                children.forEachEntry((childLetter, child) ->
                {
                    child.printSubtree(depth+1, childLetter);

                    return true;
                });
            }

            //todo just mutate the ArrayList instead of allocating one on every function call
            public ArrayList<UnionTrieNode> getSubtreeLeafNodes()
            {
                ArrayList<UnionTrieNode> subtreeLeafNodes = new ArrayList<>();

                children.forEachValue(node ->
                {
                    if (node.isLeaf())
                    {
                        subtreeLeafNodes.add(node);
                    }
                    else
                    {
                        subtreeLeafNodes.addAll(node.getSubtreeLeafNodes());
                    }

                    return true;
                });

                return subtreeLeafNodes;
            }
        }
    }


    /**
     * Exception class indicating that a regular expression was
     * specified that is not compatible with the stream processing
     * paradigm this DFA Library is based on
     */
    public static class IncompatibleRegexException extends Exception
    {

    }


    /**
     * Constructs a DFA representing the union of all the strings provided as arguments
     * @param addends The words that should be accepted by the constructed DFA
     * @throws IncompatibleRegexException if the provided word cannot be added to this trie and result in a valid trie
     */
    public UnionDFA(String[] addends) throws IncompatibleRegexException
    {
        super(setUpDfa(addends));
    }
}

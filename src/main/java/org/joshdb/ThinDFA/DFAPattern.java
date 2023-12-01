//package org.joshdb.ThinDFA;
//
//import gnu.trove.map.hash.TCharIntHashMap;
//import gnu.trove.set.hash.TCharHashSet;
//
//import java.util.ArrayList;
//import java.util.regex.Pattern;
//import java.util.regex.PatternSyntaxException;
//
///**
// * Created by josh.hight on 6/14/17.
// */
//public class DFAPattern
//{
//    private final String pattern;
//    private final char[] patternBuffer;
//
//    private static final TCharHashSet expressionDelimiters = new TCharHashSet();
//    static
//    {
//        //todo
//    }
//
//    private static final TCharHashSet specialCharacters = new TCharHashSet();
//    static
//    {
//        //todo
//    }
//
//    //todo add some syntax for luhn checked sequences(the traits of which are of course length and
//    //prefix)
//
//    public DFAPattern(String pattern)
//    {
//        this.pattern = pattern;
//        this.patternBuffer = pattern.toCharArray();
//    }
//
//    Pattern toJavaPattern()
//    {
//        return Pattern.compile(pattern);
//    }
//
//
//    private int cursor = 0;
//
//    synchronized ThinDFA toDFA()
//    {
//
//        //todo this should be a generic Expression that can be either a sequence or a union
//        //so we can change out behavior depending on what class type it is
//        SequenceExpression globalSequence  = new SequenceExpression();
//
//
//        while (cursor < pattern.length())
//        {
//            char currentChar = patternBuffer[cursor];
//
//            if (isLiteral(currentChar))
//            {
//                globalSequence.add(literalExpression());
//            }
//            else if (currentChar == '[')
//            {
//                globalSequence.add(charSetExpression());
//            }
//            else if (currentChar == '|')
//            {
//                //so the idea is that we'll just pop the last expression off the globalSequence
//                //and create a new union expression with that and whatever comes after it
//                //my question is how do we do that in one loop when there may be subsequent '|'s
//                //
//            }
////            else if (currentChar == '(')
////            {
////                globalSequence.add(groupExpression());
////            }
//        }
//
//        TCharIntHashMap[] dfaStates = new TCharIntHashMap[globalSequence.getNumStates()];
//        //todo fancy stuff involving Expression.addStates()
//        return new ThinDFA(dfaStates);
//    }
//
//    //TODO groups (.*) are just going to be a recursive use of the main loop in toDFA (which will get factored out into
//    //its own method, so let's do that last
//    //
////    private Expression groupExpression()
////    {
////        SequenceExpression groupSequence = new SequenceExpression();
////
////    }
//    //previous expression should be
//    private Expression unionExpression(Expression previousBranch)
//    {
//
//    }
//
//    private Expression charSetExpression()
//    {
//        int charSetEnd = cursor + 1;
//        for (;charSetEnd < patternBuffer.length; charSetEnd++)
//        {
//            if (patternBuffer[charSetEnd] == ']')
//            {
//                break;
//            }
//        }
//
//        if (charSetEnd == patternBuffer.length)
//        {
//            throw new PatternSyntaxException("didn't find a ] to match the [", pattern, cursor);
//        }
//        int length = charSetEnd - (cursor + 1);
//
//        return new CharSetExpression(patternBuffer, cursor+1, length);
//    }
//
//    private boolean isLiteral(char possiblyLiteral)
//    {
//        return !expressionDelimiters.contains(possiblyLiteral) && !specialCharacters.contains(possiblyLiteral);
//    }
//
//
//    private Expression literalExpression()
//    {
//        int beginLiteral = cursor;
//        int endLiteral;
//
//        for (endLiteral = beginLiteral;
//                (
//                        endLiteral < patternBuffer.length
//                                && isLiteral(patternBuffer[endLiteral])
//                );
//                endLiteral++);
//
//        int length = endLiteral-beginLiteral;
//
//        return new LiteralExpression(patternBuffer, beginLiteral, length);
//    }
//
//    /**
//     * I have the same concerns with this bit that I had about
//     * OptionalExpression
//     */
//    private static class KleeneExpression extends Expression
//    {
//        Expression optional;
//
//        @Override
//        int getNumStates()
//        {
//            return 0;
//        }
//    }
//
//    /**
//     * I think we'll have to either disallow optional expressions
//     * at the end of expressions or change the way we deal with dfa
//     * completion
//     * a?
//     * ab?a
//     * (a|b)?a
//     */
//    private static class OptionalExpression extends Expression
//    {
//
//        @Override
//        int getNumStates()
//        {
//            //todo
//            return 0;
//        }
//    }
//
//    private static class UnionExpression extends Expression
//    {
//        ArrayList<Expression> branches;
//
//        UnionExpression()
//        {
//            branches = new ArrayList<>();
//        }
//
//        void addBranch(Expression branch)
//        {
//            //todo make sure none of the branches have a common prefix
//            //by turning it into a sequnce expression with the common prefix then a union expression
//            //to ensure that things don't sneak in we must also normalize all union expressions that
//            //are added together, ie ((a|b)|ab) -> (a|b|ab) -> (ab?|b)
//
//            if (branch instanceof UnionExpression)
//            {
//                branches.addAll(((UnionExpression) branch).branches);
//            }
//
//
//            branches.add(branch);
//
//        }
//
//
//        @Override
//        int getNumStates()
//        {
//            return branches.stream().mapToInt(Expression::getNumStates).sum();
//        }
//    }
//
//    //this class is stupid, and should be supplanted by a RangeExpression class and the already extant UnionExpression
//    //since a charset is secretly just a union of single chars,
//    private static class CharSetExpression extends Expression
//    {
//        //todo ranges
//        final char[] chars;
//
//        CharSetExpression(char[] chars)
//        {
//            this.chars = chars;
//        }
//
//        CharSetExpression(char[] buffer, int offset, int length)
//        {
//            //todo for CharSetExpression and LiteralExpression copy from the main patternBuffer
//            //into their own little char[]s, that's stupid, there's no reason we couldn't just maintain
//            //the offsets into the main pattern buffer
//            chars = new char[length];
//            System.arraycopy(buffer, offset, chars, 0, length);
//        }
//
//        @Override
//        int getNumStates()
//        {
//            //will always be exactly one
//            return 1;
//        }
//    }
//
//    //this class should really just contain one character, and sequences of characters should be represented with
//    //a SequenceExpression of LiteralExpressions
//    /**
//     * a sequence of literal characters
//     * //todo should this be a sequence of CharSetExpressions?
//     */
////    private static class CharacterExpression extends Expression
////    {
////        char thisChar = '\0';
////
////        LiteralExpression(char[] chars)
////        {
////            literalChars = chars;
////        }
////
////        LiteralExpression(char[] src, int offset, int length)
////        {
////            literalChars = new char[length];
////            System.arraycopy(src, offset, literalChars, 0, length);
////        }
////
////        @Override
////        int getNumStates()
////        {
////            return 1;
////        }
////    }
//
//    private static final class SequenceExpression extends Expression
//    {
//        final ArrayList<Expression> expressionSequence;
//
//        public SequenceExpression()
//        {
//            this.expressionSequence = new ArrayList<>();
//        }
//
//        public SequenceExpression(int length)
//        {
//            this.expressionSequence = new ArrayList<>(length);
//        }
//
//        public void add(Expression next)
//        {
//            expressionSequence.add(next);
//        }
//
//        @Override
//        int getNumStates()
//        {
//            return expressionSequence.stream().mapToInt(Expression::getNumStates).sum();
//        }
//    }
//
//    private static abstract class Expression
//    {
//        abstract int getNumStates();
//        /**
//         *
//         * Adds all of the DFA states represented by this expression, starting at firstState, and after
//         * all the states in this expression have been transitioned through, the last transition shall be
//         * to linkTo.
//         * returns the next available index in the array
//         */
//        //abstract int addStates(int firstState, int linkTo);
//
//    }
//}

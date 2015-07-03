package se.vgregion.ifeed.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2015-06-30.
 * Structure type to illustrate, roughly, the structure of a JavaScript (could probably be used to other treeish
 * purposes as well).
 */
public class Token implements Serializable, Cloneable {

    private String start, end;

    private final Tokens children = new Tokens();

    private Token parent;

    /*{
        private final List<Token> me = this;
        @Override
        public Object clone() {
            return deepClone(me);
        }
    };*/

    /**
     * Creates an instance.
     * @param start initial value of start.
     * @param end initial value of end.
     */
    public Token(String start, String end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Creates an instance and sets 'start' and 'end' to empty string.
     */
    public Token() {
        start = end = "";
    }

    /**
     * Getter for...
     * @return the children attribute.
     */
    public Tokens getChildren() {
        return children;
    }

    /**
     * Getter for...
     * @return the start attribute.
     */
    public String getStart() {
        return start;
    }

    /**
     * Setter for the start attribute.
     * @param start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Getter for...
     * @return the end attribute.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Setter for the end attribute.
     * @param end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Outputs the text of its 'start' and 'end' attribute with its childrens to-string-values there in-between.
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        for (Token token : getChildren()) {
            sb.append(token.toString());
        }
        sb.append(end);
        return sb.toString();
    }

    /**
     * Concattenates tokens to-string results to one big text.
     * @param tokens items to merge to-strings values.
     * @return resulting mass of text.
     */
    public static String toString(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }

    /**
     * Makes a deep clone of this token/tree-root.
     * @return the clone.
     * @throws CloneNotSupportedException
     */
    @Override
    public Token clone() throws CloneNotSupportedException {
        return (Token) deepClone(this);
    }

    /**
     * General purpose function to make a deep clone of any object - that also must implement the serializable interface.
     * @param object
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepClone(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method implementing the visitor-pattern. It accept one implementation of a Visitor and applies that to one-self
     * and to all ot its children.
     * @param visitor the actor to perform al of the visitations with.
     */
    public void visit(Visitor visitor) {
        ArrayList<Token> children = new ArrayList<Token>(this.children);
        visitor.before(this);
        for (Token token : children) {
            token.visit(visitor);
        }
        visitor.after(this);
    }

    /**
     * Getter for the parent attribute.
     * @return the parent.
     */
    public Token getParent() {
        return parent;
    }

    /**
     * Setter for the parent attribute.
     * @param parent new instance of the parent attribute.
     */
    public void setParent(Token parent) {
        this.parent = parent;
    }

    /**
     * Template for a visitor to use with instances of the Token class.
     * Contains two functions: before and after.
     * The Token.visit calls first 'before' then all of its childrens visit-methods, and lastly it calls the after
     * method itself.
     */
    public static abstract class Visitor {
        /**
         * Is applied before the childrens visit-method has been called with the instance of Visitor in question.
         * @param token the token that currently is being called with its Token.visit.
         */
        public void before(Token token) {

        }

        /**
         * Is applied after the childrens visit-method has been called with the instance of Visitor in question.
         * @param token the token that currently is being called with its Token.visit.
         */
        public void after(Token token) {

        }
    }

    /**
     * Removes this instances from its parent (its parents children list).
     */
    public void removeFromParent() {
        getParent().getChildren().remove(this);
        setParent(null);
    }

    /**
     * Collection implementation that is designed to hold Tokens.
     */
    public static class Tokens extends ArrayList<Token> {

        /**
         * Convinience method to set al of the items parent.
         * @param newParent new parent for all of the items in this collection.
         */
        public void alterParentOfItems(Token newParent) {
            for (Token token : this) {
                token.setParent(newParent);
            }
        }

    }

    /**
     * Finds the first previous Token in this Tokens parents children list - that has a value inside its start-attribute
     * that matches (by regexp) the provided pattern.
     * @param matchingStartTextPattern the pattern to test previous Token(s).start with.
     * @return the found Token or null if no such token exists.
     */
    public Token previous(String matchingStartTextPattern) {
        Tokens siblings = getParent().getChildren();
        int selfIndex = siblings.indexOf(Token.this);
        for (int i = selfIndex; i >= 0; i--) {
            Token sibling = siblings.get(i);
            if (sibling.getStart().trim().matches(matchingStartTextPattern)) {
                return sibling;
            }
        }
        return null;
    }

    /**
     * Finds the first next Token in this Tokens parents children list - that has a value inside its start-attribute
     * that matches (by regexp) the provided pattern.
     * @param matchingStartTextPattern the pattern to test previous Token(s).start with.
     * @return the found Token or null if no such token exists.
     */
    public Token next(String matchingStartTextPattern) {
        Tokens siblings = getParent().getChildren();
        int selfIndex = siblings.indexOf(Token.this);
        for (int i = selfIndex; i < siblings.size(); i++) {
            Token sibling = siblings.get(i);
            if (sibling.getStart().trim().matches(matchingStartTextPattern)) {
                return sibling;
            }
        }
        return null;
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.ArrayList;
import java.util.List;

/**
 * I got this from stack overflow then added some getters
 *
 * @author http://stackoverflow.com/questions/3522454/java-tree-data-structure
 * @author Matthew Vaughan
 */
public class Tree<T> {

   private Node<T> root;

   public Tree(T rootData) {
      root = new Node<T>();
      root.data = rootData;
      root.children = new ArrayList<Node<T>>();
   }

   public Node<T> getNode() {
      return root;
   }

   public static class Node<T> {

      private T data;
      private Node<T> parent;
      private List<Node<T>> children;
      
      public T getData() {
         return data;
      }
      
      public Node<T> getParent() {
         return parent;
      }

      public List<Node<T>> getChildren() {
         return children;
      }

      public Node<T> addChild(Node<T> child) {
         
         child.parent = this;
         
         children.add(child);
         return children.get( children.size() - 1);
      }
   }
}

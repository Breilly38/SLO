/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uml.cs.slo;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for our environment of Deferred Substitutions
 * @author mattvaughan
 */
public class DeferredSubst {
   
   // holds the current defSubst
   private static DeferredSubst deferredSubst   = new DeferredSubst();

   // gives us back the currentInstance
   public static DeferredSubst getInstance()    { return deferredSubst; }
   
   // our environment
   private Map<String,Expression> environment;
   
   // who is our parent defSubst?
   private DeferredSubst parent;
   
   
   public static DeferredSubst setScope( DeferredSubst newScope ) {
      DeferredSubst oldScope = deferredSubst;
      deferredSubst = newScope;
      return oldScope;
   }
   
   public DeferredSubst newScope() {
      DeferredSubst newScope = new DeferredSubst();
      newScope.parent = this;
      return newScope;
   }
   
   public static DeferredSubst restoreScope( DeferredSubst oldScope ) {
      deferredSubst = oldScope;
      return deferredSubst;
   }

   // private constructor... we should only get scopes with newScope, setScope, and getInstance
   private DeferredSubst() {
       environment = new HashMap<String,Expression>();
       parent = null;   // should be null ONLY for the root!
   }
   
   // copy constructor... used by newScope() to provide local scope for function applications
   private DeferredSubst( DeferredSubst df ) {
       environment = new HashMap<String,Expression>(df.environment);
       parent = df.getParent();
   }
   
   public Expression lookup( String name ) {
      
      if ( ! environment.containsKey(name) && this.parent == null ) {
         System.err.println("Error in DeferredSubst.lookup() looking up " + name + " - Unbound identifier!");
         System.exit(1);
      }
      else if ( ! environment.containsKey(name) ) {
         return this.parent.lookup(name);
      }
      
      return environment.get(name);
   }
   
   public DeferredSubst getParent() {
      return parent;
   }
   
   public boolean hasID( String name ) {
      return environment.containsKey(name);
   } 
   
   public void addID( ID id, Expression expr ) {
      
      // get the id name
      String name = id.getName().trim();
      
      // if it's already in the envirnment then warn the user
      if ( environment.containsKey( name ) ) {
         System.err.println("Warning from DeferredSubst.addID() - " + name + " already defined, new value will be used.");
      }
      
      // add the deferred substitution to the environment
      environment.put( name , expr );
   }
}

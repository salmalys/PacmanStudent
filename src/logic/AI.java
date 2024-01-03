package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * class used to represent plan. It will provide for a given set of results an
 * action to perform in each result
 */
class Plans {
	ArrayList<Result> results;
	ArrayList<ArrayList<String>> actions;

	/**
	 * construct an empty plan
	 */
	public Plans() {
		this.results = new ArrayList<Result>();
		this.actions = new ArrayList<ArrayList<String>>();
	}

	/**
	 * add a new pair of belief-state and corresponding (equivalent) actions
	 * 
	 * @param beliefBeliefState the belief state to add
	 * @param action            a list of alternative actions to perform. Only one
	 *                          of them is chosen but their results should be
	 *                          similar
	 */
	public void addPlan(Result beliefBeliefState, ArrayList<String> action) {
		this.results.add(beliefBeliefState);
		this.actions.add(action);
	}

	/**
	 * return the number of belief-states/actions pairs
	 * 
	 * @return the number of belief-states/actions pairs
	 */
	public int size() {
		return this.results.size();
	}

	/**
	 * return one of the belief-state of the plan
	 * 
	 * @param index index of the belief-state
	 * @return the belief-state corresponding to the index
	 */
	public Result getResult(int index) {
		return this.results.get(index);
	}

	/**
	 * return the list of actions performed for a given belief-state
	 * 
	 * @param index index of the belief-state
	 * @return the set of actions to perform for the belief-state corresponding to
	 *         the index
	 */
	public ArrayList<String> getAction(int index) {
		return this.actions.get(index);
	}
}

/**
 * class used to represent a transition function i.e., a set of possible belief
 * states the agent may be in after performing an action
 */
class Result {
	private ArrayList<BeliefState> beliefStates;

	/**
	 * construct a new result
	 * 
	 * @param states the set of states corresponding to the new belief state
	 */
	public Result(ArrayList<BeliefState> states) {
		this.beliefStates = states;
	}

	/**
	 * returns the number of belief states
	 * 
	 * @return the number of belief states
	 */
	public int size() {
		return this.beliefStates.size();
	}

	/**
	 * return one of the belief state
	 * 
	 * @param index the index of the belief state to return
	 * @return the belief state to return
	 */
	public BeliefState getBeliefState(int index) {
		return this.beliefStates.get(index);
	}

	/**
	 * return the list of belief-states
	 * 
	 * @return the list of belief-states
	 */
	public ArrayList<BeliefState> getBeliefStates() {
		return this.beliefStates;
	}
}

/*
 * class Node implements Comparable { public final int value; public final
 * BeliefState state; public final Node dad; public final String action;
 * 
 * public Node(int value, BeliefState state, Node dad, String action) {
 * this.value = value; this.state = state; this.dad = dad; this.action = action;
 * }
 * 
 * @Override public int compareTo(Object o) { if(o instanceof Node) { Node other
 * = (Node) o; return this.value - other.value ; } return Integer.MAX_VALUE; }
 * 
 * @Override public boolean equals(Object o) { if(o == this) { return true; }
 * if(o == null) return false; if(! (o instanceof Node)) return false; Node
 * other = (Node)o; return (state.getPacmanPos().getRow() ==
 * other.state.getPacmanPos().getRow()) && (state.getPacmanPos().getColumn() ==
 * other.state.getPacmanPos().getColumn()) && (state.getNbrOfGommes() ==
 * other.state.getNbrOfGommes()); }
 * 
 * @Override public int hashCode() { return 1; } }
 */

class Tuple<K, V> {
	public final K val1;
	public final V val2;

	public Tuple(K val1, V val2) {
		this.val1 = val1;
		this.val2 = val2;
	}
}

class Node implements Comparable {
	public final int valeur;
	public final BeliefState state;
	public final String action;
	public final Node parent;
	public final int depth;

	public Node(int valeur, BeliefState state, Node dad, String action, int depth) {
		this.state = state;
		this.action = action;
		this.parent = dad;
		this.valeur = valeur;
		this.depth = depth;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Node) {
			Node other = (Node) o;
			return valeur - other.valeur;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof Node))
			return false;
		Node other = (Node) o;
		return other.state.getPacmanPos().equals(state.getPacmanPos())
				&& other.state.getNbrOfGommes() == state.getNbrOfGommes();
	}
}

/**
 * class implement the AI to choose the next move of the Pacman
 */
public class AI {
	
	/**
	 * function that compute the next action to do (among UP, DOWN, LEFT, RIGHT)
	 * 
	 * @param beliefState the current belief-state of the agent
	 * @return a string describing the next action (among PacManLauncher.UP/DOWN/LEFT/RIGHT)
	 */
	public static String findNextMove(BeliefState beliefState) {
		String nextMove = search(beliefState);
		
		//Cas ou la recherche n'aboutit plus, on retourne une action aleatoire
		if (nextMove.equals("default")||nextMove.isEmpty()){
            String[] possibleActions = {PacManLauncher.LEFT, PacManLauncher.UP, PacManLauncher.RIGHT, PacManLauncher.DOWN};
            nextMove = possibleActions[(int)(Math.random()*possibleActions.length)];
            System.out.println(nextMove);
		}
		System.out.println("------------- Action Choisie: "+nextMove+" -------------\n\n");
		return nextMove;
	}

  public static String search(BeliefState initialState) {
	    PriorityQueue<Node> frontier = new PriorityQueue<>();
	    Set<Node> explored = new HashSet<>();
	    int val =  calculateMinimumDistanceToGomme(initialState);
	    Node node = new Node(val, initialState, null, "", 0);
	    frontier.add(node);
	    
	    while (!frontier.isEmpty()) {
	        node = frontier.remove();
	        explored.add(node);
	        
	        Plans possiblePlans = node.state.extendsBeliefState();
	        
	        for (int i = 0; i < possiblePlans.size(); i++) {
	            Result result = possiblePlans.getResult(i);
	            
	            int evaluation = 0;
	            int minDistG = Integer.MAX_VALUE;
	            BeliefState bestChildState = null;
	            
	            for (BeliefState childState : result.getBeliefStates()){
	                int meanDist = calculateMeanMinimumDistanceToGhost(childState);
	                
	                if (meanDist < minDistG) {
	                    minDistG = meanDist;
	                    bestChildState = childState;
	                }
	            }
	            
	            evaluation = node.depth+1 + calculateMinimumDistanceToGomme(bestChildState)-minDistG;
	            int distToNearestGhost = calculateMinimumDistanceToGhost(bestChildState, 0); // Supposons que le fantôme le plus proche est le premier de la liste
	            if (distToNearestGhost < 3) {
	                // Si le fantôme est très proche (par exemple, à moins de 3 cases), réduisez l'évaluation pour éviter de s'approcher davantage
	                evaluation -= 50;
	            }
	            
	            String action = possiblePlans.getAction(i).get(0);
	            
	            Position nextPosition = bestChildState.getPacmanPos();
	            
	            if (nextPosition!=null && bestChildState.getMap()[nextPosition.getRow()][nextPosition.getColumn()]!= '#') {
	                Node child = new Node(evaluation, bestChildState, node, action, node.depth + 1);
	                if (initialState.getScore() < child.state.getScore() && child.state.getLife() > 0) {
	                    while ((child.parent).parent != null) {
	                        child = child.parent;
	                    }
	                    return child.action;
	                }
	                if (explored.contains(child) || frontier.contains(child)) {
	                    continue;
	                }
	                
	                if (!action.isEmpty()) {
	                    frontier.add(child);
	                }
	            }
	        }
	    }
	    return "default";
	}
  
  	public static int calculateMeanMinimumDistanceToGhost(BeliefState state) {
  		int meanDist = 0;
        for (int k = 0; k < state.getNbrOfGhost(); k++) {
            int distToGhost = calculateMinimumDistanceToGhost(state, k);
            meanDist += distToGhost;
        }
        meanDist /= state.getNbrOfGhost();
        return meanDist;
  	}
  
	public static int calculateMinimumDistanceToGhost(BeliefState s, int ghostIndex) {
		TreeSet<Position> possiblePositions = s.getGhostPositions(ghostIndex);
		Position pacman = s.getPacmanPos();
		int minDistance = Integer.MAX_VALUE;
		for (Position p : possiblePositions) {
			int distance = Math.abs(pacman.getRow() - p.getRow()) + Math.abs(pacman.getColumn() - p.getColumn());
			minDistance = Math.min(minDistance, distance);
		}

		return minDistance;
	}

	public static int calculateMinimumDistanceToGomme(BeliefState beliefState) {
		char[][] map = beliefState.getMap();
		int minDist = Integer.MAX_VALUE;
		Position pos = beliefState.getPacmanPos();
		int row = pos.getRow(), col = pos.getColumn();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == '*' || map[i][j] == '.') {
					minDist = Math.min(minDist, Math.abs(i - row) + Math.abs(j - col));
				}
			}
		}
		return minDist;
	}

}
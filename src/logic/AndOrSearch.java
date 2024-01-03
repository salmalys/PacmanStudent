package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class AndOrSearch {
    private static final double SCORE_WEIGHT = 1.5;
    private static final double GOMMES_WEIGHT = -0.3;
    private static final double SUPER_GOMMES_WEIGHT = -0.4;
    private static final double PACMAN_DISTANCE_WEIGHT = -0.5;

    private BeliefState currentState;

    public AndOrSearch(BeliefState initialState) {
        this.currentState = initialState;
    }
    
	public static Tuple<Integer, Integer> minDistGum(BeliefState beliefState) {
		char[][] map = beliefState.getMap();
		int minDist = Integer.MAX_VALUE;
		int minDistSuper = Integer.MAX_VALUE;
		Position pos = beliefState.getPacmanPos();
		int row = pos.getRow(), col = pos.getColumn();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == '*' || map[i][j] == '.') {
					minDist = Math.min(minDist, Math.abs(i - row) + Math.abs(j - col));
				}
				if (map[i][j] == '*') {
					minDistSuper = Math.min(minDistSuper, Math.abs(i - row) + Math.abs(j - col));
				}
			}
		}
		return new Tuple<>(minDist, minDistSuper);
	}

	public static int minDistGhost(BeliefState s, int ghost) {

		TreeSet<Position> pos = s.getGhostPositions(ghost);
		Position pacman = s.getPacmanPos();
		int minDist = Integer.MAX_VALUE;
		for (Position p : pos) {
			int dist = Math.abs(pacman.getRow() - p.getRow()) + Math.abs(pacman.getColumn() - p.getColumn());
			if (dist < minDist)
				minDist = dist;
		}

		return minDist;
	}

	public static boolean hasEatenPowerUp(BeliefState state) {
		int pacManX = state.getPacmanPosition().getRow();
		int pacManY = state.getPacmanPosition().getColumn();
		char[][] map = state.getMap();
		double minDistance = Double.MAX_VALUE;

		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				if (map[x][y] == '.' || map[x][y] == '*') { // Vérifiez les gommes ou super gommes
					double distance = Math.abs(pacManX - x) + Math.abs(pacManY - y);
					if (distance < minDistance) {
						minDistance = distance;
					}
				}
			}
		}

			// cela signifie que Pac-Man a mangé une super gomme
		if (minDistance < 0.1) {
			return true;
		}

		// Pac-Man n'a pas mangé de super gomme
		return false;
	}
    
	  public static int evaluate(BeliefState state) {
	        int evaluation = 0;

	        // Ajoutez un terme basé sur la distance aux fantômes
	        for (int k = 0; k < state.getNbrOfGhost(); k++) {
	            int distToGhost = minDistGhost(state, k);
	            evaluation -= distToGhost; // Plus la distance est courte, plus l'évaluation est basse
	        }

	        // Ajoutez un terme pour manger les fantômes effrayés s'ils sont présents dans l'état suivant
	        if (hasEatenPowerUp(state)) {
	            for (int k = 0; k < state.getNbrOfGhost(); k++) {
	                int fearCounter = state.getCompteurPeur(k);
	                if (fearCounter > 0) {
	                    evaluation += 50; // Ajoutez une valeur positive appropriée
	                }
	            }
	        }

	        // Ajoutez un terme pour encourager l'évitement des fantômes proches
	        int distToNearestGhost = minDistGhost(state, 0); // Supposons que le fantôme le plus proche est le premier de la liste
	        if (distToNearestGhost < 3) {
	            evaluation -= 50; // Réduisez l'évaluation si le fantôme est très proche (à moins de 3 cases)
	        }

	        // Ajoutez un terme positif pour maximiser le score
	        evaluation += state.getScore() * 50;

	        return evaluation;
	    }

    // Fonction heuristique modifiée
    private double heuristic(BeliefState state) {
        double scoreComponent = SCORE_WEIGHT * state.getScore();
        double gommesComponent = GOMMES_WEIGHT * state.getNbrOfGommes();
        double superGommesComponent = SUPER_GOMMES_WEIGHT * state.getNbrOfSuperGommes();
        double pacmanDistanceComponent = PACMAN_DISTANCE_WEIGHT * calculateDistanceToGommes(state);

        // Affichage pour le débogage
        System.out.println("Score Component: " + scoreComponent);
        System.out.println("Gommes Component: " + gommesComponent);
        System.out.println("Super Gommes Component: " + superGommesComponent);
        System.out.println("Pacman Distance Component: " + pacmanDistanceComponent);

        return scoreComponent + gommesComponent + superGommesComponent + pacmanDistanceComponent;
    }

    // Méthode de recherche modifiée pour augmenter la profondeur
    public String andOrSearch(BeliefState state, int depth) {
        if (depth == 0 || isGoalState(state)) {
            return null;
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        String bestAction = null;
        Plans possiblePlans = state.extendsBeliefState();

        for (int i = 0; i < possiblePlans.size(); i++) {
            Result result = possiblePlans.getResult(i);
            double value = 0;
            for (BeliefState nextState : result.getBeliefStates()) {
                String action = andOrSearch(nextState, depth - 1);
                if (action != null) {
                    value += evaluate(nextState);
                }
            }

            if (value > bestValue) {
                bestValue = value;
                bestAction = possiblePlans.getAction(i).get(0);
            }
        }

        return bestAction;
    }

    private boolean isGoalState(BeliefState state) {
        return state.getNbrOfGommes() == 0 || state.getLife() <= 0;
    }
   
    private double calculateDistanceToGommes(BeliefState state) {
        int pacManX = state.getPacmanPosition().getRow();
        int pacManY = state.getPacmanPosition().getColumn();
        double minDistance = Double.MAX_VALUE;

        char[][] map = state.getMap();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == '.' || map[x][y] == '*') { // Check for gums or super gums
                    double distance = Math.abs(pacManX - x) + Math.abs(pacManY - y);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }

        return minDistance;
    }
}
package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AndOrSearch {
    private static final double SCORE_WEIGHT = 1.5;
    private static final double GOMMES_WEIGHT = -0.3;
    private static final double SUPER_GOMMES_WEIGHT = -0.4;
    private static final double PACMAN_DISTANCE_WEIGHT = -0.5;

    private BeliefState currentState;

    public AndOrSearch(BeliefState initialState) {
        this.currentState = initialState;
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
                    value += heuristic(nextState);
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
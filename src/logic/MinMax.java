package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinMax {
    private static String[] actions = { "UP", "DOWN", "LEFT", "RIGHT" };
    private BeliefState initial_state;

    public MinMax(BeliefState initialState) {
        this.initial_state = initialState;
    }

    public String updateGame(int hauteur, BeliefState courant_state) {
        Plans plan = courant_state.extendsBeliefState();
        double max_value = Double.NEGATIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        String best_action = null;

        for (int i = 0; i < plan.results.size(); i++) {
            Result states = plan.getResult(i);

            // Si deux actions, cela signifie que les deux actions n'ont pas pu être
            // effectuées, donc elles sont invalides
            /* prendre une deux !! */
            if (plan.getAction(i).size() > 1) {
                continue;
            }

            // On sélectionne l'action si elle est valide
            String action = plan.getAction(i).get(0);
            double value;
            System.out.println("=================");
            System.out.println("Action : " + action);

            // On parcourt tous les états potentiels en effectuant l'action "action"
            for (BeliefState possible_state : states.getBeliefStates()) {
                value = Min_value(hauteur - 1, possible_state, alpha, beta);
                System.out.println("Value  : " + value);

                if (value > max_value) {
                    max_value = value;
                    best_action = action;
                }
            }
        }
        System.out.println("===============================================");

        return best_action;
    }

    public double Max_value(int hauteur, BeliefState courant_state, double alpha, double beta) {
        if (hauteur == 0 || courant_state.getLife() == 0 || courant_state.getNbrOfGommes() == 0) {
            return heuristique(courant_state);
        } else {
            double value = Double.NEGATIVE_INFINITY;
            Plans plan = courant_state.extendsBeliefState();

            for (int i = 0; i < plan.results.size(); i++) {
                Result states = plan.getResult(i);

                // Si pacman est dans un couloir
                if (plan.getAction(i).size() > 1) {
                    continue;
                }

                for (BeliefState possible_state : states.getBeliefStates()) {
                    value = Math.max(value, Min_value(hauteur - 1, possible_state, alpha, beta));

                    // Élagage alpha-beta
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return value;
        }
    }

    public double Min_value(int hauteur, BeliefState courant_state, double alpha, double beta) {
        if (hauteur == 0 || courant_state.getLife() == 0 || courant_state.getNbrOfGommes() == 0) {
            return heuristique(courant_state);
        } else {
            double value = Double.POSITIVE_INFINITY;
            Plans plan = courant_state.extendsBeliefState();

            for (int i = 0; i < plan.results.size(); i++) {
                Result states = plan.getResult(i);

                // Si pacman est dans un couloir
                if (plan.getAction(i).size() > 1) {
                    continue;
                }

                for (BeliefState possible_state : states.getBeliefStates()) {
                    value = Math.min(value, Max_value(hauteur - 1, possible_state, alpha, beta));

                    // Élagage alpha-beta
                    beta = Math.min(beta, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return value;
        }
    }

    public double heuristique(BeliefState courant_state) {
        return courant_state.getScore() * 0.4 + courant_state.getNbrOfGommes() * 0.1
                + Math.abs(courant_state.compareTo(initial_state)) * 0.5;

        /*
         * int heuristicValue += SCORE_WEIGHT * courant_state.getScore();
         * 
         * // Nombre de Gommes
         * heuristicValue += GOMMES_WEIGHT * courant_state.getNbrOfGommes();
         * 
         * // Nombre de Super Gommes
         * heuristicValue += SUPER_GOMMES_WEIGHT * courant_state.getNbrOfSuperGommes();
         * 
         * // Distance de Pac-Man aux Gommes
         * heuristicValue += PACMAN_DISTANCE_WEIGHT *
         * Math.abs(courant_state.compareTo(initial_state));
         * 
         * // Proximité des Fantômes
         * heuristicValue += GHOST_PROXIMITY_WEIGHT *
         * calculateGhostProximity(listPGhost, pacmanPos);
         * 
         * // Nombre de Vies
         * heuristicValue += LIFE_WEIGHT * life;
         */
    }
}

package logic;

class Pair{
    private String action ;
    private Double value;

    public Pair(String first, Double second) {
        this.action = first;
        this.value = second;
    }

    public String getAction() {
        return this.action;
    }

    public Double getValue() {
        return this.value;
    }
}

public class OrAndTreeBis
{	
	public BeliefState initial_state;
	public static final double SCORE_WEIGHT= 1;
    public static final double GOMMES_WEIGHT = 0;
    public static final double SUPER_GOMMES_WEIGHT = 0;
    public static final double PACMAN_DISTANCE_WEIGHT = 0;

	public OrAndTreeBis(BeliefState initial_state) {
		this.initial_state = initial_state;
	}
	
	public String strategie(BeliefState racine, int depth) {
		return orBranch(racine, depth).getAction();
	}
	public Pair orBranch(BeliefState racine, int depth)
	{
		/*On choisit l'action qui maximise l'heuristique*/
		double max = Double.NEGATIVE_INFINITY;
		String returned_action = "UP";
		Plans p = racine.extendsBeliefState();
		/*Appliquer toutes les actions possibles à partir de cet état (l'état initial)*/
		for(int i = 0; i < p.size(); i++)
		{		
			String action = p.getAction(i).get(0);
			/*On applique l'action sur l'état courant*/
			Result r = p.getResult(i);
			System.out.println("===========================");
			System.out.println("Action : "+action);
			System.out.println("Nombre d'état potentiel "+r.size());
			double value = andBranch(r, depth-1);
			System.out.println("Valeur renvoyé "+ value);
			if(value > max) {
				max = value;
				returned_action = action;
			}
			System.out.println("=================================================================================");
		}
		System.out.println("Best action : "+returned_action);
		return new Pair(returned_action, max);
	}
	
	public double andBranch(Result racine, int depth)
	{	
		
		int i = 0;
		BeliefState s =  null;
		double total = 0;
		while(i < racine.size()) {
			/*on parcourt tous les états*/
			s = racine.getBeliefState(i);
			/*si l'état est un état finale(feuille) on calcule  l'heuristique sinon on l'étend*/
			if(depth <= 0 ||s.getNbrOfGommes() <= 0 || s.getLife()<= 0) {
				total += heuristique(s);
			}
			else {
				total += orBranch(s, depth-1).getValue();
			}
			
			i ++ ;
		}
		return total/racine.size();
	}
	
	private double heuristique(BeliefState s)
	{
		
		double heuristicValue = SCORE_WEIGHT * s.getScore();
        // Nombre de Gommes
        heuristicValue += GOMMES_WEIGHT * s.getNbrOfGommes();
        // Nombre de Super Gommes
        heuristicValue += SUPER_GOMMES_WEIGHT * s.getNbrOfSuperGommes();
        // Distance de Pac-Man aux Gommes
        heuristicValue += PACMAN_DISTANCE_WEIGHT * Math.abs(s.compareTo(initial_state));
        
        return heuristicValue;
	}
}

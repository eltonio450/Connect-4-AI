
public class Memo {
	private final static int SIZE_OF_DEPTH_MEMORY = 50000000; // Max avec 2Gb : 45 000 000
	private final static int SIZE_OF_RECENT_MEMORY = 1000000; // Max avec 2Gb (et depth mem 45M : 15M)
	private static myHashMap memoireProfondeur = new myHashMap (SIZE_OF_DEPTH_MEMORY, true); // Mémoire donnant la priorité aux premiers coups dans la partie
	private static myHashMap memoireRecente = new myHashMap (SIZE_OF_RECENT_MEMORY, false);	 // Mémoire donnant la priorité aux derniers coups examinés
	public static int DEPTH_MAX = 35;														 // Pour éviter d'essayer d'ajouter des coups trop profonds à la table de coups de début de partie
	public static int leaveVisited = 0;
	private static long timeIni = System.currentTimeMillis();


	/**
	 * Ajoute (ou non, selon la stratégie) le tableau t associé au score score dans les tables
	 * 
	 * @param t
	 * @param score
	 * 
	 */
	public static void add (TableauPositionV3 t, int score, int depth) {		
		if (depth < DEPTH_MAX) {
			// Infos facultatives
			if (depth <= 2) {
				System.out.println("Coup de profondeur "+ depth + " analysé");
				System.out.println("Temps " + (System.currentTimeMillis() - timeIni));
				System.out.print("Memoire profondeur : ");
				memoireProfondeur.printInfos();
				System.out.print("Memoire récente : ");
				memoireRecente.printInfos();
				System.out.println("Leaves visited : " + leaveVisited);
			}

			// Stockage
			if(!memoireProfondeur.put(t.tableau.a, t.tableau.b, score, 100-depth))
				memoireRecente.put(t.tableau.a, t.tableau.b, score);
		} else {
			memoireRecente.put(t.tableau.a, t.tableau.b, score);
		}
	}

	/**
	 * Cherche le tableau adans les tables
	 * @param t : position qu'on cherche
	 * @return : le score associé s'il est stocké, -1001 sinon
	 * 
	 */	
	public static int get (TableauPositionV3 t) {
		leaveVisited ++;

		PairLong p = t.tableau;

		Integer retour = memoireProfondeur.get(p.a, p.b);
		if (retour != -1001) {
			return retour;
		}

		return memoireRecente.get(p.a, p.b);
	}

	/**
	 * Réinitialise les tables
	 * @param verbose
	 */
	public static void flush (boolean verbose) {
		if (verbose) {
			System.out.print("Memoire profondeur : ");
			memoireProfondeur.printInfos();
			System.out.print("Memoire récente : ");
			memoireRecente.printInfos();
			System.out.println("Leaves visited : " + leaveVisited);
		}
		memoireProfondeur.clear();
		memoireRecente.clear();
	}
	
	/** Supprime une entrée
	 * 
	 * @param a
	 * @param b
	 */
	public static void delete (long a, long b) {
		memoireRecente.delete(a, b);
		memoireProfondeur.delete(a,b);
	}
}

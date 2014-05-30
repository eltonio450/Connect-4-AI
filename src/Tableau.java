
public interface Tableau {
	PairLong getVersionCompacte();
	
	boolean blancsJouent();
	boolean colonneLibre(int colonne);

	int largeur ();
	int hauteur ();
	
	void ajouterJeton(int colonne);
	void retirerJeton(int colonne);
	void imprimerTableau();
	int coupGagnant(int colonne);
}

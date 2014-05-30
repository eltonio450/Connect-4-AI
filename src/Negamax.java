
// Les blancs maximisent
// Pas très optimal (deux fois le même code) mais évite de s'embrouiller avec les signes
// J'ai déplacé l'enum Joueur {BLANC, NOIR} en tête du fichier Main

public class Negamax {
	/**
	 * Fonction negamax
	 * @param t tableau duquel on part
	 * @param j Joueur qui joue (contenu dans le tableau mais bref.)
	 * @return -1000 : Victoire noire, 1000 : Victoire blanche, 0 : pat
	 * 
	 * 
	 */	
	static int [] colonnes;
	static int largeur;
	static int [] ordre = new int[] {0,4,2,5,1,3,6};

	public static void iniMax(int largeur) {
		colonnes = new int [largeur];
		colonnes[0] = largeur/2;
		if (largeur%2 == 1) {
			for (int i = 1; i <= largeur/2; i++) {
				colonnes[2*i-1] = largeur/2 - i;
				colonnes[2*i] = largeur/2 + i;		
			}
		} else {
			for (int i=1; i < largeur/2; i++) {
				colonnes[2*i-1] = largeur/2 -i;
				colonnes[2*i] = largeur/2 +i;
			}
			colonnes[largeur-1] = 0;
		}
		Negamax.largeur = largeur;
		
		
	}

	public static int negamax (TableauPositionV3 t, int depth) {
		int resultat, buff, j = 0;

		resultat = Memo.get(t);
		
		if (resultat != -1001) {
			return resultat;
		}

		
		if (t.blancsJouent()) {
			// maximisation du score
			resultat = -1001;
			
			for (int i=0; i<7 && resultat < 1000; i++) {
				if(ordre[i] >= largeur)
					i++;
				else
				{
					
					if (t.colonneLibre(ordre[i])) 
					{
						t.ajouterJeton(ordre[i]);
	
	
						// Si ce coup est gagnant
						if (t.coupGagnant(ordre[i]) == 1000) {
							resultat = 1000;
						}
						else {
							buff = negamax (t, depth+1);
							if (buff > resultat)
								resultat = buff;
						}
	
						
	
						t.retirerJeton(ordre[i]);
					}
				}
			}
		}


		else {
			// minimisation du score
			resultat = 1001;
			for (int i=0; i<7 && resultat > -1000; i++) {
				if(ordre[i]>=largeur)
					i++;
				else
				{
					if (t.colonneLibre(ordre[i])) {
						t.ajouterJeton(ordre[i]);
	
						// Si ce coup est gagnant
						if (t.coupGagnant(ordre[i]) == -1000) {
							buff = -1000;	
						}
						else {
							buff = negamax (t, depth+1);
						}
	
						if (buff < resultat)
							resultat = buff;
	
						t.retirerJeton(ordre[i]);
					}
				}
			}
		}

		if (resultat == -1001 || resultat == 1001)	// Plus de colonne libre
			return 0;

		Memo.add(t, resultat, depth);
		return resultat;
	}



/*
	public static int getNextMove(TableauPosition t) {
		int resultat, buff;
		int nextMove = -1;
		resultat = Memo.get(t);
		if (resultat != -1001) {
			return resultat;
		}


		if (t.blancsJouent()) {
			// maximisation du score
			resultat = -1001;
			for (int colonne = 0; colonne < t.largeur() && resultat < 1000; colonne ++) {
				if (t.colonneLibre(colonne)) {
					t.ajouterJeton(colonne);

					// Si ce coup est gagnant
					if (t.coupGagnant(colonne) == 1000) {
						buff = 1000;
					}
					else {
						buff = negamax (t, 1);
					}

					if (buff > resultat) {
						resultat = buff;
						nextMove = colonne;
					}

					t.retirerJeton(colonne);
				}
			}
		}


		else {
			// minimisation du score
			resultat = 1001;
			for (int colonne = 0; colonne < t.largeur() && resultat > -1000; colonne ++) {
				if (t.colonneLibre(colonne)) {
					t.ajouterJeton(colonne);

					// Si ce coup est gagnant
					if (t.coupGagnant(colonne) == -1000) {
						buff = -1000;
						System.out.println("Blo");
					}
					else {
						buff = negamax (t, 1);
					}

					if (buff < resultat) {
						resultat = buff;
						nextMove = colonne;
					}

					t.retirerJeton(colonne);
				}
			}
		}

		if (resultat == -1001 || resultat == 1001)	// Plus de colonne libre
			return 0;

		Memo.add(t, resultat, 0);
		return nextMove;
	}
	*/
}


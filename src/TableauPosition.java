import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableauPosition implements Tableau{

	//variables strictement nécessaires

	private Jeton[][] tableau;
	public final int largeur;
	public final int hauteur;
	public boolean blancsJouent;
	private PairLong versionCompacte;

	//variables ajoutées pour plus d'efficacité 

	public final int[] hauteurColonne;



	//constructeur du tableau dans le cas où on le veut vide
	public TableauPosition(int n, int m){

		tableau = new Jeton[n][m];

		largeur = m;
		hauteur = n;

		blancsJouent=true;


		//remplissage à "VIDE" à la création
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				tableau[i][j] = Jeton.VIDE;
		
		hauteurColonne = new int[largeur];
		for (int i=0; i<largeur; i++) {
			hauteurColonne[i] = 0;
			for (int j=hauteur-1; j>=0; j--) {
				if (tableau[j][i] != Jeton.VIDE)
					hauteurColonne[i] ++;
				else break;
			}
		}
		
		versionCompacte = new PairLong(0,0);
	}

	//Constructeur qui remplit un tableau depuis un fichier (à peu près valide)
	public TableauPosition(String nomDuFichier){

		Pattern regexpTailleTableau = Pattern.compile("^([1-9])x([1-9]).*");
		Pattern regexpPositions = Pattern.compile("^((@|0|\\.)+)$");
		Matcher t;
		int i = 0,j = 0;

		versionCompacte = new PairLong(0,0);

		//Cette partie met la position dans une chaine de caractères en utilisant les expressions régulières

		/*Difficultés : 
		 * Supprimer la ligne de commentaire
		 * => on trie les lignes selon leur match à une expression regulière
		 * Gérer le fait que certains sont sur une seule ligne 
		 * => on met toutes les positions sur une seule ligne, pas gênant car on connait la taille du tableau
		 */

		Scanner scanner;
		
		int buffLargeur = 0, buffHauteur = 0;
		
		
		try {
			scanner = new Scanner(new File(nomDuFichier));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				t = regexpTailleTableau.matcher(line);

				//création du tableau vide, normalement dès la première ligne. 

				if(t.matches()){				
					buffLargeur = Integer.parseInt(t.group(1));
					buffHauteur = Integer.parseInt(t.group(2));
					tableau = new Jeton[buffHauteur][buffLargeur];
					blancsJouent=true;

				}

				t = regexpPositions.matcher(line);

				if(t.matches())
				{
					for(j=0;j<buffLargeur;j++)
					{
						switch(t.group(1).charAt(j)){
						case('.'):
						{
							this.setState(i, j, Jeton.VIDE);
							break;
						}
						case('@'):
						{
							this.setState(i, j, Jeton.NOIR);
							
							blancsJouent = !blancsJouent;
							break;
						}
						case('0'):
						{
							this.setState(i, j, Jeton.BLANC);
							blancsJouent = !blancsJouent;
							break;
						}

						}
					}

					i++;

				}

			}
			scanner.close();
			//this.imprimerTableau();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		hauteur = buffHauteur;
		largeur = buffLargeur;
		
		hauteurColonne = new int[largeur];
		for (i=0; i<largeur; i++) {
			hauteurColonne[i] = 0;
			for (j=hauteur-1; j>=0; j--) {
				if (tableau[j][i] != Jeton.VIDE)
					hauteurColonne[i] ++;
				else break;
			}
		}
		
		versionCompacte = new PairLong(0,0);
	}
	
	/**
	 * 
	 * @param ligne
	 * @param colonne
	 * @return le jeton
	 */
	public Jeton getPosition(int ligne, int colonne) {
		return tableau[ligne][colonne];
	}

	//Fonction definit autoritairement l'état d'une case du tableau
	private void setState(int i, int j, Jeton type){
		try{
			tableau[i][j] = type;
		} catch (Exception e){
			System.out.println(e);
		}

	}

	/** Fonction qui ajoute le jeton dans la colonne
	 * 
	 * @param int colonne
	 * @return -1000 si noirs gagnent, +1000 si blancs gagnent, 0 sinon.
	 * Change le joueur courant en conséquence
	 */
	public void ajouterJeton(int colonne)
	{
		assert hauteurColonne[colonne] < hauteur;
		int ligne = hauteur-hauteurColonne[colonne]-1;
		tableau[ligne][colonne] = blancsJouent ? Jeton.BLANC : Jeton.NOIR;
		
		if (blancsJouent)
			versionCompacte.a = versionCompacte.a | (1L << (ligne*largeur + colonne));
		else
			versionCompacte.b = versionCompacte.b | (1L << (ligne*largeur + colonne));
				
		blancsJouent = !blancsJouent;
		hauteurColonne[colonne] ++;
	}

	/** 
	 * @param int colonne : dernière position changée
	 * @return -1000 si noirs gagnent, +1000 si blancs gagnent, 0 sinon.
	 *
	 */	
	public int coupGagnant (int colonne) {
		int ligne = hauteur-hauteurColonne[colonne];
		Jeton j = tableau[ligne][colonne];
		int buff, i;

		// Vertical
		for (i=1; i<4 && ligne+i<hauteur && tableau[ligne+i][colonne] == j; i++) {}
		buff = i-1;
		for (i=1; i<4 && ligne-i>=0 && tableau[ligne-i][colonne] == j; i++) {}
		if (i+buff >= 4)
			return (j == Jeton.BLANC) ? 1000 : -1000;

		// Diagonale 1
		for (i=1; i<4 && colonne+i<largeur && ligne+i<hauteur && tableau[ligne+i][colonne+i] == j; i++) {}
		buff = i-1;
		for (i=1; i<4 && colonne-i>=0 && ligne-i>=0 && tableau[ligne-i][colonne-i] == j; i++) {}
		if (i+buff >= 4)
			return (j == Jeton.BLANC) ? 1000 : -1000;

		// Horizontal
		for (i=1; i<4 && colonne+i<largeur && tableau[ligne][colonne+i] == j; i++) {}
		buff = i-1;
		for (i=1; i<4 && colonne-i>=0 && tableau[ligne][colonne-i] == j; i++) {}
		if (i+buff >= 4)
			return (j == Jeton.BLANC) ? 1000 : -1000;

		// Diagonale 1
		for (i=1; i<4 && colonne+i<largeur && ligne-i>=0 && tableau[ligne-i][colonne+i] == j; i++) {}
		buff = i-1;
		for (i=1; i<4 && colonne-i>=0 && ligne+i<hauteur && tableau[ligne+i][colonne-i] == j; i++) {}
		if (i+buff >= 4)
			return (j == Jeton.BLANC) ? 1000 : -1000;
		
		return 0;
	}

	/**
	 * 
	 * @param colonne : colonne dont le jeton est retiré
	 * Change le joueur courant en conséquence
	 */
	public void retirerJeton(int colonne) {
		assert hauteurColonne[colonne] > 0;

		int ligne = hauteur - hauteurColonne[colonne];
		
		tableau[ligne][colonne] = Jeton.VIDE;
		blancsJouent = !blancsJouent;
		hauteurColonne[colonne]--;
		
		if (blancsJouent) {
			//System.out.println(ligne + " - " + colonne + " - " + versionCompacte.a);
			versionCompacte.a = versionCompacte.a ^ (1L << (largeur*ligne + colonne));
			//System.out.println(versionCompacte.a);
		}
		else
			versionCompacte.b = versionCompacte.b ^ (1L << (largeur*ligne + colonne));
	}

	public boolean colonneLibre (int colonne) {
		return hauteurColonne[colonne] < hauteur;
	}

	public int largeur () {
		return this.largeur;
	}

	//Fonction qui imprimer le tableau en cours
	public void imprimerTableau(){
		System.out.println("Largeur : " + largeur + ", Hauteur : " + hauteur);
		for(int i = 0; i < hauteur; i++)
		{
			for(int j = 0; j < largeur; j++)
			{
				switch(tableau[i][j])
				{
				case BLANC:
					if(j == 0)
						System.out.print('|');
					System.out.print('O');
					break;
				case NOIR:
					if(j == 0)
						System.out.print('|');
					System.out.print('X');
					break;
				case VIDE:
					if(j == 0)
						System.out.print('|');
					System.out.print(' ');
					break;
				}
			}
			System.out.print("|\n");

		}
		if(blancsJouent)
			System.out.println("C'est aux blancs de jouer.");
		else
			System.out.println("C'est aux noirs de jouer.");
	}
	
	public boolean blancsJouent() {
		return blancsJouent;
	}
	
	public PairLong getVersionCompacte() {
		return versionCompacte;
	}
	
	public int hauteur () {
		return this.hauteur;
	}
}
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableauPositionV3 implements Tableau {

	//variables strictement nécessaires

	public final int largeur;
	public final int hauteur;
	public boolean blancsJouent;
	private PairLong tableau;

	//variables ajoutées pour plus d'efficacité 

	public final int[] hauteurColonne;



	/**
	 * Constructeur pour un tableau vide
	 * @param n hauteur
	 * @param m largeur
	 */
	public TableauPositionV3(int n, int m){
		largeur = m;
		hauteur = n;

		blancsJouent=true;

		this.tableau = new PairLong(0,0);
		this.hauteurColonne = new int [largeur];
		for (int i = 0; i< largeur; i++)
			hauteurColonne[i] = 0;
	}

	/**
	 * Constructeur qui remplit un tableau depuis un fichier
	 * @param nomDuFichier
	 */
	public TableauPositionV3(String nomDuFichier){

		Pattern regexpTailleTableau = Pattern.compile("^([1-9])x([1-9]).*");
		Pattern regexpPositions = Pattern.compile("^((@|0|\\.)+)$");
		Matcher t;
		int i = 0,j = 0;



		//Cette partie met la position dans une chaine de caractères en utilisant les expressions régulières

		/*Difficultés : 
		 * Supprimer la ligne de commentaire
		 * => on trie les lignes selon leur match à une expression regulière
		 * Gérer le fait que certains sont sur une seule ligne 
		 * => on met toutes les positions sur une seule ligne, pas gênant car on connait la taille du tableau
		 */

		Scanner scanner;



		this.tableau = new PairLong(0,0);
		int buffLargeur = 0, buffHauteur = 0;
		int[] buffHauteurColonne = null;

		try {
			scanner = new Scanner(new File(nomDuFichier));
			
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				t = regexpTailleTableau.matcher(line);

				//création du tableauBuff vide, normalement dès la première ligne. 

				if(t.matches()){				
					buffLargeur = Integer.parseInt(t.group(1));
					buffHauteur = Integer.parseInt(t.group(2));
					blancsJouent=true;
					buffHauteurColonne = new int[buffLargeur];
					for (int m=0; m<buffLargeur; m++)
						buffHauteurColonne[m] = 0;
				}

				t = regexpPositions.matcher(line);

				if(t.matches())
				{
					for(j=0;j<buffLargeur;j++)
					{
						switch(t.group(1).charAt(j)){
						case('@'):
						{
							tableau.b = tableau.b | (1L << ((buffHauteur-i-1)*8 + j));
							blancsJouent = !blancsJouent;
							break;
						}
						case('0'):
						{
							tableau.a = tableau.a | (1L << ((buffHauteur-i-1)*8 + j));
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
		hauteurColonne = buffHauteurColonne;
	}


	/** Fonction qui ajoute le jeton dans la colonne
	 * 
	 * @param int colonne
	 * @return -1000 si noirs gagnent, +1000 si blancs gagnent, 0 sinon.
	 * Change le joueur courant en conséquence
	 */
	public void ajouterJeton(int colonne)
	{
		if (blancsJouent)
			tableau.a = tableau.a | (1L << (hauteurColonne[colonne]*8 + colonne));
		else
			tableau.b = tableau.b | (1L << (hauteurColonne[colonne]*8 + colonne));

		blancsJouent = !blancsJouent;
		hauteurColonne[colonne] ++;
	}

	/** 
	 * Vérifie la victoire ou défaite
	 * A priori pas très utile de donner la colonne du dernier coup
	 * (Servait avant, gardé dans l'interface mais à supprimer)
	 * 
	 * @return -1000 si noirs gagnent, +1000 si blancs gagnent, 0 sinon.
	 * 
	 * Attention : à appeler APRES un ajouter colonne. Vérifiera le dernier emplacement rempli de la colonne
	 * 					pour le joueur qui ne joue pas (celui qui vient de jouer, donc)
	 *
	 */	
	public int coupGagnant (int colonne) {
		int position = (hauteurColonne[colonne]-1)*8 + colonne;
		

		if (!blancsJouent) {
			long buff2 = (tableau.a & tableau.a << 1 & tableau.a << 2 & tableau.a << 3) | (tableau.a & tableau.a << 8 & tableau.a << 16 & tableau.a << 24)
					| (tableau.a & tableau.a << 7 & tableau.a << 14 & tableau.a << 21) | (tableau.a & tableau.a << 9 & tableau.a << 18 & tableau.a << 27);
				
			if (buff2 != 0)
				return 1000;
		}

		else {
			long buff2 = (tableau.b & tableau.b << 1 & tableau.b << 2 & tableau.b << 3) | (tableau.b & tableau.b << 8 & tableau.b << 16 & tableau.b << 24)
					| (tableau.b & tableau.b << 7 & tableau.b << 14 & tableau.b << 21) | (tableau.b & tableau.b << 9 & tableau.b << 18 & tableau.b << 27);

			
			if (buff2 != 0)
				return -1000;
		}
		
		return 0;
	}

	/**
	 * 
	 * @param colonne : colonne dont le jeton est retiré
	 * Change le joueur courant en conséquence
	 */
	public void retirerJeton(int colonne) {		
		blancsJouent = !blancsJouent;
		hauteurColonne[colonne]--;

		if (blancsJouent) {
			tableau.a = tableau.a ^ (1L << (8*hauteurColonne[colonne] + colonne));
		}
		else
			tableau.b = tableau.b ^ (1L << (8*hauteurColonne[colonne] + colonne));
	}

	public boolean colonneLibre (int colonne) {
		return hauteurColonne[colonne] < hauteur;
	}

	public int largeur () {
		return this.largeur;
	}

	/**
	 * Imprime le tableau en cours
	 */
	public void imprimerTableau(){
		Jeton [][] tableauBuff = new Jeton[hauteur][largeur];
		for (int i=0; i<hauteur; i++) {
			for (int j=0; j<largeur; j++) {
				if (((tableau.a >> (8*(hauteur-i-1)) + j) & 1) == 1)
					tableauBuff[i][j] = Jeton.BLANC;
				else if (((tableau.b >> (8*(hauteur-i-1) + j)) & 1) == 1)
					tableauBuff[i][j] = Jeton.NOIR;
				else tableauBuff[i][j] = Jeton.VIDE;
			}
		}
		System.out.println("Largeur : " + largeur + ", Hauteur : " + hauteur);
		for(int i = 0; i < hauteur; i++)
		{
			for(int j = 0; j < largeur; j++)
			{
				switch(tableauBuff[i][j])
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
		return tableau;
	}

	public int hauteur () {
		return this.hauteur;
	}
}
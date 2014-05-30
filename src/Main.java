enum Joueur { BLANC, NOIR };
enum Jeton { BLANC, NOIR, VIDE };

public class Main {

	public static void main(String[] args) {	
		TableauPositionV3 depart = new TableauPositionV3(6,4);
		PairLong.iniSym(depart.hauteur(),depart.largeur());
		
		long t = System.currentTimeMillis();
		Negamax.iniMax(depart.largeur());
		int buff = Negamax.negamax(depart, 0);
		System.out.println("Exécuté en " + (System.currentTimeMillis() - t) + " ms.");

		if (buff == 1000) {
			System.out.println("WIN");
		}
		else if (buff == -1000) {
			System.out.println("LOSS");
		}
		else System.out.println("DRAW");
		Memo.flush(true);
	}
}
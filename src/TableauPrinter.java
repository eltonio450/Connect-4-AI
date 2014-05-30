
public class TableauPrinter {
	public static void printer (long a, long b, long mask) {
		int hauteur = Long.bitCount(mask & 0x0101010101010101L);
		int largeur = Long.bitCount(mask & 0xFFL);
		Jeton [][] tableauBuff = new Jeton[hauteur][largeur];
		for (int i=0; i<hauteur; i++) {
			for (int j=0; j<largeur; j++) {
				if (((a >> (8*(hauteur-i-1)) + j) & 1) == 1)
					tableauBuff[i][j] = Jeton.BLANC;
				else if (((b >> (8*(hauteur-i-1) + j)) & 1) == 1)
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
	}
}

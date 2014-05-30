public class PairLong {
	long a, b;
	
	private static int shift;	// taille du tampon de 0 entre deux lignes
	private static long mask;	// mask des bits utiles (nécessaire pour les jetons fantômes)

	public PairLong(long a, long b) {
		this.a = a;
		this.b = b;
	}

	public boolean equals(Object o) {
		boolean resultat = this.a == ((PairLong)o).a && this.b == ((PairLong)o).b;
		long c = sym(a);
		long d = sym(b);

		return resultat || (c == ((PairLong)o).a && d == ((PairLong)o).b) ;
	}

	public static boolean same(long a, long b, long c, long d) {
		if ((a == c && b == d) || (sym(a) == c && sym(b) == d))
			return true;
		
		
		long inutiles = getInutiles(a,b);
		a = a | inutiles;
		b = b | inutiles;
		inutiles = getInutiles(c,d);
		c = c | inutiles;
		d = d | inutiles;
		
		return (a == c && b == d)|| (sym(a) == c && sym(b) == d);
	}

	public int hashCode () {
		return (int) Long.valueOf(a*sym(a) - b*sym(b)).hashCode();
	}

	public static int hashCode (long a, long b) {
		long inutiles = getInutiles(a,b);
		a = a | inutiles;
		b = b | inutiles;
		return (int) Long.valueOf(a*sym(a) - b*sym(b)).hashCode();
	}

	/** Intervertit une position (symétrie centrale)
	 * 
	 * ATTENTION : Ne fonctionne que pour les tableaux avec la convention de TableaupositionV3 
	 * 
	 * @param n
	 * @return n inversé
	 */
	static long sym (long n) {
		n = ((n << 4) & 0xF0F0F0F0F0F0F0F0L) | ((n >> 4) & 0x0F0F0F0F0F0F0F0FL);
		n = ((n << 2) & 0xCCCCCCCCCCCCCCCCL) | ((n >> 2) & 0x3333333333333333L);
		n = ((n << 1) & 0xAAAAAAAAAAAAAAAAL) | ((n >> 1) & 0x5555555555555555L);
		n = n >> shift;
		return n;
	}

	
	static long getInutiles (long a, long b) {
		long inutiles = b | a;

		
		long buff;
		long complete =  (b | (~a)) & mask;
		
		
		buff = complete;
		buff = buff & buff << 1 & buff << 2 & buff << 3;
		buff = buff | buff >> 1 | buff >> 2 | buff >> 3;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 8 & buff >> 16 & buff >> 24;
		buff = buff | buff << 8 | buff << 16 | buff << 24;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 9 & buff >> 18 & buff >> 27;
		buff = buff | buff << 9 | buff << 18 | buff << 27;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 7 & buff >> 14 & buff >> 21;
		buff = buff | buff << 7 | buff << 14 | buff << 21;
		inutiles &= ~buff;

		
		complete = (a | (~b)) & mask;
		buff = complete;
		buff = buff & buff << 1 & buff << 2 & buff << 3;
		buff = buff | buff >> 1 | buff >> 2 | buff >> 3;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 8 & buff >> 16 & buff >> 24;
		buff = buff | buff << 8 | buff << 16 | buff << 24;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 9 & buff >> 18 & buff >> 27;
		buff = buff | buff << 9 | buff << 18 | buff << 27;
		inutiles &= ~buff;
		buff = complete;
		buff = buff & buff >> 7 & buff >> 14 & buff >> 21;
		buff = buff | buff << 7 | buff << 14 | buff << 21;
		inutiles &= ~buff;
		

		return inutiles;
	}
	
	
	/**
	 * Fonction heuristique basique
	 * NE doit PAS être appelée sans avant vérifier si la position est gagnante ou non
	 * 
	 * @param a jetons de a
	 * @param b jetons de b
	 * @return entre -1000 et 1000, -1000 : victoire noire certaine; 1000 : victoire blanche certaine
	 */
	static int basicEval(long a, long b) {
		long buff;
		int score = 0;
		
		long libres = (~(a|b)) & mask;
		long threatening1a = 0; // Bits libres alignés avec trois autres pour a
		
		long threatening2 = 0; // Bits libres alignés avec deux bits (menaçants au second degré) pour a
		
		long threatening1b = 0; // Bits libres alignés avec trois autres pour b
	
		buff = a;
		buff &= buff << 1 & buff << 2;
		buff |= buff >> 1 | buff >> 2;	// Buff contient les alignements de 3 horizontaux de a
		threatening1a |= (buff << 1 | buff >> 1) & libres;
		
		buff = a;
		buff &= buff << 8 & buff << 16;
		buff |= buff >> 8 | buff >> 16;	// Buff contient les alignements de 3 verticaux de a
		threatening1a |= (buff << 8 | buff >> 8) & libres;
		
		buff = a;
		buff &= buff << 7 & buff << 14;
		buff |= buff >> 7 | buff >> 14;	// Buff contient les alignements de 3 diagonaux1 de a
		threatening1a |= (buff << 7 | buff >> 7) & libres;
		
		buff = a;
		buff &= buff << 9 & buff << 18;
		buff |= buff >> 9 | buff >> 18;	// Buff contient les alignements de 3 verticaux de a
		threatening1a |= (buff << 9 | buff >> 9) & libres;
		
		
		buff = a | libres;
		buff &= buff << 1 & buff << 2 & buff << 3;
		buff |= buff >> 1 | buff >> 2 | buff >> 3;
		threatening2 |= buff;
		buff = a | libres;
		buff &= buff << 8 & buff << 16 & buff << 24;
		buff |= buff >> 8 | buff >> 16 | buff >> 24;
		threatening2 |= buff;
		buff = a | libres;
		buff &= buff << 9 & buff << 18 & buff << 27;
		buff |= buff >> 9 | buff >> 18 | buff >> 27;
		threatening2 |= buff;
		buff = a | libres;
		buff &= buff << 7 & buff << 14 & buff << 21;
		buff |= buff >> 7 | buff >> 14 | buff >> 21;
		threatening2 |= buff;
		
		score += Long.bitCount(threatening2);
		
		
		
		buff = b;
		buff &= buff << 1 & buff << 2;
		buff |= buff >> 1 | buff >> 2;	// Buff contient les alignements de 3 horizontaux de b
		threatening1b |= (buff << 1 | buff >> 1) & libres;
		
		buff = b;
		buff &= buff << 8 & buff << 16;
		buff |= buff >> 8 | buff >> 16;	// Buff contient les alignements de 3 verticaux de b
		threatening1b |= (buff << 8 | buff >> 8) & libres;
		
		buff = b;
		buff &= buff << 7 & buff << 14;
		buff |= buff >> 7 | buff >> 14;	// Buff contient les alignements de 3 diagonaux1 de b
		threatening1b |= (buff << 7 | buff >> 7) & libres;
		
		buff = b;
		buff &= buff << 9 & buff << 18;
		buff |= buff >> 9 | buff >> 18;	// Buff contient les alignements de 3 verticaux de b
		threatening1b |= (buff << 9 | buff >> 9) & libres;
		
		
		threatening2 = 0;
		buff = b | libres;
		buff &= buff << 1 & buff << 2 & buff << 3;
		buff |= buff >> 1 | buff >> 2 | buff >> 3;
		threatening2 |= buff;
		buff = b | libres;
		buff &= buff << 8 & buff << 16 & buff << 24;
		buff |= buff >> 8 | buff >> 16 | buff >> 24;
		threatening2 |= buff;
		buff = b | libres;
		buff &= buff << 9 & buff << 18 & buff << 27;
		buff |= buff >> 9 | buff >> 18 | buff >> 27;
		threatening2 |= buff;
		buff = b | libres;
		buff &= buff << 7 & buff << 14 & buff << 21;
		buff |= buff >> 7 | buff >> 14 | buff >> 21;
		threatening2 |= buff;
		
		score -= Long.bitCount(threatening2);
		
		
		
		
		int retour = score + 10*(Long.bitCount(threatening1a)-Long.bitCount(threatening1b)) 
				+ 100*Long.bitCount(threatening1a & (threatening1a << 1 | threatening1a << 8 |threatening1a << 7 | threatening1a << 9))
				- 100*Long.bitCount(threatening1b & (threatening1b << 1 | threatening1b << 8 |threatening1b << 7 | threatening1b << 9));
		
		return Math.max(Math.min(retour, 999), -999);
	}

	/**
	 * Initialisation 
	 * Utile pour le mask et la symétrie (shift, taille du tampon entre deux lignes) suivant la taille du tableau considéré
	 * @param hauteur
	 * @param largeur
	 */
	static void iniSym(int hauteur, int largeur) {
		PairLong.shift = 8-largeur;
		mask = (1L << 8-shift) -1;
		mask = mask | mask << 8 | mask << 16 | mask << 24 | mask << 32 | mask << 40 | mask << 48 | mask << 56;
		mask = mask & ((1L << (8*hauteur)) - 1);
	}
}
public class myHashMap {
	final int [] values;
	final long [] keys;	   // Les entrées sont stockées sur deux entiers consécutifs (limite utilisation d'objets => moins de GC et limite les temps de chargement de mémoire)
	final int [] priority; // Pour spécifier une politique de remplacement
	final int size;
	int collisionsCounter;
	int entryCounter;
	int hitCounter;
	
	
	/**
	 * 
	 * @param sizeOfInternalArray : size of the hashmap
	 * @param priorityPolicy : whether or not we keep the priority of the key
	 */
	public myHashMap (int sizeOfInternalArray, boolean priorityPolicy) {
		size = sizeOfInternalArray;
		values = new int [size];
		if (priorityPolicy)
			priority = new int [size];
		else priority = null;
		keys = new long [2*size];
		collisionsCounter = 0;
		entryCounter = 0;
		hitCounter = 0;
		
		for (int i=0; i<size; i++) {
			keys[2*i] = Long.MAX_VALUE;
		}
	}
	
	/**
	 * Ajoute une clef avec priorité
	 * Ecrase ssi la priorité est plus grande que celle de l'ancienne clef
	 * 
	 * @param key
	 * @param value
	 * @param priority
	 * 
	 * Enter key if the hashMap supports priorities
	 * Will crash otherwise
	 */
	public boolean put (long a, long b, int value, int priority) {
		entryCounter++;
		int address = Math.abs(PairLong.hashCode(a, b) % size); // Optimisation probablement inutile mais ça simplifie aussi le code
		if (keys[2*address] == Long.MAX_VALUE) {
			this.keys[2*address] = a;
			this.keys[2*address+1] = b;
			this.values[address] = value;
			this.priority[address] = priority;
			return true;
		}
		else if (this.priority[address] < priority) {
			this.keys[2*address] = a;
			this.keys[2*address+1] = b;
			this.values[address]= value;
			this.priority[address] = priority;
			collisionsCounter ++;
			return true;
		}
		collisionsCounter ++;
		return false;
	}
	
	/**
	 * Ajoute une clef sans priorité
	 * Ecrase toute autre clef en collision
	 * 
	 * @param key
	 * @param value
	 * 
	 * Enter key without priority
	 * Replace existing key
	 */
	public void put (long a, long b, int value) {
		int address = Math.abs(PairLong.hashCode(a, b) % size);
		if (this.keys[2*address] != Long.MAX_VALUE) collisionsCounter++;
		this.keys[2*address] = a;
		this.keys[2*address+1] = b;
		this.values[address] = value;
		entryCounter++;
	}
	
	/**
	 * Renvoie la valeur correspondant à la clef
	 * @param a
	 * @param b
	 * @return
	 */
	public int get (long a, long b) {
		int address = Math.abs(PairLong.hashCode(a, b) % size);
		if (PairLong.same(a, b, keys[2*address], keys[2*address+1])) {
			hitCounter ++;
			return values[address];
		}
		return -1001;
	}
	
	/** 
	 * "Efface" la hashmap
	 */
	public void clear () {
		for (int i=0; i<size; i++) {
			keys[2*i] = Long.MAX_VALUE;
		}
	}
	
	/**
	 * Supprime l'entrée de même hashcode que (a,b)
	 * @param a
	 * @param b
	 */
	public void delete (long a, long b) {
		keys[2*Math.abs(PairLong.hashCode(a, b) % size)] = Long.MAX_VALUE;
	}
	
	/**
	 * Informations...
	 */
	public void printInfos () {
		System.out.println(" Number of entries available : " + (entryCounter - collisionsCounter) + " - Number of collisions : " + collisionsCounter + " - Number of hits : " + hitCounter);
	}
}

package ca.ucalgary.cpsc.ase.examplefinder.model;

import java.util.HashMap;
import java.util.Map;


/**
 * Keeps track of the number of occurrences of a specific type in a test method. 
 * It separates different ways of referencing a type and record occurrence numbers for each of them. 
 * @author smn
 *
 */
public class TypeCounter {
	private Map<TypeReferenceType, Integer> typeCounts = new HashMap<TypeReferenceType, Integer>();
	
	public TypeCounter(){
		for (TypeReferenceType t: TypeReferenceType.values()){
			typeCounts.put(t, 0);
		}
	}
	
	/**
	 * Returns the count for a specific entry
	 * @param trt
	 * @return
	 */
	public int getTypeCount(TypeReferenceType trt){
		return typeCounts.get(trt);
	}

	/**
	 * Adds a TypeCounter to this one
	 * @param tc
	 */
	public void add(TypeCounter tc) {
		for (TypeReferenceType t: TypeReferenceType.values()){	
			this.typeCounts.put(t, this.getTypeCount(t) + tc.getTypeCount(t));
		}
	}

	/**
	 * Increment a specified entry of this TypeCounter by one 
	 * @param t
	 * @param count 
	 */
	public void inc(TypeReferenceType t, int count) {
		this.typeCounts.put(t, this.getTypeCount(t) + count);
	}
}

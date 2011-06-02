package ca.ucalgary.cpsc.ase.examplefinder.model;

/**
 * Indicates different ways that a type (class/interface) could be used inside a test method
 * @author smn
 *
 */
public enum TypeReferenceType {
	OBJECT_INSTANTIATION ("Obj-Inst", 5), METHOD_INVOCATION ("Mthd-Inv", 3), METHOD_PARAMETER ("Mthd-Param", 2);
	
	private String shortName;
	private int weight;

	public int getWeight() {
		return weight;
	}

	TypeReferenceType (String shortName, int weight){
		this.shortName = shortName;
		this.weight = weight;
	}
	
	public String getName(){
		return shortName;
	}
}

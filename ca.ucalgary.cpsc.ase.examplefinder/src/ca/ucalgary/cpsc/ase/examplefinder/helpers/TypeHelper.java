package ca.ucalgary.cpsc.ase.examplefinder.helpers;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class TypeHelper {

	public static boolean isAJDKType(ITypeBinding newType) {
		if (newType.getQualifiedName().startsWith("java.lang"))
			return true;
		return false;
	}
	
	public static boolean isAJunitType(ITypeBinding newType) {
		if (newType.getQualifiedName().startsWith("org.junit") ||
			newType.getQualifiedName().startsWith("junit"))
			return true;
		return false;
	}
}

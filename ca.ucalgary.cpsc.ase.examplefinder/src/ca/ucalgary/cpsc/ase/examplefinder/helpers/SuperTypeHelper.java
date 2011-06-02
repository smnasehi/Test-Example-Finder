package ca.ucalgary.cpsc.ase.examplefinder.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class SuperTypeHelper {

	public static List<ITypeBinding> getAllSuperClassAndInterfaces(ITypeBinding type){
		List<ITypeBinding> superClasses = new ArrayList<ITypeBinding>();
		
		ITypeBinding temp = type;
		while (temp.getSuperclass() != null)
		{
			temp = temp.getSuperclass();
			superClasses.add(temp);
		}
		
		List<ITypeBinding> interfaces = getAllSuperInterfaces(type);
		superClasses.addAll(interfaces);
		
		return superClasses;
	}

	public static List<ITypeBinding> getAllSuperInterfaces(ITypeBinding type) {
		List<ITypeBinding> interfaces = new ArrayList<ITypeBinding>();
		
		for (ITypeBinding temp: type.getInterfaces()){
			if (temp.isInterface()){
				interfaces.add(temp);
				List<ITypeBinding> superInterfaces = getAllSuperClassAndInterfaces(temp);
				if (superInterfaces.size() > 0)
					interfaces.addAll(superInterfaces);
			}
		}
		return interfaces;
	}
	
	public static boolean isSuperType(ITypeBinding wouldBeSuperType, ITypeBinding type) {		
		List<ITypeBinding> superclasses = SuperTypeHelper.getAllSuperClassAndInterfaces(type);
		for(ITypeBinding t: superclasses)
			if (t.equals(wouldBeSuperType))
				return true;
		return false;
	}

}

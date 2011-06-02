package ca.ucalgary.cpsc.ase.examplefinder.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;

public class MyComiplationUnitFactory extends MyCompilationUnit{

	private MyComiplationUnitFactory(ICompilationUnit unit,
			TestMethodModel model) {
		super(unit, model);
	}

	//This map Guarantees that we don't create multiple MyComiplationUnit objects for the same IComilationUnit.
	private static Map<ICompilationUnit, MyCompilationUnit> map = new HashMap<ICompilationUnit, MyCompilationUnit>();
	
	public static MyCompilationUnit create(ICompilationUnit unit,
			TestMethodModel model) {
		if (map.containsKey(unit))
			return map.get(unit);
		MyCompilationUnit myUnit = new MyCompilationUnit(unit, model);
		map.put(unit, myUnit);
		return myUnit;
	}

}

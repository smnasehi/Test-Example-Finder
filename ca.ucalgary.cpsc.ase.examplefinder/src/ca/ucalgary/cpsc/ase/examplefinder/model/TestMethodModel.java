package ca.ucalgary.cpsc.ase.examplefinder.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import ca.ucalgary.cpsc.ase.examplefinder.providers.ITestMethodModelListener;

/**
 * This class represents both the model and the controller for extracted information from the test methods. :P
 * @author smn
 *
 */
public class TestMethodModel {
	private Map<MyCompilationUnit, Map<MethodDeclaration, MyMethodDeclaration>> map;
	private Map<MethodDeclaration, MyCompilationUnit> reverseMap;
	private Set<ITestMethodModelListener> listeners;
	private String packagePrefix;
	private Map<MyCompilationUnit, Map<MethodDeclaration, MyMethodDeclaration>> setUpMap;
	private boolean isStarted;
	
	private static TestMethodModel model;
	
	/**
	 * This class is a Singleton.
	 */
	private TestMethodModel() {
		map = new HashMap<MyCompilationUnit, Map<MethodDeclaration, MyMethodDeclaration>>();
		setUpMap = new HashMap<MyCompilationUnit, Map<MethodDeclaration, MyMethodDeclaration>>();
		listeners = new HashSet<ITestMethodModelListener>();
		reverseMap = new HashMap<MethodDeclaration, MyCompilationUnit>();
		isStarted = false;
	}
	
	public static TestMethodModel getTestMethodModel(){
		if (model == null)
			model = new TestMethodModel();
		return model;
	}
	
	/**
	 * Starts adding new info to the maps
	 */
	public void start(){
		map.clear();
		reverseMap.clear();
		isStarted = true;
	}
	
	public void done(){
		fireTestMethodChanged();
		isStarted = false;
	}
	
	public Set<MyCompilationUnit> getClasses(){
		return map.keySet();
	}
	
	public Set<MyMethodDeclaration> getMyMethods(MyCompilationUnit unit){
		return new HashSet<MyMethodDeclaration>(map.get(unit).values());
	}
	
	public MyMethodDeclaration getMyMethodDeclaration(MethodDeclaration method){
		MyCompilationUnit unit = getCompilationUnit(method);
		if (map.containsKey(unit)){
			Map<MethodDeclaration, MyMethodDeclaration> methodMap = map.get(unit);
			return methodMap.get(method);
		}
		
		return null;
	}
	
	public MyCompilationUnit getCompilationUnit(MethodDeclaration method) {	
		return reverseMap.get(method);
	}

	public void addTestMethodModelListener(ITestMethodModelListener listener){
		listeners.add(listener);
	}
	
	public void removeTestMethodModelListener(ITestMethodModelListener listener){
		listeners.remove(listener);
	}
	
	private void fireTestMethodChanged(){
		//TestMethodEvent event = new TestMethodEvent(this, itemsAdded, itemsRemoved);
		for (ITestMethodModelListener listener: listeners)
			listener.testMethodsChanged();
	}

	public void addTestMethods(ICompilationUnit unit,
			Map<MethodDeclaration, MyMethodDeclaration> m) {
		MyCompilationUnit myUnit = MyComiplationUnitFactory.create(unit, this);//new MyCompilationUnit(unit, this);
		map.put(myUnit, m);
		for (MethodDeclaration md: m.keySet())
			reverseMap.put(md, myUnit);
		
		//fireTestMethodChanged(new ICompilationUnit[]{unit}, new ICompilationUnit[]{});
	}
	
	public void addSetUpMethods(ICompilationUnit unit,
			Map<MethodDeclaration, MyMethodDeclaration> m) {
		MyCompilationUnit myUnit = MyComiplationUnitFactory.create(unit, this);
		setUpMap.put(myUnit, m);
		for (MethodDeclaration md: m.keySet())
			reverseMap.put(md, myUnit);
	}

	public Set<MyMethodDeclaration> getMyMethodDeclarations(MyCompilationUnit unit) {
		return new HashSet<MyMethodDeclaration>(map.get(unit).values());
		
	}

	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}
	
	public String getPackagePrefix(){
		return packagePrefix;
	}

	/**
	 * Returns the MyMethodDeclaration object for the setUp method which is declared in the same class as the test method represented by 'my'.
	 * If there is no such setUp method, it returns null.
	 * @param methodDeclaration
	 * @return
	 */
	public MyMethodDeclaration getSetUp(MyMethodDeclaration my) {
		MyCompilationUnit unit = reverseMap.get(my.getMethodDeclaration());
		if(setUpMap.containsKey(unit)){
			Collection<MyMethodDeclaration> setups = setUpMap.get(unit).values();
			for (MyMethodDeclaration md : setups){
				if (md.getDeclaringClass().equals(my.getDeclaringClass()))
						return md;
			}
		}
		return null;
	}
	
	public boolean isDone(){
		return !isStarted;
	}

}

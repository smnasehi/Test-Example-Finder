package ca.ucalgary.cpsc.ase.examplefinder.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import ca.ucalgary.cpsc.ase.examplefinder.helpers.SuperTypeHelper;
import ca.ucalgary.cpsc.ase.examplefinder.helpers.TypeHelper;

/**
 * A wrapper class around the MethodDeclaration class.
 * We need this class to compute the suitability score of the method that it represents.
 * It records the list of the types being used inside the test method and counts the number of occurrences of each type inside that method. 
 * @author smn
 *
 */
public class MyMethodDeclaration  implements IPropertySource{
	private static final int METHOD_THRESHOLD = 200;
	private MethodDeclaration methodDeclaration;
	private IMethodBinding methodBinding;
	private ITypeBinding declaringClass;
	private Map<ITypeBinding, TypeCounter> types = new HashMap<ITypeBinding, TypeCounter>();
	private boolean firstTimeToComputeMetrics = true;
	
	public MyMethodDeclaration(MethodDeclaration md){
		this.methodDeclaration = md;
		methodBinding = md.resolveBinding();
		declaringClass = methodBinding.getDeclaringClass();
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public Set<TypeMetric> getTypeMetrics() {
		if (firstTimeToComputeMetrics){
			//Time to merge types inside this method and types inside the setup method
			MyMethodDeclaration setUp = TestMethodModel.getTestMethodModel().getSetUp(this);
			if (setUp != null){
				addTypes(setUp);
				//System.out.println("Found: "+setUp.getDeclaringClass().getQualifiedName());
			}
			firstTimeToComputeMetrics = false;
		}
		Set<TypeMetric> typeMetrics = new HashSet<TypeMetric>(); 
		for (ITypeBinding type: types.keySet()){
			TypeMetric tm = new TypeMetric(type, types.get(type));
			typeMetrics.add(tm);
		}
	
		return typeMetrics;
	}


	public IMethodBinding getMethodBinding() {
		return methodBinding;
	}


	public ITypeBinding getDeclaringClass() {
		return declaringClass;
	}

	
	/**
	 * Adding types from inside another method, like the setUp method, to the types used inside this method.
	 * @param md
	 */
	private void addTypes(MyMethodDeclaration md) {
		for (ITypeBinding type: md.types.keySet()){
			TypeCounter tc = md.types.get(type);
			for (TypeReferenceType trt: TypeReferenceType.values()){
				int count = tc.getTypeCount(trt);
				add(type, trt, count);
			}
		}
		
	}
	
	/**
	 * Adding the newType to the list of used types. 
	 * If there exists a super type of this type, we just update the counter of the supertype.
	 * If there exists some subtypes of the newType, we remove them and add their counters to the counter of the newType.
	 * @param newType
	 * @param trt
	 */
	public void add(ITypeBinding newType, TypeReferenceType trt, int count) {
		
		if (TypeHelper.isAJDKType(newType) || TypeHelper.isAJunitType(newType))
			return;
		
		ITypeBinding superType;
		if ((superType = getExistingSuperType(newType)) != null) {
			TypeCounter tc = types.get(superType);
			tc.inc(trt, count);
			return;
		}
		
		TypeCounter tc = removeSubTypesOf(newType);
		
		if (types.containsKey(newType)) {
			tc = types.get(newType);
		}
		tc.inc(trt, count);
		types.put(newType, tc);
	}
	
	public void add(ITypeBinding newType, TypeReferenceType trt){
		add(newType, trt, 1);
	}
	
	private TypeCounter removeSubTypesOf(ITypeBinding newType) {
		TypeCounter tc = new TypeCounter();
		Iterator<ITypeBinding> iterator = types.keySet().iterator();
		
		while (iterator.hasNext()){
			ITypeBinding existingType = iterator.next();
			if (SuperTypeHelper.isSuperType(newType, existingType)){
				//adds the current counter of the subtype to the counter of the supertype
				tc.add(types.get(existingType));
				iterator.remove();
			}
		}
		return tc;
	}

	private ITypeBinding getExistingSuperType(ITypeBinding newType) {
		for (ITypeBinding existingType: types.keySet()){
			if (SuperTypeHelper.isSuperType(existingType, newType))
				return existingType;
		}
		return null;
	}

	public String getName() {
		return methodDeclaration.getName().toString();
	}
	
	public int getMethodScore(){
		int result = 0;
			for (TypeMetric tm: getTypeMetrics()){
				result += tm.getMetric();
			}
		return result;
	}
	
	public boolean isACandidate(){
		if (getMethodScore()>METHOD_THRESHOLD)
			return true;
		return false;
	}

	//Providers for the Properties View
	private static final String DECLARING_CLASS_ID = "ca.ucalgary.cpsc.ase.examplefinder.declaringclass";
	private static final TextPropertyDescriptor DECLARING_CLASS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(DECLARING_CLASS_ID, "Declaring Class Name");
	private static final String COMPUTED_WEIGHT_ID = "ca.ucalgary.cpsc.ase.examplefinder.computedweight";
	private static final TextPropertyDescriptor COMPUTED_WEIGHT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(COMPUTED_WEIGHT_ID, "Computed Weight");
	private static final IPropertyDescriptor[] DESCRIPTORS = {DECLARING_CLASS_PROPERTY_DESCRIPTOR, COMPUTED_WEIGHT_PROPERTY_DESCRIPTOR};
	
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return DESCRIPTORS;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (DECLARING_CLASS_ID.equals(id))
			return getDeclaringClass().getQualifiedName();
		if (COMPUTED_WEIGHT_ID.equals(id))
			return getMethodScore();
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub
		
	}

}

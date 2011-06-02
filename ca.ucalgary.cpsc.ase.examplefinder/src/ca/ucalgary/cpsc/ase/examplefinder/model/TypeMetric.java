package ca.ucalgary.cpsc.ase.examplefinder.model;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


public class TypeMetric implements IPropertySource{

	public static final int API_TYPES_WEIGHT = 5;
	public static final int OTHER_TYPES_WEIGHT = 1;
	
	private boolean doesTypeBelongToAPI = false;
	private ITypeBinding type;
	private TypeCounter typecounter;

	public TypeMetric(ITypeBinding type, TypeCounter typeCounter) {
		this.type = type;
		this.typecounter = typeCounter;
		
		String packagePrefix = TestMethodModel.getTestMethodModel().getPackagePrefix();
		if (type.getQualifiedName().startsWith(packagePrefix))
			doesTypeBelongToAPI = true;
	}

	public String getInfo() {
		String result = type.getName() + ":  ";
		for (TypeReferenceType trt:TypeReferenceType.values()){
			int count = typecounter.getTypeCount(trt);
			result += trt.getName() + "(" + count + "), ";
		}
		return result.substring(0, result.length()-2);
	}

	public int getMetric() {
		int count = 0;
		for (TypeReferenceType trt:TypeReferenceType.values())
			count += typecounter.getTypeCount(trt)*trt.getWeight();
		return count*getTypeWeightBasedOnItsPackage();
	}

	private int getTypeWeightBasedOnItsPackage() {
		return doesTypeBelongToAPI ? API_TYPES_WEIGHT : OTHER_TYPES_WEIGHT;
	}

	//Providers for the Properties View
	private static final String TYPE_PACKAGE_ID = "ca.ucalgary.cpsc.ase.examplefinder.typepackage";
	private static final TextPropertyDescriptor TYPE_PACKAGE_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(TYPE_PACKAGE_ID, "Package Name");
	private static final String TYPE_SCORE_ID = "ca.ucalgary.cpsc.ase.examplefinder.typescore";
	private static final TextPropertyDescriptor TYPE_SCORE_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(TYPE_SCORE_ID, "Type Computed Score");
	private static final IPropertyDescriptor[] DESCRIPTORS = {TYPE_PACKAGE_PROPERTY_DESCRIPTOR, TYPE_SCORE_PROPERTY_DESCRIPTOR};
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
		if (TYPE_PACKAGE_ID.equals(id))
			return type.getPackage().getName();
		if (TYPE_SCORE_ID.equals(id))
			return getMetric();
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

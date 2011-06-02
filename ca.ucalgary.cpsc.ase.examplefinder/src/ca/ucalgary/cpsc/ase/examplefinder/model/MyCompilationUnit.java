package ca.ucalgary.cpsc.ase.examplefinder.model;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A wrapper class around the ICompilationUnit interface.
 * We need this to keep track of the test methods inside this compilation unit that potentially provide some examples. 
 * @author smn
 *
 */
public class MyCompilationUnit implements IPropertySource{

	private ICompilationUnit compilationUnit;
	private TestMethodModel model;

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	protected MyCompilationUnit(ICompilationUnit unit, TestMethodModel model){
		this.compilationUnit = unit;
		this.model = model;
	}

	public boolean hasCandidateMethods() {
		Set<MyMethodDeclaration> methodDecls = model.getMyMethodDeclarations(this);
		for (MyMethodDeclaration md : methodDecls)
			if (md.isACandidate())
				return true;
		return false;
	}

	public String getPackageName() {
		try {
			IPackageDeclaration[] packages = compilationUnit.getPackageDeclarations();
			if (packages != null && packages.length > 0)
				return packages[0].getElementName();
		} catch (JavaModelException e) {
			return "";
		}
		return "";
	}
	
	private Integer getPotentialMethodsCount() {
		int count = 0;
		Set<MyMethodDeclaration> methodDecls = model.getMyMethodDeclarations(this);
		for (MyMethodDeclaration md : methodDecls)
			if (md.isACandidate())
				count++;
		return count;
	}

	//Providers for the Properties View
	private static final String PACKAGE_ID = "ca.ucalgary.cpsc.ase.examplefinder.package";
	private static final TextPropertyDescriptor PACKAGE_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(PACKAGE_ID, "Package Name");
	private static final String POTENTIAL_METHODS_ID = "ca.ucalgary.cpsc.ase.examplefinder.potentialmethods";
	private static final TextPropertyDescriptor POTENTIAL_METHODS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(POTENTIAL_METHODS_ID, "Number of Methods");
	private static final IPropertyDescriptor[] DESCRIPTORS = {PACKAGE_PROPERTY_DESCRIPTOR, POTENTIAL_METHODS_PROPERTY_DESCRIPTOR};
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
		if (PACKAGE_ID.equals(id))
			return getPackageName();
		if (POTENTIAL_METHODS_ID.equals(id))
			return getPotentialMethodsCount();
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

package ca.ucalgary.cpsc.ase.examplefinder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TypeReferenceType;

public class TestVisitor extends ASTVisitor {

	private Map<MethodDeclaration, MyMethodDeclaration> testMethods = 
		new HashMap<MethodDeclaration, MyMethodDeclaration>();
	private Map<MethodDeclaration, MyMethodDeclaration> setUpMethods = 
		new HashMap<MethodDeclaration, MyMethodDeclaration>();
	private boolean isSetUp = false;
	
	private MethodDeclaration currentMethod = null;
	@Override
	public boolean visit(MethodDeclaration node) {
		if (isSetUpMethod(node)){
			//This is a setUp method
			setUpMethods.put(node, new MyMethodDeclaration(node));
			currentMethod = node;
			isSetUp = true;
			return true;
		}
		if (isTestMethod(node)){
			//start parsing a test method
			testMethods.put(node, new MyMethodDeclaration(node));
			currentMethod = node;
			return true;
		}
		return false; //?
	}
	
	

	@Override
	public void endVisit(MethodDeclaration node) {
		currentMethod = null;
		isSetUp = false;
		super.endVisit(node);
	}

	private boolean isTestMethod(MethodDeclaration node){
		if(node.getName().toString().startsWith("test"))
			return true;
		for (Object modifier: node.modifiers()){
			if (modifier instanceof Annotation)
			{
				Annotation annotation = (Annotation) modifier;
				if(annotation.getTypeName().toString().equals("Test"))
					return true;
			}
		}
		return false;
	}

	private boolean isSetUpMethod(MethodDeclaration node) {
		if(node.getName().toString().startsWith("setUp"))
			return true;
		for (Object modifier: node.modifiers()){
			if (modifier instanceof Annotation)
			{
				Annotation annotation = (Annotation) modifier;
				if(annotation.getTypeName().toString().equals("Before"))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		//We are inside a test method
		if (currentMethod != null)
		{	
			ITypeBinding typeBinding = node.resolveTypeBinding();
			//Type type = node.getType();
			addType(typeBinding, currentMethod, TypeReferenceType.OBJECT_INSTANTIATION);
		}
		return super.visit(node);
	}

	

	@Override
	public boolean visit(MethodInvocation node) {
		//We are inside a test method
		if (currentMethod != null)
		{
			IMethodBinding mBinding = node.resolveMethodBinding();
			ITypeBinding typeBinding = mBinding.getDeclaringClass();
			addType(typeBinding, currentMethod, TypeReferenceType.METHOD_INVOCATION);
			
			//check for parameter types
			ITypeBinding[] paramTypes = mBinding.getParameterTypes();
			for (ITypeBinding type: paramTypes)
				if (type.isClass() || type.isGenericType() || type.isInterface() || type.isParameterizedType())
					addType(type, currentMethod, TypeReferenceType.METHOD_PARAMETER);
		}
		return super.visit(node);
	}

	/**
	 * Add 'type' to the list of types being used inside 'method' by using a MyMethodDeclaration object
	 * @param typeBinding
	 * @param method
	 * @param trt 
	 */
	private void addType(ITypeBinding typeBinding, MethodDeclaration method, TypeReferenceType trt) {
		//finding test method wrapper for this method 
		MyMethodDeclaration my = getMethods().get(method);
		//Add the type to the list of classes that are used in the method body
		my.add(typeBinding, trt);
	}
	
	private Map<MethodDeclaration, MyMethodDeclaration> getMethods() {
		return isSetUp ? getSetUpMethods() : getTestMethods();
	}



	public Map<MethodDeclaration, MyMethodDeclaration> getTestMethods(){
		return testMethods;
	}
	
	public Map<MethodDeclaration, MyMethodDeclaration> getSetUpMethods(){
		return setUpMethods;
	}
}

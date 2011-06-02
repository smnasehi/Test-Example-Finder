package ca.ucalgary.cpsc.ase.examplefinder.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.ucalgary.cpsc.ase.examplefinder.TestVisitor;
import ca.ucalgary.cpsc.ase.examplefinder.helpers.InputHelper;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TestMethodModel;


public class TestHandler extends AbstractHandler {

	private TestMethodModel model = TestMethodModel.getTestMethodModel();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		//get all the projects in the workspace
		IProject[] projects = root.getProjects();*/
		
		/*PrintWriter out = null;
		try {
			FileWriter outFile = new FileWriter("E:\\Workspaces\\runtime-EclipseApplication\\test\\out.txt");
			out = new PrintWriter(outFile);
		}
		catch (IOException e){			
		}*/
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
		Object firstElement = selection.getFirstElement();
		
		if (firstElement instanceof IJavaProject){
			IJavaProject project = (IJavaProject) firstElement;
			
		String packagePrefix = InputHelper.getPackagePrefix();	
		if (packagePrefix == null)
			return null;
		
		model.setPackagePrefix(packagePrefix);
		//loop over all projects
		//for (IProject project: projects){
			try {
				//if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")){
					//IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
				model.start();
				IPackageFragment[] packages = project.getPackageFragments();
					for (IPackageFragment mypackage: packages){
						if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE){
							for (ICompilationUnit unit : mypackage.getCompilationUnits()){
								
								//We are interested in test classes
								if (!isTestClass(unit))
									continue;
								
								
								//now create the AST 
								CompilationUnit parse = parse(unit);
								TestVisitor visitor = new TestVisitor();
								parse.accept(visitor);
								
								//Getting the types inside each test method
								Map<MethodDeclaration, MyMethodDeclaration> m = visitor.getTestMethods();
								model.addTestMethods(unit, m);
								Map<MethodDeclaration, MyMethodDeclaration> m2 = visitor.getSetUpMethods();
								model.addSetUpMethods(unit, m2);
								//Writing the result to a file
								/*	for (MethodDeclaration md : m.keySet()){
										TestMethodManager mm = m.get(md);
										out.println(mm);
										out.println();
											
									}*/
							}
						}
					}
				//}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			finally {
				//out.close();
				model.done();
				//System.out.println("Done!");
			}
		}
		
		return null;
	}

	private boolean isTestClass(ICompilationUnit unit) throws JavaModelException {
		IType[] types = unit.getAllTypes();
		for (IType type: types){
			
			//JUnit 3 test class
			//Todo: TestCase might not be the direct superclass of the test class
			if ("TestCase".equals(type.getSuperclassName()))
				return true;
			
			//Find out if this is a JUnit 4 test class
			if (hasAnnotatedTestMethod(type))
				return true;
			
		}
		return false;
	}

	private boolean hasAnnotatedTestMethod(IType type) throws JavaModelException {
		for (IMethod method: type.getMethods()){
			for (IAnnotation annotation: method.getAnnotations())
				if (annotation.getElementName().equals("Test"))
					return true;
		}
		return false;
	}

	private CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

}

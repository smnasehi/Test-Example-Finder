package ca.ucalgary.cpsc.ase.examplefinder.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import ca.ucalgary.cpsc.ase.examplefinder.model.MyCompilationUnit;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TestMethodModel;

public class TestMethodContentProvider implements ITreeContentProvider, ITestMethodModelListener {

	private TestMethodModel model;
	private TreeViewer viewer;
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (model != null)
			model.removeTestMethodModelListener(this);
		this.model = (TestMethodModel) newInput;
		if (model != null)
			model.addTestMethodModelListener(this);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.getClasses().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MyCompilationUnit){
			MyCompilationUnit unit = (MyCompilationUnit) parentElement;
			return model.getMyMethods(unit).toArray();
		}
		if (parentElement instanceof MyMethodDeclaration){
			MyMethodDeclaration md = (MyMethodDeclaration) parentElement;
			return md.getTypeMetrics().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if ((element instanceof MyCompilationUnit) || (element instanceof MyMethodDeclaration))
			return true;
		return false;
	}

	@Override
	public void testMethodsChanged() {
		viewer.setInput(TestMethodModel.getTestMethodModel());
	}

}

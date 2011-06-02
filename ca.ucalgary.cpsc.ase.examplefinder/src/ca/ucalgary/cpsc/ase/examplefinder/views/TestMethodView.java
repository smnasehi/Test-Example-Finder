package ca.ucalgary.cpsc.ase.examplefinder.views;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import ca.ucalgary.cpsc.ase.examplefinder.model.MyCompilationUnit;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TestMethodModel;
import ca.ucalgary.cpsc.ase.examplefinder.providers.TestMethodContentProvider;
import ca.ucalgary.cpsc.ase.examplefinder.providers.TestMethodLabelProvider;

public class TestMethodView extends ViewPart {
	
	public static final String ID = "ca.ucalgary.cpsc.ase.examplefinder.views.testmethodview";
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new TestMethodContentProvider());
		viewer.setLabelProvider(new TestMethodLabelProvider());
		// Expand the tree 
		viewer.setAutoExpandLevel(1);
		// Provide the input to the ContentProvider
		viewer.setInput(TestMethodModel.getTestMethodModel());
		
		//Adding Export action for the View's Menu
		ExportAction exportAction = new ExportAction("Export Test Method Info", viewer);
		//exportAction.setImageDescriptor(JavaUI.getSharedImages().getImageDescriptor(ISharedImages.IMG_FIELD_PROTECTED));
		
		getViewSite().getActionBars().getToolBarManager().add(exportAction);
		
		viewer.setSorter(new ViewerSorter(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof MyCompilationUnit && e2 instanceof MyCompilationUnit){
					MyCompilationUnit unit1 = (MyCompilationUnit) e1;
					MyCompilationUnit unit2 = (MyCompilationUnit) e2;
					String name1 = unit1.getPackageName() + "." + unit1.getCompilationUnit().getElementName();
					String name2 = unit2.getPackageName() + "." + unit2.getCompilationUnit().getElementName();
					return name1.compareTo(name2);
				}
				return super.compare(viewer, e1, e2);
			}
			
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection =
					(IStructuredSelection) event.getSelection();
					Object element = selection.getFirstElement();
					//Opening a java editor and showing the selected method
					if (element instanceof MyMethodDeclaration){
						MyMethodDeclaration md = (MyMethodDeclaration) element;
						TestMethodModel model = TestMethodModel.getTestMethodModel();
						MyCompilationUnit unit = model.getCompilationUnit(md.getMethodDeclaration());
						
						try {
							IEditorPart javaEditor = JavaUI.openInEditor(unit.getCompilationUnit());
							JavaUI.revealInEditor(javaEditor, md.getMethodBinding().getJavaElement());
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
		});
		
		getSite().setSelectionProvider(viewer);
		//ISelectionChangedListener listener = new MySelectionChangedListener();
		//viewer.addSelectionChangedListener(listener );
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}

class MySelectionChangedListener implements ISelectionChangedListener{

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (!event.getSelection().isEmpty()){
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			Object element = selection.getFirstElement();
			
			if (element instanceof MyCompilationUnit){
				MyCompilationUnit unit = (MyCompilationUnit) element;
				System.out.println(unit.getPackageName());
			}
		}
	}
	
}

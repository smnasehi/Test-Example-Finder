package ca.ucalgary.cpsc.ase.examplefinder.views;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import ca.ucalgary.cpsc.ase.examplefinder.Activator;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyCompilationUnit;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TestMethodModel;


public class ExportAction extends Action implements IWorkbenchAction {

	private static final String ID = "ca.ucalgary.cpsc.ase.examplefinder.views.ExportAction";
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	public ExportAction(String text, TreeViewer viewer){
		super(text,Activator.getDefault().getImageRegistry().getDescriptor(Activator.MY_IMAGE_ID));
		setId(ID);
	}
	
	public void run(){
		if (!TestMethodModel.getTestMethodModel().isDone())
			return;
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();  
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save Test Method List");
		String[] filterExt = { "*.txt", "*.*" };
		fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        if (selected != null)
        	exportList(selected);
	}

	private void exportList(String filePath) {
		TestMethodModel model = TestMethodModel.getTestMethodModel();
		
		Map<String, PriorityQueue<String>> elements = new TreeMap<String, PriorityQueue<String>>();
		
		for (MyCompilationUnit mcu : model.getClasses()){
			String packageName = "";
			String compilationUnitName = "";
			PriorityQueue<String> methodsList = null;

			try {
				IPackageDeclaration[] packageDecls = mcu.getCompilationUnit().getPackageDeclarations();
				packageName = packageDecls.length > 0 ? packageDecls[0].getElementName() : "default"; 
				compilationUnitName = mcu.getCompilationUnit().getElementName();
				
				if (elements.containsKey(packageName))
					methodsList = elements.get(packageName);
				else {
					methodsList = new PriorityQueue<String>();
					elements.put(packageName, methodsList);
				}
				
				//System.out.println(mcu.getCompilationUnit().getPackageDeclarations()[0].getElementName()+"\t"+mcu.getCompilationUnit().getElementName());
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (MyMethodDeclaration mmd : model.getMyMethodDeclarations(mcu)){
				//System.out.println(mmd.getDeclaringClass().getName()+"\t"+mmd.getName());
				String methodName = compilationUnitName + ":" + mmd.getDeclaringClass().getName() + "." + mmd.getName() 
				 + "(" + mmd.getMethodScore() + ")";
				methodsList.add(methodName);
			}
			
		}
		
		saveResults(elements, filePath);
	}

	private void saveResults(Map<String, PriorityQueue<String>> elements, String fileName) {
		try {
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
				for (String packageName:elements.keySet()){
					out.write(packageName+"\n");
					for (String methodName: elements.get(packageName))
						out.write("\t"+methodName+"\n");
					out.write("\n");
				}
				out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

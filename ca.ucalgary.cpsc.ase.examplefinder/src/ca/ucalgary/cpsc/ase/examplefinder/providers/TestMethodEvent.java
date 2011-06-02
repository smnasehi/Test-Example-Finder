package ca.ucalgary.cpsc.ase.examplefinder.providers;

import java.util.EventObject;

import org.eclipse.jdt.core.ICompilationUnit;

import ca.ucalgary.cpsc.ase.examplefinder.model.TestMethodModel;

public class TestMethodEvent extends EventObject{

	private static final long serialVersionUID = 1L;
	private final ICompilationUnit[] added;
	private final ICompilationUnit[] removed;

	public TestMethodEvent(TestMethodModel source ,ICompilationUnit[] itemsAdded, ICompilationUnit[] itemsRemoved) {
		super(source);
		this.added = itemsAdded;
		this.removed = itemsRemoved;
	}

	public ICompilationUnit[] getItemsAdded(){
		return added;
	}
	
	public ICompilationUnit[] getItemsRemoved(){
		return removed;
	}

}

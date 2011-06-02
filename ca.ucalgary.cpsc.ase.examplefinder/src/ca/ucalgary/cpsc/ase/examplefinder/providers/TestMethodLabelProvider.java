package ca.ucalgary.cpsc.ase.examplefinder.providers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jdt.ui.ISharedImages;

import ca.ucalgary.cpsc.ase.examplefinder.model.MyCompilationUnit;
import ca.ucalgary.cpsc.ase.examplefinder.model.MyMethodDeclaration;
import ca.ucalgary.cpsc.ase.examplefinder.model.TypeMetric;

public class TestMethodLabelProvider extends LabelProvider implements IColorProvider{

	private static final Color RED = new Color(Display.getCurrent(),255, 0 ,0);
	private static final Color YELLOW = new Color(Display.getCurrent(),255, 255 ,0);
	@Override
	public Color getForeground(Object element) {
		if (element instanceof MyMethodDeclaration){
			MyMethodDeclaration md = (MyMethodDeclaration) element;
			if (md.isACandidate())
				return RED;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof MyCompilationUnit){
			MyCompilationUnit unit = (MyCompilationUnit) element;
			if (unit.hasCandidateMethods())
				return YELLOW;
		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof MyCompilationUnit){
			ICompilationUnit unit = ((MyCompilationUnit) element).getCompilationUnit();
			return unit.getElementName();
		}
		if (element instanceof MyMethodDeclaration){
			MyMethodDeclaration md = (MyMethodDeclaration) element;
			return md.getName();
		}
		if (element instanceof TypeMetric){
			TypeMetric tm = (TypeMetric) element;
			return tm.getInfo();
		}
		return "";
	}

	@Override
	public Image getImage(Object element) {
		ISharedImages images = JavaUI.getSharedImages();
		if (element instanceof MyCompilationUnit){
			//ICompilationUnit unit = (ICompilationUnit) element;
			return images.getImage(ISharedImages.IMG_OBJS_CUNIT);
		}
		if (element instanceof MyMethodDeclaration){
			//MethodDeclaration md = (MethodDeclaration) element;
			return images.getImage(ISharedImages.IMG_OBJS_IMPCONT);
		}
		if (element instanceof TypeMetric){
			//TypeMetric tm = (TypeMetric) element;
			return images.getImage(ISharedImages.IMG_OBJS_CLASS);
		}
		return super.getImage(element);
	}

	
	
}

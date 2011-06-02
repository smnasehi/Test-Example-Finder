package ca.ucalgary.cpsc.ase.examplefinder.helpers;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class InputHelper {

	public static String getPackagePrefix() {
		InputDialog input = new InputDialog(Display.getCurrent().getActiveShell(), "API Package", 
				"Please enter the API package prefix", "", new PrefixValidator());
		if (input.open() == Window.OK){
			return input.getValue();
		}
		return null;
	}

}

class PrefixValidator implements IInputValidator{

	@Override
	public String isValid(String newText) {
		if (newText.startsWith(".") || newText.endsWith("."))
			return "Package prefixes must not start and end with a period.";
		
		return null;
	}
	
}

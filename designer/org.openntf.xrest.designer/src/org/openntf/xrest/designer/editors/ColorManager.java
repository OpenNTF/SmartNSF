package org.openntf.xrest.designer.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.ibm.commons.util.NotImplementedException;

public class ColorManager implements IColorManager {

	protected Map fColorTable = new HashMap(10);

	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	@Override
	public Color getColor(String arg0) {
		return null;
	}
}

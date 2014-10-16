/**
 * <copyright>
 *
 * Copyright (c) 2014 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis - Initial API and implementation
 *     itemis - [446576] Add support for providing labels with different fonts and styles for model elements through BasicExplorerLabelProvider
 *
 * </copyright>
 */
package org.eclipse.sphinx.emf.ui.edit;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.StyledString.Fragment;
import org.eclipse.emf.edit.ui.provider.ExtendedColorRegistry;
import org.eclipse.emf.edit.ui.provider.ExtendedFontRegistry;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;

public class StyledDelegatingWrapperItemProvider extends DelegatingWrapperItemProvider implements IItemStyledLabelProvider {

	/**
	 * The font that will be used when no font is specified.
	 */
	protected Font defaultFont;

	/**
	 * The foreground color that will be used when no foreground color is specified.
	 */
	protected Color defaultForeground;

	/**
	 * The background color that will be used when no background color is specified.
	 */
	protected Color defaultBackground;

	public StyledDelegatingWrapperItemProvider(Object value, Object owner, EStructuralFeature feature, int index, AdapterFactory adapterFactory) {
		super(value, owner, feature, index, adapterFactory);
	}

	@Override
	public StyledString getStyledText(Object element) {
		return delegateItemProvider instanceof IItemStyledLabelProvider ? getStyledStringFromObject(((IItemStyledLabelProvider) delegateItemProvider)
				.getStyledText(getDelegateValue())) : new StyledString(getText(element));
	}

	protected StyledString getStyledStringFromObject(Object object) {
		if (object == null) {
			return new StyledString();
		} else if (object instanceof StyledString) {
			return (StyledString) object;
		} else if (object instanceof String) {
			return new StyledString((String) object);
		} else if (object instanceof org.eclipse.emf.edit.provider.StyledString) {
			return toJFaceStyledString((org.eclipse.emf.edit.provider.StyledString) object);
		} else {
			return new StyledString(object.toString());
		}
	}

	protected StyledString toJFaceStyledString(org.eclipse.emf.edit.provider.StyledString styledString) {
		StyledString result = new StyledString();
		for (Fragment fragment : styledString) {
			org.eclipse.emf.edit.provider.StyledString.Style style = fragment.getStyle();
			String string = fragment.getString();
			if (string == null) {
				result.append(""); //$NON-NLS-1$
			} else if (style == org.eclipse.emf.edit.provider.StyledString.Style.NO_STYLE) {
				result.append(string);
			} else if (style == org.eclipse.emf.edit.provider.StyledString.Style.COUNTER_STYLER) {
				result.append(string, StyledString.COUNTER_STYLER);
			} else if (style == org.eclipse.emf.edit.provider.StyledString.Style.DECORATIONS_STYLER) {
				result.append(string, StyledString.DECORATIONS_STYLER);
			} else if (style == org.eclipse.emf.edit.provider.StyledString.Style.QUALIFIER_STYLER) {
				result.append(string, StyledString.QUALIFIER_STYLER);
			} else {
				Styler styler = createStyler(style);
				result.append(string, styler);
			}
		}
		return result;
	}

	/**
	 * Returns a new styler with the same styling information as the given style.
	 */
	protected Styler createStyler(org.eclipse.emf.edit.provider.StyledString.Style style) {
		return new ConvertingStyler(style);
	}

	protected Font getFontFromObject(Object object) {
		return object == null ? null : ExtendedFontRegistry.INSTANCE.getFont(defaultFont, object);
	}

	protected Color getColorFromObject(Object object) {
		return object == null ? null : ExtendedColorRegistry.INSTANCE.getColor(defaultForeground, defaultBackground, object);
	}

	/**
	 * Return the default font.
	 */
	public Font getDefaultFont() {
		return defaultFont;
	}

	/**
	 * Set the default font.
	 */
	public void setDefaultFont(Font font) {
		defaultFont = font;
	}

	/**
	 * Return the default foreground color.
	 */
	public Color getDefaultForeground() {
		return defaultForeground;
	}

	/**
	 * Set the default foreground color.
	 */
	public void setDefaultForeground(Color color) {
		defaultForeground = color;
	}

	/**
	 * Return the default background color.
	 */
	public Color getDefaultBackground() {
		return defaultBackground;
	}

	/**
	 * Set the default background color.
	 */
	public void setDefaultBackground(Color color) {
		defaultBackground = color;
	}

	/**
	 * A extended {@link Styler} that wraps a {@link org.eclipse.emf.edit.provider.StyledString.Style} and uses it as
	 * data source for {@link #applyStyles(TextStyle) applying style}.
	 */
	protected class ConvertingStyler extends Styler {
		/**
		 * The wrapped style .
		 */
		protected final org.eclipse.emf.edit.provider.StyledString.Style style;

		protected ConvertingStyler(org.eclipse.emf.edit.provider.StyledString.Style style) {
			this.style = style;
		}

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.font = getFontFromObject(style.getFont());

			textStyle.background = getColorFromObject(style.getBackgoundColor());
			textStyle.foreground = getColorFromObject(style.getForegroundColor());

			textStyle.strikeout = style.isStrikedout();
			textStyle.strikeoutColor = getColorFromObject(style.getStrikeoutColor());

			textStyle.borderColor = getColorFromObject(style.getBorderColor());
			switch (style.getBorderStyle()) {
			case NONE: {
				textStyle.borderStyle = SWT.NONE;
				break;
			}
			case SOLID: {
				textStyle.borderStyle = SWT.BORDER_SOLID;
				break;
			}
			case DOT: {
				textStyle.borderStyle = SWT.BORDER_DOT;
				break;
			}
			case DASH: {
				textStyle.borderStyle = SWT.BORDER_DASH;
				break;
			}
			}

			switch (style.getUnderlineStyle()) {
			case NONE: {
				textStyle.underline = false;
				break;
			}
			case SINGLE: {
				textStyle.underline = true;
				textStyle.underlineStyle = SWT.UNDERLINE_SINGLE;
				textStyle.underlineColor = getColorFromObject(style.getUnderlineColor());
				break;
			}
			case DOUBLE: {
				textStyle.underline = true;
				textStyle.underlineStyle = SWT.UNDERLINE_DOUBLE;
				textStyle.underlineColor = getColorFromObject(style.getUnderlineColor());
				break;
			}
			case ERROR: {
				textStyle.underline = true;
				textStyle.underlineStyle = SWT.UNDERLINE_ERROR;
				textStyle.underlineColor = getColorFromObject(style.getUnderlineColor());
				break;
			}
			case LINK: {
				textStyle.underline = true;
				textStyle.underlineStyle = SWT.UNDERLINE_LINK;
				textStyle.underlineColor = getColorFromObject(style.getUnderlineColor());
				break;
			}
			case SQUIGGLE: {
				textStyle.underline = true;
				textStyle.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
				textStyle.underlineColor = getColorFromObject(style.getUnderlineColor());
				break;
			}
			}
		}
	}
}
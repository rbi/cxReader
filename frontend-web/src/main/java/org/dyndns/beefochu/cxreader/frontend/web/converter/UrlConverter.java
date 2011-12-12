package org.dyndns.beefochu.cxreader.frontend.web.converter;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("org.beefochu.UrlConverter")
public class UrlConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			return new URL(arg2);
		} catch (MalformedURLException exp) {
			throw new ConverterException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Invalid URL",
					"The address you've entered is not a valid URL."));
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}

}

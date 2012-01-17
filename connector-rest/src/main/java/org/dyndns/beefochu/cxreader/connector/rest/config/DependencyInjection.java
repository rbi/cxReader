package org.dyndns.beefochu.cxreader.connector.rest.config;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public class DependencyInjection {

	//TODO assumption that DatatypeFactory is thread safe which is not backed by the spec.
	@Produces
	@Singleton
	public DatatypeFactory createDatatypeFactory() {
		try {
			return DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("The factory for XML types could not be created.", e);
		}
	}
}

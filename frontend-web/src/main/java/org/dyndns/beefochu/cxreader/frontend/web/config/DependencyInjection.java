package org.dyndns.beefochu.cxreader.frontend.web.config;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class DependencyInjection {
    
    @Produces @RequestScoped
    public FacesContext getCurrentFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}

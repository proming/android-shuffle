package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.AppUser", locator="org.dodgybits.shuffle.server.locator.ObjectifyLocator"  )
public interface AppUserProxy extends EntityProxy
{
	String getEmail();
}
/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dodgybits.shuffle.gwt;

import org.dodgybits.shuffle.gwt.gin.ClientGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Shuffle implements EntryPoint {

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("../../../../../resources/org/dodgybits/shuffle/gwt/global.css")
		CssResource css();
	}

	private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Wire the request factory and the event bus
		ginjector.getRequestFactory().initialize(ginjector.getEventBus());

		// Inject global styles.
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();

		// This is required for Gwt-Platform proxy's generator
		DelayedBindRegistry.bind(ginjector);

		ginjector.getPlaceManager().revealCurrentPlace();
	}
}

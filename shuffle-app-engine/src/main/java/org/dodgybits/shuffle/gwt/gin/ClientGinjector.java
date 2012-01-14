package org.dodgybits.shuffle.gwt.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.dodgybits.shuffle.client.ShuffleRequestFactory;
import org.dodgybits.shuffle.gwt.core.EditActionPresenter;
import org.dodgybits.shuffle.gwt.core.ErrorPresenter;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.core.NavigationPresenter;
import org.dodgybits.shuffle.gwt.core.tasklist.TaskListPresenter;
import org.dodgybits.shuffle.gwt.settings.RestoreFromBackupPresenter;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	AsyncProvider<ErrorPresenter> getErrorPresenter();

	Provider<MainPresenter> getMainPresenter();

    AsyncProvider<TaskListPresenter> getTaskListPresenter();

	AsyncProvider<EditActionPresenter> getNewActionPresenter();
	
	Provider<NavigationPresenter> getNavigationPresenter();

    AsyncProvider<RestoreFromBackupPresenter> getRestoreFromBackupPresenter();

	ShuffleRequestFactory getRequestFactory();
}

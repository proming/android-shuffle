package org.dodgybits.shuffle.gwt.settings;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.place.NameTokens;

public class RestoreFromBackupPresenter  extends
        Presenter<RestoreFromBackupPresenter.MyView, RestoreFromBackupPresenter.MyProxy> {

    public interface MyView extends View {
        // TODO Put your view methods here
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.restoreFromBackup)
    public interface MyProxy extends ProxyPlace<RestoreFromBackupPresenter> {
    }

    @Inject
    public RestoreFromBackupPresenter(final EventBus eventBus, final MyView view,
                          final MyProxy proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
    }
}

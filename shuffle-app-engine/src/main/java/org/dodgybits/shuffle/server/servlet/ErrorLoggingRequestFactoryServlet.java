package org.dodgybits.shuffle.server.servlet;

import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import java.util.logging.Level;
import java.util.logging.Logger;

// see http://cleancodematters.wordpress.com/2011/05/29/improved-exceptionhandling-with-gwts-requestfactory/

public class ErrorLoggingRequestFactoryServlet extends RequestFactoryServlet {

    static class LoquaciousExceptionHandler implements ExceptionHandler {
        private static final Logger log = Logger
                .getLogger(LoquaciousExceptionHandler.class.getCanonicalName());

        @Override
        public ServerFailure createServerFailure( Throwable throwable ) {
            log.log(Level.SEVERE, "Server error", throwable);
            return new ServerFailure( throwable.getMessage(),
                    throwable.getClass().getName(), null, true );
        }
    }

    public ErrorLoggingRequestFactoryServlet() {
        super( new LoquaciousExceptionHandler() );
    }

}

package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.ObjectifyOpts;

import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionalObjectifyDao<T> extends ObjectifyDao<T> {

    private static final Logger log = Logger.getLogger(TransactionalObjectifyDao.class.getName());

    /**
     * Alternate interface to Runnable for executing transactions
     */
    public static interface Transactional {
        void run(TransactionalObjectifyDao daot);
    }

    /**
     * Provides a place to put the result too.  Note that the result
     * is only valid if the transaction completes successfully; otherwise
     * it should be ignored because it is not necessarily valid.
     */
    abstract public static class Transact<T> implements Transactional {
        protected T result;

        public T getResult() {
            return this.result;
        }
    }

    /**
     * Create a default TransactionalObjectifyDao and run the transaction through it
     */
    public static void runInTransaction(Class clazz, Transactional t) {
        TransactionalObjectifyDao daot = new TransactionalObjectifyDao(clazz);
        daot.doTransaction(t);
    }

    /**
     * Run this task through transactions until it succeeds without an optimistic
     * concurrency failure.
     */
    public static void repeatInTransaction(Class clazz, Transactional t) {
        while (true) {
            try {
                runInTransaction(clazz, t);
                break;
            } catch (ConcurrentModificationException ex) {
                if (log.isLoggable(Level.WARNING))
                    log.log(Level.WARNING, "Optimistic concurrency failure for " + t + ": " + ex);
            }
        }
    }

    /**
     * Starts out with a transaction and session cache
     */
    public TransactionalObjectifyDao(Class<T> clazz) {
        super(clazz, new ObjectifyOpts().setSessionCache(true).setBeginTransaction(true));
    }

    /**
     * Adds transaction to whatever you pass in
     */
    public TransactionalObjectifyDao(Class<T> clazz, ObjectifyOpts opts) {
        super(clazz, opts.setBeginTransaction(true));
    }

    /**
     * Executes the task in the transactional context of this DAO/ofy.
     */
    public void doTransaction(final Runnable task) {
        this.doTransaction(new Transactional() {
            @Override
            public void run(TransactionalObjectifyDao daot) {
                task.run();
            }
        });
    }

    /**
     * Executes the task in the transactional context of this DAO/ofy.
     */
    public void doTransaction(Transactional task) {
        try {
            task.run(this);
            ofy().getTxn().commit();
        } finally {
            if (ofy().getTxn().isActive())
                ofy().getTxn().rollback();
        }
    }

}

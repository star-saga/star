package org.event.driven.light.kafkaserialize.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootContext.class);

    public static final String KEY_XID = "TX_XID";

    public static final String KEY_GLOBAL_LOCK_FLAG = "TX_LOCK";

    public static final String KEY_FIRST_PHRASE_FLAG = "TX_FIRST";

    public static final String KEY_LAST_SERVICE_FLAG =  "TX_LAST";

    public static final String KEY_BACKEND_SERVICE_FLAG = "TX_BACKEND";

    private static ContextCore CONTEXT_HOLDER = new ThreadLocalContextCore();

    /**
     * Gets xid.
     *
     * @return the xid
     */
    public static String getXID() {
        return CONTEXT_HOLDER.get(KEY_XID);
    }

    /**
     * Bind.
     *
     * @param xid the xid
     */
    public static void bind(String xid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind " + xid);
        }
        CONTEXT_HOLDER.put(KEY_XID, xid);
    }

    /**
     * declare local transactions will use global lock check for update/delete/insert/selectForUpdate SQL
     */
    public static void bindGlobalLockFlag() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Local Transaction Global Lock support enabled");
        }

        //just put something not null
        CONTEXT_HOLDER.put(KEY_GLOBAL_LOCK_FLAG, KEY_GLOBAL_LOCK_FLAG);
    }

    public static void bindFirstPhraseFlag() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("First Phrase Start");
        }

        //just put something not null
        CONTEXT_HOLDER.put(KEY_FIRST_PHRASE_FLAG, KEY_FIRST_PHRASE_FLAG);
    }

    public static void bindLastServiceFlag() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Last Service");
        }

        CONTEXT_HOLDER.put(KEY_LAST_SERVICE_FLAG, KEY_LAST_SERVICE_FLAG);
    }

    public static void bindBackendServiceFlag() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Backend Service");
        }

        CONTEXT_HOLDER.put(KEY_BACKEND_SERVICE_FLAG, KEY_BACKEND_SERVICE_FLAG);
    }

    /**
     * Unbind string.
     *
     * @return the string
     */
    public static String unbind() {
        String xid = CONTEXT_HOLDER.remove(KEY_XID);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind " + xid);
        }
        return xid;
    }

    public static void unbindGlobalLockFlag() {
        String lockFlag = CONTEXT_HOLDER.remove(KEY_GLOBAL_LOCK_FLAG);
        if (LOGGER.isDebugEnabled() && lockFlag != null) {
            LOGGER.debug("unbind global lock flag");
        }
    }

    public static void unbindFirstPhraseFlag() {
        String firstFlag = CONTEXT_HOLDER.remove(KEY_FIRST_PHRASE_FLAG);
        if (LOGGER.isDebugEnabled() && firstFlag != null) {
            LOGGER.debug("unbind first phrase flag");
        }
    }

    public static void unbindLastServiceFlag() {
        String lastFlag = CONTEXT_HOLDER.remove(KEY_LAST_SERVICE_FLAG);
        if (LOGGER.isDebugEnabled() && lastFlag != null) {
            LOGGER.debug("unbind last service flag");
        }
    }

    public static void unbindBackendServiceFlag() {
        String backendFlag = CONTEXT_HOLDER.remove(KEY_BACKEND_SERVICE_FLAG);
        if (LOGGER.isDebugEnabled() && backendFlag != null) {
            LOGGER.debug("unbind backend service flag");
        }
    }

    /**
     * In global transaction boolean.
     *
     * @return the boolean
     */
    public static boolean inGlobalTransaction() {
        return CONTEXT_HOLDER.get(KEY_XID) != null;
    }

    public static boolean inGlobalLock() {
        return CONTEXT_HOLDER.get(KEY_GLOBAL_LOCK_FLAG) != null;
    }

    public static boolean inFirstPhrase() {
        return CONTEXT_HOLDER.get(KEY_FIRST_PHRASE_FLAG) != null;
    }

    public static boolean isLastService() {
        return CONTEXT_HOLDER.get(KEY_LAST_SERVICE_FLAG) == null;
    }

    public static boolean isBackendService() {
        return CONTEXT_HOLDER.get(KEY_BACKEND_SERVICE_FLAG) != null;
    }

    /**
     * requires global lock check
     *
     * @return
     */
    public static boolean requireGlobalLock() {
        return true;
    }

}

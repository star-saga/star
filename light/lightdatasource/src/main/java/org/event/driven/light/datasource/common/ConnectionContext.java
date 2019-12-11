/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.event.driven.light.datasource.common;

import org.event.driven.light.datasource.exception.ShouldNeverHappenException;
import java.util.*;

public class ConnectionContext {
    private String globalId;
    private String localId;
    private boolean isGlobalLockRequire;
    private boolean isFirstPhrase;
    //table and primary key should not be duplicated
    private Set<String> lockKeysBuffer = new HashSet<>();

    /**
     * whether requires global lock in this connection
     *
     * @return
     */
    public boolean isGlobalLockRequire() {
        return isGlobalLockRequire;
    }

    public boolean isFirstPhrase() {
        return isFirstPhrase;
    }

    /**
     * set whether requires global lock in this connection
     *
     * @param isGlobalLockRequire
     */
    public void setGlobalLockRequire(boolean isGlobalLockRequire) {
        this.isGlobalLockRequire = isGlobalLockRequire;
    }

    /**
     * Append lock key.
     *
     * @param lockKey the lock key
     */
    public void appendLockKey(String lockKey) {
        lockKeysBuffer.add(lockKey);
    }

    /**
     * In global transaction boolean.
     *
     * @return the boolean
     */
    public boolean inGlobalTransaction() {
        return globalId != null;
    }

    /**
     * Is branch registered boolean.
     *
     * @return the boolean
     */
    public boolean isBranchRegistered() {
        return localId != null;
    }

    /**
     * Bind.
     *
     * @param globalId the xid
     */
    public void bind(String globalId) {
        if (globalId == null) {
            throw new IllegalArgumentException("globalId should not be null");
        }
        if (!inGlobalTransaction()) {
            setGlobalId(globalId);
        } else {
            if (!this.globalId.equals(globalId)) {
                throw new ShouldNeverHappenException();
            }
        }
    }

    /**
     * Gets xid.
     *
     * @return the xid
     */
    public String getGlobalId() {
        return globalId;
    }

    /**
     * Sets xid.
     *
     * @param globalId the globalId
     */
    void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    /**
     * Gets branch id.
     *
     * @return the branch id
     */
    public String getLocalIdd() {
        return localId;
    }

    /**
     * Sets branch id.
     *
     * @param localId the branch id
     */
    void setLocalId(String localId) {
        this.localId = localId;
    }


    /**
     * Reset.
     */
    public void reset() {
        this.reset(null);
    }

    /**
     * Reset.
     *
     * @param globalId the globalId
     */
    void reset(String globalId) {
        this.globalId = globalId;
        localId = "";
        this.isGlobalLockRequire = true;
        lockKeysBuffer.clear();
    }

    /**
     * Build lock keys string.
     *
     * @return the string
     */
    public String buildLockKeys() {
        if (lockKeysBuffer.isEmpty()) {
            return null;
        }
        StringBuilder appender = new StringBuilder();
        Iterator<String> iterable = lockKeysBuffer.iterator();
        while (iterable.hasNext()) {
            appender.append(iterable.next());
            if (iterable.hasNext()) {
                appender.append(";");
            }
        }
        return appender.toString();
    }

    @Override
    public String toString() {
        return "ConnectionContext [globalId=" + globalId + ", localId=" + localId + ", lockKeysBuffer=" + lockKeysBuffer + "]";
    }

}

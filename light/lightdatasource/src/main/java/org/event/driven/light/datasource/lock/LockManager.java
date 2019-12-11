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
package org.event.driven.light.datasource.lock;

import org.event.driven.light.datasource.exception.TransactionException;

public interface LockManager {

    boolean acquireLock(String xid, String resourceId, String lockKey) throws TransactionException;

    boolean releaseBranchLock(String xid, String resourceId, String lockKey) throws TransactionException;

    boolean isLockable(String xid, String resourceId, String lockKey) throws TransactionException;

 //   void cleanAllLocks() throws TransactionException;

}

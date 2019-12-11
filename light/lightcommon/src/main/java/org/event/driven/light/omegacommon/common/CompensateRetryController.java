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
package org.event.driven.light.omegacommon.common;

import org.event.driven.light.kafkaserialize.exception.WaitTimeoutException;

/**
 * The type Lock retry controller.
 *
 * @author sharajava
 */
public class CompensateRetryController {

    private static int COMPENSATE_RETRY_INTERNAL = 10;
    private static int COMPENSATE_RETRY_TIMES = 5;

    private int compensateRetryInternal = COMPENSATE_RETRY_INTERNAL;
    private int compensateRetryTimes = COMPENSATE_RETRY_TIMES;

    /**
     * Instantiates a new Lock retry controller.
     */
    public CompensateRetryController() {
    }

    /**
     * Sleep.
     *
     * @param e the e
     * @throws WaitTimeoutException the lock wait timeout exception
     */
    public void sleep(Exception e) throws WaitTimeoutException {
        if (--compensateRetryTimes < 0) {
            throw new WaitTimeoutException("Global lock wait timeout", e);
        }

        try {
            Thread.sleep(compensateRetryInternal);
        } catch (InterruptedException ignore) {
        }
    }
}
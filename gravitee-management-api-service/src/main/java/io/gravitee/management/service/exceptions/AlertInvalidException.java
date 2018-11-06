/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.service.exceptions;

import static io.gravitee.common.http.HttpStatusCode.BAD_REQUEST_400;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
public class AlertInvalidException extends AbstractManagementException {

    private final String type;
    private final String referenceType;

    public AlertInvalidException(String type, String referenceType) {
        this.type = type;
        this.referenceType = referenceType;
    }

    @Override
    public int getHttpStatusCode() {
        return BAD_REQUEST_400;
    }

    @Override
    public String getMessage() {
        return referenceType + " alert is invalid for type [" + type + "]";
    }
}

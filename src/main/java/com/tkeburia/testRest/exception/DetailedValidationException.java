/*
 * Copyright 2018 Tornike Keburia <tornike.keburia@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tkeburia.testRest.exception;

import org.everit.json.schema.ValidationException;

import java.util.HashSet;
import java.util.Set;

public class DetailedValidationException extends RuntimeException {

    public DetailedValidationException(ValidationException e) {
        this(e.getMessage(), new HashSet<>(e.getAllMessages()));
    }

    private DetailedValidationException(String baseMessage, Set<String> violations) {
        this(String.format("%s. %d violations found: %s", baseMessage, violations.size(), violations.toString()));
    }

    private DetailedValidationException(String message) {
        super(message);
    }
}

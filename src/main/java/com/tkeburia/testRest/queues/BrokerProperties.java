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

package com.tkeburia.testRest.queues;

import lombok.Data;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;

@Data
public abstract class BrokerProperties {
    private Map<String, String> uris;
    private Map<String, String> userNames;
    private Map<String, String> passwords;
    private Map<String, String> queueNames;

    public Set<String> getIds() {
        return Stream.of(uris, userNames, passwords, queueNames).filter(Objects::nonNull).map(Map::keySet).findFirst().orElse(
                emptySet());
    }
}

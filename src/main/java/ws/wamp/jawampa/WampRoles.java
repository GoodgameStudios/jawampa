/*
 * Copyright 2014 Matthias Einwag
 *
 * The jawampa authors license this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package ws.wamp.jawampa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Possible roles for WAMP peers
 */
public enum WampRoles {
    
    Callee("callee"),
    Caller("caller"),
    Publisher("publisher"),
    Subscriber("subscriber", Arrays.asList( "pattern_based_subscription" )),
    Dealer("dealer"),
    Broker("broker");
    
    private final String stringValue;
    private final List<String> capabilities;

    WampRoles(String stringValue) {
        this(stringValue, Collections.emptyList());
    }

    WampRoles(String stringValue, List<String> capabilities) {
        this.stringValue = stringValue;
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return stringValue;
    }
    
    public List<String> getCapabilities() {
        return capabilities;
    }

    public static WampRoles fromString(String role) {
        if (role == null) return null;
        else if (role.equals("callee")) return Callee;
        else if (role.equals("caller")) return Caller;
        else if (role.equals("publisher")) return Publisher;
        else if (role.equals("subscriber")) return Subscriber;
        else if (role.equals("dealer")) return Dealer;
        else if (role.equals("broker")) return Broker;
        return null;
    }
    
}

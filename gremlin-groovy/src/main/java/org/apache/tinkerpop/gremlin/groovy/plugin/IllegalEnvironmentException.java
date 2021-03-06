/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.groovy.plugin;

/**
 * An exception thrown when the environment variables passed via {@link PluginAcceptor#environment()} do not meet
 * the needs of the {@link GremlinPlugin}.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class IllegalEnvironmentException extends GremlinPluginException {
    public IllegalEnvironmentException(final GremlinPlugin plugin, final String... expectedKeys) {
        super(String.format("The %s plugin may not be compatible with this environment - requires the follow ScriptEngine environment keys [%s]", plugin.getName(), String.join(",", expectedKeys)));
    }
}

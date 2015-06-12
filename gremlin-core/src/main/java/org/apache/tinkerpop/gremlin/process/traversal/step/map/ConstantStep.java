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
package org.apache.tinkerpop.gremlin.process.traversal.step.map;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Objects;

public class ConstantStep<S, E> extends MapStep<S, E> {

    private final E constant;

    public ConstantStep(final Traversal.Admin traversal, final E constant) {
        super(traversal);
        this.constant = constant;
    }

    @Override
    protected E map(final Traverser.Admin<S> traverser) {
        return this.constant;
    }

    @Override
    public String toString() {
        return StringFactory.stepString(this, this.constant);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Objects.hashCode(this.constant);
    }
}

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

import org.apache.tinkerpop.gremlin.process.computer.KeyValue;
import org.apache.tinkerpop.gremlin.process.computer.MapReduce;
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram;
import org.apache.tinkerpop.gremlin.process.computer.util.StaticMapReduce;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.MapReducer;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ReducingBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.util.TraverserSet;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.function.ConstantSupplier;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class MinGlobalStep<S extends Number> extends ReducingBarrierStep<S, S> implements MapReducer {

    public MinGlobalStep(final Traversal.Admin traversal) {
        super(traversal);
        this.setSeedSupplier(new ConstantSupplier<>(null));
        this.setBiFunction(MinGlobalBiFunction.instance());
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return Collections.singleton(TraverserRequirement.OBJECT);
    }

    @Override
    public MapReduce<MapReduce.NullObject, Number, MapReduce.NullObject, Number, Number> getMapReduce() {
        return MinGlobalMapReduce.instance();
    }

    /////

    private static class MinGlobalBiFunction<S extends Number> implements BiFunction<S, Traverser<S>, S>, Serializable {

        private static final MinGlobalBiFunction INSTANCE = new MinGlobalBiFunction();

        private MinGlobalBiFunction() {

        }

        @Override
        public S apply(final S mutatingSeed, final Traverser<S> traverser) {
            return mutatingSeed != null && mutatingSeed.doubleValue() <= traverser.get().doubleValue() ? mutatingSeed : traverser.get();
        }

        public final static <S extends Number> MinGlobalBiFunction<S> instance() {
            return INSTANCE;
        }
    }

    ///////////

    private static class MinGlobalMapReduce extends StaticMapReduce<MapReduce.NullObject, Number, MapReduce.NullObject, Number, Number> {

        private static final MinGlobalMapReduce INSTANCE = new MinGlobalMapReduce();

        private MinGlobalMapReduce() {

        }

        @Override
        public boolean doStage(final MapReduce.Stage stage) {
            return true;
        }

        @Override
        public void map(final Vertex vertex, final MapEmitter<NullObject, Number> emitter) {
            vertex.<TraverserSet<Number>>property(TraversalVertexProgram.HALTED_TRAVERSERS).ifPresent(traverserSet -> traverserSet.forEach(traverser -> emitter.emit(traverser.get())));
        }

        @Override
        public void combine(final NullObject key, final Iterator<Number> values, final ReduceEmitter<NullObject, Number> emitter) {
            this.reduce(key, values, emitter);
        }

        @Override
        public void reduce(final NullObject key, final Iterator<Number> values, final ReduceEmitter<NullObject, Number> emitter) {
            if (values.hasNext()) {
                Number min = Double.MAX_VALUE;
                while (values.hasNext()) {
                    final Number value = values.next();
                    if (value.doubleValue() < min.doubleValue())
                        min = value;
                }
                emitter.emit(min);
            }
        }

        @Override
        public String getMemoryKey() {
            return REDUCING;
        }

        @Override
        public Number generateFinalResult(final Iterator<KeyValue<NullObject, Number>> keyValues) {
            return keyValues.hasNext() ? keyValues.next().getValue() : Double.NaN;
        }

        public static final MinGlobalMapReduce instance() {
            return INSTANCE;
        }
    }
}
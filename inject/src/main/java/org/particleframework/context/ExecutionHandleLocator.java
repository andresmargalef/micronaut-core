/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.context;

import org.particleframework.inject.ExecutableMethod;
import org.particleframework.inject.MethodExecutionHandle;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interface for components that are able to locate and return {@link org.particleframework.inject.ExecutionHandle} instances
 * for beans
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface ExecutionHandleLocator {
    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    <R> Optional<MethodExecutionHandle<R>> findExecutionHandle(Class<?> beanType, String method, Class... arguments);

    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param bean The bean to invoke the method on
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    <R> Optional<MethodExecutionHandle<R>> findExecutionHandle(Object bean, String method, Class... arguments);

    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    <T, R> Optional<ExecutableMethod<T,R>> findExecutableMethod(Class<T> beanType, String method, Class... arguments);

    /**
     * Finds the original unproxied method for a {@link org.particleframework.inject.ProxyBeanDefinition}
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    <T, R> Optional<ExecutableMethod<T,R>> findProxyTargetMethod(Class<T> beanType, String method, Class... arguments);

    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    default <T, R> ExecutableMethod<T,R> getExecutableMethod(Class<T> beanType, String method, Class... arguments) throws NoSuchMethodException {
        Optional<ExecutableMethod<T, R>> executableMethod = this.findExecutableMethod(beanType, method, arguments);
        return executableMethod.orElseThrow(()->
                new NoSuchMethodException("No such method ["+method+"("+ Arrays.stream(arguments).map(Class::getName).collect(Collectors.joining(","))+") ] for bean ["+beanType.getName()+"]")
        );
    }

    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    default <T, R> ExecutableMethod<T,R> getProxyTargetMethod(Class<T> beanType, String method, Class... arguments) throws NoSuchMethodException {
        Optional<ExecutableMethod<T, R>> executableMethod = this.findProxyTargetMethod(beanType, method, arguments);
        return executableMethod.orElseThrow(()->
                new NoSuchMethodException("No such method ["+method+"("+ Arrays.stream(arguments).map(Class::getName).collect(Collectors.joining(","))+") ] for bean ["+beanType.getName()+"]")
        );
    }


    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param beanType The bean type
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     * @throws NoSuchMethodException if the method cannot be found
     */
    default <R> MethodExecutionHandle<R> getExecutionHandle(Class<?> beanType, String method, Class... arguments) throws NoSuchMethodException {
        return this.<R>findExecutionHandle(beanType, method, arguments).orElseThrow(()->
                new NoSuchMethodException("No such method ["+method+"("+ Arrays.stream(arguments).map(Class::getName).collect(Collectors.joining(","))+") ] for bean ["+beanType.getName()+"]")
        );
    }

    /**
     * Finds an optimized execution handle for invoking a bean method. The execution handle may or may not be implemented by generated byte code.
     *
     * @param <R> The result type of the execution handle
     * @param bean The bean to invoke the method on
     * @param method The method
     * @param arguments The arguments
     * @return The execution handle
     */
    default <R> MethodExecutionHandle<R> getExecutionHandle(Object bean, String method, Class... arguments) throws NoSuchMethodException {
        return this.<R>findExecutionHandle(bean, method, arguments).orElseThrow(()->
                new NoSuchMethodException("No such method ["+method+"("+ Arrays.stream(arguments).map(Class::getName).collect(Collectors.joining(","))+") ] for bean ["+bean+"]")
        );
    }
}

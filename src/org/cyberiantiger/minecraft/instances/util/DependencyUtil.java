/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.instances.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author antony
 */
public class DependencyUtil {

    public static <T extends Dependency> T merge(final Logger log, final Class<T> type, final List<DependencyFactory<T>> factories) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // We only support merging for methods which return void.
                if (method.getReturnType() != Void.TYPE)  {
                    throw new UnsupportedOperationException();
                }
                Iterator<DependencyFactory<T>> i = factories.iterator();
                while(i.hasNext()) {
                    DependencyFactory<T> factory = i.next();
                    T instance = factory.getDependecy();
                    if (instance != null) {
                        try {
                            method.invoke(instance, args);
                        } catch (InvocationTargetException e) {
                            // Allow declared Exceptions and RuntimeExceptions to be
                            // passed up the stack.
                            for (Class<?> exceptionType : method.getExceptionTypes()) {
                                if (exceptionType.isInstance(e.getTargetException()))
                                    throw e.getTargetException();
                            }
                            if (e.getTargetException() instanceof RuntimeException) {
                                throw e.getTargetException();
                            }
                            // The exception is not declared or a runtime exception,
                            // blacklist the dependency.
                            log.log(Level.WARNING, "Blacklisting dependency on " + factory.getPlugin(), e.getTargetException());
                            i.remove();
                        }
                    }
                }
                return null;
            }
        });
    }

    public static <T extends Dependency> T first(final Logger log, final Class<T> type, final List<DependencyFactory<T>> factories) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Iterator<DependencyFactory<T>> i = factories.iterator();
                while(i.hasNext()) {
                    DependencyFactory<T> factory = i.next();
                    T instance = factory.getDependecy();
                    if (instance != null) {
                        try {
                            return method.invoke(instance, args);
                        } catch (InvocationTargetException e) {
                            // Allow declared Exceptions and RuntimeExceptions to be
                            // passed up the stack.
                            for (Class<?> exceptionType : method.getExceptionTypes()) {
                                if (exceptionType.isInstance(e.getTargetException()))
                                    throw e.getTargetException();
                            }
                            if (e.getTargetException() instanceof RuntimeException) {
                                throw e.getTargetException();
                            }
                            // The exception is not declared or a runtime exception,
                            // blacklist the dependency.
                            log.log(Level.WARNING, "Blacklisting dependency on " + factory.getPlugin(), e.getTargetException());
                            i.remove();
                        }
                    }
                }
                throw new UnsupportedOperationException();
            }
        });
    }
}

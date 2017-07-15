/*
 * Copyright 2017 Illya Kysil <ikysil@ikysil.name>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.ikysil.nullifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 *
 *
 * @author Illya Kysil <ikysil@ikysil.name>
 */
public class Nullifier {

    public static <T> T root(final T value) {
        CURRENT_VALUE.remove();
        if (value == null) {
            return null;
        }
        return proxify(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T eval(final T value) {
        T result = (T) CURRENT_VALUE.get();
        CURRENT_VALUE.remove();
        return result;
    }

    private static <T> T proxify(final T value) throws IllegalStateException {
        final Class<?> clazz = value.getClass();
        return proxify(clazz, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxify(final Class<?> clazz, final T o) throws IllegalStateException {
        CURRENT_VALUE.set(o);
        try {
            T result;
            if (Modifier.isFinal(clazz.getModifiers())) {
                result = (T) clazz.newInstance();
            }
            else {
                final ProxyFactory pf = new ProxyFactory();
                if (clazz.isInterface()) {
                    pf.setSuperclass(Object.class);
                    pf.setInterfaces(new Class<?>[]{ clazz });
                }
                else {
                    pf.setSuperclass(clazz);
                }
                pf.setFilter(new DefaultMethodFilter());
                final MethodHandler mh = o == null ? new NullValueMethodHandler() : new DefaultMethodHandler();
                result = (T) pf.create(new Class<?>[0], new Object[0], mh);
            }
            return result;
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(String.format("Can't instantiate %s", clazz.getName()), ex);
        }
    }

    private static class DefaultMethodFilter implements MethodFilter {

        private final Set<String> ignoredMethods = new HashSet<>();

        public DefaultMethodFilter() {
            ignoredMethods.add("finalize");
        }

        @Override
        public boolean isHandled(Method m) {
            return !ignoredMethods.contains(m.getName());
        }

    }

    private static final ThreadLocal<Object> CURRENT_VALUE = new ThreadLocal<>();

    private static class NullValueMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            final Class<?> returnType = thisMethod.getReturnType();
            return proxify(returnType, null);
        }

    }

    private static class DefaultMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            final Class<?> returnType = thisMethod.getReturnType();
            final Object result = proceed == null ? null : proceed.invoke(self, args);
            return proxify(returnType, result);
        }

    }

}

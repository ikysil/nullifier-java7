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

import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Test;

import static name.ikysil.nullifier.Nullifier.eval;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static name.ikysil.nullifier.Nullifier.root;

/**
 *
 * @author Illya Kysil <ikysil@ikysil.name>
 */
public class NullifierTest {

    public NullifierTest() {
    }

    @Test
    public void testSomeMethod() {
        final TestBeanA valueA = new TestBeanA();
        assertThat(eval(root(valueA).getChild()), equalTo(valueA.getChild()));
        assertThat(eval(root(new TestBeanA()).getChild().getCalendar()), nullValue());
        assertThat(eval(root(new TestBeanA()).getChild().getCalendar().getClass()), nullValue());
        assertThat(eval(root(new TestBeanA()).getChild().getCalendar().getClass().getAnnotations()), nullValue());
        assertThat(eval(root(new TestBeanA()).getChild().getParent().getChild()), nullValue());
        assertThat(eval(root(new TestBeanA()).getStrings()), not(nullValue()));
    }

    public static class TestBeanA {

        public List<String> getStrings() {
            return Collections.<String>emptyList();
        }

        public void setData(Collection<?> data) {

        }

        public Object[] getArray() {
            return null;
        }

        public Object[][] get2DArray() {
            return null;
        }

        public int[][] get3DArray() {
            return null;
        }

        public Long getLong() {
            return null;
        }

        public TestBeanB getChild() {
            return null;
        }

        public XMLGregorianCalendar getCalendar() {
            return null;
        }

    }

    public static class TestBeanB {

        public TestBeanA getParent() {
            return null;
        }

        public Object getGrandChild() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public GregorianCalendar getCalendar() {
            return null;
        }

    }

}

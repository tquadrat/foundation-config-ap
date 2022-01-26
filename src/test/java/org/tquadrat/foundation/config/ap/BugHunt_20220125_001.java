/*
 * ============================================================================
 * Copyright © 2002-2020 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.config.ap;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.config.ap.CodeGenerationConfiguration.createStringConverterRegistry;

import java.util.ServiceLoader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.StringConverter;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  It seems that the
 *  {@link ServiceLoader}
 *  does not work in the context of an annotation processor.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20220125_001.java 997 2022-01-26 14:55:05Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20220125_001.java 997 2022-01-26 14:55:05Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.config.ap.BugHunt_20220125_001" )
public class BugHunt_20220125_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests the method
     *  {@link CodeGenerationConfiguration#createStringConverterRegistry()}
     *  that uses the
     *  {@link StringConverter}
     *  service.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testCreateStringConverterRegistry() throws Exception
    {
        skipThreadTest();

        final var registry = createStringConverterRegistry();
        assertNotNull( registry );
        assertFalse( registry.isEmpty() );
        assertEquals( StringConverter.list().size(), registry.size() );
        for( final var entry : registry.entrySet() )
        {
            out.printf( "SubjectClass: %1$s – Converter: %2$s%n", entry.getKey(), entry.getValue() );
        }
    }   //  testCreateStringConverterRegistry()

    /**
     *  Tests the implementation for the service and the service provider.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testImplementation1() throws Exception
    {
        skipThreadTest();

        final var services = ServiceLoader.load( StringConverter.class );
        assertNotNull( services );
        assertFalse( services.findFirst().isEmpty() );
    }   //  testImplementation1()

    /**
     *  Tests the implementation for the service and the service provider.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testImplementation2() throws Exception
    {
        skipThreadTest();

        final var services = ServiceLoader.load( StringConverter.class );
        assertNotNull( services );
        assertFalse( services.stream().findFirst().isEmpty() );
    }   //  testImplementation2()

    /**
     *  Test the method
     *  {@link StringConverter#list()}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testList() throws Exception
    {
        skipThreadTest();

        final var instances = StringConverter.list();
        assertNotNull( instances );
        assertFalse( instances.isEmpty() );
    }   //  testList()
}
//  class BugHunt_20220125_001

/*
 *  End of File
 */
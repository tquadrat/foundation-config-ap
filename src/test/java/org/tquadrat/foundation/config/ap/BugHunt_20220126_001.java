/*
 * ============================================================================
 *  Copyright © 2002-2022 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.config.ap;

import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.config.ap.CodeGenerationConfiguration.validateStringConverterClass;

import java.io.File;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
import org.tquadrat.foundation.util.stringconverter.FileStringConverter;

/**
 *  Some tests for
 *  {@link CodeGenerationConfiguration#validateStringConverterClass(TypeName,ClassName)}.
 *
 *  @version $Id: BugHunt_20220126_001.java 997 2022-01-26 14:55:05Z tquadrat $
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20220126_001.java 997 2022-01-26 14:55:05Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "org.tquadrat.foundation.config.ap.BugHunt_20220126_001" )
public class BugHunt_20220126_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Some tests for
     *  {@link CodeGenerationConfiguration#validateStringConverterClass(TypeName,ClassName)}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testValidateStringConverterClass() throws Exception
    {
        skipThreadTest();

        TypeName subjectClass;
        ClassName converterClass;

        subjectClass = TypeName.from( File.class );
        converterClass = ClassName.from( FileStringConverter.class );
        assertTrue( validateStringConverterClass( subjectClass, converterClass ) );

        subjectClass = TypeName.from( boolean.class );
        converterClass = ClassName.from( BooleanStringConverter.class );
        assertTrue( validateStringConverterClass( subjectClass, converterClass ) );
    }   //  testValidateStringConverterClass()
}
//  class BugHunt_20220126_001

/*
 *  End of File
 */
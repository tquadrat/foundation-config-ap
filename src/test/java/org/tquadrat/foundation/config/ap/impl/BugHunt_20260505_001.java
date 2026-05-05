/*
 * ============================================================================
 *  Copyright © 2002-2026 by Thomas Thrien.
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

package org.tquadrat.foundation.config.ap.impl;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MANDATORY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_OPTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_REQUIRES_SYNCHRONIZATION;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.JavaUtils.composeSetterName;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.nio.file.PathMatcher;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.CLIBeanSpec;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.ap.CollectionKind;
import org.tquadrat.foundation.config.cli.PathMatcherValueHandler;
import org.tquadrat.foundation.config.spi.prefs.PreferenceChangeListenerImpl;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.test.NameImpl;
import org.tquadrat.foundation.test.helper.CodeGeneratorTestBase;

/**
 *  <p>{@summary We have an issue with a custom
 *  {@link org.tquadrat.foundation.config.cli.CmdLineValueHandler}.}</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20260505_001.java 1231 2026-05-05 14:28:23Z tquadrat $
 *  @since 0.25.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20260505_001.java 1231 2026-05-05 14:28:23Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.config.ap.impl.BugHunt_20260505_001" )
public class BugHunt_20260505_001 extends CodeGeneratorTestBase
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a generic code generation configuration.
     *
     *  @param  className   The name for the generated class.
     *  @param  specName    The name for the configuration bean specification.
     *  @param  environment The mock for the
     *      {@link APHelper}.
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @return The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeGenerationConfiguration createMyConfiguration( final String className, final String specName, final APHelper environment, final boolean flag )
    {
        final var packageName = "org.tquadrat.foundation.test.generated";

        //---* The constructor arguments *-------------------------------------
        final var composer = new JavaComposer( LAYOUT_FOUNDATION, flag );
        final var specificationClass = ClassName.from( "org.tquadrat.foundation.test", requireNotEmptyArgument( specName, "specName" ) );
        final var configurationBeanClassName = new NameImpl( requireNotEmptyArgument( className, "className" ) );
        final var configurationBeanPackageName = new NameImpl( packageName );
        final var baseClass = ClassName.from( "org.tquadrat.foundation.test.config", "BaseClass" );
        final var synchronizeAccess = false;

        //---* Create the return value *---------------------------------------
        final var retValue = new CodeGenerationConfiguration( environment, composer, specificationClass, configurationBeanClassName, configurationBeanPackageName, baseClass, synchronizeAccess );

        /*
         * Additional settings.
         */
        retValue.setI18NParameters( "MSG", format( "%s.TextsAndMessages", getClass().getPackageName() ) );

        retValue.setPreferencesRoot( format( "%s.%s", packageName, className).replace( '.', '/' ) );
        retValue.setPreferenceChangeListenerClass( ClassName.from( PreferenceChangeListenerImpl.class ) );
        retValue.setINIFileConfig( "/home/tquadrat/config/dummy.ini", true,
            """
            This is a dummy INI file used for the tests of the code generation stuff.\
            """ );
        retValue.addINIGroup( "Group1",
            """
             The comment for group 1.
             """ );
        retValue.addINIGroup( "Group2",
            """
             The comment for group 2.
             """ );
        retValue.addINIGroup( "Group3",
            """
             The comment for group 3.
             """ );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createMyConfiguration()

    /**
     *  Creates the PathMatcher property and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went awfully wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createPathMatcherProperty( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyType = ParameterizedTypeName.from( ClassName.from( List.class ), ClassName.from( PathMatcher.class ) );

        final var propertyName = "pathMatcher";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setGetterReturnType( propertyType );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY );
        property.setPropertyType( propertyType );
        property.setCollectionKind( CollectionKind.LIST );
        property.setIsEnum( false );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        //property.setStringConverterClass( ClassName.from( EnumStringConverter.class ) );
        property.setCLIOptionNames( List.of( "--PathMatcher" ) );
        property.setCLIValueHandlerClass( TypeName.from( PathMatcherValueHandler.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_enum1()

    /**
     *  The test bed.
     *
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @throws Exception   Something went awfully wrong.
     */
    @ParameterizedTest( name = "testForBug [{index}] = {0}" )
    @ValueSource( booleans = { true, false } )
    final void testForBug( final boolean flag ) throws Exception
    {
        skipThreadTest();

        final APHelper environment = mock( APHelper.class );
        final var configuration = assertDoesNotThrow( () -> createMyConfiguration( "BugHuntImpl", "BugHuntSpec", environment, flag ) );
        assertInstanceOf( CodeGenerationConfiguration.class, configuration );

        //---* Add the interfaces to implement *-------------------------------
        final var interfacesToImplement =
            List.of(
                ClassName.from( ConfigBeanSpec.class ),
                ClassName.from( CLIBeanSpec.class )
            );
        assertDoesNotThrow( () -> configuration.addInterfacesToImplement( interfacesToImplement ) );

        //---* Add the properties *--------------------------------------------
        createProperty_resourceBundle( configuration, true );
        assertDoesNotThrow( () -> createPathMatcherProperty( configuration ) );

        //---* Run the test *--------------------------------------------------
        replayAll();
        final var candidate = assertDoesNotThrow( () -> new CodeGenerator( configuration ) );
        assertInstanceOf( CodeGenerator.class, candidate );

        final var code = assertDoesNotThrow( candidate::createCode );
        assertInstanceOf( JavaFile.class, code );
        final var actual = new StringBuilder();
        code.writeTo( actual );
        assertTrue( isNotEmptyOrBlank( actual ) );
    }   //  testForBug()
}
//  class BugHunt_20260505_001

/*
 *  End of File
 */
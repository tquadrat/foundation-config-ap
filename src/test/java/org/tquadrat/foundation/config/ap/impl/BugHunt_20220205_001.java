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

package org.tquadrat.foundation.config.ap.impl;

import static java.lang.System.out;
import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_charset;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_clock;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_enum1;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_isDebug;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_isTest;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_locale;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_messagePrefix;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_processId;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_random;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_resourceBundle;
import static org.tquadrat.foundation.test.helper.CodeGeneratorTestBase.createProperty_timezone;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.util.List;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.CLIBeanSpec;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.spi.prefs.PreferenceChangeListenerImpl;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.test.NameImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Enum properties are causing issues.
 *
 *  @version $Id: BugHunt_20220205_001.java 1015 2022-02-09 08:25:36Z tquadrat $
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20220205_001.java 1015 2022-02-09 08:25:36Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "org.tquadrat.foundation.config.ap.impl.BugHunt_20220205_001" )
public class BugHunt_20220205_001 extends TestBaseClass
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
    private final CodeGenerationConfiguration createConfiguration( final String className, final String specName, final APHelper environment, final boolean flag )
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
    }   //  createConfiguration()

    /**
     *  Creates the properties for this test run, and adds them to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      properties.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private void createProperties( final CodeGenerationConfiguration configuration ) throws Exception
    {
        createProperty_charset( configuration );
        createProperty_clock( configuration );
        createProperty_isDebug( configuration );
        createProperty_isTest( configuration );
        createProperty_locale( configuration );
        createProperty_messagePrefix( configuration );
        createProperty_processId( configuration );
        createProperty_random( configuration );
        createProperty_resourceBundle( configuration, true );
        createProperty_timezone( configuration );
        createProperty_enum1( configuration );
    }   //  createProperties()

    /**
     *  A test for the code generation.
     *
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest( name = "testCodeGeneration7 [{index}] = {0}" )
    @ValueSource( booleans = { true, false } )
    final void testCodeGeneration( final boolean flag ) throws Exception
    {
        final var header = format( "%n//----< %2$s >%1$s", "-".repeat( 80 ), "testCodeGeneration" ).substring( 0, 80 );

        skipThreadTest();

        final APHelper environment = mock( APHelper.class );
        final var configuration = createConfiguration( "BugHuntImpl", "BugHuntSpec", environment, flag );
        assertNotNull( configuration );

        //---* Add the interfaces to implement *-------------------------------
        final var interfacesToImplement =
            List.of(
                ClassName.from( ConfigBeanSpec.class ),
                ClassName.from( CLIBeanSpec.class )
            );
        configuration.addInterfacesToImplement( interfacesToImplement );

        //---* Add the properties *--------------------------------------------
        createProperties( configuration );

        //---* Run the test *--------------------------------------------------
        replayAll();
        final var candidate = new CodeGenerator( configuration );
        assertNotNull( candidate );

        final var code = candidate.createCode();
        assertNotNull( code );
        final var actual = new StringBuilder();
        code.writeTo( actual );
        assertTrue( isNotEmptyOrBlank( actual ) );
        if( flag )
        {
            out.println( header );
            out.println( actual );
            out.println( header );
            out.println();
        }
        else
        {
            /*
             * We added the CLIBeanSpec interface to the basic configuration
             * bean specification; that should give us the methods and
             * attributes for the CLI handling.
             */
            @SuppressWarnings( "GrazieInspection" )
            final var expected =
                """
                    /*
                     * ============================================================================
                     * This file inherits the copyright and license(s) from the interface that is
                     * implemented by the class
                     *
                     *     org.tquadrat.foundation.test.generated.BugHuntImpl
                     *
                     * Refer to
                     *
                     *     org.tquadrat.foundation.test.BugHuntSpec
                     *
                     * and the file comment there for the details.
                     * ============================================================================
                     */

                    package org.tquadrat.foundation.test.generated;

                    import static java.lang.System.getProperty;
                    import static java.nio.charset.Charset.defaultCharset;
                    import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
                    import static org.tquadrat.foundation.lang.Objects.nonNull;
                    import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
                    import static org.tquadrat.foundation.util.StringUtils.format;
                    import static org.tquadrat.foundation.util.SystemUtils.getPID;

                    import java.io.IOException;
                    import java.io.OutputStream;
                    import java.lang.CharSequence;
                    import java.lang.Override;
                    import java.lang.String;
                    import java.lang.SuppressWarnings;
                    import java.nio.charset.Charset;
                    import java.security.SecureRandom;
                    import java.time.Clock;
                    import java.time.ZoneId;
                    import java.util.ArrayList;
                    import java.util.List;
                    import java.util.Locale;
                    import java.util.Optional;
                    import java.util.Random;
                    import java.util.ResourceBundle;
                    import java.util.StringJoiner;
                    import java.util.function.BiConsumer;
                    import org.tquadrat.config.test.MyEnum;
                    import org.tquadrat.foundation.annotation.ClassVersion;
                    import org.tquadrat.foundation.config.CmdLineException;
                    import org.tquadrat.foundation.config.ConfigUtil;
                    import org.tquadrat.foundation.config.ConfigurationChangeListener;
                    import org.tquadrat.foundation.config.cli.CmdLineValueHandler;
                    import org.tquadrat.foundation.config.cli.SimpleCmdLineValueHandler;
                    import org.tquadrat.foundation.config.spi.CLIDefinition;
                    import org.tquadrat.foundation.config.spi.CLIOptionDefinition;
                    import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
                    import org.tquadrat.foundation.lang.Objects;
                    import org.tquadrat.foundation.test.BugHuntSpec;
                    import org.tquadrat.foundation.test.config.BaseClass;
                    import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.CharsetStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.EnumStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.LocaleStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.ZoneIdStringConverter;

                    /**
                     * The configuration bean that implements
                     * {@link BugHuntSpec}.
                     */
                    @ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at [[[BUILD_DATETIME]]]", isGenerated = true )
                    @SuppressWarnings( {"ClassWithTooManyMethods", "OverlyComplexClass", "OverlyCoupledClass"} )
                    public final class BugHuntImpl extends BaseClass implements BugHuntSpec
                    {
                            /*------------*\\
                        ====** Attributes **=======================================================
                            \\*------------*/
                        /**
                         * Property: &quot;charset&quot;.
                         */
                        private Charset m_Charset;

                        /**
                         * The registry for the CLI definitions
                         */
                        private final List<CLIDefinition> m_CLIDefinitions = new ArrayList<>();

                        /**
                         * The last error message from a call to
                         * {@link #parseCommandLine(String[])}.
                         *
                         * @see #retrieveParseErrorMessage()
                         */
                        private String m_CLIErrorMessage = null;

                        /**
                         * Special Property: &quot;clock&quot;.
                         */
                        private Clock m_Clock;

                        /**
                         * Property: &quot;enum1&quot;.
                         */
                        private MyEnum m_Enum1;

                        /**
                         * Property: &quot;isDebug&quot;.
                         */
                        private final boolean m_IsDebug;

                        /**
                         * Property: &quot;isTest&quot;.
                         */
                        private final boolean m_IsTest;

                        /**
                         * The support for the configuration change listener.
                         */
                        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
                        private final ConfigChangeListenerSupport m_ListenerSupport;

                        /**
                         * Property: &quot;locale&quot;.
                         */
                        private Locale m_Locale;

                        /**
                         * Special Property: &quot;processId&quot;.
                         */
                        private final long m_ProcessId;

                        /**
                         * Special Property: &quot;random&quot;.
                         */
                        private final Random m_Random;

                        /**
                         * Special Property: &quot;resourceBundle&quot;.
                         */
                        private ResourceBundle m_ResourceBundle = null;

                        /**
                         * Property: &quot;timezone&quot;.
                         */
                        private ZoneId m_Timezone;

                            /*--------------*\\
                        ====** Constructors **=====================================================
                            \\*--------------*/
                        /**
                         * Creates a new {@code BugHuntImpl} instance.
                         */
                        public BugHuntImpl()
                        {
                            //---* Initialise the listener support *-------------------------------
                            m_ListenerSupport = new ConfigChangeListenerSupport( this );

                            /*
                             * Initialise the property 'charset'.
                             */
                            m_Charset = defaultCharset();

                            /*
                             * Initialise the property 'clock'.
                             */
                            m_Clock = Clock.systemDefaultZone();

                            /*
                             * Initialise the property 'isDebug' from the system properties.
                             */
                            {
                                final var stringConverter = BooleanStringConverter.INSTANCE;
                                final var value = getProperty( "isDebug", "false" );
                                m_IsDebug = stringConverter.fromString( value );
                            }
                                    
                            /*
                             * Initialise the property 'isTest' from the system properties.
                             */
                            {
                                final var stringConverter = BooleanStringConverter.INSTANCE;
                                final var value = getProperty( "isTest", "false" );
                                m_IsTest = stringConverter.fromString( value );
                            }
                                    
                            /*
                             * Initialise the property 'locale'.
                             */
                            m_Locale = Locale.getDefault();

                            /*
                             * Initialise the property 'processId'.
                             */
                            m_ProcessId = getPID();

                            /*
                             * Initialise the property 'random'.
                             */
                            m_Random = new SecureRandom();

                            /*
                             * Initialise the property 'timezone'.
                             */
                            m_Timezone = ZoneId.systemDefault();

                            /*
                             * Initialise the CLI definitions.
                             */
                            CmdLineValueHandler<?> valueHandler;
                            CLIDefinition cliDefinition;

                            /*
                             * CLI definition for Property &quot;enum1&quot;.
                             */
                            valueHandler = composeValueHandler_Enum1();
                            cliDefinition = new CLIOptionDefinition( "enum1", List.of( "--enum1" ), null, null, null, true, valueHandler, false, null );
                            m_CLIDefinitions.add( cliDefinition );
                        }  //  BugHuntImpl()

                            /*---------*\\
                        ====** Methods **==========================================================
                            \\*---------*/
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void addListener( final ConfigurationChangeListener listener )
                        {
                            m_ListenerSupport.addListener( listener );
                        }  //  addListener()

                        /**
                         * Creates the value handler for the property &quot;enum1.&quot;.
                         *
                         * @return The value handler.
                         */
                        private final CmdLineValueHandler<?> composeValueHandler_Enum1()
                        {
                            @SuppressWarnings( "RedundantExplicitVariableType" )
                            final BiConsumer<String, MyEnum> lambda = (propertyName,value) -> m_Enum1 = value;
                            final CmdLineValueHandler<?> retValue = new SimpleCmdLineValueHandler<>( lambda, new EnumStringConverter( MyEnum.class ) );

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  composeValueHandler_Enum1()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void dumpParamFileTemplate( final OutputStream outputStream ) throws IOException
                        {
                            ConfigUtil.dumpParamFileTemplate( m_CLIDefinitions, outputStream );
                        }  //  dumpParamFileTemplate()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Charset getCharset()
                        {
                            return m_Charset;
                        }  //  getCharset()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Clock getClock()
                        {
                            return m_Clock;
                        }  //  getClock()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Locale getLocale()
                        {
                            return m_Locale;
                        }  //  getLocale()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final String getMessagePrefix()
                        {
                            return "MSG";
                        }  //  getMessagePrefix()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final long getProcessId()
                        {
                            return m_ProcessId;
                        }  //  getProcessId()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Random getRandom()
                        {
                            return m_Random;
                        }  //  getRandom()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Optional<ResourceBundle> getResourceBundle()
                        {
                            return Optional.ofNullable( m_ResourceBundle );
                        }  //  getResourceBundle()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final ZoneId getTimezone()
                        {
                            return m_Timezone;
                        }  //  getTimezone()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final boolean isDebug()
                        {
                            return m_IsDebug;
                        }  //  isDebug()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final boolean isTest()
                        {
                            return m_IsTest;
                        }  //  isTest()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final boolean parseCommandLine( final String[] args )
                        {
                            var retValue = true;
                            try
                            {
                                ConfigUtil.parseCommandLine( m_CLIDefinitions, args );
                                m_CLIErrorMessage = null;
                            }
                            catch( final CmdLineException e )
                            {
                                m_CLIErrorMessage = e.getLocalizedMessage();
                                retValue = false;
                            }

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  parseCommandLine()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void printUsage( final OutputStream outputStream, final CharSequence command )
                                throws IOException
                        {
                            ConfigUtil.printUsage( outputStream, getResourceBundle(), command, m_CLIDefinitions );
                        }  //  printUsage()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void removeListener( final ConfigurationChangeListener listener )
                        {
                            m_ListenerSupport.removeListener( listener );
                        }  //  removeListener()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Optional<String> retrieveParseErrorMessage()
                        {
                            return Optional.ofNullable( m_CLIErrorMessage );
                        }  //  retrieveParseErrorMessage()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setCharset( final Charset charset )
                        {
                            final var newValue = requireNonNullArgument( charset, "charset" );
                            m_ListenerSupport.fireEvent( "charset", m_Charset, newValue );
                            m_Charset = newValue;
                        }  //  setCharset()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setClock( final Clock clock )
                        {
                            final var newValue = requireNonNullArgument( clock, "clock" );
                            m_ListenerSupport.fireEvent( "clock", m_Clock, newValue );
                            m_Clock = newValue;
                        }  //  setClock()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setEnum1( final MyEnum enum1 )
                        {
                            final var newValue = enum1;
                            m_ListenerSupport.fireEvent( "enum1", m_Enum1, newValue );
                            m_Enum1 = newValue;
                        }  //  setEnum1()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setLocale( final Locale locale )
                        {
                            final var newValue = requireNonNullArgument( locale, "locale" );
                            m_ListenerSupport.fireEvent( "locale", m_Locale, newValue );
                            m_Locale = newValue;
                        }  //  setLocale()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setResourceBundle( final ResourceBundle resourceBundle )
                        {
                            final var newValue = resourceBundle;
                            m_ListenerSupport.fireEvent( "resourceBundle", m_ResourceBundle, newValue );
                            m_ResourceBundle = newValue;
                        }  //  setResourceBundle()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setTimezone( final ZoneId timezone )
                        {
                            final var newValue = requireNonNullArgument( timezone, "timezone" );
                            m_ListenerSupport.fireEvent( "timezone", m_Timezone, newValue );
                            m_Timezone = newValue;
                        }  //  setTimezone()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public String toString()
                        {
                            final var prefix = format ( "%s [", getClass().getName() );
                            final var joiner = new StringJoiner( ", ", prefix, "]" );

                            //---* Property "charset" *--------------------------------------------
                            {
                                final var stringConverter = CharsetStringConverter.INSTANCE;
                                final var value = stringConverter.toString( m_Charset );
                                joiner.add( format( "charset = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Property "enum1" *----------------------------------------------
                            {
                                final var stringConverter = new EnumStringConverter( MyEnum.class );
                                final var value = stringConverter.toString( m_Enum1 );
                                joiner.add( format( "enum1 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Property "isDebug" *--------------------------------------------
                            {
                                final var stringConverter = BooleanStringConverter.INSTANCE;
                                final var value = stringConverter.toString( m_IsDebug );
                                joiner.add( format( "isDebug = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Property "isTest" *---------------------------------------------
                            {
                                final var stringConverter = BooleanStringConverter.INSTANCE;
                                final var value = stringConverter.toString( m_IsTest );
                                joiner.add( format( "isTest = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Property "locale" *---------------------------------------------
                            {
                                final var stringConverter = LocaleStringConverter.INSTANCE;
                                final var value = stringConverter.toString( m_Locale );
                                joiner.add( format( "locale = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Property "processId" *------------------------------------------
                            {
                                joiner.add( format( "processId = \\"%1$S\\"", Objects.toString( m_ProcessId ) ) );
                            }

                            //---* Property "timezone" *-------------------------------------------
                            {
                                final var stringConverter = ZoneIdStringConverter.INSTANCE;
                                final var value = stringConverter.toString( m_Timezone );
                                joiner.add( format( "timezone = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                            }

                            //---* Create the return value *---------------------------------------
                            final var retValue = joiner.toString();

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  toString()
                    }
                    //  class BugHuntImpl

                    /*
                     * End of File
                     */"""
                .replace( "[[[BUILD_DATETIME]]]", configuration.getBuildTime().toString() );
            assertNotNull( expected );
            if( isNotEmpty( expected ) )
            {
                assertEquals( expected, actual.toString() );
            }
            else
            {
                out.println( header );
                out.println( actual );
                out.println( header );
                out.println();
            }
        }
    }   //  testCodeGeneration()
}
//  class BugHunt_20220205_001

/*
 *  End of File
 */
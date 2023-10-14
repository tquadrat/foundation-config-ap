/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
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
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.test.helper.CodeGeneratorTestBase;

/**
 *  Tests the generation of a configuration bean after the introduction of the
 *  {@link org.tquadrat.foundation.config.SystemPreference &#64;SystemPreference}
 *  annotation.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestConfigBeanBuilder2.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: TestConfigBeanBuilder2.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.config.ap.impl.TestConfigBeanBuilder2" )
public class TestConfigBeanBuilder2 extends CodeGeneratorTestBase
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  A test for the code generation.
     *
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest( name = "testCodeGeneration8 [{index}] = {0}" )
    @ValueSource( booleans = { true, false } )
    final void testCodeGeneration( final boolean flag ) throws Exception
    {
        skipThreadTest();

        final var header = format( "%n//----< %2$s >%1$s", "-".repeat( 80 ), "testCodeGeneration8" ).substring( 0, 80 );

        final APHelper environment = mock( APHelper.class );
        final var configuration = createConfiguration( "ConfigBean", "ConfigSpec", environment, flag );
        assertNotNull( configuration );

        //---* Add the interfaces to implement *-------------------------------
        final var interfacesToImplement = List.of( ClassName.from( ConfigBeanSpec.class ) );
        configuration.addInterfacesToImplement( interfacesToImplement );

        //---* Add the properties *--------------------------------------------
        createPropertiesForConfigBeanSpec( configuration );
        createPropertiesForSystemPrefsInit( configuration );

        //---* Run the test *--------------------------------------------------
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
             * We have added only ConfigBeanSpec, so we will get only a basic
             * configuration bean.
             */
            final var expected =
                """
                    /*
                     * ============================================================================
                     * This file inherits the copyright and license(s) from the interface that is
                     * implemented by the class
                     *
                     *     org.tquadrat.foundation.test.generated.ConfigBean
                     *
                     * Refer to
                     *
                     *     org.tquadrat.foundation.test.ConfigSpec
                     *
                     * and the file comment there for the details.
                     * ============================================================================
                     */
                                    
                    package org.tquadrat.foundation.test.generated;
                                    
                    import static java.lang.String.format;
                    import static java.lang.System.getProperty;
                    import static java.nio.charset.Charset.defaultCharset;
                    import static java.util.prefs.Preferences.systemRoot;
                    import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
                    import static org.tquadrat.foundation.lang.Objects.nonNull;
                    import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
                                    
                    import java.lang.ExceptionInInitializerError;
                    import java.lang.Override;
                    import java.lang.String;
                    import java.lang.SuppressWarnings;
                    import java.nio.charset.Charset;
                    import java.time.ZoneId;
                    import java.util.Locale;
                    import java.util.Optional;
                    import java.util.ResourceBundle;
                    import java.util.StringJoiner;
                    import java.util.concurrent.locks.ReentrantReadWriteLock;
                    import java.util.prefs.BackingStoreException;
                    import org.tquadrat.foundation.annotation.ClassVersion;
                    import org.tquadrat.foundation.config.ConfigurationChangeListener;
                    import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
                    import org.tquadrat.foundation.config.spi.prefs.StringAccessor;
                    import org.tquadrat.foundation.lang.AutoLock;
                    import org.tquadrat.foundation.test.ConfigSpec;
                    import org.tquadrat.foundation.test.config.BaseClass;
                    import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.CharsetStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.LocaleStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.StringStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.ZoneIdStringConverter;
                                    
                    /**
                     * The configuration bean that implements
                     * {@link ConfigSpec}.
                     */
                    @ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at [[[BUILD_DATETIME]]]", isGenerated = true )
                    @SuppressWarnings( {"OverlyComplexClass", "OverlyCoupledClass"} )
                    public final class ConfigBean extends BaseClass implements ConfigSpec
                    {
                            /*------------*\\
                        ====** Attributes **=======================================================
                            \\*------------*/
                        /**
                         * Property: &quot;charset&quot;.
                         */
                        private Charset m_Charset;
                                    
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
                         * The &quot;read&quot; lock.
                         */
                        private final AutoLock m_ReadLock;
                                    
                        /**
                         * Special Property: &quot;resourceBundle&quot;.
                         */
                        @SuppressWarnings( "FieldMayBeFinal" )
                        private ResourceBundle m_ResourceBundle = null;
                                    
                        /**
                         * Property: &quot;systemPrefsString&quot;.
                         */
                        private String m_SystemPrefsString;
                                    
                        /**
                         * Property: &quot;timezone&quot;.
                         */
                        private ZoneId m_Timezone;
                                    
                        /**
                         * The &quot;write&quot; lock.
                         */
                        private final AutoLock m_WriteLock;
                                    
                            /*--------------*\\
                        ====** Constructors **=====================================================
                            \\*--------------*/
                        /**
                         * Creates a new {@code ConfigBean} instance.
                         */
                        public ConfigBean()
                        {
                            //---* Initialise the listener support *-------------------------------
                            m_ListenerSupport = new ConfigChangeListenerSupport( this );
                                    
                            //---* Create the locks and initialise them *--------------------------
                            final var lock = new ReentrantReadWriteLock();
                            m_ReadLock = AutoLock.of( lock.readLock() );
                            m_WriteLock = AutoLock.of( lock.writeLock() );
                                    
                            /*
                             * Initialise the property 'charset'.
                             */
                            m_Charset = defaultCharset();
                                    
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
                             * Initialise the property 'systemPrefsString' from the SYSTEM {@code Preferences}.
                             *
                             * Path: /org/tquadrat/foundation/test
                             * Key : system_preference
                             */
                            try
                            {
                                if( systemRoot().nodeExists( "/org/tquadrat/foundation/test" ) )
                                {
                                    final var node = systemRoot().node( "/org/tquadrat/foundation/test" );
                                    final var accessor = new StringAccessor( "system_preference", () -> m_SystemPrefsString, p -> m_SystemPrefsString = p );
                                    accessor.readPreference( node );
                                }
                            }
                            catch( final BackingStoreException e )
                            {
                                throw new ExceptionInInitializerError( e );
                            }
                                    
                            /*
                             * Initialise the property 'timezone'.
                             */
                            m_Timezone = ZoneId.systemDefault();
                        }  //  ConfigBean()
                                    
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
                         * {@inheritDoc}
                         */
                        @Override
                        public final Charset getCharset()
                        {
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return m_Charset;
                            }
                        }  //  getCharset()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Locale getLocale()
                        {
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return m_Locale;
                            }
                        }  //  getLocale()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Optional<ResourceBundle> getResourceBundle()
                        {
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return Optional.ofNullable( m_ResourceBundle );
                            }
                        }  //  getResourceBundle()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final String getSystemPrefsString()
                        {
                            return m_SystemPrefsString;
                        }  //  getSystemPrefsString()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final ZoneId getTimezone()
                        {
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return m_Timezone;
                            }
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
                        public final void removeListener( final ConfigurationChangeListener listener )
                        {
                            m_ListenerSupport.removeListener( listener );
                        }  //  removeListener()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setCharset( final Charset charset )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = requireNonNullArgument( charset, "charset" );
                                m_ListenerSupport.fireEvent( "charset", m_Charset, newValue );
                                m_Charset = newValue;
                            }
                        }  //  setCharset()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setLocale( final Locale locale )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = requireNonNullArgument( locale, "locale" );
                                m_ListenerSupport.fireEvent( "locale", m_Locale, newValue );
                                m_Locale = newValue;
                            }
                        }  //  setLocale()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setTimezone( final ZoneId timezone )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = requireNonNullArgument( timezone, "timezone" );
                                m_ListenerSupport.fireEvent( "timezone", m_Timezone, newValue );
                                m_Timezone = newValue;
                            }
                        }  //  setTimezone()
                                    
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public String toString()
                        {
                            final var prefix = format ( "%s [", getClass().getName() );
                            final var joiner = new StringJoiner( ", ", prefix, "]" );
                                    
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                //---* Property "charset" *----------------------------------------
                                {
                                    final var stringConverter = CharsetStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Charset );
                                    joiner.add( format( "charset = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                                    
                                //---* Property "isDebug" *----------------------------------------
                                {
                                    final var stringConverter = BooleanStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_IsDebug );
                                    joiner.add( format( "isDebug = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                                    
                                //---* Property "isTest" *-----------------------------------------
                                {
                                    final var stringConverter = BooleanStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_IsTest );
                                    joiner.add( format( "isTest = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                                    
                                //---* Property "locale" *-----------------------------------------
                                {
                                    final var stringConverter = LocaleStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Locale );
                                    joiner.add( format( "locale = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                                    
                                //---* Property "systemPrefsString" *------------------------------
                                {
                                    final var stringConverter = StringStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_SystemPrefsString );
                                    joiner.add( format( "systemPrefsString = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                                    
                                //---* Property "timezone" *---------------------------------------
                                {
                                    final var stringConverter = ZoneIdStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Timezone );
                                    joiner.add( format( "timezone = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
                            }
                                    
                            //---* Create the return value *---------------------------------------
                            final var retValue = joiner.toString();
                                    
                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  toString()
                    }
                    //  class ConfigBean
                                    
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
//  class TestConfigBeanBuilder2

/*
 *  End of File
 */
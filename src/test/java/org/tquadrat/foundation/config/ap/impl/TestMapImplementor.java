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
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.test.helper.CodeGeneratorTestBase;

/**
 *  Tests the generation of a configuration bean that implements the
 *  {@link Map}
 *  interface.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestMapImplementor.java 1077 2023-10-14 23:00:23Z tquadrat $
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: TestMapImplementor.java 1077 2023-10-14 23:00:23Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.config.ap.impl.TestMapImplementor" )
public class TestMapImplementor extends CodeGeneratorTestBase
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
    @ParameterizedTest( name = "testCodeGeneration5 [{index}] = {0}" )
    @ValueSource( booleans = { true, false } )
    final void testCodeGeneration5( final boolean flag ) throws Exception
    {
        final var header = format( "%n//----< %2$s >%1$s", "-".repeat( 80 ), "testCodeGeneration5" ).substring( 0, 80 );

        skipThreadTest();

        final APHelper environment = mock( APHelper.class );
        final var configuration = createConfiguration( environment, flag );
        assertNotNull( configuration );

        //---* Add the interfaces to implement *-------------------------------
        final var interfacesToImplement = List.of( ClassName.from( ConfigBeanSpec.class ), ClassName.from( Map.class ) );
        configuration.addInterfacesToImplement( interfacesToImplement );

        //---* Add the properties *--------------------------------------------
        createPropertiesForConfigBeanSpec( configuration );
        createCustomProperties1( configuration );
        createCustomProperties2( configuration );

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
             * We added the Map interface to the basic configuration bean
             * specification; that should give us the Map methods.
             */
            final var expected =
                """
                    /*
                     * ============================================================================
                     * This file inherits the copyright and license(s) from the interface that is
                     * implemented by the class
                     *
                     *     org.tquadrat.foundation.test.generated.MyConfigurationBean
                     *
                     * Refer to
                     *
                     *     org.tquadrat.foundation.test.MyConfigurationBeanSpecification
                     *
                     * and the file comment there for the details.
                     * ============================================================================
                     */

                    package org.tquadrat.foundation.test.generated;

                    import static java.lang.String.format;
                    import static java.lang.System.getProperty;
                    import static java.nio.charset.Charset.defaultCharset;
                    import static java.util.stream.Collectors.toUnmodifiableList;
                    import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
                    import static org.tquadrat.foundation.lang.Objects.isNull;
                    import static org.tquadrat.foundation.lang.Objects.nonNull;
                    import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
                    import static org.tquadrat.foundation.util.SystemUtils.getPID;

                    import java.io.FileNotFoundException;
                    import java.io.IOException;
                    import java.lang.ExceptionInInitializerError;
                    import java.lang.Integer;
                    import java.lang.Object;
                    import java.lang.Override;
                    import java.lang.String;
                    import java.lang.SuppressWarnings;
                    import java.lang.Throwable;
                    import java.lang.UnsupportedOperationException;
                    import java.nio.charset.Charset;
                    import java.security.SecureRandom;
                    import java.time.Clock;
                    import java.time.Instant;
                    import java.time.ZoneId;
                    import java.util.Collection;
                    import java.util.HashSet;
                    import java.util.Locale;
                    import java.util.Map;
                    import java.util.Optional;
                    import java.util.Properties;
                    import java.util.Random;
                    import java.util.ResourceBundle;
                    import java.util.Set;
                    import java.util.StringJoiner;
                    import java.util.TreeMap;
                    import java.util.concurrent.locks.ReentrantReadWriteLock;
                    import java.util.function.Supplier;
                    import org.tquadrat.foundation.annotation.ClassVersion;
                    import org.tquadrat.foundation.config.ConfigurationChangeListener;
                    import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
                    import org.tquadrat.foundation.exception.ValidationException;
                    import org.tquadrat.foundation.lang.AutoLock;
                    import org.tquadrat.foundation.lang.Objects;
                    import org.tquadrat.foundation.test.MyConfigurationBeanSpecification;
                    import org.tquadrat.foundation.test.config.BaseClass;
                    import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.CharsetStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.InstantStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.IntegerStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.LocaleStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.StringStringConverter;
                    import org.tquadrat.foundation.util.stringconverter.ZoneIdStringConverter;

                    /**
                     * The configuration bean that implements
                     * {@link MyConfigurationBeanSpecification}.
                     */
                    @ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at [[[BUILD_DATETIME]]]", isGenerated = true )
                    @SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass", "OverlyCoupledClass"} )
                    public final class MyConfigurationBean extends BaseClass implements MyConfigurationBeanSpecification
                    {
                            /*------------*\\
                        ====** Attributes **=======================================================
                            \\*------------*/
                        /**
                         * Property: &quot;charset&quot;.
                         */
                        private Charset m_Charset;

                        /**
                         * Special Property: &quot;clock&quot;.
                         */
                        private Clock m_Clock;

                        /**
                         * Property: &quot;date1&quot;.
                         */
                        private Instant m_Date1;

                        /**
                         * Property: &quot;date2&quot;.
                         */
                        private Instant m_Date2;

                        /**
                         * Property: &quot;int1&quot;.
                         */
                        private int m_Int1;

                        /**
                         * Property: &quot;int2&quot;.
                         */
                        private Integer m_Int2;

                        /**
                         * Property: &quot;int3&quot;.
                         */
                        private int m_Int3;

                        /**
                         * Property: &quot;int4&quot;.
                         */
                        private Integer m_Int4;

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
                         * The &quot;read&quot; lock.
                         */
                        private final AutoLock m_ReadLock;

                        /**
                         * Special Property: &quot;resourceBundle&quot;.
                         */
                        private ResourceBundle m_ResourceBundle = null;

                        /**
                         * The shadow map for the properties, used for the implementation of the
                         * {@link Map}
                         * interface.
                         */
                        private final Map<String, Supplier<?>> m_ShadowMap = new TreeMap<>();

                        /**
                         * Property: &quot;string1&quot;.
                         */
                        private String m_String1;

                        /**
                         * Property: &quot;string2&quot;.
                         */
                        private String m_String2;

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
                         * Creates a new {@code MyConfigurationBean} instance.
                         */
                        @SuppressWarnings( "ThrowCaughtLocally" )
                        public MyConfigurationBean()
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
                             * Initialise the properties from 'initData()'.
                             */
                            try
                            {
                                final var initData = initData();
                                if( isNull( initData ) )
                                {
                                    throw new ValidationException( "initData() returns null" );
                                }
                                if( initData.containsKey( "date1" ) )
                                {
                                    m_Date1 = (Instant) initData.get( "date1" );
                                }
                                if( initData.containsKey( "date2" ) )
                                {
                                    m_Date2 = (Instant) initData.get( "date2" );
                                }
                                if( initData.containsKey( "int1" ) )
                                {
                                    m_Int1 = (Integer) initData.get( "int1" );
                                }
                                if( initData.containsKey( "int2" ) )
                                {
                                    m_Int2 = (Integer) initData.get( "int2" );
                                }
                                if( initData.containsKey( "int3" ) )
                                {
                                    m_Int3 = (Integer) initData.get( "int3" );
                                }
                                if( initData.containsKey( "int4" ) )
                                {
                                    m_Int4 = (Integer) initData.get( "int4" );
                                }
                                if( initData.containsKey( "resourceBundle" ) )
                                {
                                    m_ResourceBundle = (ResourceBundle) initData.get( "resourceBundle" );
                                }
                                if( initData.containsKey( "string1" ) )
                                {
                                    m_String1 = (String) initData.get( "string1" );
                                }
                                if( initData.containsKey( "string2" ) )
                                {
                                    m_String2 = (String) initData.get( "string2" );
                                }
                            }
                            catch( final Throwable t )
                            {
                                final var eiie = new ExceptionInInitializerError( "initData() failed" );
                                eiie.addSuppressed( t );
                                throw eiie;
                            }

                            /*
                             * Load initialisation data from resource "MyConfigurationBeanSpecification.properties".
                             */
                            {
                                final var resource = MyConfigurationBeanSpecification.class.getResource( "MyConfigurationBeanSpecification.properties" );
                                if( isNull( resource ) )
                                {
                                    final var fnfe = new FileNotFoundException( "Resource 'MyConfigurationBeanSpecification.properties'" );
                                    final var eiie = new ExceptionInInitializerError( "Cannot find resource 'MyConfigurationBeanSpecification.properties'" );
                                    eiie.addSuppressed( fnfe );
                                    throw eiie;
                                }

                                final var initData = new Properties();
                                try( final var inputStream = resource.openStream() )
                                {
                                    initData.load( inputStream );
                                }
                                catch( final IOException e )
                                {
                                    final var eiie = new ExceptionInInitializerError( "Cannot load resource '%s'".formatted( resource.toExternalForm() ) );
                                    eiie.addSuppressed( e );
                                    throw eiie;
                                }

                                /*
                                 * Initialise the properties.
                                 */
                                String value;

                                value = initData.getProperty( "date1" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = InstantStringConverter.INSTANCE;
                                    m_Date1 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "date2" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = InstantStringConverter.INSTANCE;
                                    m_Date2 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "int1" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    m_Int1 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "int2" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    m_Int2 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "int3" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    m_Int3 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "int4" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    m_Int4 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "string1" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = StringStringConverter.INSTANCE;
                                    m_String1 = stringConverter.fromString( value );
                                }

                                value = initData.getProperty( "string2" );
                                if( nonNull( value ) )
                                {
                                    final var stringConverter = StringStringConverter.INSTANCE;
                                    m_String2 = stringConverter.fromString( value );
                                }
                            }

                            /*
                             * Initialising the shadow map.
                             */
                            m_ShadowMap.put( "charset", () -> m_Charset );
                            m_ShadowMap.put( "clock", () -> m_Clock );
                            m_ShadowMap.put( "date1", () -> m_Date1 );
                            m_ShadowMap.put( "date2", () -> m_Date2 );
                            m_ShadowMap.put( "int1", () -> m_Int1 );
                            m_ShadowMap.put( "int2", () -> m_Int2 );
                            m_ShadowMap.put( "int3", () -> m_Int3 );
                            m_ShadowMap.put( "int4", () -> m_Int4 );
                            m_ShadowMap.put( "isDebug", () -> m_IsDebug );
                            m_ShadowMap.put( "isTest", () -> m_IsTest );
                            m_ShadowMap.put( "locale", () -> m_Locale );
                            m_ShadowMap.put( "object1", this::getObject1 );
                            m_ShadowMap.put( "processId", () -> m_ProcessId );
                            m_ShadowMap.put( "random", () -> m_Random );
                            m_ShadowMap.put( "resourceBundle", () -> m_ResourceBundle );
                            m_ShadowMap.put( "string1", () -> m_String1 );
                            m_ShadowMap.put( "string2", () -> m_String2 );
                            m_ShadowMap.put( "timezone", () -> m_Timezone );
                        }  //  MyConfigurationBean()

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
                        public final void clear()
                        {
                            throw new UnsupportedOperationException();
                        }  //  clear()

                        /**
                         * {@inheritDoc}
                         */
                        @SuppressWarnings( "unlikely-arg-type" )
                        @Override
                        public final boolean containsKey( final Object key )
                        {
                            return m_ShadowMap.containsKey( key );
                        }  //  containsKey()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final boolean containsValue( final Object value )
                        {
                            return values().stream().anyMatch( v -> Objects.equals( v, value ) );
                        }  //  containsValue()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Set<Map.Entry<String, Object>> entrySet()
                        {
                            final Set<Map.Entry<String, Object>> retValue = new HashSet<>();
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                for( final var entry : m_ShadowMap.entrySet() )
                                {
                                    final var key = entry.getKey();
                                    final var value = entry.getValue().get();
                                    retValue.add( Map.entry( key, value ) );
                                }
                            }

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  entrySet()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final boolean equals( final Object o )
                        {
                            return this == o;
                        }  //  equals()

                        /**
                         * {@inheritDoc}
                         */
                        @SuppressWarnings( "unlikely-arg-type" )
                        @Override
                        public final Object get( final Object key )
                        {
                            Object retValue = null;
                            final var supplier = m_ShadowMap.get( key );
                            if( nonNull( supplier) )
                            {
                                try( final var ignored = m_ReadLock.lock() )
                                {
                                    retValue = supplier.get();
                                }
                            }

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  get()

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
                        public final Clock getClock()
                        {
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return m_Clock;
                            }
                        }  //  getClock()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Optional<Instant> getDate1()
                        {
                            return Optional.ofNullable( m_Date1 );
                        }  //  getDate1()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final int getInt1()
                        {
                            return m_Int1;
                        }  //  getInt1()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Integer getInt2()
                        {
                            return m_Int2;
                        }  //  getInt2()

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
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                return Optional.ofNullable( m_ResourceBundle );
                            }
                        }  //  getResourceBundle()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final String getString1()
                        {
                            return m_String1;
                        }  //  getString1()

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
                        public final int hashCode()
                        {
                            return m_ShadowMap.hashCode();
                        }  //  hashCode()

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
                        public final boolean isEmpty()
                        {
                            return m_ShadowMap.isEmpty();
                        }  //  isEmpty()

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
                        public final Set<String> keySet()
                        {
                            return m_ShadowMap.keySet();
                        }  //  keySet()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Object put( final String key, final Object value )
                        {
                            throw new UnsupportedOperationException();
                        }  //  put()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void putAll( final Map<? extends String, ?> m )
                        {
                            throw new UnsupportedOperationException();
                        }  //  putAll()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final Object remove( final Object key )
                        {
                            throw new UnsupportedOperationException();
                        }  //  remove()

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
                        public final void setClock( final Clock clock )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = requireNonNullArgument( clock, "clock" );
                                m_ListenerSupport.fireEvent( "clock", m_Clock, newValue );
                                m_Clock = newValue;
                            }
                        }  //  setClock()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setDate2( final Instant date2 )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = date2;
                                m_ListenerSupport.fireEvent( "date2", m_Date2, newValue );
                                m_Date2 = newValue;
                            }
                        }  //  setDate2()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setInt3( final int int3 )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = int3;
                                m_ListenerSupport.fireEvent( "int3", m_Int3, newValue );
                                m_Int3 = newValue;
                            }
                        }  //  setInt3()

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public final void setInt4( final Integer int4 )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = int4;
                                m_ListenerSupport.fireEvent( "int4", m_Int4, newValue );
                                m_Int4 = newValue;
                            }
                        }  //  setInt4()

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
                        public final void setString2( final String string2 )
                        {
                            try( final var ignored = m_WriteLock.lock() )
                            {
                                final var newValue = string2;
                                m_ListenerSupport.fireEvent( "string2", m_String2, newValue );
                                m_String2 = newValue;
                            }
                        }  //  setString2()

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
                        public final int size()
                        {
                            return m_ShadowMap.size();
                        }  //  size()

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
        
                                //---* Property "date1" *------------------------------------------
                                {
                                    final var stringConverter = InstantStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Date1 );
                                    joiner.add( format( "date1 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "date2" *------------------------------------------
                                {
                                    final var stringConverter = InstantStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Date2 );
                                    joiner.add( format( "date2 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "int1" *-------------------------------------------
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Int1 );
                                    joiner.add( format( "int1 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "int2" *-------------------------------------------
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Int2 );
                                    joiner.add( format( "int2 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "int3" *-------------------------------------------
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Int3 );
                                    joiner.add( format( "int3 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "int4" *-------------------------------------------
                                {
                                    final var stringConverter = IntegerStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_Int4 );
                                    joiner.add( format( "int4 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
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
        
                                //---* Property "object1" *----------------------------------------
                                {
                                    joiner.add( format( "object1 = \\"%1$s\\"", Objects.toString( getObject1() ) ) );
                                }
        
                                //---* Property "processId" *--------------------------------------
                                {
                                    joiner.add( format( "processId = \\"%1$S\\"", Objects.toString( m_ProcessId ) ) );
                                }
        
                                //---* Property "string1" *----------------------------------------
                                {
                                    final var stringConverter = StringStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_String1 );
                                    joiner.add( format( "string1 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
                                }
        
                                //---* Property "string2" *----------------------------------------
                                {
                                    final var stringConverter = StringStringConverter.INSTANCE;
                                    final var value = stringConverter.toString( m_String2 );
                                    joiner.add( format( "string2 = \\"%1$s\\"", nonNull( value ) ? value : NULL_STRING ) );
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

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        @SuppressWarnings( "SimplifyStreamApiCallChains" )
                        public final Collection<Object> values()
                        {
                            final Collection<Object> retValue;
                            try( final var ignored = m_ReadLock.lock() )
                            {
                                retValue = m_ShadowMap.values()
                                            .stream()
                                            .map( Supplier::get )
                                            .collect( toUnmodifiableList() );
                            }

                            //---* Done *----------------------------------------------------------
                            return retValue;
                        }  //  values()
                    }
                    //  class MyConfigurationBean

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
    }   //  testCodeGeneration5()
}
//  class TestMapImplementor

/*
 *  End of File
 */
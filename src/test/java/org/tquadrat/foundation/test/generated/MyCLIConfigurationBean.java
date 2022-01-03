/*
 * ============================================================================
 * This file inherits the copyright and license(s) from the interface that is
 * implemented by the class
 *
 *     org.tquadrat.foundation.test.generated.MyCLIConfigurationBean
 *
 * Refer to
 *
 *     org.tquadrat.foundation.test.MyCLIBeanSpecification
 *
 * and the file comment there for the details.
 * ============================================================================
 */

package org.tquadrat.foundation.test.generated;

import static java.lang.System.getProperty;
import static java.nio.charset.Charset.defaultCharset;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.SystemUtils.getPID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.CharSequence;
import java.lang.ExceptionInInitializerError;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Throwable;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.CmdLineException;
import org.tquadrat.foundation.config.ConfigUtil;
import org.tquadrat.foundation.config.ConfigurationChangeListener;
import org.tquadrat.foundation.config.cli.CmdLineValueHandler;
import org.tquadrat.foundation.config.cli.InstantValueHandler;
import org.tquadrat.foundation.config.cli.SimpleCmdLineValueHandler;
import org.tquadrat.foundation.config.cli.StringValueHandler;
import org.tquadrat.foundation.config.spi.CLIDefinition;
import org.tquadrat.foundation.config.spi.CLIOptionDefinition;
import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.lang.AutoLock;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.test.MyCLIBeanSpecification;
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
 * {@link MyCLIBeanSpecification}.
 */
@ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at 2021-12-20T00:58:00.671012366Z", isGenerated = true )
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyCoupledClass"} )
public final class MyCLIConfigurationBean extends BaseClass implements MyCLIBeanSpecification
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
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

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     * Creates a new {@code MyCLIConfigurationBean} instance.
     */
    @SuppressWarnings( "ThrowCaughtLocally" )
    public MyCLIConfigurationBean()
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
            final var value = getProperty( "isDebug" );
            m_IsDebug = stringConverter.fromString( value );
        }

        /*
         * Initialise the property 'isTest' from the system properties.
         */
        {
            final var stringConverter = BooleanStringConverter.INSTANCE;
            final var value = getProperty( "isTest" );
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
         * Load initialisation data from resource "MyCLIBeanSpecification.properties".
         */
        {
            final var resource = MyCLIBeanSpecification.class.getResource( "MyCLIBeanSpecification.properties" );
            if( isNull( resource ) )
            {
                final var fnfe = new FileNotFoundException( "Resource 'MyCLIBeanSpecification.properties'" );
                final var eiie = new ExceptionInInitializerError( "Cannot find resource 'MyCLIBeanSpecification.properties'" );
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
                final var eiie = new ExceptionInInitializerError( format( "Cannot load resource '%s'", resource.toExternalForm() ) );
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
         * Initialise the CLI definitions.
         */
        CmdLineValueHandler<?> valueHandler;
        CLIDefinition cliDefinition;

        /*
         * CLI definition for Property &quot;date1&quot;.
         */
        valueHandler = composeValueHandler_date1();
        cliDefinition = new CLIOptionDefinition( "date1", List.of( "--date1" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;date2&quot;.
         */
        valueHandler = composeValueHandler_date2();
        cliDefinition = new CLIOptionDefinition( "date2", List.of( "--date2" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;int1&quot;.
         */
        valueHandler = composeValueHandler_int1();
        cliDefinition = new CLIOptionDefinition( "int1", List.of( "--int1" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;int2&quot;.
         */
        valueHandler = composeValueHandler_int2();
        cliDefinition = new CLIOptionDefinition( "int2", List.of( "--int2" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;int3&quot;.
         */
        valueHandler = composeValueHandler_int3();
        cliDefinition = new CLIOptionDefinition( "int3", List.of( "--int3" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;int4&quot;.
         */
        valueHandler = composeValueHandler_int4();
        cliDefinition = new CLIOptionDefinition( "int4", List.of( "--int4" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;string1&quot;.
         */
        valueHandler = composeValueHandler_string1();
        cliDefinition = new CLIOptionDefinition( "string1", List.of( "--string1" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );

        /*
         * CLI definition for Property &quot;string2&quot;.
         */
        valueHandler = composeValueHandler_string2();
        cliDefinition = new CLIOptionDefinition( "string2", List.of( "--string2" ), null, null, null, true, valueHandler, false, null );
        m_CLIDefinitions.add( cliDefinition );
    }  //  MyCLIConfigurationBean()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addListener( final ConfigurationChangeListener listener )
    {
        m_ListenerSupport.addListener( listener );
    }  //  addListener()

    /**
     * Creates the value handler for the property &quot;date1.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_date1()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Instant> lambda = (propertyName,value) -> m_Date1 = value;
        final CmdLineValueHandler<?> retValue = new InstantValueHandler( lambda ) ;

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_date1()

    /**
     * Creates the value handler for the property &quot;date2.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_date2()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Instant> lambda = (propertyName,value) -> m_Date2 = value;
        final CmdLineValueHandler<?> retValue = new InstantValueHandler( lambda ) ;

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_date2()

    /**
     * Creates the value handler for the property &quot;int1.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_int1()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Integer> lambda = (propertyName,value) -> m_Int1 = value;
        final CmdLineValueHandler<?> retValue = new SimpleCmdLineValueHandler<>( lambda, IntegerStringConverter.INSTANCE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_int1()

    /**
     * Creates the value handler for the property &quot;int2.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_int2()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Integer> lambda = (propertyName,value) -> m_Int2 = value;
        final CmdLineValueHandler<?> retValue = new SimpleCmdLineValueHandler<>( lambda, IntegerStringConverter.INSTANCE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_int2()

    /**
     * Creates the value handler for the property &quot;int3.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_int3()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Integer> lambda = (propertyName,value) -> m_Int3 = value;
        final CmdLineValueHandler<?> retValue = new SimpleCmdLineValueHandler<>( lambda, IntegerStringConverter.INSTANCE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_int3()

    /**
     * Creates the value handler for the property &quot;int4.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_int4()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, Integer> lambda = (propertyName,value) -> m_Int4 = value;
        final CmdLineValueHandler<?> retValue = new SimpleCmdLineValueHandler<>( lambda, IntegerStringConverter.INSTANCE );

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_int4()

    /**
     * Creates the value handler for the property &quot;string1.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_string1()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, String> lambda = (propertyName,value) -> m_String1 = value;
        final CmdLineValueHandler<?> retValue = new StringValueHandler( lambda ) ;

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_string1()

    /**
     * Creates the value handler for the property &quot;string2.&quot;.
     *
     * @return The value handler.
     */
    private final CmdLineValueHandler<?> composeValueHandler_string2()
    {
        @SuppressWarnings( "RedundantExplicitVariableType" )
        final BiConsumer<String, String> lambda = (propertyName,value) -> m_String2 = value;
        final CmdLineValueHandler<?> retValue = new StringValueHandler( lambda ) ;

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  composeValueHandler_string2()

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
        try( final var ignored = m_WriteLock.lock() )
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
    public final void setDate2( final Instant value )
    {
        try( final var ignored = m_WriteLock.lock() )
        {
            final var newValue = value;
            m_ListenerSupport.fireEvent( "date2", m_Date2, newValue );
            m_Date2 = newValue;
        }
    }  //  setDate2()

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setInt3( final int value )
    {
        try( final var ignored = m_WriteLock.lock() )
        {
            final var newValue = value;
            m_ListenerSupport.fireEvent( "int3", m_Int3, newValue );
            m_Int3 = newValue;
        }
    }  //  setInt3()

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setInt4( final Integer value )
    {
        try( final var ignored = m_WriteLock.lock() )
        {
            final var newValue = value;
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
    public final void setString2( final String value )
    {
        try( final var ignored = m_WriteLock.lock() )
        {
            final var newValue = value;
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
    public String toString()
    {
        final var prefix = format ( "%s [", getClass().getName() );
        final var joiner = new StringJoiner( ", ", prefix, "]" );
        try( final var ignored = m_ReadLock.lock() )
        {
            // Property "charset"
            {
                final var stringConverter = CharsetStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Charset );
                if( nonNull( value ) )
                {
                    joiner.add( format( "charset = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "charset = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "date1"
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Date1 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "date1 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "date1 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "date2"
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Date2 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "date2 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "date2 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "int1"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int1 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int1 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int1 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "int2"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int2 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int2 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int2 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "int3"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int3 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int3 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int3 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "int4"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int4 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int4 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int4 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "isDebug"
            {
                final var stringConverter = BooleanStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_IsDebug );
                if( nonNull( value ) )
                {
                    joiner.add( format( "isDebug = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "isDebug = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "isTest"
            {
                final var stringConverter = BooleanStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_IsTest );
                if( nonNull( value ) )
                {
                    joiner.add( format( "isTest = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "isTest = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "locale"
            {
                final var stringConverter = LocaleStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Locale );
                if( nonNull( value ) )
                {
                    joiner.add( format( "locale = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "locale = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "object1"
            {
                joiner.add( format( "object1 = \"%1S\"", Objects.toString( getObject1() ) ) );
            }

            // Property "processId"
            {
                joiner.add( format( "processId = \"%1S\"", Objects.toString( m_ProcessId ) ) );
            }

            // Property "string1"
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_String1 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "string1 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "string1 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "string2"
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_String2 );
                if( nonNull( value ) )
                {
                    joiner.add( format( "string2 = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "string2 = \"%1S\"", NULL_STRING ) );
                }
            }

            // Property "timezone"
            {
                final var stringConverter = ZoneIdStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Timezone );
                if( nonNull( value ) )
                {
                    joiner.add( format( "timezone = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "timezone = \"%1S\"", NULL_STRING ) );
                }
            }
        }

        //---* Create the return value *---------------------------------------
        final var retValue = joiner.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  toString()
}
//  class MyCLIConfigurationBean

/*
 * End of File
 */
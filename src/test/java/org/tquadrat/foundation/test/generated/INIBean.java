/*
 * ============================================================================
 * This file inherits the copyright and license(s) from the interface that is
 * implemented by the class
 *
 *     org.tquadrat.foundation.test.generated.INIBean
 *
 * Refer to
 *
 *     org.tquadrat.foundation.test.INISpec
 *
 * and the file comment there for the details.
 * ============================================================================
 */

package org.tquadrat.foundation.test.generated;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.exists;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.SystemUtils.getPID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ExceptionInInitializerError;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Throwable;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ConfigurationChangeListener;
import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
import org.tquadrat.foundation.config.spi.prefs.PreferencesException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.inifile.INIFile;
import org.tquadrat.foundation.lang.AutoLock;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.test.INISpec;
import org.tquadrat.foundation.test.config.BaseClass;
import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
import org.tquadrat.foundation.util.stringconverter.CharsetStringConverter;
import org.tquadrat.foundation.util.stringconverter.InstantStringConverter;
import org.tquadrat.foundation.util.stringconverter.IntegerStringConverter;
import org.tquadrat.foundation.util.stringconverter.LocaleStringConverter;
import org.tquadrat.foundation.util.stringconverter.PathStringConverter;
import org.tquadrat.foundation.util.stringconverter.StringStringConverter;
import org.tquadrat.foundation.util.stringconverter.ZoneIdStringConverter;

/**
 * The configuration bean that implements
 * {@link INISpec}.
 */
@ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at 2021-12-23T21:28:43.669073712Z", isGenerated = true )
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass", "OverlyCoupledClass"} )
public final class INIBean extends BaseClass implements INISpec
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
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
     * Property: &quot;date1Ini&quot;.
     */
    private Instant m_Date1Ini;

    /**
     * Property: &quot;date2&quot;.
     */
    private Instant m_Date2;

    /**
     * The INIFile instance that is used by this configuration bean to
     * persist (some of) its properties.
     */
    private final INIFile m_INIFile;

    /**
     * The file that backs the INIFile used by this configuration bean.
     */
    @SuppressWarnings( "FieldCanBeLocal" )
    private final Path m_INIFilePath = PathStringConverter.INSTANCE.fromString( "/home/tquadrat/config/dummy.ini" );

    /**
     * Property: &quot;int1&quot;.
     */
    private int m_Int1;

    /**
     * Property: &quot;int1Ini&quot;.
     */
    private int m_Int1Ini;

    /**
     * Property: &quot;int2&quot;.
     */
    private Integer m_Int2;

    /**
     * Property: &quot;int2Ini&quot;.
     */
    private Integer m_Int2Ini;

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
     * Property: &quot;string1Ini&quot;.
     */
    private String m_String1Ini;

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
     * Creates a new {@code INIBean} instance.
     */
    @SuppressWarnings( "ThrowCaughtLocally" )
    public INIBean()
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
            if( initData.containsKey( "date1Ini" ) )
            {
                m_Date1Ini = (Instant) initData.get( "date1Ini" );
            }
            if( initData.containsKey( "date2" ) )
            {
                m_Date2 = (Instant) initData.get( "date2" );
            }
            if( initData.containsKey( "int1" ) )
            {
                m_Int1 = (Integer) initData.get( "int1" );
            }
            if( initData.containsKey( "int1Ini" ) )
            {
                m_Int1Ini = (Integer) initData.get( "int1Ini" );
            }
            if( initData.containsKey( "int2" ) )
            {
                m_Int2 = (Integer) initData.get( "int2" );
            }
            if( initData.containsKey( "int2Ini" ) )
            {
                m_Int2Ini = (Integer) initData.get( "int2Ini" );
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
            if( initData.containsKey( "string1Ini" ) )
            {
                m_String1Ini = (String) initData.get( "string1Ini" );
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
         * Load initialisation data from resource "INISpec.properties".
         */
        {
            final var resource = INISpec.class.getResource( "INISpec.properties" );
            if( isNull( resource ) )
            {
                final var fnfe = new FileNotFoundException( "Resource 'INISpec.properties'" );
                final var eiie = new ExceptionInInitializerError( "Cannot find resource 'INISpec.properties'" );
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

            value = initData.getProperty( "date1Ini" );
            if( nonNull( value ) )
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                m_Date1Ini = stringConverter.fromString( value );
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

            value = initData.getProperty( "int1Ini" );
            if( nonNull( value ) )
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_Int1Ini = stringConverter.fromString( value );
            }

            value = initData.getProperty( "int2" );
            if( nonNull( value ) )
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_Int2 = stringConverter.fromString( value );
            }

            value = initData.getProperty( "int2Ini" );
            if( nonNull( value ) )
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_Int2Ini = stringConverter.fromString( value );
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

            value = initData.getProperty( "string1Ini" );
            if( nonNull( value ) )
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                m_String1Ini = stringConverter.fromString( value );
            }

            value = initData.getProperty( "string2" );
            if( nonNull( value ) )
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                m_String2 = stringConverter.fromString( value );
            }
        }

        //---* Initialise the INI file *----------------------------------------
        m_INIFile = createINIFile( m_INIFilePath );
    }  //  INIBean()

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
     * Creates the
     * {@link INIFile}
     * instance that is connected with this configuration bean.
     *
     * @throws ExceptionInInitializerError Something went wrong on creating/opening the INI file.
     * @param path The path for the file that backs the {@code INIFile}.
     * @return The {@code INIFile} instance.
     */
    @SuppressWarnings( "ThrowCaughtLocally" )
    private static final INIFile createINIFile( final Path path ) throws ExceptionInInitializerError
    {
        final INIFile retValue;
        try
        {
            if( !exists( requireNonNullArgument( path, "path" ) ) )
            {
                throw new FileNotFoundException( path.toString() );
            }
            retValue = INIFile.open( path );
        }
        catch( final IOException e )
        {
            throw new ExceptionInInitializerError( e );
        }

        // Sets the structure of the INIFile
        if( !retValue.hasGroup( "Group3" ) )
        {
            retValue.addComment( "Group3", "The comment for group 3.\n" );
        }
        if( !retValue.hasGroup( "Group2" ) )
        {
            retValue.addComment( "Group2", "The comment for group 2.\n" );
        }
        if( !retValue.hasGroup( "Group1" ) )
        {
            retValue.addComment( "Group1", "The comment for group 1.\n" );
        }
        if( !retValue.hasValue( "Group1", "date1Ini" ) )
        {
            retValue.addComment( "Group1", "date1Ini", "Property 'date1Ini'" );
        }
        if( !retValue.hasValue( "Group1", "int1Ini" ) )
        {
            retValue.addComment( "Group1", "int1Ini", "Property 'int1Ini'" );
        }
        if( !retValue.hasValue( "Group1", "int2Ini" ) )
        {
            retValue.addComment( "Group1", "int2Ini", "Property 'int2Ini'" );
        }
        if( !retValue.hasValue( "Group1", "string1Ini" ) )
        {
            retValue.addComment( "Group1", "string1Ini", "Property 'string1Ini'" );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }  //  createINIFile()

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
    public final Optional<Instant> getDate1Ini()
    {
        return Optional.ofNullable( m_Date1Ini );
    }  //  getDate1Ini()

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
    public final int getInt1Ini()
    {
        return m_Int1Ini;
    }  //  getInt1Ini()

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
    public final Integer getInt2Ini()
    {
        return m_Int2Ini;
    }  //  getInt2Ini()

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
    public final String getString1Ini()
    {
        return m_String1Ini;
    }  //  getString1Ini()

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
    public final void loadINIFile()
    {
        try( final var ignore = m_WriteLock.lock() )
        {
            m_INIFile.refresh();

            /*
             * Load the data.
             */
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                m_Date1Ini = m_INIFile.getValue( "Group1", "date1Ini", stringConverter ).orElse( m_Date1Ini );
            }
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_Int1Ini = m_INIFile.getValue( "Group1", "int1Ini", stringConverter ).orElse( m_Int1Ini );
            }
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_Int2Ini = m_INIFile.getValue( "Group1", "int2Ini", stringConverter ).orElse( m_Int2Ini );
            }
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                m_String1Ini = m_INIFile.getValue( "Group1", "string1Ini", stringConverter ).orElse( m_String1Ini );
            }
        }
        catch( final IOException e )
        {
            throw new PreferencesException( e );
        }
    }  //  loadINIFile()

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<INIFile> obtainINIFile()
    {
        return Optional.of( m_INIFile );
    }  //  obtainINIFile()

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

            // Property "date1Ini"
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Date1Ini );
                if( nonNull( value ) )
                {
                    joiner.add( format( "date1Ini = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "date1Ini = \"%1S\"", NULL_STRING ) );
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

            // Property "int1Ini"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int1Ini );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int1Ini = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int1Ini = \"%1S\"", NULL_STRING ) );
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

            // Property "int2Ini"
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_Int2Ini );
                if( nonNull( value ) )
                {
                    joiner.add( format( "int2Ini = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "int2Ini = \"%1S\"", NULL_STRING ) );
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

            // Property "string1Ini"
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_String1Ini );
                if( nonNull( value ) )
                {
                    joiner.add( format( "string1Ini = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "string1Ini = \"%1S\"", NULL_STRING ) );
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final void updateINIFile()
    {
        try( final var ignore = m_ReadLock.lock() )
        {
            /*
             * Write the data.
             */
            {
                final var stringConverter = InstantStringConverter.INSTANCE;
                m_INIFile.setValue( "Group1", "date1Ini", m_Date1Ini, stringConverter );
            }
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_INIFile.setValue( "Group1", "int1Ini", m_Int1Ini, stringConverter );
            }
            {
                final var stringConverter = IntegerStringConverter.INSTANCE;
                m_INIFile.setValue( "Group1", "int2Ini", m_Int2Ini, stringConverter );
            }
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                m_INIFile.setValue( "Group1", "string1Ini", m_String1Ini, stringConverter );
            }

            m_INIFile.save();
        }
        catch( final IOException e )
        {
            throw new PreferencesException( e );
        }
    }  //  updateINIFile()
}
//  class INIBean

/*
 * End of File
 */
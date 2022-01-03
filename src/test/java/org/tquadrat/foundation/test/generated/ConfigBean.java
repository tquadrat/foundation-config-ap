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

import static java.lang.System.getProperty;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.prefs.Preferences.systemRoot;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

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
@ClassVersion( sourceVersion = "Generated through org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor at 2021-12-20T01:22:29.024412673Z", isGenerated = true )
@SuppressWarnings( "OverlyCoupledClass" )
public final class ConfigBean extends BaseClass implements ConfigSpec
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
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

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
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

            // Property "systemPrefsString"
            {
                final var stringConverter = StringStringConverter.INSTANCE;
                final var value = stringConverter.toString( m_SystemPrefsString );
                if( nonNull( value ) )
                {
                    joiner.add( format( "systemPrefsString = \"%1S\"", value ) );
                }
                else
                {
                    joiner.add( format( "systemPrefsString = \"%1S\"", NULL_STRING ) );
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
//  class ConfigBean

/*
 * End of File
 */
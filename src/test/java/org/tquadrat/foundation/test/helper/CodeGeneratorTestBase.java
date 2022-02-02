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

package org.tquadrat.foundation.test.helper;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CHARSET;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CLOCK;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_LOCALE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_MESSAGEPREFIX;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_PID;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RANDOM;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RESOURCEBUNDLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_SESSION;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_TIMEZONE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_INIFILE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_IS_DEFAULT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_ON_MAP;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_RETURNS_OPTIONAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MANDATORY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_OPTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_SPECIAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_REQUIRES_SYNCHRONIZATION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PREFERENCE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PROPERTY;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_IS_DEBUG;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_IS_TEST;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.JavaUtils.composeGetterName;
import static org.tquadrat.foundation.util.JavaUtils.composeSetterName;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.config.I18nSupport;
import org.tquadrat.foundation.config.SessionBeanSpec;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.config.spi.prefs.IntegerAccessor;
import org.tquadrat.foundation.config.spi.prefs.PreferenceChangeListenerImpl;
import org.tquadrat.foundation.config.spi.prefs.PrimitiveIntAccessor;
import org.tquadrat.foundation.config.spi.prefs.SimplePreferenceAccessor;
import org.tquadrat.foundation.config.spi.prefs.StringAccessor;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.test.NameImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.foundation.util.stringconverter.BooleanStringConverter;
import org.tquadrat.foundation.util.stringconverter.InstantStringConverter;
import org.tquadrat.foundation.util.stringconverter.IntegerStringConverter;
import org.tquadrat.foundation.util.stringconverter.StringStringConverter;

/**
 *  The base class for the code generation tests.
 *
 *  @version $Id: CodeGeneratorTestBase.java 1002 2022-02-01 21:33:00Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( {"AbstractClassWithoutAbstractMethods", "OverlyCoupledClass", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: CodeGeneratorTestBase.java 1002 2022-02-01 21:33:00Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public abstract class CodeGeneratorTestBase extends TestBaseClass
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The method reference for
     *  {@code ConfigAnnotationProcessor.composeFieldName(String)}.
     */
    private static Method m_ComposeFieldNameMethod = null;

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a code generation configuration for a CLI configuration bean.
     *
     *  @param  environment The mock for the
     *      {@link APHelper}.
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @return The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final CodeGenerationConfiguration createCLIConfiguration( final APHelper environment, final boolean flag )
    {
        //---* Create the return value *---------------------------------------
        final var retValue = createConfiguration( "MyCLIConfigurationBean", "MyCLIBeanSpecification", environment, flag );

        /*
         * Additional settings.
         */

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createCLIConfiguration()

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
    protected final CodeGenerationConfiguration createConfiguration( final String className, final String specName, final APHelper environment, final boolean flag )
    {
        final var packageName = "org.tquadrat.foundation.test.generated";

        //---* The constructor arguments *-------------------------------------
        final var composer = new JavaComposer( LAYOUT_FOUNDATION, flag );
        final var specificationClass = ClassName.from( "org.tquadrat.foundation.test", requireNotEmptyArgument( specName, "specName" ) );
        final var configurationBeanClassName = new NameImpl( requireNotEmptyArgument( className, "className" ) );
        final var configurationBeanPackageName = new NameImpl( packageName );
        final var baseClass = ClassName.from( "org.tquadrat.foundation.test.config", "BaseClass" );
        final var synchronizeAccess = true;

        //---* Create the return value *---------------------------------------
        final var retValue = new CodeGenerationConfiguration( environment, composer, specificationClass, configurationBeanClassName, configurationBeanPackageName, baseClass, synchronizeAccess );

        /*
         * Additional settings.
         */
        retValue.setPreferencesRoot( format( "%s.%s", packageName, className).replace( '.', '/' ) );
        retValue.setPreferenceChangeListenerClass( ClassName.from( PreferenceChangeListenerImpl.class ) );
        retValue.setINIFileConfig( Paths.get( "/", "home", "tquadrat", "config", "dummy.ini" ), true,
            """
            This is a dummy INI file used for the tests of the code generation stuff.\
            """);
        retValue.addINIGroup( "Group1",
            """
             The comment for group 1.
             """);
        retValue.addINIGroup( "Group2",
            """
             The comment for group 2.
             """);
        retValue.addINIGroup( "Group3",
            """
             The comment for group 3.
             """);

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConfiguration()

    /**
     *  Creates a code generation configuration.
     *
     *  @param  environment The mock for the
     *      {@link APHelper}.
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @return The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final CodeGenerationConfiguration createConfiguration( final APHelper environment, final boolean flag )
    {
        //---* Create the return value *---------------------------------------
        final var retValue = createConfiguration( "MyConfigurationBean", "MyConfigurationBeanSpecification", environment, flag );

        /*
         * Additional settings.
         */

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConfiguration()

    /**
     *  Creates some custom properties and adds them to the configuration.
     *
     *  @param  configuration   The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createCustomProperties1( final CodeGenerationConfiguration configuration )
    {
        PropertySpecImpl property;

        //---* Create the custom properties *----------------------------------
        property = new PropertySpecImpl( "int1" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( TypeName.from( int.class ) );
        property.setFieldName( "m_Int1" );
        property.setGetterMethodName( new NameImpl( "getInt1" ) );
        property.setGetterReturnType( TypeName.from( int.class ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--int1") );
        property.setPrefsKey( "int1" );
        property.setPrefsAccessorClass( ClassName.from( PrimitiveIntAccessor.class ) );
        property.setINIConfiguration( "group", "int1", "Property int1" );

        property = new PropertySpecImpl( "int2" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Integer.class ) );
        property.setFieldName( "m_Int2" );
        property.setGetterMethodName( new NameImpl( "getInt2" ) );
        property.setGetterReturnType( ClassName.from( Integer.class ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--int2") );
        property.setPrefsKey( "int2" );
        property.setPrefsAccessorClass( ClassName.from( IntegerAccessor.class ) );
        property.setINIConfiguration( "group", "int2", "Property int2" );

        property = new PropertySpecImpl( "date1" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, GETTER_RETURNS_OPTIONAL, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Instant.class ) );
        property.setFieldName( "m_Date1" );
        property.setGetterMethodName( new NameImpl( "getDate1" ) );
        property.setGetterReturnType( ParameterizedTypeName.from( Optional.class, Instant.class ) );
        property.setStringConverterClass( ClassName.from( InstantStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--date1") );
        property.setPrefsKey( "date1" );
        property.setPrefsAccessorClass( ClassName.from( SimplePreferenceAccessor.class ) );
        property.setINIConfiguration( "group", "date1", "Property date1" );

        property = new PropertySpecImpl( "string1" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( String.class ) );
        property.setFieldName( "m_String1" );
        property.setGetterMethodName( new NameImpl( "getString1" ) );
        property.setGetterReturnType( ClassName.from( String.class ) );
        property.setStringConverterClass( ClassName.from( StringStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--string1") );
        property.setPrefsKey( "string1" );
        property.setPrefsAccessorClass( ClassName.from( StringAccessor.class ) );
        property.setINIConfiguration( "group", "string1", "Property string1" );

        createProperty_object1( configuration );

        /*
         * Those special properties that are optional.
         */
        createProperty_clock( configuration );

        //---* The process id property *---------------------------------------
        property = new PropertySpecImpl( CONFIG_PROPERTY_PID.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_PID );
        property.setGetterMethodName( new NameImpl( "getProcessId" ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        //---* The random property *-------------------------------------------
        property = new PropertySpecImpl( CONFIG_PROPERTY_RANDOM.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_RANDOM );
        property.setGetterMethodName( new NameImpl( "getRandom" ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        /*
         * Additional settings.
         */
        //---* The initData() method *-----------------------------------------
        final var initDataMethod = configuration.getComposer()
            .methodBuilder( ConfigAnnotationProcessor.METHODNAME_ConfigBeanSpec_InitData )
            .addModifiers( PUBLIC, DEFAULT )
            .returns( ParameterizedTypeName.from( Map.class, String.class, Object.class ) )
            .build();
        configuration.setInitDataMethod( initDataMethod );

        //---* The initialisation data resource *------------------------------
        final var resource = format( "%1$s.properties", configuration.getSpecification().simpleName() );
        configuration.setInitDataResource( resource );
    }   //  createCustomProperties1()

    /**
     *  Creates some custom properties especially for the test of the
     *  {@link org.tquadrat.foundation.config.ap.impl.codebuilders.MapImplementor}
     *  and adds them to the configuration.
     *
     *  @param  configuration   The configuration.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createCustomProperties2( final CodeGenerationConfiguration configuration ) throws Exception
    {
        PropertySpecImpl property;

        //---* Create the custom properties *----------------------------------
        createProperty_int3( configuration );
        createProperty_int4( configuration );

        createProperty_date2( configuration );
        createProperty_string2( configuration );
    }   //  createCustomProperties2()

    /**
     *  Creates the properties for
     *  {@link ConfigBeanSpec}
     *  and adds them to the configuration.
     *
     *  @param  configuration   The configuration.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForConfigBeanSpec( final CodeGenerationConfiguration configuration ) throws Exception
    {
        createProperty_charset( configuration );
        createProperty_isDebug( configuration );
        createProperty_isTest( configuration );
        createProperty_locale( configuration );
        createProperty_resourceBundle( configuration, false );
        createProperty_timezone( configuration );
    }   //  createPropertiesForConfigBeanSpec()

    /**
     *  Creates the properties for
     *  {@link ConfigBeanSpec}
     *  and adds them to the configuration.
     *
     *  @param  configuration   The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForI18NSupport( final CodeGenerationConfiguration configuration )
    {
        //---* The method that returns the message prefix *--------------------
        createProperty_messagePrefix( configuration );

        /*
         * Additional settings.
         */
        configuration.setI18NParameters( "MSG", format( "%s.TextsAndMessages", getClass().getPackageName() ) );
    }   //  createPropertiesForI18NSupport()

    /**
     *  Creates some custom properties for the INIBeanSpec and adds them to the
     *  configuration.
     *
     *  @param  configuration   The configuration.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForINIBeanSpec( final CodeGenerationConfiguration configuration ) throws Exception
    {
        PropertySpecImpl property;
        String name;

        //---* Create the custom properties *----------------------------------
        name = "int1Ini";
        property = new PropertySpecImpl( name );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_INIFILE );
        property.setPropertyType( TypeName.from( int.class ) );
        property.setFieldName( makeFieldName( name ) );
        property.setGetterMethodName( new NameImpl( composeGetterName( name ) ) );
        property.setGetterReturnType( TypeName.from( int.class ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setINIConfiguration( "Group1", name, format( "Property '%s'", name) );

        name = "int2Ini";
        property = new PropertySpecImpl( name );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_INIFILE );
        property.setPropertyType( ClassName.from( Integer.class ) );
        property.setFieldName( makeFieldName( name ) );
        property.setGetterMethodName( new NameImpl( composeGetterName( name ) ) );
        property.setGetterReturnType( ClassName.from( Integer.class ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setINIConfiguration( "Group1", name, format( "Property '%s'", name) );

        name = "date1Ini";
        property = new PropertySpecImpl( name );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, GETTER_RETURNS_OPTIONAL, ALLOWS_INIFILE );
        property.setPropertyType( ClassName.from( Instant.class ) );
        property.setFieldName( makeFieldName( name ) );
        property.setGetterMethodName( new NameImpl( composeGetterName( name ) ) );
        property.setGetterReturnType( ParameterizedTypeName.from( Optional.class, Instant.class ) );
        property.setStringConverterClass( ClassName.from( InstantStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setINIConfiguration( "Group1", name, format( "Property '%s'", name) );

        name = "string1Ini";
        property = new PropertySpecImpl( name );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_INIFILE );
        property.setPropertyType( ClassName.from( String.class ) );
        property.setFieldName( makeFieldName( name ) );
        property.setGetterMethodName( new NameImpl( composeGetterName( name ) ) );
        property.setGetterReturnType( ClassName.from( String.class ) );
        property.setStringConverterClass( ClassName.from( StringStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setINIConfiguration( "Group1", name, format( "Property '%s'", name) );
    }   //  createPropertiesForINIBeanSpec()

    /**
     *  Creates properties for
     *  {@link ConfigBeanSpec},
     *  that will be initialised from the SYSTEM {@code Preferences}, and adds
     *  them to the configuration.
     *
     *  @param  configuration   The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForSystemPrefsInit( final CodeGenerationConfiguration configuration )
    {
        final PropertySpecImpl property;

        //---* The method that returns the message prefix *--------------------
        property = new PropertySpecImpl( "systemPrefsString" );
        configuration.addProperty( property );
        property.setFlag( SYSTEM_PREFERENCE );
        property.setGetterMethodName( new NameImpl( "getSystemPrefsString" ) );
        property.setGetterReturnType( ClassName.from( String.class ) );
        property.setPropertyType( ClassName.from( String.class ) );
        property.setFieldName( "m_SystemPrefsString" );
        property.setStringConverterClass( ClassName.from( StringStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property.setSystemPrefsPath( "/org/tquadrat/foundation/test" );
        property.setPrefsKey( "system_preference" );
        property.setPrefsAccessorClass( ClassName.from( StringAccessor.class ) );

        /*
         * Additional settings.
         */
    }   //  createPropertiesForSystemPrefsInit()

    /**
     *  Creates the property 'charset', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_charset( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_CHARSET.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL, PROPERTY_IS_MUTABLE );
        property.setSpecialPropertyType( CONFIG_PROPERTY_CHARSET );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_charset()

    /**
     *  Creates the property 'clock', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_clock( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_CLOCK.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL, PROPERTY_IS_MUTABLE );
        property.setSpecialPropertyType( CONFIG_PROPERTY_CLOCK );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_clock()

    /**
     *  Creates the property 'date2', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_date2( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "date2";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, GETTER_RETURNS_OPTIONAL, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Instant.class ) );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        property.setStringConverterClass( ClassName.from( InstantStringConverter.class ) );
        property.setCLIOptionNames( List.of( format( "--%s", propertyName ) ) );
        property.setPrefsKey( propertyName );
        property.setPrefsAccessorClass( ClassName.from( PrimitiveIntAccessor.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_date2()

    /**
     *  Creates the property 'int3', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_int3( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "int3";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( TypeName.from( int.class ) );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        property.setCLIOptionNames( List.of( format( "--%s", propertyName ) ) );
        property.setPrefsKey( propertyName );
        property.setPrefsAccessorClass( ClassName.from( PrimitiveIntAccessor.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_int3()

    /**
     *  Creates the property 'int4', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_int4( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "int4";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Integer.class ) );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        property.setCLIOptionNames( List.of( format( "--%s", propertyName ) ) );
        property.setPrefsKey( propertyName );
        property.setPrefsAccessorClass( ClassName.from( IntegerAccessor.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_int4()

    /**
     *  Creates the property 'isDebug', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_isDebug( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "isDebug";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( SYSTEM_PROPERTY );
        property.setGetterMethodName( new NameImpl( "isDebug" ) );
        property.setPropertyType( BOOLEAN );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setGetterReturnType( BOOLEAN );
        property.setSystemPropertyName( PROPERTY_IS_DEBUG );
        property.setStringConverterClass( ClassName.from( BooleanStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_isDebug()

    /**
     *  Creates the property 'isTest', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_isTest( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "isTest";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( SYSTEM_PROPERTY );
        property.setGetterMethodName( new NameImpl( "isTest" ) );
        property.setPropertyType( BOOLEAN );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setGetterReturnType( BOOLEAN );
        property.setSystemPropertyName( PROPERTY_IS_TEST );
        property.setStringConverterClass( ClassName.from( BooleanStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_isDebug()

    /**
     *  Creates the property 'locale', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_locale( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_LOCALE.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL, PROPERTY_IS_MUTABLE );
        property.setSpecialPropertyType( CONFIG_PROPERTY_LOCALE );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_locale()

    /**
     *  Creates the property 'messagePrefix', and adds it to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_messagePrefix( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_MESSAGEPREFIX.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_MESSAGEPREFIX );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_messagePrefix()

    /**
     *  Creates the property 'object1', and adds it to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_object1( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = "object1";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( GETTER_IS_DEFAULT, GETTER_ON_MAP );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        property.setGetterReturnType( ClassName.from( Object.class ) );
    }   //  createProperty_object1()

    /**
     *  Creates the property 'processId', and adds it to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_processId( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_PID.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_PID );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_processId()

    /**
     *  Creates the property 'random', and adds it to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_random( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_RANDOM.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_RANDOM );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_random()

    /**
     *  Creates the property 'resourceBundle', and adds it to the
     *  configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @param  isMutable   {@code true} if the property should be mutable,
     *      {@code false}  otherwise.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_resourceBundle( final CodeGenerationConfiguration configuration, final boolean isMutable )
    {
        final var propertyName = CONFIG_PROPERTY_RESOURCEBUNDLE.getPropertyName();
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_RESOURCEBUNDLE );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        if( !configuration.implementInterface( I18nSupport.class ) && isMutable )
        {
            property.setFlag( PROPERTY_IS_MUTABLE );
            property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
            property.setSetterArgumentName( new NameImpl( propertyName ) );
        }
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_resourceBundle()

    /**
     *  Creates the property 'timezone', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_timezone( final CodeGenerationConfiguration configuration )
    {
        final var propertyName = CONFIG_PROPERTY_TIMEZONE.getPropertyName();
        final var property = new PropertySpecImpl( CONFIG_PROPERTY_TIMEZONE.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_TIMEZONE );
        property.setGetterMethodName( new NameImpl( composeGetterName( propertyName ) ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_timezone()

    /**
     *  Creates the property 'string2', and adds it to the configuration.
     *
     *  @param  configuration   The configuration that takes the created
     *      property.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final void createProperty_string2( final CodeGenerationConfiguration configuration ) throws Exception
    {
        final var propertyName = "string2";
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( String.class ) );
        property.setFieldName( makeFieldName( propertyName ) );
        property.setSetterMethodName( new NameImpl( composeSetterName( propertyName ) ) );
        property.setSetterArgumentName( new NameImpl( propertyName ) );
        property.setStringConverterClass( ClassName.from( StringStringConverter.class ) );
        property.setCLIOptionNames( List.of( format( "--%s", propertyName ) ) );
        property.setPrefsKey( propertyName );
        property.setPrefsAccessorClass( ClassName.from( PrimitiveIntAccessor.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
    }   //  createProperty_string2()

    /**
     *  Creates a code generation configuration for the generation of a session
     *  configuration bean.
     *
     *  @param  environment The mock for the
     *      {@link APHelper}.
     *  @param  flag    {@code true} if debug output should be created,
     *      {@code false} if not.
     *  @return The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final CodeGenerationConfiguration createSessionConfig( final APHelper environment, final boolean flag )
    {
        //---* The constructor arguments *-------------------------------------
        final var composer = new JavaComposer( LAYOUT_FOUNDATION, flag );
        final var specificationClass = ClassName.from( "org.tquadrat.foundation.test", "MySessionConfigurationBeanSpecification" );
        final var configurationBeanClassName = new NameImpl( "MySessionConfigurationBean" );
        final var configurationBeanPackageName = new NameImpl( "org.tquadrat.foundation.test.generated" );
        final var baseClass = ClassName.from( "org.tquadrat.foundation.test.config", "BaseClass" );
        final var synchronizeAccess = true;

        //---* Create the return value *---------------------------------------
        final var retValue = new CodeGenerationConfiguration( environment, composer, specificationClass, configurationBeanClassName, configurationBeanPackageName, baseClass, synchronizeAccess );

        /*
         * Additional settings.
         */
        //---* Add the interface *---------------------------------------------
        retValue.addInterfacesToImplement( List.of( ClassName.from( SessionBeanSpec.class ) ) );

        //---* Add the property *----------------------------------------------
        final var property = new PropertySpecImpl( CONFIG_PROPERTY_SESSION.getPropertyName() );
        retValue.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_SESSION );
        property.setGetterMethodName( new NameImpl( "getSessionKey" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSessionConfig()

    /**
     *  Composes a field name from the property name.
     *
     *  @param  name    The property name.
     *  @return The field name.
     *
     *  @throws ClassNotFoundException  Unable to load the class
     *      {@link ConfigAnnotationProcessor}.
     *  @throws NoSuchMethodException   Unable to find the method
     *      {@code composeFieldName(String} in
     *      {@link ConfigAnnotationProcessor}.
     *  @throws InvocationTargetException  {@code composeFieldName(String}
     *      threw an exception.
     *  @throws IllegalAccessException  Not allowed accessing
     *      {@code composeFieldName(String}.
     */
    protected static final String makeFieldName( final String name ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        if( isNull( m_ComposeFieldNameMethod ) )
        {
            final var annotationProcessorClass = Class.forName( ConfigAnnotationProcessor.class.getName() );
            m_ComposeFieldNameMethod = annotationProcessorClass.getDeclaredMethod( "composeFieldName", String.class );
            m_ComposeFieldNameMethod.setAccessible( true );
        }
        final var retValue = m_ComposeFieldNameMethod.invoke( null, name ).toString();


        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  makeFieldName()
}
//  class CodeGeneratorTestBase

/*
 *  End of File
 */
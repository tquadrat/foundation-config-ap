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
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_PID;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RANDOM;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RESOURCEBUNDLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_SESSION;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_TIMEZONE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_INIFILE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_IS_DEFAULT;
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
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.config.ConfigBeanSpec;
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
 *  @version $Id: CodeGeneratorTestBase.java 947 2021-12-23 21:44:25Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( {"AbstractClassWithoutAbstractMethods", "OverlyCoupledClass"} )
@ClassVersion( sourceVersion = "$Id: CodeGeneratorTestBase.java 947 2021-12-23 21:44:25Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public abstract class CodeGeneratorTestBase extends TestBaseClass
{
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

        property = new PropertySpecImpl( "object1" );
        configuration.addProperty( property );
        property.setFlag( GETTER_IS_DEFAULT );
        property.setGetterMethodName( new NameImpl( "getObject1" ) );
        property.setGetterReturnType( ClassName.from( Object.class ) );

        /*
         * Those special properties that are optional.
         */
        //---* The clock property *--------------------------------------------
        property = new PropertySpecImpl( CONFIG_PROPERTY_CLOCK.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL, PROPERTY_IS_MUTABLE );
        property.setSpecialPropertyType( CONFIG_PROPERTY_CLOCK );
        property.setGetterMethodName( new NameImpl( "getClock" ) );
        property.setSetterMethodName( new NameImpl( "setClock" ) );
        property.setSetterArgumentName( "clock" );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

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
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createCustomProperties2( final CodeGenerationConfiguration configuration )
    {
        PropertySpecImpl property;

        //---* Create the custom properties *----------------------------------
        property = new PropertySpecImpl( "int3" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( TypeName.from( int.class ) );
        property.setFieldName( "m_Int3" );
        property.setSetterMethodName( new NameImpl( "setInt3" ) );
        property.setSetterArgumentName( "value" );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--int3") );
        property.setPrefsKey( "int3" );
        property.setPrefsAccessorClass( ClassName.from( PrimitiveIntAccessor.class ) );

        property = new PropertySpecImpl( "int4" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Integer.class ) );
        property.setFieldName( "m_Int4" );
        property.setSetterMethodName( new NameImpl( "setInt4" ) );
        property.setSetterArgumentName( "value" );
        property.setStringConverterClass( ClassName.from( IntegerStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--int4") );
        property.setPrefsKey( "int4" );
        property.setPrefsAccessorClass( ClassName.from( IntegerAccessor.class ) );

        property = new PropertySpecImpl( "date2" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, GETTER_RETURNS_OPTIONAL, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( Instant.class ) );
        property.setFieldName( "m_Date2" );
        property.setSetterMethodName( new NameImpl( "setDate2" ) );
        property.setSetterArgumentName( "value" );
        property.setStringConverterClass( ClassName.from( InstantStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--date2") );
        property.setPrefsKey( "date2" );
        property.setPrefsAccessorClass( ClassName.from( SimplePreferenceAccessor.class ) );

        property = new PropertySpecImpl( "string2" );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_OPTION, PROPERTY_CLI_MANDATORY, ALLOWS_PREFERENCES );
        property.setPropertyType( ClassName.from( String.class ) );
        property.setFieldName( "m_String2" );
        property.setSetterMethodName( new NameImpl( "setString2" ) );
        property.setSetterArgumentName( "value" );
        property.setStringConverterClass( ClassName.from( StringStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
        property.setCLIOptionNames( List.of( "--string2") );
        property.setPrefsKey( "string2" );
        property.setPrefsAccessorClass( ClassName.from( StringAccessor.class ) );
    }   //  createCustomProperties2()

    /**
     *  Creates the properties for
     *  {@link ConfigBeanSpec}
     *  and adds them to the configuration.
     *
     *  @param  configuration   The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForConfigBeanSpec( final CodeGenerationConfiguration configuration )
    {
        PropertySpecImpl property;

        property = new PropertySpecImpl( CONFIG_PROPERTY_CHARSET.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_CHARSET );
        property.setGetterMethodName( new NameImpl( "getCharset" ) );
        property.setSetterMethodName( new NameImpl( "setCharset" ) );
        property.setSetterArgumentName( "charset" );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property = new PropertySpecImpl( CONFIG_PROPERTY_LOCALE.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_LOCALE );
        property.setGetterMethodName( new NameImpl( "getLocale" ) );
        property.setSetterMethodName( new NameImpl( "setLocale" ) );
        property.setSetterArgumentName( "locale" );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property = new PropertySpecImpl( CONFIG_PROPERTY_RESOURCEBUNDLE.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_RESOURCEBUNDLE );
        property.setGetterMethodName( new NameImpl( "getResourceBundle" ) );
        property.setSetterArgumentName( "resourceBundle" );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property = new PropertySpecImpl( CONFIG_PROPERTY_TIMEZONE.getPropertyName() );
        configuration.addProperty( property );
        property.setFlag( PROPERTY_IS_SPECIAL );
        property.setSpecialPropertyType( CONFIG_PROPERTY_TIMEZONE );
        property.setGetterMethodName( new NameImpl( "getTimezone" ) );
        property.setSetterMethodName( new NameImpl( "setTimezone" ) );
        property.setSetterArgumentName( "timezone" );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property = new PropertySpecImpl( "isDebug" );
        configuration.addProperty( property );
        property.setFlag( SYSTEM_PROPERTY );
        property.setGetterMethodName( new NameImpl( "isDebug" ) );
        property.setPropertyType( BOOLEAN );
        property.setFieldName( "m_IsDebug" );
        property.setGetterReturnType( BOOLEAN );
        property.setSystemPropertyName( PROPERTY_IS_DEBUG );
        property.setStringConverterClass( ClassName.from( BooleanStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        property = new PropertySpecImpl( "isTest" );
        configuration.addProperty( property );
        property.setFlag( SYSTEM_PROPERTY );
        property.setGetterMethodName( new NameImpl( "isTest" ) );
        property.setPropertyType( BOOLEAN );
        property.setFieldName( "m_IsTest" );
        property.setGetterReturnType( BOOLEAN );
        property.setSystemPropertyName( PROPERTY_IS_TEST );
        property.setStringConverterClass( ClassName.from( BooleanStringConverter.class ) );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );
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
        final PropertySpecImpl property;

        //---* The method that returns the message prefix *--------------------
        property = new PropertySpecImpl( "messagePrefix" );
        configuration.addProperty( property );
        property.setFlag( GETTER_IS_DEFAULT );
        property.setGetterMethodName( new NameImpl( "getMessagePrefix" ) );
        property.setGetterReturnType( ClassName.from( String.class ) );

        /*
         * Additional settings.
         */
        configuration.setI18NParameters( "BaseClass.MESSAGE_PREFIX", "BaseClass.BASE_BUNDLE_NAME" );
    }   //  createPropertiesForI18NSupport()

    /**
     *  Creates some custom properties for the INIBeanSpec and adds them to the
     *  configuration.
     *
     *  @param  configuration   The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final void createPropertiesForINIBeanSpec( final CodeGenerationConfiguration configuration )
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
        property.setGetterMethodName( new NameImpl( makeGetterName( name ) ) );
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
        property.setGetterMethodName( new NameImpl( makeGetterName( name ) ) );
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
        property.setGetterMethodName( new NameImpl( makeGetterName( name ) ) );
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
        property.setGetterMethodName( new NameImpl( makeGetterName( name ) ) );
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
     */
    protected static final String makeFieldName( final String name ) { return format( "m_%s", capitalize( name ) ); }

    /**
     *  Composes a getter name from the property name.
     *
     *  @param  name    The property name.
     *  @return The getter name.
     */
    protected static final String makeGetterName( final String name ) { return format( "get%s", capitalize( name ) ); }

    /**
     *  Composes a setter name from the property name.
     *
     *  @param  name    The property name.
     *  @return The setter name.
     */
    protected static final String makeSetterName( final String name ) { return format( "set%s", capitalize( name ) ); }
}
//  class CodeGeneratorTestBase

/*
 *  End of File
 */
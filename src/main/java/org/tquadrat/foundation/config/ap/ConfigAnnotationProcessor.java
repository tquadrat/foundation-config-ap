/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static java.lang.Boolean.FALSE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.ap.CollectionKind.LIST;
import static org.tquadrat.foundation.config.ap.CollectionKind.MAP;
import static org.tquadrat.foundation.config.ap.CollectionKind.NO_COLLECTION;
import static org.tquadrat.foundation.config.ap.CollectionKind.SET;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_INIFILE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ELEMENTTYPE_IS_ENUM;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.EXEMPT_FROM_TOSTRING;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_IS_DEFAULT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_ON_MAP;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_RETURNS_OPTIONAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MANDATORY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MULTIVALUED;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_ARGUMENT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_OPTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_SPECIAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_REQUIRES_SYNCHRONIZATION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_CHECK_EMPTY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_CHECK_NULL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_IS_DEFAULT;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.CommonConstants.UTF8;
import static org.tquadrat.foundation.lang.DebugOutput.ifDebug;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.StringConverter.METHOD_NAME_GetSubjectClass;
import static org.tquadrat.foundation.util.JavaUtils.PREFIX_GET;
import static org.tquadrat.foundation.util.JavaUtils.PREFIX_IS;
import static org.tquadrat.foundation.util.JavaUtils.isAddMethod;
import static org.tquadrat.foundation.util.JavaUtils.isGetter;
import static org.tquadrat.foundation.util.JavaUtils.isSetter;
import static org.tquadrat.foundation.util.JavaUtils.loadClass;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.decapitalize;
import static org.tquadrat.foundation.util.StringUtils.isEmpty;
import static org.tquadrat.foundation.util.StringUtils.isEmptyOrBlank;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.PropertyName;
import org.tquadrat.foundation.ap.APBase;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.ap.IllegalAnnotationError;
import org.tquadrat.foundation.config.Argument;
import org.tquadrat.foundation.config.CheckEmpty;
import org.tquadrat.foundation.config.CheckNull;
import org.tquadrat.foundation.config.ConfigurationBeanSpecification;
import org.tquadrat.foundation.config.EnvironmentVariable;
import org.tquadrat.foundation.config.ExemptFromToString;
import org.tquadrat.foundation.config.I18nSupport;
import org.tquadrat.foundation.config.INIFileConfig;
import org.tquadrat.foundation.config.INIGroup;
import org.tquadrat.foundation.config.INIValue;
import org.tquadrat.foundation.config.NoPreference;
import org.tquadrat.foundation.config.Option;
import org.tquadrat.foundation.config.Preference;
import org.tquadrat.foundation.config.PreferencesRoot;
import org.tquadrat.foundation.config.SpecialProperty;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.StringConversion;
import org.tquadrat.foundation.config.SystemPreference;
import org.tquadrat.foundation.config.SystemProperty;
import org.tquadrat.foundation.config.ap.impl.CodeGenerator;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.config.cli.CmdLineValueHandler;
import org.tquadrat.foundation.config.internal.ClassRegistry;
import org.tquadrat.foundation.config.spi.prefs.EnumAccessor;
import org.tquadrat.foundation.config.spi.prefs.ListAccessor;
import org.tquadrat.foundation.config.spi.prefs.MapAccessor;
import org.tquadrat.foundation.config.spi.prefs.PreferenceAccessor;
import org.tquadrat.foundation.config.spi.prefs.SetAccessor;
import org.tquadrat.foundation.config.spi.prefs.SimplePreferenceAccessor;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.i18n.BaseBundleName;
import org.tquadrat.foundation.i18n.MessagePrefix;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.lang.StringConverter;
import org.tquadrat.foundation.util.JavaUtils;
import org.tquadrat.foundation.util.LazyMap;
import org.tquadrat.foundation.util.stringconverter.EnumStringConverter;

/**
 *  The annotation processor for the {@code org.tquadrat.foundation.config}
 *  module.
 *
 *  @version $Id: ConfigAnnotationProcessor.java 1076 2023-10-03 18:36:07Z tquadrat $
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( {"OverlyCoupledClass", "OverlyComplexClass", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: ConfigAnnotationProcessor.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@SupportedSourceVersion( SourceVersion.RELEASE_17 )
@SupportedOptions( { APBase.ADD_DEBUG_OUTPUT, APBase.MAVEN_GOAL } )
public class ConfigAnnotationProcessor extends APBase
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name for
     *  {@link org.tquadrat.foundation.config.ConfigBeanSpec#addListener(org.tquadrat.foundation.config.ConfigurationChangeListener)}: {@value}.
     */
    public static final String METHODNAME_ConfigBeanSpec_AddListener = "addListener";

    /**
     *  The name for
     *  {@link org.tquadrat.foundation.config.ConfigBeanSpec#getResourceBundle()}: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String METHODNAME_ConfigBeanSpec_GetResourceBundle = "getResourceBundle";

    /**
     *  The name for the optional {@code initData } method: {@value}.
     */
    public static final String METHODNAME_ConfigBeanSpec_InitData = "initData";

    /**
     *  The name for
     *  {@link Map#isEmpty()}: {@value}.
     */
    public static final String METHODNAME_Map_IsEmpty = "isEmpty";

    /**
     *  The message that indicates that no accessor class is given for the
     *  {@link SystemPreference &#64;SystemPreference} annotation: {@value}.
     */
    public static final String MSG_AccessorMissing = "No accessor is given for the @SystemPreference annotation on '%1$s'";

    /**
     *  The message that indicates the erroneous attempt to define an 'add'
     *  method for a property that is not a collection.
     */
    public static final String MSG_AddMethodNotAllowed = "The method '%1$s' is not allowed, as the property type is not a collection";

    /**
     *  The message that indicates that a mirror cannot be retrieved: {@value}.
     */
    public static final String MSG_CannotRetrieveMirror = "Cannot retrieve Mirror for '%1$s'";

    /**
     *  The message that indicates a clash of CLI annotations.
     */
    public static final String MSG_CLIAnnotationClash = "Annotations @Argument and @Option are mutually exclusive";

    /**
     *  The message that indicates the failure of the code generation for the
     *  configuration bean specification: {@value}.
     */
    public static final String MSG_CodeGenerationFailed = "Code generation for '%1$s.%2$s' failed";

    /**
     *  The message that indicates that a property was specified twice:
     *  {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_DuplicateProperty = "Duplicate property: %1$s";

    /**
     *  The message that indicates that an option name was used twice:
     *  {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_DuplicateOptionName = "Option name '%s' for property '%s' is already used";

    /**
     *  The message that indicates a missing default getter for the message
     *  prefix: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_GetterMustBeDEFAULT = "The getter for the message prefix must be defined as default";

    /**
     *  The message that indicates that an illegal annotation had been applied
     *  to an 'add' method: {@value}.
     */
    public static final String MSG_IllegalAnnotationOnAddMethod = "Invalid annotation '%1$s' on 'add' method '%2$s'";

    /**
     *  The message that indicates that an illegal annotation had been applied
     *  to a getter: {@value}.
     */
    public static final String MSG_IllegalAnnotationOnGetter = "Invalid annotation '%1$s' on getter '%2$s'";

    /**
     *  The message that indicates that an illegal annotation had been applied
     *  to a setter: {@value}.
     */
    public static final String MSG_IllegalAnnotationOnSetter = "Illegal annotation '%1$s' on setter '%2$s'";

    /**
     *  The message that indicates that an invalid implementation for an
     *  interface was used: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_IllegalImplementation = "Illegal implementation for '%1$s': %2$s";

    /**
     *  The message that indicates that a mutator was provided for an immutable
     *  property: {@value}.
     */
    public static final String MSG_IllegalMutator = "No mutator allowed for property '%1$s'";

    /**
     *  The message that indicates that the attribute
     *  {@link INIValue#group()}
     *  was not properly populated: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_INIGroupMissing = "The group for @INIValue on '%s' is missing";

    /**
     *  The message that indicates that the attribute
     *  {@link INIValue#key()}
     *  was not properly populated: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_INIKeyMissing = "The key for @INIValue on '%s' is missing";

    /**
     *  The message that indicates that the attribute
     *  {@link INIFileConfig#path()}
     *  was not properly populated: {@value}.
     */
    public static final String MSG_INIPathMissing = "The path for @INIFileConfig is not set properly";

    /**
     *  The message that indicates that an annotation is valid only for
     *  interfaces: {@value}.
     */
    public static final String MSG_InterfacesOnly = "Only allowed for interfaces";

    /**
     *  The message that indicates that a CLI property is invalid: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_InvalidCLIType = "Property '%s' is neither argument nor option";

    /**
     *  The message that indicates a missing environment variable name for a
     *  property: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_MissingEnvironmentVar = "The name of the environment variable is missing for property '%1$s'";

    /**
     *  The message that indicates a missing getter for a property: {@value}.
     */
    public static final String MSG_MissingGetter = "A getter method for the property '%1$s' is missing";

    /**
     *  The message that indicates that the configuration bean specification
     *  does not extend
     *  {@link org.tquadrat.foundation.config.ConfigBeanSpec}:
     *  {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_MissingInterface = "The configuration bean specification '%1$s' does not extend '%2$s'";

    /**
     *  The message that indicates a missing definition for a property:
     *  {@value}.
     */
    public static final String MSG_MissingPropertyDefinition = "There is neither a getter nor a setter method for the property '%1$s', first introduced by the 'add' method '%2$s'";

    /**
     *  The message that indicates a missing
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  for a property: {@value}.
     */
    public static final String MSG_MissingStringConverter = "There is no StringConverter for the property '%1$s'";

    /**
     *  The message that indicates a missing
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  for a property: {@value}.
     */
    public static final String MSG_MissingStringConverterWithType = "There is no StringConverter for the property '%1$s' than converts '%2$s'";

    /**
     *  The message that indicates a missing system property name for a
     *  property: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_MissingSystemProp = "The name of the system property is missing for property '%1$s'";

    /**
     *  The message that indicates a missing argument index for a CLI argument
     *  property: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_NoArgumentIndex = "No argument index for property '%s'";

    /**
     *  The message that indicates a missing base bundle name configuration: {@value}.
     */
    public static final String MSG_NoBaseBundleName = "There is no public static field providing the base bundle name";

    /**
     *  The message that indicates that an 'add' method was provided for a non-collection property.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_NoCollection = "Method '%1$s' not allowed for property '%2$s': property type is not List, Set or Map";

    /**
     *  The message that indicates a missing message prefix field: {@value}.
     */
    public static final String MSG_NoMessagePrefix = "There is no public static field providing the message prefix";

    /**
     *  The message that indicates a missing property name for a CLI option
     *  property: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_NoOptionName = "No option name for property '%s'";

    /**
     *   The message that indicates that a given method is not a setter:
     *   {@value}.
     */
    public static final String MSG_NoSetter = "The method '%1$s' is neither a setter nor an add method";

    /**
     *  The message that indicates a missing property type.
     */
    public static final String MSG_NoType = "Type for property '%2$s' is missing and cannot be inferred (method: %1$s)";

    /**
     *  The message that indicates that a value cannot be retrieved from a
     *  mirror: {@value}.
     */
    public static final String MSG_NoValueForMirror = "Cannot get value for '%2$s' from Annotation Mirror for '%1$s'";

    /**
     *  The message that indicates a clash of preference annotations: {@value}.
     */
    public static final String MSG_PreferenceAnnotationClash = "Annotations @Preference and @NoPreference are mutually exclusive";

    /**
     *  The message that indicates invalid &quot;preferences&quot;
     *  configuration for a property: {@value}.
     */
    public static final String MSG_PreferencesNotConfigured = "The 'preferences' configuration for '%1$s' is invalid";

    /**
     *  The message that indicates that no {@code Preferences} key is given for
     *  the
     *  {@link SystemPreference &#64;SystemPreference} annotation: {@value}.
     */
    public static final String MSG_PrefsKeyMissing = "No key is given for the @SystemPreference annotation on '%1$s'";

    /**
     *  The message that indicates a wrong return type for
     *  {@link org.tquadrat.foundation.config.ConfigBeanSpec#getResourceBundle()}:
     *  {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_ResourceBundleWrongReturnType = "The return type of getResourceBundle() must be Optional(ResourceBundle)";

    /**
     *  The message that indicates that the session property was not defined:
     *  {@value}.
     *
     *  @see    org.tquadrat.foundation.config.SessionBeanSpec#getSessionKey()
     *  @see    SpecialPropertyType#CONFIG_PROPERTY_SESSION
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_SessionPropertyMissing = "Session property was not defined";

    /**
     *  The message that indicates a mismatch of the values for the
     *  {@link SpecialProperty &#64;SpecialProperty}
     *  annotation: {@value}.
     */
    public static final String MSG_SpecialPropertyMismatch = "%1$s annotation value for '%2$s' is '%3$s', but '%4$s' was expected";

    /**
     *  The message that indicates that the
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  inferred from the setter does not match with that from the getter.
     */
    public static final String MSG_StringConverterMismatch = "Inferred StringConverter does not match the previously determined one: %1$s";

    /**
     *  The message that indicates that the
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  is invalid for the property type.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String MSG_StringConverterNotCompatible = "StringConverter '%2$s' cannot handle '%1$s'";

    /**
     *  The message that indicates a mismatch of the types for the setter
     *  argument and the property itself: {@value}.
     */
    public static final String MSG_TypeMismatch = "Parameter type '%1$s' of setter '%2$s' does not match with property type '%3$s'";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  <p>{@summary The base bundle name.} The value will be set in
     *  {@link #process(Set, RoundEnvironment)}
     *  from a String constant that is annotated with the annotation
     *  {@link BaseBundleName &#64;BaseBundleName}.</p>
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private Optional<String> m_BaseBundleName;

    /**
     *  <p>{@summary The message prefix.} The value will be set in
     *  {@link #process(Set, RoundEnvironment)}
     *  from a String constant that is annotated with the annotation
     *  {@link MessagePrefix &#64;MessagePrefix}.</p>
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private Optional<String> m_MessagePrefix;

    /**
     *  The preferences accessor classes.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    private final Map<TypeName,ClassName> m_PrefsAccessorClasses;

    /**
     *  A map of
     *  {@link StringConverter}
     *  implementations that use instances of
     *  {@link TypeName}
     *  as keys.
     */
    private final LazyMap<TypeName,ClassName> m_StringConvertersForTypeNames;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ConfigAnnotationProcessor} instance.
     */
    public ConfigAnnotationProcessor()
    {
        super();

        m_StringConvertersForTypeNames = LazyMap.use( true, this::initStringConvertersForTypeNames );

        final var buffer = new HashMap<TypeName,ClassName>();
        for( final var entry : ClassRegistry.m_PrefsAccessorClasses.entrySet() )
        {
            buffer.put( TypeName.from( entry.getKey() ), ClassName.from( entry.getValue() ) );
        }
        m_PrefsAccessorClasses = Map.copyOf( buffer );
    }   //  ConfigAnnotationProcessor()

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The type name for the class
     *  {@link org.tquadrat.foundation.config.spi.prefs.SimplePreferenceAccessor}.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final TypeName DEFAULT_ACCESSOR_TYPE;

    /**
     *  The type name for the class
     *  {@link EnumAccessor}.
     */
    @API( status = INTERNAL, since = "0.0.1" )
    public static final TypeName ENUM_ACCESSOR_TYPE;

    /**
     *  The type name for the class
     *  {@link ListAccessor}.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final TypeName LIST_ACCESSOR_TYPE;

    /**
     *  The type name for the class
     *  {@link MapAccessor}.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final TypeName MAP_ACCESSOR_TYPE;

    /**
     *  The type name for the class
     *  {@link PreferenceAccessor}.
     */
    @API( status = INTERNAL, since = "0.0.1" )
    public static final TypeName PREFS_ACCESSOR_TYPE;

    /**
     *  The type name for the class
     *  {@link SetAccessor}.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    public static final TypeName SET_ACCESSOR_TYPE;

    static
    {
        DEFAULT_ACCESSOR_TYPE = ClassName.from( SimplePreferenceAccessor.class );
        ENUM_ACCESSOR_TYPE = ClassName.from( EnumAccessor.class );
        LIST_ACCESSOR_TYPE = ClassName.from( ListAccessor.class );
        MAP_ACCESSOR_TYPE = ClassName.from( MapAccessor.class );
        PREFS_ACCESSOR_TYPE = ClassName.from( PreferenceAccessor.class );
        SET_ACCESSOR_TYPE = ClassName.from( SetAccessor.class );
    }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Checks whether the given type is inappropriate for a configuration bean
     *  property and therefore deserves that an
     *  {@link InappropriateTypeError}
     *  is thrown.
     *
     *  @param  typeName    The type to check.
     *  @throws InappropriateTypeError  The given type may not be chosen for
     *      a configuration bean property.
     *
     *  @see    InputStream
     *  @see    Stream
     *  @see    DoubleStream
     *  @see    IntStream
     *  @see    LongStream
     */
    private final void checkAppropriate( final TypeMirror typeName ) throws InappropriateTypeError
    {
        KindSwitch: switch( requireNonNullArgument( typeName, "type" ).getKind() )
        {
            case ARRAY ->
            {
                //---* Check the component type *------------------------------
                /*
                 * Currently (as of 2023-03-08 and for Java 17), the class
                 * SimpleTypeVisitor14 is the latest incarnation of this type.
                 */
                @SuppressWarnings( {"AnonymousInnerClass"} )
                final var componentType = typeName.accept( new SimpleTypeVisitor14<TypeMirror,Void>()
                {
                    /**
                     *  {@inheritDoc}
                     */
                    @Override
                    public final TypeMirror visitArray( final ArrayType arrayType, final Void ignore )
                    {
                        return arrayType.getComponentType();
                    }   //  visitArray()
                }, null );

                if( nonNull( componentType ) ) checkAppropriate( componentType );
            }

            case BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT ->
            { /* The primitives work just fine */}

            case DECLARED ->
            {
                final var erasure = getTypeUtils().erasure( typeName );
                final var unwantedTypes = List.of( DoubleStream.class, IntStream.class, LongStream.class, InputStream.class, Stream.class );
                final var isInappropriate = unwantedTypes.stream()
                    .map( Class::getName )
                    .map( s -> getElementUtils().getTypeElement( s ) )
                    .map( Element::asType )
                    .map( e -> getTypeUtils().erasure( e ) )
                    .anyMatch( t -> getTypeUtils().isAssignable( erasure, t ) );
                if( isInappropriate ) throw new InappropriateTypeError( TypeName.from( typeName ) );
            }

            default -> throw new UnsupportedEnumError( typeName.getKind() );
        }   //  KindSwitch:
    }   //  checkAppropriate()

    /**
     *  <p>Composes the name of the field from the given property name.</p>
     *
     *  @param  propertyName    The name of the property.
     *  @return The name of the field.
     */
    private static final String composeFieldName( final String propertyName )
    {
        final var retValue = format( "m_%s", capitalize( propertyName ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeFieldName()

    /**
     *  <p>{@summary Creates a registry of the known
     *  {@link StringConverter}
     *  implementations.}</p>
     *  <p>The
     *  {@link TypeName}
     *  of the subject class is the key for that map, the {@code TypeName} for
     *  the {@code Class} implementing the {@code StringConverter} is the
     *  value.</p>
     *
     *  @return An immutable map of
     *      {@link StringConverter}
     *      implementations.
     *
     *  @throws IOException Failed to read the resource files with the
     *      {@code StringConverter} implementations.
     */
    @SuppressWarnings( "NestedTryStatement" )
    @API( status = INTERNAL, since = "0.1.0" )
    public static final Map<TypeName,ClassName> createStringConverterRegistry() throws IOException
    {
        final Map<TypeName,ClassName> buffer = new HashMap<>();

        /*
         * For some reason, the original code for this method does not work:
         *
         * for( final var c : StringConverter.list() )
         * {
         *    final var container = StringConverter.forClass( c );
         *    if( container.isPresent() )
         *    {
         *       final var stringConverterClass = TypeName.from( container.get().getClass() );
         *       final var key = TypeName.from( c );
         *       buffer.put( key, stringConverterClass );
         *       ifDebug( "StringConverters: %1$s => %2$s"::formatted, key, stringConverterClass );
         *    }
         * }
         *
         * This code relies on the code in the foundation-util module, so I
         * assumed the problem there and move the code to here:
         *
         * final var moduleLayer = StringConverter.class.getModule().getLayer();
         * final var converters = isNull( moduleLayer )
         *   ? ServiceLoader.load( StringConverter.class )
         *   : ServiceLoader.load( moduleLayer, StringConverter.class );
         *
         * for( final StringConverter<?> c : converters )
         * {
         *   StringConverter<?> converter;
         *   try
         *   {
         *     final var providerMethod = c.getClass().getMethod( METHOD_NAME_Provider );
         *     converter = (StringConverter<?>) providerMethod.invoke( null );
         *   }
         *   catch( final NoSuchMethodException | IllegalAccessException | InvocationTargetException e )
         *   {
         *     converter = c;
         *   }
         *
         *   for( final var subjectClass : retrieveSubjectClasses( converter ) )
         *   {
         *     buffer.put( TypeName.from( subjectClass ), TypeName.from( converter.getClass() ) );
         *   }
         * }
         *
         * I raised a question on StackOverflow regarding this issue:
         *   https://stackoverflow.com/questions/70861635/java-util-serviceloader-does-not-work-inside-of-an-annotationprocessor
         */

        final var classLoader = CodeGenerationConfiguration.class.getClassLoader();
        final var resources = classLoader.getResources( "META-INF/services/%s".formatted( StringConverter.class.getName() ) );
        for( final var file : list( resources ) )
        {
            try( final var reader = new BufferedReader( new InputStreamReader( file.openStream(), UTF8 ) ) )
            {
                final var converterClasses = reader.lines()
                    .map( String::trim )
                    .filter( s -> !s.startsWith( "#" ) )
                    .map( s -> loadClass( classLoader, s, StringConverter.class ) )
                    .filter( Optional::isPresent )
                    .map( Optional::get )
                    .toList();
                CreateLoop: for( final var aClass : converterClasses )
                {
                    try
                    {
                        final var constructor = aClass.getConstructor();
                        final var instance = constructor.newInstance();
                        for( final var subjectClass : retrieveSubjectClasses( instance ) )
                        {
                            buffer.put( TypeName.from( subjectClass ), ClassName.from( aClass ) );
                        }
                    }
                    catch( final InvocationTargetException | NoSuchMethodException |InstantiationException | IllegalAccessException e )
                    {
                        ifDebug( e );

                        //---* Deliberately ignored! *-------------------------
                        continue CreateLoop;
                    }
                }   //  CreateLoop:
            }
        }

        final var retValue = Map.copyOf( buffer );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createStringConverterRegistry()

    /**
     *  Determines whether the given
     *  {@link TypeMirror type}
     *  is a collection of some type and returns the respective kind.
     *
     *  @param  type The type to check.
     *  @return The collection kind.
     */
    private final CollectionKind determineCollectionKind( final TypeMirror type )
    {
        final var focusType = getTypeUtils().erasure( requireNonNullArgument( type, "type" ) );

        final var listType = getTypeUtils().erasure( getElementUtils().getTypeElement( List.class.getName() ).asType() );
        final var mapType = getTypeUtils().erasure( getElementUtils().getTypeElement( Map.class.getName() ).asType() );
        final var setType = getTypeUtils().erasure( getElementUtils().getTypeElement( Set.class.getName() ).asType() );

        var retValue = NO_COLLECTION;
        if( getTypeUtils().isAssignable( focusType, listType ) ) retValue = LIST;
        if( getTypeUtils().isAssignable( focusType, mapType ) ) retValue = MAP;
        if( getTypeUtils().isAssignable( focusType, setType ) ) retValue = SET;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determineCollectionKind()

    /**
     *  <p>{@summary Determines the element type from the given
     *  {@link TypeMirror}
     *  instance representing a collection.}</p>
     *
     *  @param  type   The type.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the element type.
     */
    private final Optional<TypeMirror> determineElementType( final TypeMirror type )
    {
        final var retValue = switch( determineCollectionKind( requireNonNullArgument( type, "type" ) ) )
            {
                case LIST, SET ->
                {
                    final var genericTypes = retrieveGenericTypes( type );
                    yield genericTypes.size() == 1 ? Optional.of( genericTypes.getFirst() ) : Optional.<TypeMirror>empty();
                }

                default -> Optional.<TypeMirror>empty();
            };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determineElementType()

    /**
     *  <p>{@summary Retrieves the name of the property from the name of the
     *  given executable element for a method that is either a getter, a setter
     *  or an 'add' method.}</p>
     *  <p>Alternatively the method has an annotation that provides the name of
     *  the property.</p>
     *
     *  @param  method  The method.
     *  @return The name of the property.
     *
     *  @see PropertyName
     *  @see SpecialProperty
     *  @see SpecialPropertyType
     */
    private final String determinePropertyNameFromMethod( @SuppressWarnings( "TypeMayBeWeakened" ) final ExecutableElement method )
    {
        final String retValue;
        final var specialPropertyAnnotation = method.getAnnotation( SpecialProperty.class );
        final var propertyNameAnnotation = method.getAnnotation( PropertyName.class );
        if( nonNull( specialPropertyAnnotation ) )
        {
            if( nonNull( propertyNameAnnotation ) )
            {
                throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, PropertyName.class.getName(), method.getSimpleName() ) );
            }
            final var specialPropertyType = specialPropertyAnnotation.value();
            retValue = specialPropertyType.getPropertyName();
        }
        else
        {
            if( nonNull( propertyNameAnnotation ) )
            {
                retValue = propertyNameAnnotation.value();
            }
            else
            {
                final var methodName = method.getSimpleName().toString();

                /*
                 * We know that the method is either a getter, a setter or an 'add'
                 * method. Therefore, we know also that the name starts either with
                 * "get", "set", "add" or "is".
                 */
                final var pos = methodName.startsWith( PREFIX_IS ) ? PREFIX_IS.length() : PREFIX_GET.length();
                retValue = decapitalize( methodName.substring( pos ) );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determinePropertyNameFromMethod()

    /**
     *  <p>{@summary Determines the property type from the given
     *  {@link TypeMirror}
     *  instance.} This is either the return type of a
     *  getter or the argument type of a setter.</p>
     *  <p>Usually the property type will be the respective
     *  {@link TypeMirror}
     *  for the given type as is, only in case of
     *  {@link Optional},
     *  it will be the parameter type.</p>
     *
     *  @param  type   The type.
     *  @return The property type.
     */
    private final TypeMirror determinePropertyType( final TypeMirror type )
    {
        var retValue = requireNonNullArgument( type, "type" );
        final var optionalType = getTypeUtils().erasure( getElementUtils().getTypeElement( Optional.class.getName() ).asType() );
        if( getTypeUtils().isAssignable( getTypeUtils().erasure( retValue ), optionalType ) )
        {
            /*
             * The return type is Optional, we need to get the type parameter
             * and have to look at that.
             */
            final var genericTypes = retrieveGenericTypes( retValue );
            if( genericTypes.size() == 1 ) retValue = genericTypes.getFirst();
        }

        //---* Check whether the property type is appropriate *----------------
        checkAppropriate( retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determinePropertyType()

    /**
     *  Determines the implementation of
     *  {@link StringConverter}
     *  that can translate a String into an instance of the given type and
     *  vice-versa.
     *
     *  @param  method  The annotated method; it is only used to get the
     *      instance of
     *      {@link StringConversion &#64;StringConversion}
     *      from it.
     *  @param  type    The target type.
     *  @param  isEnum  {@code true} if the target type is an
     *      {@link Enum enum}
     *      type, {@code false} otherwise.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the determined class.
     */
    private final Optional<ClassName> determineStringConverterClass( final ExecutableElement method, final TypeName type, final boolean isEnum )
    {
        requireNonNullArgument( type, "type" );
        requireNonNullArgument( method, "method" );
        ifDebug( a -> "Method: %2$s%n\tType for StringConverter request: %1$s%n\tisEnum: %3$b".formatted( a [0].toString(), ((Element) a[1]).getSimpleName(), a [2] ), type, method, Boolean.valueOf( isEnum ) );

        //---* Retrieve the StringConverter from the annotation *--------------
        final var retValue = extractStringConverterClass( method )
            .or( () -> Optional.ofNullable( isEnum ? ClassName.from( EnumStringConverter.class ) : m_StringConvertersForTypeNames.get( type ) ) );
        //noinspection unchecked
        ifDebug( a -> ((Optional<TypeName>) a [0]).map( "Detected StringConverter: %1$s"::formatted ).orElse( "Could not find a StringConverter" ), retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determineStringConverterClass

    /**
     *  <p>{@summary Retrieves the value for the
     *  {@link StringConversion &#64;StringConversion}
     *  annotation from the given method.}</p>
     *  <p>The type for the annotation value is an instance of
     *  {@link Class Class&lt;? extends StringConverter&gt;},
     *  so it cannot be retrieved directly. Therefore this method will return
     *  the
     *  {@link TypeName}
     *  for the
     *  {@link StringConverter}
     *  implementation class.</p>
     *
     *  @param  method  The annotated method.
     *  @return An instance of
     *      {@link Optional}
     *      holding the type name that represents the annotation value
     *      &quot;<i>stringConverter</i>&quot;.
     */
    public final Optional<ClassName> extractStringConverterClass( final ExecutableElement method )
    {
        final var retValue = getAnnotationMirror( requireNonNullArgument( method, "method" ), StringConversion.class )
            .flatMap( this::getAnnotationValue )
            .map( annotationValue -> TypeName.from( (TypeMirror) annotationValue.getValue() ) )
            .map( TypeName::toString )
            .map( JavaUtils::loadClass )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .map( ClassName::from );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  extractStringConverterClass()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final Collection<Class<? extends Annotation>> getSupportedAnnotationClasses()
    {
        final Collection<Class<? extends Annotation>> retValue =
            List.of( ConfigurationBeanSpecification.class );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getSupportedAnnotationClasses()

    /**
     *  Processes the given
     *  {@link ExecutableElement}
     *  instance for an 'add' method.
     *
     *  @param  configuration   The code generation configuration.
     *  @param  addMethod   The 'add' method.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "OverlyCoupledMethod", "OverlyComplexMethod"} )
    private final void handleAddMethod( final CodeGenerationConfiguration configuration, final ExecutableElement addMethod )
    {
        //---* Get the method name *-------------------------------------------
        final var addMethodName = addMethod.getSimpleName();

        //---* Check for unwanted annotations *--------------------------------
        @SuppressWarnings( "unchecked" )
        Class<? extends Annotation> [] unwantedAnnotations = new Class[]
        {
            SystemProperty.class,
            EnvironmentVariable.class,
            SystemPreference.class,
            Preference.class,
            NoPreference.class,
            Argument.class,
            Option.class,
            INIValue.class
        };
        for( final var annotationClass : unwantedAnnotations )
        {
            if( nonNull( addMethod.getAnnotation( annotationClass ) ) )
            {
                throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnAddMethod, annotationClass.getName(), addMethodName ) );
            }
        }

        /*
         * If the 'add' method is default, our job is mostly done already.
         */
        final var isDefault = addMethod.getModifiers().contains( DEFAULT );
        if( isDefault )
        {
            //noinspection unchecked
            unwantedAnnotations = new Class[]
                {
                    CheckEmpty.class,
                    CheckNull.class,
                    SpecialProperty.class
                };
            for( final var annotationClass : unwantedAnnotations )
            {
                if( nonNull( addMethod.getAnnotation( annotationClass ) ) )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnAddMethod, annotationClass.getName(), addMethodName ) );
                }
            }
        }
        else
        {
            //---* Get the property name *-------------------------------------
            final var propertyName = determinePropertyNameFromMethod( addMethod );

            /*
             * As we cannot infer the property type from an add method, it is
             * required that we already have a property specification for this
             * property.
             */
            final var property = (PropertySpecImpl) configuration.getProperty( propertyName )
                .orElseThrow( () -> new org.tquadrat.foundation.ap.CodeGenerationError( format( MSG_MissingPropertyDefinition, propertyName, addMethodName ) ) );

            //---* Only collections may have 'add' methods *-------------------
            if( property.getCollectionKind() == NO_COLLECTION )
            {
                throw new CodeGenerationError( format( MSG_AddMethodNotAllowed, addMethodName ) );
            }

            //---* Immutable special properties may not have an add method *---
            if( property.hasFlag( PROPERTY_IS_SPECIAL ) )
            {
                property.getSpecialPropertyType()
                    .map( CodeGenerator::getSpecialPropertySpecification )
                    .filter( spec -> spec.hasFlag( PROPERTY_IS_MUTABLE ) )
                    .orElseThrow( () -> new CodeGenerationError( format( MSG_IllegalMutator, propertyName ) ) );
            }

            //---* Save the method name and variable name *--------------------
            property.setAddMethodName( addMethodName );
            property.setAddMethodArgumentName( retrieveSetterArgumentName( addMethod ) );

            //---* We have an add method, so the property is mutable *---------
            property.setFlag( PROPERTY_IS_MUTABLE );
            if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

            /*
             * For a special property, all the definitions are made there, so
             * nothing to do here …
             */
            if( !property.hasFlag( PROPERTY_IS_SPECIAL ) )
            {
                final var propertyType = property.getPropertyType();
                if( isNull( propertyType ) )
                {
                    throw new CodeGenerationError( format( MSG_NoType, addMethodName, propertyName ) );
                }

                /*
                 * Get the StringConverter; as this cannot be inferred it has
                 * to be taken from the annotation @StringConversion, if
                 * present. And then it will override the already set one.
                 */
                extractStringConverterClass( addMethod )
                    .ifPresent( property::setStringConverterClass );
            }

            /*
             * Shall the property be added to the result of toString()?
             */
            if( nonNull( addMethod.getAnnotation( ExemptFromToString.class ) ) )
            {
                property.setFlag( EXEMPT_FROM_TOSTRING );
            }

            //---* … and now we create the method spec *-----------------------
            final var methodBuilder = configuration.getComposer()
                .overridingMethodBuilder( addMethod );
            property.setAddMethodBuilder( methodBuilder );
        }
    }   //  handleAddMethod()

    /**
     *  Processes the given
     *  {@link ExecutableElement}
     *  instance for a getter method.
     *
     *  @param  configuration   The code generation configuration.
     *  @param  getter  The getter method.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "OverlyCoupledMethod", "OverlyLongMethod", "OverlyComplexMethod"} )
    private final void handleGetter( final CodeGenerationConfiguration configuration, final ExecutableElement getter )
    {
        //---* Get the property name *-----------------------------------------
        final var propertyName = determinePropertyNameFromMethod( getter );

        /*
         * Create the new property spec and store it. The getters will be
         * created first, so a property with the same name, defined by another
         * getter, may not exist already. The respective check is made on
         * storing.
         */
        final var property = new PropertySpecImpl( propertyName );
        configuration.addProperty( property );

        //---* Keep the name of the getter method *----------------------------
        final var getterMethodName = getter.getSimpleName();
        property.setGetterMethodName( getterMethodName );

        //---* Shall the property be added to the result of toString()? *------
        if( nonNull( getter.getAnnotation( ExemptFromToString.class ) ) )
        {
            property.setFlag( EXEMPT_FROM_TOSTRING );
        }

        //---* Default getters are handled differently *-----------------------
        final var isDefault = getter.getModifiers().contains( DEFAULT );
        if( isDefault )
        {
            /*
             * Several annotations are not allowed for a default getter.
             */
            @SuppressWarnings( "unchecked" )
            final Class<? extends Annotation> [] unwantedAnnotations = new Class[]
            {
                SystemProperty.class,
                EnvironmentVariable.class,
                SystemPreference.class,
                Argument.class,
                Option.class,
                Preference.class,
                SpecialProperty.class,
                INIValue.class
            };
            for( final var annotationClass : unwantedAnnotations )
            {
                if( nonNull( getter.getAnnotation( annotationClass ) ) )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, annotationClass.getName(), getter.getSimpleName() ) );
                }
            }
            property.setFlag( GETTER_IS_DEFAULT, GETTER_ON_MAP );
        }
        var allowsPreferences = !isDefault;

        //---* Determine the property type *-----------------------------------
        final var rawReturnType = getter.getReturnType();
        final var rawPropertyType = determinePropertyType( rawReturnType );
        final var propertyType = TypeName.from( rawPropertyType );
        final var returnType = TypeName.from( rawReturnType );
        property.setPropertyType( propertyType );
        final var collectionKind = determineCollectionKind( rawPropertyType );
        property.setCollectionKind( collectionKind );
        final var isEnum = isEnumType( rawPropertyType );
        property.setIsEnum( isEnum );
        switch( collectionKind )
        {
            case LIST, SET ->
                determineElementType( rawPropertyType )
                    .filter( this::isEnumType )
                    .ifPresent( $ -> property.setFlag( ELEMENTTYPE_IS_ENUM ) );

            case MAP ->
            {
                // Does nothing currently
            }

            case NO_COLLECTION -> { /* Nothing to do */ }
        }

        /*
         * Some properties are 'special', and that is reflected by the
         * annotation @SpecialProperty.
         */
        final var specialPropertyAnnotation = getter.getAnnotation( SpecialProperty.class );
        if( nonNull( specialPropertyAnnotation ) )
        {
            /*
             * No further analysis required because everything is determined by
             * the special property spec, even the property name.
             */
            property.setSpecialPropertyType( specialPropertyAnnotation.value() );
        }
        else
        {
            //---* Keep the return type *--------------------------------------
            property.setGetterReturnType( returnType );

            /*
             * Check whether the return type is Optional; only when the return
             * type is Optional, it can be different from the property type.
             */
            if( !returnType.equals( propertyType ) ) property.setFlag( GETTER_RETURNS_OPTIONAL );

            /*
             * Determine the string converter instance, either from the
             * annotation or guess it from the property type.
             */
            determineStringConverterClass( getter, propertyType, isEnum ).ifPresent( property::setStringConverterClass );

            if( !isDefault )
            {
                //---* Set the field name *------------------------------------
                property.setFieldName( composeFieldName( propertyName ) );
            }

            //---* Additional annotations *------------------------------------
            final Optional<INIValue> iniValue = property.getStringConverterClass().isPresent()
                ? Optional.ofNullable( getter.getAnnotation( INIValue.class ) )
                : Optional.empty();
            iniValue.ifPresent( a ->
                {
                    property.setINIConfiguration( a );
                    property.setFlag( ALLOWS_INIFILE, PROPERTY_IS_MUTABLE );
                } );
            //noinspection NonShortCircuitBooleanExpression
            allowsPreferences &= iniValue.isEmpty();

            final var systemPropertyAnnotation = getter.getAnnotation( SystemProperty.class );
            if( nonNull( systemPropertyAnnotation ) )
            {
                if( iniValue.isPresent() )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, INIValue.class.getName(), getter.getSimpleName() ) );
                }

                parseSystemPropertyAnnotation( systemPropertyAnnotation, property );

                /*
                 * System properties may not be stored to/retrieved from
                 * preferences or an INIFile.
                 */
                allowsPreferences = false;
            }

            final var environmentVariableAnnotation = getter.getAnnotation( EnvironmentVariable.class );
            if( nonNull( environmentVariableAnnotation ) )
            {
                if( iniValue.isPresent() )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, INIValue.class.getName(), getter.getSimpleName() ) );
                }

                /*
                 * @SystemProperty and @EnvironmentVariable are mutual
                 * exclusive.
                 */
                if( nonNull( systemPropertyAnnotation ) )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, EnvironmentVariable.class.getName(), getter.getSimpleName() ) );
                }

                parseEnvironmentVariableAnnotation( environmentVariableAnnotation, property );

                /*
                 * Environment variable values may not be stored
                 * to/retrieved from preferences.
                 */
                allowsPreferences = false;
            }

            final var systemPrefsAnnotation = getter.getAnnotation( SystemPreference.class );
            if( nonNull( systemPrefsAnnotation ) )
            {
                /*
                 * @INIValue, @SystemProperty, @EnvironmentVariable and
                 * @SystemPreference are mutual exclusive.
                 */
                if( nonNull( systemPropertyAnnotation ) || nonNull( environmentVariableAnnotation ) || iniValue.isPresent() )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, SystemPreference.class.getName(), getter.getSimpleName() ) );
                }

                /*
                 * The property will be initialised from a system preference
                 * with the name given in the annotation.
                 */
                property.setSystemPrefsPath( systemPrefsAnnotation.path() );
                final var prefsKey = systemPrefsAnnotation.key();
                if( isNotEmptyOrBlank( prefsKey ) )
                {
                    property.setPrefsKey( prefsKey );
                }
                else
                {
                    throw new IllegalAnnotationError( format( MSG_PrefsKeyMissing, propertyName ) );
                }

                final var accessorClass = getAnnotationMirror( getter, SystemPreference.class )
                    .flatMap( mirror -> getAnnotationValue( mirror, "accessor" ) )
                    .map( annotationValue -> TypeName.from( (TypeMirror) annotationValue.getValue() ) )
                    .orElseThrow( () -> new IllegalAnnotationError( format( MSG_AccessorMissing, propertyName ) ) );
                property.setPrefsAccessorClass( accessorClass );

                /*
                 * System preference values may not be stored to/retrieved from
                 * preferences.
                 */
                allowsPreferences = false;
            }

            //---* Process the CLI annotations *-------------------------------
            final var argumentAnnotation = getter.getAnnotation( Argument.class );
            final var optionAnnotation = getter.getAnnotation( Option.class );
            if( nonNull( argumentAnnotation) && nonNull( optionAnnotation ) )
            {
                throw new IllegalAnnotationError( MSG_CLIAnnotationClash, optionAnnotation );
            }
            if( nonNull( argumentAnnotation ) )
            {
                parseArgumentAnnotation( argumentAnnotation, getter, property );
            }
            if( nonNull( optionAnnotation ) )
            {
                parseOptionAnnotation( optionAnnotation, getter, property );
            }

            //---* Process the preferences annotations *-----------------------
            final var noPreferenceAnnotation = getter.getAnnotation( NoPreference.class );
            final var preferenceAnnotation = getter.getAnnotation( Preference.class );
            if( !allowsPreferences && nonNull( preferenceAnnotation ) )
            {
                throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnGetter, Preference.class.getName(), getter.getSimpleName() ) );
            }
            if( nonNull(  preferenceAnnotation ) && nonNull( noPreferenceAnnotation ) )
            {
                throw new IllegalAnnotationError( MSG_PreferenceAnnotationClash, noPreferenceAnnotation );
            }
            if( nonNull( noPreferenceAnnotation ) ) allowsPreferences = false;
            if( allowsPreferences )
            {
                property.setFlag( ALLOWS_PREFERENCES );

                //---* The default values *------------------------------------
                var preferenceKey = propertyName;
                var accessorClass = PREFS_ACCESSOR_TYPE;

                //---* Check the annotation *----------------------------------
                if( nonNull( preferenceAnnotation ) )
                {
                    if( isNotEmptyOrBlank( preferenceAnnotation.key() ) ) preferenceKey = preferenceAnnotation.key();

                    try
                    {
                        final var name = "accessor";
                        accessorClass = getTypeMirrorFromAnnotationValue( getter, Preference.class, name )
                            .map( TypeName::from )
                            .orElseThrow( () -> new CodeGenerationError( format( MSG_NoValueForMirror, Preference.class.getName(), name ) ) );
                    }
                    catch( final NoSuchElementException e )
                    {
                        throw new CodeGenerationError( format( MSG_CannotRetrieveMirror, Preference.class.getName() ), e );
                    }
                }

                //---* Translate the default, if required *--------------------
                accessorClass = retrieveAccessorClass( accessorClass, rawPropertyType, collectionKind );

                //---* Keep the values *---------------------------------------
                property.setPrefsKey( preferenceKey );
                property.setPrefsAccessorClass( accessorClass );
            }

            //---* … and now we create the method spec *-------------------
            final var methodBuilder = configuration.getComposer()
                .overridingMethodBuilder( getter );
            property.setGetterBuilder( methodBuilder );
        }
    }   //  handleGetter()

    /**
     *  Processes the given
     *  {@link ExecutableElement}
     *  instance for a setter method.
     *
     *  @param  configuration   The code generation configuration.
     *  @param  setter  The setter method.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "OverlyCoupledMethod", "OverlyLongMethod", "OverlyComplexMethod"} )
    private final void handleSetter( final CodeGenerationConfiguration configuration, final ExecutableElement setter )
    {
        //---* Get the method name *-------------------------------------------
        final var setterMethodName = setter.getSimpleName();

        //---* Check for unwanted annotations *--------------------------------
        @SuppressWarnings( "unchecked" )
        final Class<? extends Annotation> [] unwantedAnnotations = new Class[]
        {
            SystemProperty.class,
            EnvironmentVariable.class,
            SystemPreference.class,
            Argument.class,
            Option.class,
            INIValue.class
        };
        for( final var annotationClass : unwantedAnnotations )
        {
            if( nonNull( setter.getAnnotation( annotationClass ) ) )
            {
                throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, annotationClass.getName(), setterMethodName ) );
            }
        }

        //---* Get the property name *-----------------------------------------
        final var propertyName = determinePropertyNameFromMethod( setter );

        /*
         * Let's see if we have such a property already; if not, we have to
         * create it.
         */
        final PropertySpecImpl property;
        final TypeName propertyType;
        final CollectionKind collectionKind;
        ifDebug( "propertyName: %s"::formatted, propertyName );
        final var rawArgumentType = setter.getParameters().getFirst().asType();
        final boolean isEnum;
        if( configuration.hasProperty( propertyName ) )
        {
            ifDebug( "property '%s' exists already"::formatted, propertyName );
            try
            {
                property = (PropertySpecImpl) configuration.getProperty( propertyName ).orElseThrow();
            }
            catch( final NoSuchElementException e )
            {
                throw new UnexpectedExceptionError( e );
            }
            ifDebug( property.hasFlag( PROPERTY_IS_SPECIAL ), "property '%s' is special"::formatted, propertyName );

            /*
             *  The setter's argument type must match the property type, also
             *  for special properties – even when this is checked elsewhere
             *  again.
             */
            propertyType = property.getPropertyType();
            if( !propertyType.equals( TypeName.from( setter.getParameters().getFirst().asType() ) ) )
            {
                throw new CodeGenerationError( format( MSG_TypeMismatch, TypeName.from( setter.getParameters().getFirst().asType() ).toString(), setterMethodName, propertyType.toString() ) );
            }
            collectionKind = property.getCollectionKind();
            isEnum = property.isEnum();
        }
        else
        {
            //---* Create the new property *-----------------------------------
            property = new PropertySpecImpl( propertyName );
            configuration.addProperty( property );
            checkAppropriate( rawArgumentType );
            propertyType = TypeName.from( rawArgumentType );
            property.setPropertyType( propertyType );
            collectionKind = determineCollectionKind( rawArgumentType );
            property.setCollectionKind( collectionKind );
            isEnum = isEnumType( rawArgumentType );
            property.setIsEnum( isEnum );
        }

        //---* Immutable special properties may not have a setter *------------
        if( property.hasFlag( PROPERTY_IS_SPECIAL ) )
        {
            property.getSpecialPropertyType()
                .map( CodeGenerator::getSpecialPropertySpecification )
                .filter( spec -> spec.hasFlag( PROPERTY_IS_MUTABLE ) )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_IllegalMutator, propertyName ) ) );
        }

        //---* There is a setter, so the property is mutable *-----------------
        property.setFlag( PROPERTY_IS_MUTABLE );
        if( configuration.getSynchronizationRequired() ) property.setFlag( PROPERTY_REQUIRES_SYNCHRONIZATION );

        //---* Keep the name of the setter method *----------------------------
        property.setSetterMethodName( setterMethodName );

        //---* Shall the property be added to the result of toString()? *------
        if( nonNull( setter.getAnnotation( ExemptFromToString.class ) ) )
        {
            property.setFlag( EXEMPT_FROM_TOSTRING );
        }

        /*
         * Default setters are handled differently, and they must have a
         * corresponding default getter.
         */
        final var isDefault = setter.getModifiers().contains( DEFAULT );
        if( isDefault)
        {
            if( property.getGetterMethodName().isEmpty() || !property.hasFlag( GETTER_IS_DEFAULT ) )
            {
                throw new CodeGenerationError( format( MSG_MissingGetter, propertyName ) );
            }
            property.setFlag( SETTER_IS_DEFAULT );
        }

        /*
         * If this setter is for a special property, we need to check whether
         * we have a getter, and whether that getter is for the same special
         * property.
         */
        final var specialPropertyAnnotation = setter.getAnnotation( SpecialProperty.class );
        if( nonNull( specialPropertyAnnotation ) )
        {
            /*
             * A default setter cannot be a setter for a special property.
             */
            if( isDefault ) throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, SpecialProperty.class.getName(), setterMethodName ) );

            final var specialPropertyType = specialPropertyAnnotation.value();
            final var specialPropertyTypeOptional = property.getSpecialPropertyType();
            if( specialPropertyTypeOptional.isEmpty() )
            {
                /*
                 * No further analysis required because everything is
                 * determined by the special property spec, even the property
                 * name.
                 */
                property.setSpecialPropertyType( specialPropertyType );
            }
            else
            {
                if( specialPropertyTypeOptional.get() != specialPropertyType )
                {
                    throw new IllegalAnnotationError( format( MSG_SpecialPropertyMismatch, SpecialProperty.class.getName(), setterMethodName, specialPropertyType.name(), specialPropertyTypeOptional.get().name() ) );
                }
            }
        }
        else
        {
            //---* Process the preferences annotations *-----------------------
            final var noPreferenceAnnotation = setter.getAnnotation( NoPreference.class );
            final var preferenceAnnotation = setter.getAnnotation( Preference.class );
            final var allowsPreferences = isNull( noPreferenceAnnotation );

            if( nonNull(  preferenceAnnotation ) && nonNull( noPreferenceAnnotation ) )
            {
                throw new IllegalAnnotationError( MSG_PreferenceAnnotationClash, noPreferenceAnnotation );
            }

            /*
             * If there is a getter for the property, the configuration for
             * preferences is already made there.
             * For a setter, the preferences annotations are only allowed if
             * there is no getter.
             */
            if( property.getGetterMethodName().isPresent() )
            {
                /*
                 * For a setter, the preferences annotations are only allowed
                 * if there is no getter.
                 */
                if( nonNull( preferenceAnnotation ) || nonNull( noPreferenceAnnotation ) )
                {
                    final var currentAnnotation = nonNull( preferenceAnnotation ) ? preferenceAnnotation : noPreferenceAnnotation;
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, currentAnnotation.getClass().getName(), setterMethodName ) );
                }
            }
            else
            {
                if( allowsPreferences )
                {
                    property.setFlag( ALLOWS_PREFERENCES );

                    //---* The default values *--------------------------------
                    var preferenceKey = propertyName;
                    var accessorClass = PREFS_ACCESSOR_TYPE;

                    //---* Check the annotation *------------------------------
                    if( nonNull( preferenceAnnotation ) )
                    {
                        if( isNotEmptyOrBlank( preferenceAnnotation.key() ) ) preferenceKey = preferenceAnnotation.key();

                        try
                        {
                            final var name = "accessor";
                            accessorClass = getTypeMirrorFromAnnotationValue( setter, Preference.class, name )
                                .map( TypeName::from )
                                .orElseThrow( () -> new CodeGenerationError( format( MSG_NoValueForMirror, Preference.class.getName(), name ) ) );
                        }
                        catch( final NoSuchElementException e )
                        {
                            throw new CodeGenerationError( format( MSG_CannotRetrieveMirror, Preference.class.getName() ), e );
                        }
                    }

                    //---* Translate the default, if required *--------------------
                    accessorClass = retrieveAccessorClass( accessorClass, rawArgumentType, collectionKind );

                    //---* Keep the values *-----------------------------------
                    property.setPrefsKey( preferenceKey );
                    property.setPrefsAccessorClass( accessorClass );
                }
            }

            /*
             * Determine the string converter instance, either from the
             * annotation or guess it from the property type.
             */
            final var stringConverterOptional = determineStringConverterClass( setter, propertyType, isEnum );
            property.getStringConverterClass()
                .ifPresentOrElse( stringConverterClass ->
                {
                    final var error = new CodeGenerationError( format( MSG_StringConverterMismatch, setterMethodName ) );
                    if( !stringConverterClass.equals( stringConverterOptional.orElseThrow( () -> error ) ) ) throw error;
                },
                () -> stringConverterOptional.ifPresent( property::setStringConverterClass ) );

            //---* Configure the setter *--------------------------------------
            final var checkEmptyAnnotation = setter.getAnnotation( CheckEmpty.class );
            final var checkNullAnnotation = setter.getAnnotation( CheckNull.class );

            /*
             * The annotations @CheckEmpty and @CheckNull are mutually
             * exclusive, although non-empty forces also not-null.
             */
            if( nonNull( checkNullAnnotation ) && nonNull( checkEmptyAnnotation ) )
            {
                throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, CheckNull.class.getName(), setterMethodName ) );
            }
            if( nonNull( checkEmptyAnnotation ) )
            {
                if( isDefault )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, CheckEmpty.class.getName(), setter.getSimpleName() ) );
                }
                property.setFlag( SETTER_CHECK_EMPTY );
            }
            if( nonNull( checkNullAnnotation ) )
            {
                if( isDefault )
                {
                    throw new IllegalAnnotationError( format( MSG_IllegalAnnotationOnSetter, CheckNull.class.getName(), setter.getSimpleName() ) );
                }
                property.setFlag( SETTER_CHECK_NULL );
            }
        }

        //---* Create the setter's argument *----------------------------------
        property.setSetterArgumentName( retrieveSetterArgumentName( setter ) );

        //---* … and now we create the method spec *---------------------------
        final var methodBuilder = configuration.getComposer()
            .overridingMethodBuilder( setter );
        property.setSetterBuilder( methodBuilder );
    }   //  handleSetter()

    /**
     *  Initialises the internal attribute
     *  {@link #m_StringConvertersForTypeNames}.
     *
     *  @return The map of
     *      {@link StringConverter}
     *      implementations.
     */
    @API( status = INTERNAL, since = "0.1.0" )
    private final Map<TypeName,ClassName> initStringConvertersForTypeNames()
    {
        final Map<TypeName,ClassName> retValue;
        try
        {
            retValue = createStringConverterRegistry();
        }
        catch( final IOException e )
        {
            throw new ExceptionInInitializerError( e );
        }
        ifDebug( retValue.isEmpty(), $ -> "No StringConverters??" );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initStringConvertersForTypeNames()

    /**
     *  Parses the given annotation and updates the given property accordingly.
     *
     *  @param  annotation  The annotation.
     *  @param  method  The annotated method.
     *  @param  property    The property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void parseArgumentAnnotation( final Argument annotation, final ExecutableElement method, final PropertySpecImpl property )
    {
        property.setFlag( PROPERTY_IS_ARGUMENT );

        //---* The index *-----------------------------------------------------
        property.setCLIArgumentIndex( annotation.index() );

        //---* The other fields *----------------------------------------------
        parseCLIAnnotation( annotation, method, property );
    }   //  parseArgumentAnnotation()

    /**
     *  <p>{@summary Parses the given CLI annotation and updates the given
     *  property accordingly.}</p>
     *  <p>{@code annotation} may be only of type
     *  {@link Argument}
     *  or
     *  {@link Option},
     *  although this is not explicitly checked (the method is private!).</p>
     *  <p>Usually, the method is only called by the methods
     *  {@link #parseArgumentAnnotation(Argument, ExecutableElement, PropertySpecImpl)}
     *  and
     *  {@link #parseOptionAnnotation(Option, ExecutableElement, PropertySpecImpl)},
     *  with the proper arguments.</p>
     *
     *  @param  annotation  The annotation.
     *  @param  method  The annotated method.
     *  @param  property    The property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void parseCLIAnnotation( final Annotation annotation, @SuppressWarnings( "TypeMayBeWeakened" ) final ExecutableElement method, final PropertySpecImpl property )
    {
        final var annotationMirror = getAnnotationMirror( method, annotation.getClass() )
            .orElseThrow( () -> new CodeGenerationError( format( MSG_CannotRetrieveMirror, annotation.getClass().getName() ) ) );

        //---* The value handler class *---------------------------------------
        {
            final var name = "handler";
            final var annotationValue = getAnnotationValue( annotationMirror, name )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_NoValueForMirror, annotation.getClass().getName(), name ) ) );
            final var defaultClass = getTypeUtils().erasure( getElementUtils().getTypeElement( CmdLineValueHandler.class.getName() ).asType() );
            final var handlerClass = getTypeUtils().erasure( (TypeMirror) annotationValue.getValue() );
            if( !getTypeUtils().isSameType( defaultClass, handlerClass ) )
            {
                property.setCLIValueHandlerClass( TypeName.from( handlerClass ) );
            }
        }

        //---* The format *----------------------------------------------------
        {
            final var name = "format";
            getAnnotationValue( annotationMirror, name )
                .map( v -> (String) v.getValue() )
                .ifPresent( v -> property.setCLIFormat( isNotEmptyOrBlank( v ) ? v : null ) );
        }

        //---* The meta var *--------------------------------------------------
        {
            final var name = "metaVar";
            getAnnotationValue( annotationMirror, name )
                .map( v -> (String) v.getValue() )
                .ifPresent( v -> property.setCLIMetaVar( isNotEmptyOrBlank( v ) ? v : null ) );
        }

        //---* The multi-valued flag *-----------------------------------------
        {
            final var name = "multiValued";
            final var flag = getAnnotationValue( annotationMirror, name )
                .map( v -> (Boolean) v.getValue() )
                .orElse( FALSE )
                .booleanValue();
            if( flag ) property.setFlag( PROPERTY_CLI_MULTIVALUED );
        }

        //---* The required flag *---------------------------------------------
        {
            final var name = "required";
            final var flag = getAnnotationValue( annotationMirror, name )
                .map( v -> (Boolean) v.getValue() )
                .orElse( FALSE )
                .booleanValue();
            if( flag ) property.setFlag( PROPERTY_CLI_MANDATORY );
        }

        //---* The usage text *------------------------------------------------
        {
            final var name = "usage";
            getAnnotationValue( annotationMirror, name )
                .map( v -> (String) v.getValue() )
                .ifPresent( v -> property.setCLIUsage( isNotEmptyOrBlank( v ) ? v : null ) );
        }

        //---* The usage text *------------------------------------------------
        //noinspection UnnecessaryCodeBlock
        {
            final var name = "usageKey";
            getAnnotationValue( annotationMirror, name )
                .map( v -> (String) v.getValue() )
                .ifPresent( v -> property.setCLIUsageKey( isNotEmptyOrBlank( v ) ? v : null ) );
        }
    }   //  parseCLIAnnotation()

    /**
     *  Parses the given annotation and updates the given property accordingly.
     *
     *  @param  annotation  The annotation.
     *  @param  property    The property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void parseEnvironmentVariableAnnotation( final EnvironmentVariable annotation, final PropertySpecImpl property )
    {
        /*
         * The property will be initialised from an environment
         * variable with the name given in the annotation.
         */
        property.setEnvironmentVariableName( annotation.value() );

        //---* Set the default value *-----------------------------------------
        property.setEnvironmentDefaultValue( annotation.defaultValue() );
    }   //  parseEnvironmentVariableAnnotation()

    /**
     *  Parses the given annotation and updates the given property accordingly.
     *
     *  @param  annotation  The annotation.
     *  @param  method  The annotated method.
     *  @param  property    The property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void parseOptionAnnotation( final Option annotation, final ExecutableElement method, final PropertySpecImpl property )
    {
        property.setFlag( PROPERTY_IS_OPTION );

        //---* The name and aliases *------------------------------------------
        final List<String> names = new ArrayList<>();
        names.add( annotation.name() );
        names.addAll( asList( annotation.aliases() ) );
        property.setCLIOptionNames( names );

        //---* The other fields *----------------------------------------------
        parseCLIAnnotation( annotation, method, property );
    }   //  parseOptionAnnotation()

    /**
     *  Parses the given annotation and updates the given property accordingly.
     *
     *  @param  annotation  The annotation.
     *  @param  property    The property.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void parseSystemPropertyAnnotation( final SystemProperty annotation, final PropertySpecImpl property )
    {
        /*
         * The property will be initialised from a system property with
         * the name given in the annotation.
         */
        property.setSystemPropertyName( annotation.value() );

        //---* Set the default value *-----------------------------------------
        property.setEnvironmentDefaultValue( annotation.defaultValue() );
    }   //  parseSystemPropertyAnnotation()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "OverlyNestedMethod" )
    @Override
    public final boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment )
    {
        //---* Tell them who we are *------------------------------------------
        final var message = annotations.isEmpty() ? "No annotations to process" : annotations.stream()
            .map( TypeElement::getQualifiedName )
            .collect( joining( "', '", "Processing the annotation" + (annotations.size() > 1 ? "s '" : " '"), "'" ) );
        printMessage( NOTE, message );

        final var retValue = !roundEnvironment.errorRaised() && !annotations.isEmpty();
        if( retValue )
        {
            if( !annotations.isEmpty() )
            {
                /*
                 * Get the values for the i18n annotations and keep them.
                 */
                //---* Get the message prefix *--------------------------------
                m_MessagePrefix = retrieveAnnotatedField( roundEnvironment, MessagePrefix.class )
                    .filter( variableElement -> variableElement.getModifiers().containsAll( Set.of( PUBLIC, STATIC ) ) )
                    .map( variableElement -> Objects.toString( variableElement.getConstantValue(), EMPTY_STRING ) );

                //---* Get the base bundle name *------------------------------
                m_BaseBundleName = retrieveAnnotatedField( roundEnvironment, BaseBundleName.class )
                    .filter( variableElement -> variableElement.getModifiers().containsAll( Set.of( PUBLIC, STATIC ) ) )
                    .map( variableElement -> Objects.toString( variableElement.getConstantValue(), EMPTY_STRING ) );

                /*
                 * Process the elements that are annotated as configuration
                 * bean specification. Although not more than one per
                 * application seems logical, it could easily be more than one.
                 */
                ScanLoop: for( final var element : roundEnvironment.getElementsAnnotatedWith( ConfigurationBeanSpecification.class ) )
                {
                    /*
                     * We are only interested in elements that are type
                     * elements, and to be honest, we only want interfaces.
                     */
                    if( element instanceof final TypeElement typeElement )
                    {
                        if( typeElement.getKind() == ElementKind.INTERFACE )
                        {
                            //---* Create the configuration bean *-------------
                            try
                            {
                                processConfigurationBeanSpecification( typeElement );
                            }
                            catch( final IOException e )
                            {
                                printMessage( ERROR, e.toString(), element );
                            }
                        }
                        else
                        {
                            printMessage( ERROR, "%s: Only interfaces may be annotated with '%s'".formatted( typeElement.getQualifiedName().toString(), ConfigurationBeanSpecification.class.getSimpleName() ), element );
                            throw new IllegalAnnotationError( MSG_InterfacesOnly, ConfigurationBeanSpecification.class );
                        }
                    }
                    else
                    {
                        printMessage( ERROR, format( MSG_IllegalAnnotationUse, element.getSimpleName().toString(), ConfigurationBeanSpecification.class.getSimpleName() ), element );
                        throw new IllegalAnnotationError( ConfigurationBeanSpecification.class );
                    }
                }   //  ScanLoop:
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  process()

    /**
     *  Processes the given configuration bean specification and generates the
     *  source for the so specified configuration bean.
     *
     *  @param  specification   The specification interface.
     *  @throws IOException A problem occurred when writing the source file.
     */
    @SuppressWarnings( {"OverlyCoupledMethod", "OverlyComplexMethod"} )
    private final void processConfigurationBeanSpecification( final TypeElement specification ) throws IOException
    {
        //---* Create the composer *-------------------------------------------
        final var composer = new JavaComposer( LAYOUT_FOUNDATION, addDebugOutput() );

        final var specificationAnnotation = specification.getAnnotation( ConfigurationBeanSpecification.class );

        //---* Determine the simple name of the bean class *-------------------
        final var configurationBeanClassName = getElementUtils().getName( isEmpty( specificationAnnotation.name() ) ? format( "%sImpl", specification.getSimpleName() ) : specificationAnnotation.name() );
        final var isSamePackage = specificationAnnotation.samePackage();

        //---* Do we need to synchronise the access to the properties? *-------
        final var synchronizeAccess = specificationAnnotation.synchronizeAccess();

        //---* Get the base class *--------------------------------------------
        TypeName baseClass = null;
        try
        {
            final var annotationValueName = "baseClass";
            baseClass = getTypeMirrorFromAnnotationValue( specification, ConfigurationBeanSpecification.class, annotationValueName )
                .map( TypeName::from )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_NoValueForMirror, ConfigurationBeanSpecification.class.getName(), annotationValueName ) ) );
        }
        catch( final NoSuchElementException e )
        {
            throw new CodeGenerationError( format( MSG_CannotRetrieveMirror, ConfigurationBeanSpecification.class.getName() ), e );
        }
        if( TypeName.from( Object.class ).equals( baseClass ) )
        {
            //noinspection AssignmentToNull
            baseClass = null;
        }

        /*
         * Determine the name of the package for the bean class. As the
         * interface may be an inner type, we cannot just take the next best
         * enclosing element.
         */
        var parentElement = specification;
        while( parentElement.getNestingKind() != NestingKind.TOP_LEVEL )
        {
            parentElement = (TypeElement) specification.getEnclosingElement();
        }
        final var packageElement = (PackageElement) specification.getEnclosingElement();
        final var specificationPackageName = packageElement.getQualifiedName().toString();
        final var configurationBeanPackageName = getElementUtils().getName
            (
                isSamePackage
                ? specificationPackageName
                : packageElement.isUnnamed()
                  ? PACKAGE_NAME
                  : format( "%s.%s", specificationPackageName, PACKAGE_NAME )
            );

        //---* Create the configuration for the code generation *--------------
        final var specificationClass = ClassName.from( specification );
        final var configuration = new CodeGenerationConfiguration( this, composer, specificationClass, configurationBeanClassName, configurationBeanPackageName, baseClass, synchronizeAccess );

        //---* Determine the name for the initialisation data resource *-------
        var initDataResource = specificationAnnotation.initDataResource();
        if( isNotEmptyOrBlank( initDataResource ) )
        {
            if( "=".equals( initDataResource ) ) initDataResource = format( "%s.properties", specification.getSimpleName() );
            configuration.setInitDataResource( initDataResource );
        }

        //---* Retrieve the method for the initialisation *--------------------
        retrieveInitDataMethod( configuration, specification );

        /*
         * Retrieve all the interfaces that are implemented by the
         * specification.
         */
        final Set<TypeElement> interfaces = new HashSet<>();
        retrieveInterfaces( specification, interfaces );
        final var interfacesTypes = interfaces.stream()
            .map( element -> TypeName.from( element.asType() ) )
            .collect( Collectors.toList() );
        configuration.addInterfacesToImplement( interfacesTypes );

        //---* Retrieve the properties *---------------------------------------
        retrieveProperties( configuration, interfaces );

        //---* Add the settings for the I18nSupport *--------------------------
        if( configuration.implementInterface( I18nSupport.class ) )
        {
            configuration.setI18NParameters( m_MessagePrefix.orElseThrow( () -> new CodeGenerationError( MSG_NoMessagePrefix ) ),
                m_BaseBundleName.orElseThrow( () -> new CodeGenerationError( MSG_NoBaseBundleName ) ) );
        }

        //---* Determine the settings for the preferences stuff *--------------
        final var preferencesRootAnnotation = specification.getAnnotation( PreferencesRoot.class );
        if( nonNull( preferencesRootAnnotation ) )
        {
            configuration.setPreferencesRoot( preferencesRootAnnotation.nodeName() );
            try
            {
                getTypeMirrorFromAnnotationValue( specification, PreferencesRoot.class, "changeListenerClass" )
                    .map( TypeName::from )
                    .ifPresent( configuration::setPreferenceChangeListenerClass );
            }
            catch( final NoSuchElementException e )
            {
                throw new CodeGenerationError( format( MSG_CannotRetrieveMirror, PreferencesRoot.class.getName() ), e );
            }
        }

        //---* Determine the settings for the {@code INI} file stuff *---------
        final var iniFileConfig = specification.getAnnotation( INIFileConfig.class );
        if( nonNull( iniFileConfig ) )
        {
            final var filename = iniFileConfig.path();
            if( isEmptyOrBlank( filename ) )
            {
                throw new CodeGenerationError( MSG_INIPathMissing );
            }
            configuration.setINIFileConfig( filename, iniFileConfig.mustExist(), iniFileConfig.comment() );
            for( final var group : specification.getAnnotationsByType( INIGroup.class ) ) configuration.addINIGroup( group );
        }

        //---* Create the source code *----------------------------------------
        try
        {
            final var generator = new CodeGenerator( configuration );
            final var javaFile = generator.createCode();

            //---* Write the source file *-------------------------------------
            javaFile.writeTo( getFiler() );
        }
        catch( @SuppressWarnings( "OverlyBroadCatchBlock" ) final Exception e )
        {
            /*
             * Any exception that makes it to this point indicates a failure of
             * the code generation process.
             */
            printMessage( ERROR, format( "Code Generation failed: %s", e.getMessage() ), specification );
            throw new CodeGenerationError( format( MSG_CodeGenerationFailed, configurationBeanPackageName, configurationBeanClassName ), e );
        }
    }   //  processConfigurationBeanSpecification()

    /**
     *  Retrieves the class for the preference accessor.
     *
     *  @param  accessorType    The accessor class as defined in the
     *      annotation; if this is
     *      {@link PreferenceAccessor PreferenceAccessor.class},
     *      the effective handler class has to inferred from the
     *      {@code propertyType}.
     *  @param  propertyType    The type of the property that should be
     *      accessed.
     *  @param collectionKind   The kind of collection that is represented by
     *      the property type.
     *  @return The effective accessor class.
     *  @throws IllegalAnnotationError  There is no accessor for the given
     *      property type.
     */
    @API( status = INTERNAL, since = "0.0.1" )
    private final TypeName retrieveAccessorClass( final TypeName accessorType, final TypeMirror propertyType, final CollectionKind collectionKind ) throws IllegalAnnotationError
    {
        var retValue = accessorType;
        if( isNull( accessorType ) || accessorType.equals( PREFS_ACCESSOR_TYPE ) )
        {
            //---* Infer the effective accessor class from the property type *-
            if( isEnumType( propertyType ) )
            {
                retValue = ENUM_ACCESSOR_TYPE;
            }
            else
            {
                retValue = switch( collectionKind )
                {
                    case NO_COLLECTION -> m_PrefsAccessorClasses.getOrDefault( TypeName.from( propertyType ), (ClassName) DEFAULT_ACCESSOR_TYPE );
                    case LIST -> LIST_ACCESSOR_TYPE;
                    case MAP -> MAP_ACCESSOR_TYPE;
                    case SET -> SET_ACCESSOR_TYPE;
                };
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveAccessorClass()

    /**
     *  <p>{@summary This methods checks whether the configuration bean
     *  specification specifies an {@code initData()} method.} This method has
     *  to meet the requirements below:</p>
     *  <ul>
     *  <li>the name has to be
     *  {@value #METHODNAME_ConfigBeanSpec_InitData}</li>
     *  <li>it does not take any arguments</li>
     *  <li>it returns an instance of {@code Map<String,Object>}</li>
     *  <li>it is either {@code static} or {@code default} or implemented in a
     *  base class, although this is not checked here</li>
     *  </ul>
     *  <p>Is such a method exists, a
     *  {@link org.tquadrat.foundation.javacomposer.MethodSpec}
     *  for it will be created and added to the configuration.</p>
     *
     *  @param  configuration   The configuration for the code generation.
     *  @param  specification   The configuration bean specification interface.
     *
     *  @note   If a base class for the new configuration bean is defined, the
     *      method may be abstract, but if that base class does not implement
     *      the method it will be detected only by the final compiler run, not
     *      by the code generation here.
     */
    @SuppressWarnings( {"TypeMayBeWeakened", "UseOfConcreteClass"} )
    private final void retrieveInitDataMethod( final CodeGenerationConfiguration configuration, final TypeElement specification )
    {
        final var hasBaseClass = configuration.getBaseClass().isPresent();
        final var returnType = ParameterizedTypeName.from( Map.class, String.class, Object.class );
        final var method = specification.getEnclosedElements().stream()
            // We are only interested in methods.
            .filter( e -> e.getKind() == METHOD )
            // Methods are executable elements
            .map( e -> (ExecutableElement) e )
            /*
             * The method has to be either default or static; these modifiers
             * are mutually exclusive, so the method has either one or the
             * other, or it is abstract, having neither of default or static.
             */
            .filter( e -> hasBaseClass || e.getModifiers().contains( DEFAULT ) || e.getModifiers().contains( STATIC ) )
            // The name of the method should be "initData"
            .filter( e -> e.getSimpleName().contentEquals( METHODNAME_ConfigBeanSpec_InitData ) )
            // The method may not take any arguments
            .filter( e -> e.getParameters().isEmpty() )
            // The return type has to be Map<String,Object>
            .filter( e -> TypeName.from( e.getReturnType() ) instanceof ParameterizedTypeName )
            .filter( e -> returnType.equals( TypeName.from( e.getReturnType() ) ) )
            .findFirst();

        method.map( methodSpec -> configuration.getComposer().createMethod( methodSpec ) )
            .ifPresent( configuration::setInitDataMethod );
    }   //  retrieveInitDataMethod()

    /**
     *  <p>{@summary Scans the configuration bean specification for the
     *  properties and stores the result to the configuration.}</p>
     *  <p>A property is defined primarily by the respective
     *  {@linkplain org.tquadrat.foundation.function.Getter getter}
     *  method with its annotations, but it is also possible to define it by a
     *  {@linkplain org.tquadrat.foundation.function.Setter setter}
     *  method only – especially when the configuration bean specification
     *  extends the interface
     *  {@link java.util.Map}.</p>
     *
     *  @param  configuration   The code generation configuration.
     *  @param  interfaces  The interfaces that have to implemented by the new
     *      configuration bean.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void retrieveProperties( final CodeGenerationConfiguration configuration, final Collection<? extends TypeElement> interfaces )
    {
        final var isMap = configuration.implementInterface( Map.class );

        /*
         * Retrieve getters, setters and 'add' methods from the interfaces.
         */
        final Collection<ExecutableElement> getters = new ArrayList<>();
        final Collection<ExecutableElement> setters = new ArrayList<>();
        final Collection<ExecutableElement> addMethods = new ArrayList<>();
        //noinspection OverlyLongLambda
        interfaces.stream()
            .flatMap( element -> element.getEnclosedElements().stream() )
            .filter( e -> e.getKind() == METHOD )
            .map( e -> (ExecutableElement) e )
            .forEach( element ->
            {
                if( isGetter( element ) )
                {
                    /*
                     * There is a method Map.isEmpty(); if the configuration
                     * bean specification extends java.util.Map, this is not a
                     * property getter for the 'empty' property.
                     * If isMap == true, the method isEmpty() will be
                     * implemented elsewhere.
                     */
                    if( !element.getSimpleName().toString().equals( METHODNAME_Map_IsEmpty ) || !isMap )
                    {
                        getters.add( element );
                    }
                }
                else if( isSetter( element ) )
                {
                    setters.add( element );
                }
                else if( isAddMethod( element ) )
                {
                    /*
                     * There is a method ConfigBeanSpec.addListener() that is
                     * necessary for the listener management, and not an 'add'
                     * method for a property. Therefore, it will be implemented
                     * elsewhere.
                     */
                    if( !element.getSimpleName().toString().equals( METHODNAME_ConfigBeanSpec_AddListener ) )
                    {
                        addMethods.add( element );
                    }
                }
            } );

        /*
         *  Getters are to be processed first.
         *  This will create the properties that are stored in the
         *  configuration.
         */
        getters.forEach( getter -> handleGetter( configuration, getter ) );

        //---* Process the setters *-------------------------------------------
        setters.forEach( setter -> handleSetter( configuration, setter ) );

        //---* Process the 'add' methods *-------------------------------------
        addMethods.forEach( addMethod -> handleAddMethod( configuration, addMethod ) );
    }   //  retrieveProperties()

    /**
     *  <p>{@summary Retrieves the name of the single argument of a setter
     *  method.}</p>
     *  <p>This method will return the name of the argument as defined in the
     *  configuration bean specification if the compiler flag
     *  {@code -parameters} is set; otherwise, the arguments are just counted
     *  ({@code arg0}, {@code arg1}, {@code arg2}, …).</p>
     *
     *  @param  setter  The setter method.
     *  @return The name of the argument as defined in the configuration bean
     *      specification, or &quot;arg0&quot;
     */
    private final Name retrieveSetterArgumentName( final ExecutableElement setter )
    {
        final var parameters = retrieveArgumentNames( setter );
        if( parameters.size() != 1 ) throw new CodeGenerationError( format( MSG_NoSetter, setter.getSimpleName() ) );
        final var retValue = parameters.getFirst();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveSetterArgumentName()

    /**
     *  <p>{@summary Determines the key class for the given instance of
     *  {@link StringConverter}.}</p>
     *
     *  @note   This method was copied from
     *      {@code org.tquadrat.foundation.base/org.tquadrat.foundation.lang.internal.StringConverterService}.
     *
     *  @param  converter   The converter instance.
     *  @return The subject class.
     */
    @SuppressWarnings( {"NestedTryStatement", "unchecked"} )
    private static final Collection<Class<?>> retrieveSubjectClasses( final StringConverter<?> converter )
    {
        final var converterClass = requireNonNullArgument( converter, "converter" ).getClass();
        Collection<Class<?>> retValue;
        try
        {
            try
            {
                final var getSubjectClassMethod = converterClass.getMethod( METHOD_NAME_GetSubjectClass );
                //noinspection unchecked
                retValue = (Collection<Class<?>>) getSubjectClassMethod.invoke( converter );
            }
            catch( @SuppressWarnings( "unused" ) final NoSuchMethodException ignored )
            {
                final var fromStringMethod = converterClass.getMethod( "fromString", CharSequence.class );
                retValue = List.of( fromStringMethod.getReturnType() );
            }
        }
        catch( final NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e )
        {
            throw new UnexpectedExceptionError( e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveSubjectClass()
}
//  class ConfigAnnotationProcessor

/*
 *  End of File
 */
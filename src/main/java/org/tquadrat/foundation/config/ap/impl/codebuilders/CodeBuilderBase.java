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

package org.tquadrat.foundation.config.ap.impl.codebuilders;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.DEFAULT_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.ENUM_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.LIST_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MAP_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_AccessorMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingEnvironmentVar;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverter;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverterWithType;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingSystemProp;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoCollection;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_PreferencesNotConfigured;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.SET_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_RETURNS_OPTIONAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_ARGUMENT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_OPTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_REQUIRES_SYNCHRONIZATION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_CHECK_EMPTY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_CHECK_NULL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PREFERENCE;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ListenerSupport;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeBuilderBase.StringConverterInstantiation.AS_ENUM;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeBuilderBase.StringConverterInstantiation.BY_INSTANCE;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeBuilderBase.StringConverterInstantiation.THROUGH_CONSTRUCTOR;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.lang.StringConverter;

/**
 *  The abstract base class for all the code builders.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeBuilderBase.java 1053 2023-03-11 00:10:49Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( {"OverlyCoupledClass", "OverlyComplexClass"} )
@ClassVersion( sourceVersion = "$Id: CodeBuilderBase.java 1053 2023-03-11 00:10:49Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
abstract sealed class CodeBuilderBase implements CodeBuilder
    permits CLIBeanBuilder, ConfigBeanBuilder, I18nSupportBuilder, INIBeanBuilder, MapImplementor, PreferencesBeanBuilder, SessionBeanBuilder
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The various type to instantiate a
     *  {@link StringConverter}
     *  class.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBuilderBase.java 1053 2023-03-11 00:10:49Z tquadrat $
     *  @UMLGraph.link
     *  @since 0.1.0
     */
    @ClassVersion( sourceVersion = "$Id: CodeBuilderBase.java 1053 2023-03-11 00:10:49Z tquadrat $" )
    @API( status = INTERNAL, since = "0.1.0" )
    public static enum StringConverterInstantiation
    {
        /**
         *  The
         *  {@link StringConverter}
         *  can be accessed through the {@code INSTANCE} field.
         */
        BY_INSTANCE,

        /**
         *  The
         *  {@link StringConverter}
         *  has to be instantiated by calling its default constructor.
         */
        THROUGH_CONSTRUCTOR,

        /**
         *  The property is an
         *  {@link Enum enum},
         *  and the class for the
         *  {@link StringConverter}
         *  is
         *  {@link org.tquadrat.foundation.util.stringconverter.EnumStringConverter},
         *  so the {@code StringConverter} has to be instantiated by a call to
         *  {@link org.tquadrat.foundation.util.stringconverter.EnumStringConverter#EnumStringConverter(Class)}.
         */
        AS_ENUM
    }
    //  enum StringConverterInstantiation


        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The class builder.
     */
    private final TypeSpec.Builder m_ClassBuilder;

    /**
     *  The composer.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The configuration for the code generation.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeGenerationConfiguration m_Configuration;

    /**
     *  The builder for body of the constructor.
     */
    private final CodeBlock.Builder m_ConstructorCode;

    /**
     *  The code generator context.
     */
    private final CodeGeneratorContext m_Context;

    /**
     *  The synchronised flag.
     */
    private final boolean m_IsSynchronized;

    /**
     *  The standard fields.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final Map<StandardField,FieldSpec> m_StandardFields = new EnumMap<>( StandardField.class );

    /**
     *  The standard methods.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final Map<StandardMethod,MethodSpec> m_StandardMethods = new EnumMap<>( StandardMethod.class );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The registry for the known implementations of
     *  {@link StringConverter}
     *  implementations.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final Map<TypeName,ClassName> m_ConverterRegistry;

    static
    {
        try
        {
            m_ConverterRegistry = Map.copyOf( ConfigAnnotationProcessor.createStringConverterRegistry() );
        }
        catch( final IOException e )
        {
            throw new ExceptionInInitializerError( e );
        }
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code CodeBuilderBase}.
     *
     *  @param  context The code generator context.
     */
    protected CodeBuilderBase( final CodeGeneratorContext context )
    {
        m_Context = requireNonNullArgument( context, "context" );

        m_Configuration = m_Context.getConfiguration();
        m_Composer = m_Configuration.getComposer();
        m_ClassBuilder = m_Context.getClassBuilder();
        m_ConstructorCode = m_Context.getConstructorCodeBuilder();

        m_IsSynchronized = m_Configuration.getSynchronizationRequired();

        m_Configuration.getInitDataMethod().ifPresent( spec -> m_StandardMethods.put( StandardMethod.STD_METHOD_InitData, spec ) );
    }   //  CodeBuilderBase()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds an argument to the constructor.
     *
     *  @param  argument    The parameter to add.
     */
    protected final void addConstructorArgument( final ParameterSpec argument )
    {
        final var constructorBuilder = m_Context.getConstructorBuilder();
        constructorBuilder.addParameter( requireNonNullArgument( argument, "argument" ) );
    }   //  addConstructorArgument()

    /**
     *  Adds code to the constructor body.
     *
     *  @param  code    The code to add.
     */
    protected final void addConstructorCode( final CodeBlock code )
    {
        m_ConstructorCode.add( requireNonNullArgument( code, "code" ) );
    }   //  addConstructorCode()

    /**
     *  Adds a warning to the
     *  {@link java.lang.SuppressWarnings &#64;SuppressWarnings}
     *  annotation for the constructor of the new configuration bean.
     *
     *  @param  warning The warning to suppress.
     */
    protected final void addConstructorSuppressedWarning( final SuppressableWarnings warning )
    {
        m_Context.addConstructorSuppressedWarning( warning );
    }   //  addConstructorSuppressedWarning()

    /**
     *  Adds the given field to the new class.
     *
     *  @param  field   The field to add.
     */
    protected final void addField( final FieldSpec field )
    {
        m_ClassBuilder.addField( requireNonNullArgument( field, "field" ) );
    }   //  addField()

    /**
     *  Adds the given standard field to the new class.
     *
     *  @param  reference   The identifier for the standard field.
     *  @param  field   The field to add.
     */
    protected final void addField( final StandardField reference, final FieldSpec field )
    {
        addField( field );
        m_StandardFields.put( requireNonNullArgument( reference, "reference" ), field );
    }   //  addField()

    /**
     *  Adds the given method to the new class.
     *
     *  @param  method  The method to add.
     */
    protected final void addMethod( final MethodSpec method )
    {
        m_ClassBuilder.addMethod( requireNonNullArgument( method, "method" ) );
    }   //  addMethod()

    /**
     *  Adds the given method to the new class.
     *
     *  @param  reference   The identifier for the standard method.
     *  @param  method  The method to add.
     */
    protected final void addMethod( final StandardMethod reference, final MethodSpec method )
    {
        addMethod( method );
        m_StandardMethods.put( requireNonNullArgument( reference, "reference" ), method );
    }   //  addMethod()

    /**
     *  {@inheritDoc}
     */
    @Override
    public abstract void build();

    /**
     *  The default implementation of the method that composes an 'add' method
     *  for the given property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The method specification.
     */
    @SuppressWarnings( {"OptionalGetWithoutIsPresent", "EnhancedSwitchMigration", "UseOfConcreteClass", "StaticMethodOnlyUsedInOneClass", "OverlyCoupledMethod", "OverlyComplexMethod"} )
    public static MethodSpec composeAddMethod( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        //---* Obtain the builder *--------------------------------------------
        final var builder = property.getAddMethodBuilder()
            .orElseGet( () -> composer.methodBuilder( property.getAddMethodName().get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .returns( VOID )
            );
        builder.addModifiers( FINAL )
            .addJavadoc( composer.createInheritDocComment() );

        //---* Add the locking *-----------------------------------------------
        final var lock = property.hasFlag( PROPERTY_REQUIRES_SYNCHRONIZATION ) ? codeBuilder.getField( STD_FIELD_WriteLock ) : null;
        if( nonNull( lock) ) builder.beginControlFlow(
            """
            try( final var l = $N.lock() )
            """, lock );

        //---* Assign the value *----------------------------------------------
        final var argumentType = switch( property.getCollectionKind() )
            {
                case LIST, SET ->
                    {
                        if( property.getPropertyType() instanceof ParameterizedTypeName propertyType )
                        {
                            final var typeArguments = propertyType.typeArguments();
                            yield typeArguments.get( 0 );
                        }
                        yield ClassName.from( Object.class );
                    }

                case MAP ->
                    {
                        TypeName keyType = ClassName.from( Object.class );
                        TypeName valueType = ClassName.from( Object.class );
                        if( property.getPropertyType() instanceof ParameterizedTypeName propertyType )
                        {
                            final var typeArguments = propertyType.typeArguments();
                            keyType = typeArguments.get( 0 );
                            valueType = typeArguments.get( 1 );
                        }
                        final var entryType = ClassName.from( Map.Entry.class );
                        yield ParameterizedTypeName.from( entryType, keyType, valueType );
                    }

                case NO_COLLECTION -> throw new CodeGenerationError( format( MSG_NoCollection, property.getAddMethodName().get(), property.getPropertyName() ) );

                default -> throw new UnsupportedEnumError( property.getCollectionKind() );
            };

        //---* Create the parameter *------------------------------------------
        final var parameter = composer.parameterOf( argumentType, property.getAddMethodArgumentName(), FINAL );
        builder.addParameter( parameter );

        //---* Obtain the field *----------------------------------------------
        final var field = property.getFieldName();

        //---* Create the code *-----------------------------------------------
        builder.addStatement( "$T oldValue = null", property.getPropertyType() )
            .beginControlFlow(
                """
                if( isNull( $N ) )
                """, field )
            .addStaticImport( Objects.class, "isNull" );
        switch( property.getCollectionKind() )
        {
            case LIST:
            {
                builder.addStatement( "$1N = new $2T<>()", field, ArrayList.class )
                    .nextControlFlow(
                        """

                        else
                        """ )
                    .addStatement( "oldValue = $1T.copyOf( $2N )", List.class, field )
                    .endControlFlow()
                    .addStatement( "$1N.add( requireNonNullArgument( $2N, $3S ) )", field, parameter, property.getAddMethodArgumentName() )
                    .addStaticImport( Objects.class, "requireNonNullArgument" )
                    .addStatement( "$1N.fireEvent( $2S, oldValue, $3T.copyOf( $4N ) )", codeBuilder.getField( STD_FIELD_ListenerSupport ), property.getPropertyName(), List.class, field );
                break;
            }

            case MAP:
            {
                builder.addStatement( "$1N = new $2T<>()", field, HashMap.class )
                    .nextControlFlow(
                        """

                        else
                        """ )
                    .addStatement( "oldValue = $T.copyOf( $N )", Map.class, field )
                    .endControlFlow()
                    .addStatement( "var key = requireNonNullArgument( $1N, $2S ).getKey()", parameter, property.getAddMethodArgumentName() )
                    .addStaticImport( Objects.class, "requireNonNullArgument" )
                    .addStatement( "var value = $N.getValue()", parameter )
                    .addStatement( "$1N.put( requireNonNullArgument( key, $2S + \".key\" ), requireNonNullArgument( value, $2S + \".value\" ) )", field, property.getAddMethodArgumentName() )
                    .addStatement( "$1N.fireEvent( $2S, oldValue, $3T.copyOf( $4N ) )", codeBuilder.getField( STD_FIELD_ListenerSupport ), property.getPropertyName(), Map.class, field );
                break;
            }

            case SET:
            {
                builder.addStatement( "$1N = new $2T<>()", field, HashSet.class )
                    .nextControlFlow(
                        """

                        else
                        """ )
                    .addStatement( "oldValue = $1T.copyOf( $2N )", Set.class, field )
                    .endControlFlow()
                    .addStatement( "$1N.add( requireNonNullArgument( $2N, $3S ) )", field, parameter, property.getAddMethodArgumentName() )
                    .addStaticImport( Objects.class, "requireNonNullArgument" )
                    .addStatement( "$1N.fireEvent( $2S, oldValue, $3T.copyOf( $4N ) )", codeBuilder.getField( STD_FIELD_ListenerSupport ), property.getPropertyName(), Set.class, field );
                break;
            }

            case NO_COLLECTION: throw new CodeGenerationError( format( MSG_NoCollection, property.getAddMethodName().get(), property.getPropertyName() ) );

            default: throw new UnsupportedEnumError( property.getCollectionKind() );
        }
        builder.endControlFlow();

        //---* Cleanup *-------------------------------------------------------
        if( nonNull( lock) ) builder.endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeAddMethod()

    /**
     *  The default implementation of the method that composes a constructor
     *  fragment for the initialisation of the given property in cases it is
     *  annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "TypeMayBeWeakened", "StaticMethodOnlyUsedInOneClass"} )
    public static CodeBlock composeConstructorFragment4Environment( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var builder = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer()
            .codeBlockBuilder()
            .add(
                """
                
                /*
                 * Initialise the property '$N' from the system environment.
                 */
                """, property.getPropertyName()
            )
            .beginControlFlow( EMPTY_STRING );

        //---* Set the StringConverter *---------------------------------------
        final var stringConverter = property.getStringConverterClass()
            .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverter, property.getPropertyName() ) ) );
        switch( determineStringConverterInstantiation( stringConverter, property.isEnum() ) )
        {
            case BY_INSTANCE -> builder.addStatement( "final var stringConverter = $T.INSTANCE", stringConverter );
            case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var stringConverter = new $T()", stringConverter );
            case AS_ENUM -> builder.addStatement( "final var stringConverter = new $1T( $2T.class )", stringConverter, property.getPropertyType() );
        }

        //---* Set the value *-------------------------------------------------
        final var defaultValue = property.getEnvironmentDefaultValue();
        if( defaultValue.isPresent() )
        {
            builder.addStatement( "var value = getenv( $1S )", property.getEnvironmentVariableName().orElseThrow( () -> new CodeGenerationError( format( MSG_MissingEnvironmentVar, property.getPropertyName() ) ) ) )
                .beginControlFlow(
                    """
                        if( isNull( value ) )
                        """
                )
                .addStaticImport( Objects.class, "isNull" )
                .addStaticImport( System.class, "getenv" )
                .addStatement( "value = $1S", defaultValue.get() )
                .endControlFlow();
        }
        else
        {
            builder.addStatement( "final var value = getenv( $1S )", property.getEnvironmentVariableName().orElseThrow( () -> new CodeGenerationError( format( MSG_MissingEnvironmentVar, property.getPropertyName() ) ) ) );
        }
        builder.addStaticImport( System.class, "getenv" )
            .addStaticImport( System.class, "getenv" )
            .addStatement( "$1N = stringConverter.fromString( value )", property.getFieldName() )
            .endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeConstructorFragment4Environment()

    /**
     *  The default implementation of the method that composes a constructor
     *  fragment for the initialisation of the given property in cases it is
     *  annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "StaticMethodOnlyUsedInOneClass", "OverlyComplexMethod"} )
    public static CodeBlock composeConstructorFragment4SystemPreference( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        /*
         * Create the lambdas for the getter and the setter.
         * The getter is not used, but required for the constructor of the
         * PropertyAccess instance.
         */
        final var getter = composer.lambdaBuilder()
            .addCode( "$N", requireNonNullArgument( property, "property" ).getFieldName() )
            .build();
        final var setter = composer.lambdaBuilder()
            .addParameter( "p" )
            .addCode( "$N = p", property.getFieldName() )
            .build();

        //---* The accessor class *--------------------------------------------
        final var accessorClass = property.getPrefsAccessorClass()
            .orElseThrow( () -> new CodeGenerationError( format( MSG_AccessorMissing, property.getPropertyName() ) ) );

        //---* The path to the node *------------------------------------------
        final var path = property.getSystemPrefsPath()
            .orElseThrow( () -> new CodeGenerationError( format( MSG_PreferencesNotConfigured, property.getPropertyName() ) ) );

        //---* The key for the value *-----------------------------------------
        final var key = property.getPrefsKey()
            .orElseThrow( () -> new CodeGenerationError( format( MSG_PreferencesNotConfigured, property.getPropertyName() ) ) );

        final var builder = composer
            .codeBlockBuilder()
            .add(
                """
                
                /*
                 * Initialise the property '$N' from the SYSTEM {@code Preferences}.
                 *
                 * Path: $L
                 * Key : $L
                 */
                """, property.getPropertyName(), path, key
            )
            .beginControlFlow(
                """
                try
                """ )
            .beginControlFlow(
                """
                if( systemRoot().nodeExists( $S ) )
                """, path )
            .addStaticImport( Preferences.class, "systemRoot" )
            .addStatement( "final var node = systemRoot().node( $S )", path );

        //noinspection IfStatementWithTooManyBranches
        if( accessorClass.equals( ENUM_ACCESSOR_TYPE ) )
        {
            final var propertyType = property.getPropertyType();
            builder.addStatement( "final var accessor = new $2T<>( $1S, $3T.class, $4L, $5L )", key, accessorClass, propertyType, getter, setter );
        }
        else if( accessorClass.equals( LIST_ACCESSOR_TYPE ) || accessorClass.equals( SET_ACCESSOR_TYPE ) )
        {
            final var propertyType = (ParameterizedTypeName) property.getPropertyType();
            final var argumentType = propertyType.typeArguments().get( 0 );
            final var stringConverterType = getStringConverter( argumentType )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, property.getPropertyName(), argumentType.toString() ) ) );
            switch( determineStringConverterInstantiation( stringConverterType, false ) )
            {
                case BY_INSTANCE -> builder.addStatement( "final var accessor = new $2T<>( $1S, $3T.INSTANCE, $4L, $5L )", key, accessorClass, stringConverterType, getter, setter );
                case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var accessor = new $2T<>( $1S, new $3T(), $4L, $5L )", key, accessorClass, stringConverterType, getter, setter );
                case AS_ENUM -> builder.addStatement( "final var accessor = new $2T<>( $1S, new $3T( $6T.class ), $4L, $5L )", key, accessorClass, stringConverterType, getter, setter, propertyType );
            }
        }
        else if( accessorClass.equals( MAP_ACCESSOR_TYPE ) )
        {
            final var propertyType = (ParameterizedTypeName) property.getPropertyType();
            final var argumentTypes = propertyType.typeArguments();
            final var keyStringConverterType = getStringConverter( argumentTypes.get( 0 ) )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, property.getPropertyName(), argumentTypes.get( 0 ).toString() ) ) );
            final var keySnippet =
                switch( determineStringConverterInstantiation( keyStringConverterType, false ) )
                {
                    case BY_INSTANCE -> "$3T.INSTANCE";
                    case THROUGH_CONSTRUCTOR -> "new $3T()";
                    case AS_ENUM -> EMPTY_STRING;
                };
            final var valueStringConverterType = getStringConverter( argumentTypes.get( 1 ) )
                .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, property.getPropertyName(), argumentTypes.get( 1 ).toString() ) ) );
            final var valueSnippet =
                switch( determineStringConverterInstantiation( valueStringConverterType, false ) )
                {
                    case BY_INSTANCE -> "$4T.INSTANCE";
                    case THROUGH_CONSTRUCTOR -> "new $4T()";
                    case AS_ENUM -> EMPTY_STRING;
                };
            builder.addStatement( format( "final var accessor = new $2T<>( $1S, %1$s, %2$s, $5L, $6L )", keySnippet, valueSnippet ), key, accessorClass, keyStringConverterType, valueStringConverterType, getter, setter );
        }
        else if( accessorClass.equals( DEFAULT_ACCESSOR_TYPE ) )
        {
            final var stringConverterType = property.getStringConverterClass()
                .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverter, property.getPropertyName() ) ) );
            switch( determineStringConverterInstantiation( stringConverterType, property.isEnum() ) )
            {
                case BY_INSTANCE -> builder.addStatement( "final var accessor = new $2T<>( $1S, $4L, $5L, $3T.INSTANCE )", key, accessorClass, stringConverterType, getter, setter );
                case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var accessor = new $2T<>( $1S, $4L, $5L, new $3T() )", key, accessorClass, stringConverterType, getter, setter );
                case AS_ENUM -> builder.addStatement( "final var accessor = new $2T<>( $1S, $4L, $5L, new $3T( $6T.class ) )", key, accessorClass, stringConverterType, getter, setter, property.getPropertyType() );
            }
        }
        else
        {
            builder.addStatement( "final var accessor = new $2T( $1S, $3L, $4L )", key, accessorClass, getter, setter );
        }

        //---* Add the code to read the Preferences *--------------------------
        builder.addStatement( "accessor.readPreference( node )" )
            .endControlFlow()
            .nextControlFlow(
                """
        
                catch( final $T e )
                """, BackingStoreException.class )
            .addStatement( "throw new $T( e )", ExceptionInInitializerError.class )
            .endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeConstructorFragment4SystemPreference()

    /**
     *  The default implementation of the method that composes a constructor
     *  fragment for the initialisation of the given property in cases it is
     *  annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "TypeMayBeWeakened", "StaticMethodOnlyUsedInOneClass"} )
    public static CodeBlock composeConstructorFragment4SystemProp( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var builder = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer()
            .codeBlockBuilder()
            .add(
                """
                
                /*
                 * Initialise the property '$N' from the system properties.
                 */
                """, property.getPropertyName()
            )
            .beginControlFlow( EMPTY_STRING );

        //---* Set the StringConverter *---------------------------------------
        final var stringConverter = property.getStringConverterClass()
            .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverter, property.getPropertyName() ) ) );
        switch( determineStringConverterInstantiation( stringConverter, property.isEnum() ) )
        {
            case BY_INSTANCE -> builder.addStatement( "final var stringConverter = $T.INSTANCE", stringConverter );
            case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var stringConverter = new $T()", stringConverter );
            case AS_ENUM -> builder.addStatement( "final var stringConverter = new $1T( $2T.class )", stringConverter, property.getPropertyType() );
        }

        //---* Set the value *-------------------------------------------------
        final var defaultValue = property.getEnvironmentDefaultValue();
        if( defaultValue.isPresent() )
        {
            builder.addStatement( "final var value = getProperty( $1S, $2S )", property.getSystemPropertyName().orElseThrow( () -> new CodeGenerationError( format( MSG_MissingSystemProp, property.getPropertyName() ) ) ), defaultValue.get() );
        }
        else
        {
            builder.addStatement( "final var value = getProperty( $1S )", property.getSystemPropertyName().orElseThrow( () -> new CodeGenerationError( format( MSG_MissingSystemProp, property.getPropertyName() ) ) ) );
        }
        builder.addStaticImport( System.class, "getProperty" )
            .addStatement( "$1N = stringConverter.fromString( value )", property.getFieldName() )
            .endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeConstructorFragment4SystemProp()

    /**
     *  The default implementation of the method that composes a field for the
     *  given property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "StaticMethodOnlyUsedInOneClass"} )
    public static FieldSpec composeField( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        final var builder = composer.fieldBuilder( property.getPropertyType(), property.getFieldName(), PRIVATE )
            .addJavadoc(
                """
                Property: &quot;$L&quot;.
                """, property.getPropertyName() );
        if( Stream.of( PROPERTY_IS_MUTABLE, PROPERTY_IS_OPTION, PROPERTY_IS_ARGUMENT, ALLOWS_PREFERENCES, SYSTEM_PREFERENCE ).noneMatch( property::hasFlag ) )
        {
            builder.addModifiers( FINAL );
        }

        //---* Create the return value *--------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeField()

    /**
     *  The default implementation of the method that composes a getter for the
     *  given property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The method specification.
     */
    @SuppressWarnings( {"OptionalGetWithoutIsPresent", "UseOfConcreteClass", "TypeMayBeWeakened", "StaticMethodOnlyUsedInOneClass"} )
    public static MethodSpec composeGetter( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        //---* Obtain the builder *--------------------------------------------
        final var builder = property.getGetterBuilder()
            .orElseGet( () -> composer.methodBuilder( property.getGetterMethodName().get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .returns( property.getGetterReturnType() )
            );
        builder.addModifiers( FINAL )
            .addJavadoc( composer.createInheritDocComment() );

        //---* Add the locking *-----------------------------------------------
        final var lock = property.hasFlag( PROPERTY_REQUIRES_SYNCHRONIZATION ) && property.hasFlag( PROPERTY_IS_MUTABLE )
                         ? codeBuilder.getField( STD_FIELD_ReadLock )
                         : null;
        if( nonNull( lock) ) builder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );

        //---* Return the value *----------------------------------------------
        if( property.hasFlag( GETTER_RETURNS_OPTIONAL ) )
        {
            builder.addStatement( "return $1T.ofNullable( $2N )", Optional.class, property.getFieldName() );
        }
        else
        {
            builder.addStatement( "return $1N", property.getFieldName() );
        }

        //---* Cleanup *-------------------------------------------------------
        if( nonNull( lock) ) builder.endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeGetter()

    /**
     *  The default implementation of the method that composes a setter for the
     *  given property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The method specification.
     */
    @SuppressWarnings( {"OptionalGetWithoutIsPresent", "UseOfConcreteClass", "TypeMayBeWeakened", "StaticMethodOnlyUsedInOneClass"} )
    public static MethodSpec composeSetter( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        //---* Obtain the builder *--------------------------------------------
        final var builder = property.getSetterBuilder()
            .orElseGet( () -> composer.methodBuilder( property.getSetterMethodName().get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .addParameter( composer.parameterOf( property.getPropertyType(), property.getSetterArgumentName(), FINAL ) )
                .returns( VOID )
            );
        builder.addModifiers( FINAL )
            .addJavadoc( composer.createInheritDocComment() );

        //---* Add the locking *-----------------------------------------------
        final var lock = property.hasFlag( PROPERTY_REQUIRES_SYNCHRONIZATION ) ? codeBuilder.getField( STD_FIELD_WriteLock ) : null;
        if( nonNull( lock) ) builder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );

        //---* Assign the value *----------------------------------------------
        if( property.hasFlag( SETTER_CHECK_EMPTY ) )
        {
            final var methodName = "requireNotEmptyArgument";
            builder.addStatement(
                    """
                    final var newValue = $2N( $1N, $1S )\
                    """, property.getSetterArgumentName(), methodName )
                .addStaticImport( Objects.class, methodName );
        }
        else if( property.hasFlag( SETTER_CHECK_NULL ) )
        {
            final var methodName = "requireNonNullArgument";
            builder.addStatement(
                    """
                    final var newValue = $2N( $1N, $1S )\
                    """, property.getSetterArgumentName(), methodName )
                .addStaticImport( Objects.class, methodName );
        }
        else
        {
            builder.addStatement(
                """
                final var newValue = $1N\
                """, property.getSetterArgumentName() );
        }
        builder.addStatement(
                """
                $1N.fireEvent( $2S, $3N, newValue )\
                """, codeBuilder.getField( STD_FIELD_ListenerSupport ), property.getPropertyName(), property.getFieldName() )
            .addStatement(
                """
                $1N = newValue\
                """, property.getFieldName() );

        //---* Cleanup *-------------------------------------------------------
        if( nonNull( lock) ) builder.endControlFlow();

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeSetter()

    /**
     *  Determines how to instantiate the given implementation of
     *  {@link org.tquadrat.foundation.lang.StringConverter}.
     *
     *  @param  stringConverterClass    The String converter class.
     *  @param  isEnum  {@code true} if the property is of an
     *      {@link Enum enum} type, {@code false} otherwise.
     *  @return The type of instantiation.
     */
    protected static final StringConverterInstantiation determineStringConverterInstantiation( final TypeName stringConverterClass, final boolean isEnum )
    {
        var retValue = THROUGH_CONSTRUCTOR;
        if( requireNonNullArgument( stringConverterClass, "stringConverterClass" ) instanceof ClassName className )
        {
            try
            {
                final var candidateClass = Class.forName( className.canonicalName(), false, CodeBuilderBase.class.getClassLoader() );
                final var field = candidateClass.getField( "INSTANCE" );
                final var modifiers = field.getModifiers();
                retValue = field.canAccess( null ) && isPublic( modifiers ) && isStatic( modifiers ) ? BY_INSTANCE : THROUGH_CONSTRUCTOR;
            }
            catch( @SuppressWarnings( "unused" ) final NoSuchFieldException | SecurityException ignored )
            {
                /*
                 * There is no INSTANCE field, or it is not static, or not public,
                 * or it is otherwise not accessible to this method.
                 */
                retValue = THROUGH_CONSTRUCTOR;
            }
            catch( @SuppressWarnings( "unused" ) final ClassNotFoundException ignored )
            {
                /*
                 * The class for the StringConverter implementation does not
                 * exist.
                 */
                retValue = THROUGH_CONSTRUCTOR;
            }
        }
        if( (retValue == THROUGH_CONSTRUCTOR) && isEnum )
        {
            retValue = AS_ENUM;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determineStringConverterInstantiation

    /**
     *  {@inheritDoc}
     */
    @Override
    public final JavaComposer getComposer() { return m_Composer; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final CodeGenerationConfiguration getConfiguration() { return m_Configuration; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final FieldSpec getField( final StandardField reference )
    {
        final var retValue = m_StandardFields.get( requireNonNullArgument( reference, "reference" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getField()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final MethodSpec getMethod( final StandardMethod reference )
    {
        final var retValue = m_StandardMethods.get( requireNonNullArgument( reference, "reference" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getMethod()

    /**
     *  Returns an iterator over the configured properties.
     *
     *  @return The iterator.
     */
    protected final Iterator<PropertySpec> getProperties() { return m_Configuration.propertyIterator(); }

    /**
     *  Returns the
     *  {@link StringConverter}
     *  type for the given type.
     *
     *  @param  type    The type.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the requested implementation of
     *      {@code StringConverter}.
     */
    protected static final Optional<TypeName> getStringConverter( final TypeName type )
    {
        final var retValue = Optional.ofNullable( (TypeName) m_ConverterRegistry.get( requireNonNullArgument( type, "type" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getStringConverter()

    /**
     *  Returns the flag that controls whether the configuration bean has to be
     *  generated thread safe.
     *
     *  @return {@code true} if lock support is required, {@code false}
     *      otherwise.
     */
    protected final boolean isSynchronized() { return m_IsSynchronized; }
}
//  class CodeBuilderBase

/*
 *  End of File
 */
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

package org.tquadrat.foundation.config.ap.impl.codebuilders;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_DuplicateOptionName;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_InvalidCLIType;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoArgumentIndex;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoOptionName;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ELEMENTTYPE_IS_ENUM;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MANDATORY;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_CLI_MULTIVALUED;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_ARGUMENT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_OPTION;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_CLIDefinitions;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_CLIError;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_GetRessourceBundle;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.REDUNDANT_EXPLICIT_VARIABLE_TYPE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.DebugOutput.ifDebug;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.IllegalAnnotationError;
import org.tquadrat.foundation.config.CmdLineException;
import org.tquadrat.foundation.config.ConfigUtil;
import org.tquadrat.foundation.config.ap.CollectionKind;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.config.cli.CmdLineValueHandler;
import org.tquadrat.foundation.config.cli.SimpleCmdLineValueHandler;
import org.tquadrat.foundation.config.internal.ClassRegistry;
import org.tquadrat.foundation.config.spi.CLIArgumentDefinition;
import org.tquadrat.foundation.config.spi.CLIDefinition;
import org.tquadrat.foundation.config.spi.CLIOptionDefinition;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.util.stringconverter.EnumStringConverter;

/**
 *  The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  for the CLI stuff, as defined in
 *  {@link org.tquadrat.foundation.config.CLIBeanSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CLIBeanBuilder.java 1053 2023-03-11 00:10:49Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: CLIBeanBuilder.java 1053 2023-03-11 00:10:49Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class CLIBeanBuilder extends CodeBuilderBase
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The handler classes.
     */
    private final Map<TypeName,ClassName> m_HandlerClasses = new HashMap<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code CLIBeanBuilder}.
     *
     *  @param  context The code generator context.
     */
    public CLIBeanBuilder( final CodeGeneratorContext context )
    {
        super( context );

        for( final var entry : ClassRegistry.m_HandlerClasses.entrySet() )
        {
            m_HandlerClasses.put( TypeName.from( entry.getKey() ), ClassName.from( entry.getValue() ) );
        }
    }   //  CLIBeanBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     *  <p>This method checks whether there are any properties that are either
     *  options or arguments, and does the build only when there is at least
     *  one.</p>
     *  <p>Not building CLI stuff will let crash the compilation of the
     *  generated code, but this is intended: either the annotation for the
     *  CLI properties is missing, or the interface
     *  {@link org.tquadrat.foundation.config.CLIBeanSpec}
     *  was added to the configuration bean specification in error.</p>
     */
    @Override
    public final void build()
    {
        var doBuild = false;
        for( final var iterator = getProperties(); iterator.hasNext() && !doBuild; )
        {
            final var property = iterator.next();
            doBuild = property.hasFlag( PROPERTY_IS_OPTION ) || property.hasFlag( PROPERTY_IS_ARGUMENT );
        }
        if( doBuild ) doBuild();
    }   //  build()

    /**
     *  Composes the code that creates the CLI value handler for the given
     *  property.
     *
     *  @param  property    The property.
     *  @return The name of the method that creates the CLI value handler for
     *      this property.
     */
    @SuppressWarnings( "OverlyCoupledMethod" )
    private final String composeValueHandlerCreation( final PropertySpec property )
    {
        //---* The method name *-----------------------------------------------
        final var retValue = format( "composeValueHandler_%s", capitalize( property.getPropertyName() ) );

        final var objectType = WildcardTypeName.subtypeOf( Object.class );
        final var handlerType = ParameterizedTypeName.from( ClassName.from( CmdLineValueHandler.class ), objectType );
        final var builder = getComposer().codeBlockBuilder();

        final ParameterizedTypeName lambdaType;
        final LambdaSpec lambda;

        if( property.isCollection() )
        {
            if( property.getCollectionKind() == CollectionKind.MAP )
            {
                throw new IllegalAnnotationError( format( "Property '%s' is a map of type '%s'; maps are currently not supported for CLI properties", property.getPropertyName(), property.getPropertyType().toString() ) );
            }

            //---* Determine the element type of the collection *--------------
            final var elementType = property.getElementType()
                .orElseThrow( () -> new IllegalAnnotationError( format( "Cannot determine element type for property '%s'", property.getPropertyName() ) ) );

            //---* The lambda that adds the value to the attribute *-----------
            lambdaType = ParameterizedTypeName.from( ClassName.from( BiConsumer.class ), ClassName.from( String.class ), elementType );
            lambda = getComposer().lambdaBuilder()
                .addParameter( "propertyName" )
                .addParameter( "value" )
                .addCode( "$N.add( value )", property.getFieldName() )
                .build();

            //---* Get the StringConverter for the element type *--------------
            final var stringConverter = property.getStringConverterClass()
                .or( () -> getStringConverter( elementType ) )
                .or( () -> Optional.ofNullable( property.hasFlag( ELEMENTTYPE_IS_ENUM ) ? ClassName.from( EnumStringConverter.class ) : null ) )
                .orElseThrow( () -> new IllegalAnnotationError( format( "Property '%1$s': cannot find StringConverter for '%2$s'", property.getPropertyName(), elementType.toString() ) ) );

            switch( determineStringConverterInstantiation( stringConverter, property.hasFlag( ELEMENTTYPE_IS_ENUM ) ) )
            {
                case BY_INSTANCE -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, $3T.INSTANCE )", handlerType, SimpleCmdLineValueHandler.class, stringConverter );
                case THROUGH_CONSTRUCTOR -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, new $3T() )", handlerType, SimpleCmdLineValueHandler.class, stringConverter );
                case AS_ENUM -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, new $3T( $4T.class ) )", handlerType, SimpleCmdLineValueHandler.class, stringConverter, elementType );
            }
        }
        else
        {
            //---* The lambda that sets the value to the attribute *-----------
            lambdaType = ParameterizedTypeName.from( ClassName.from( BiConsumer.class ), ClassName.from( String.class ), property.getPropertyType().box() );
            lambda = getComposer().lambdaBuilder()
                .addParameter( "propertyName" )
                .addParameter( "value" )
                .addCode( "$N = value", property.getFieldName() )
                .build();

            //---* Retrieve the class for the value handler *------------------
            final var valueHandlerClass = retrieveValueHandlerClass( property );
            //noinspection OverlyLongLambda
            valueHandlerClass.ifPresentOrElse(
                t -> builder.addStatement( "final $T retValue = new $T( lambda ) ", handlerType, t ),
                () ->
                {
                    final var stringConverter = property.getStringConverterClass()
                        .orElseThrow( () -> new IllegalAnnotationError( format( "No String converter for property '%s'", property.getPropertyName() ) ) );
                    switch( determineStringConverterInstantiation( stringConverter, property.isEnum() ) )
                    {
                        case BY_INSTANCE -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, $3T.INSTANCE )", handlerType, SimpleCmdLineValueHandler.class, stringConverter );
                        case THROUGH_CONSTRUCTOR -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, new $3T() )", handlerType, SimpleCmdLineValueHandler.class, stringConverter );
                        case AS_ENUM -> builder.addStatement( "final $1T retValue = new $2T<>( lambda, new $3T( $4T.class ) )", handlerType, SimpleCmdLineValueHandler.class, stringConverter, property.getPropertyType() );
                    }
                });
        }

        //---* Compose the method *--------------------------------------------
        final var method = getComposer().methodBuilder( retValue )
            .addModifiers( PRIVATE, FINAL )
            .addJavadoc(
                """
                Creates the value handler for the property &quot;$L.&quot;.
                """, property.getPropertyName() )
            .returns( handlerType, "The value handler." )
            .addCode(
                """
                $L
                """, createSuppressWarningsAnnotation( getComposer(), REDUNDANT_EXPLICIT_VARIABLE_TYPE ) )
            .addStatement( "final $T lambda = $L", lambdaType, lambda )
            .addCode( builder.build() )
            .addCode( getComposer().createReturnStatement() )
            .build();
        addMethod( method );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeValueHandlerCreation()

    /**
     *  Creates the implementation for the method
     *  {@link org.tquadrat.foundation.config.CLIBeanSpec#dumpParamFileTemplate(OutputStream)}.
     */
    private final void createDumpParamFileTemplate()
    {
        final var arg = getComposer().parameterBuilder( OutputStream.class, "outputStream", FINAL )
            .build();
        final var method = getComposer().methodBuilder( "dumpParamFileTemplate" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg )
            .returns( VOID )
            .addException( IOException.class )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "$T.dumpParamFileTemplate( $N, $N )", ConfigUtil.class, getField( STD_FIELD_CLIDefinitions ), arg )
            .build();
        addMethod( method );
    }   //  createDumpParamFileTemplate()

    /**
     *  Creates the implementation for the method
     *  {@link org.tquadrat.foundation.config.CLIBeanSpec#parseCommandLine(String[])}.
     *
     *  @param  registry    The registry of the properties that are exposed for
     *      the CLI.
     *  @param  errorMsgHolder  The field for the parse errors.
     */
    private final void createParseCommandLine( final FieldSpec registry, final FieldSpec errorMsgHolder )
    {
        final TypeName typeName = ArrayTypeName.of( String.class );
        final var arg = getComposer().parameterBuilder( typeName, "args", FINAL )
            .build();
        final var methodBuilder = getComposer().methodBuilder( "parseCommandLine" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg )
            .returns( BOOLEAN )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "var retValue = true" );
        if( isSynchronized() )
        {
            methodBuilder.beginControlFlow(
            """
                try( final var ignored = $N.lock() )
                """, getField( STD_FIELD_WriteLock ) );
        }
        else
        {
            methodBuilder.beginControlFlow(
            """
                try
                """ );
        }
        methodBuilder.addStatement( "$T.parseCommandLine( $N, $N )", ConfigUtil.class, registry, arg )
            .addStatement( "$N = null", errorMsgHolder )
            .nextControlFlow(
                """

                catch( final $T e )
                """, CmdLineException.class )
            .addStatement( "$N = e.getLocalizedMessage()", errorMsgHolder )
            .addStatement( "retValue = false" )
            .endControlFlow()
            .addCode( getComposer().createReturnStatement() )
            .build();

        final var method =methodBuilder.build();

        addMethod( method );
    }   //  createParseCommandLine()

    /**
     *  Creates the implementation for the method
     *  {@link org.tquadrat.foundation.config.CLIBeanSpec#printUsage(OutputStream, CharSequence)}.
     *
     *  @param  registry    The registry of the properties that are exposed for
     *      the CLI.
     */
    private final void createPrintUsage( final FieldSpec registry )
    {
        final var arg0 = getComposer().parameterBuilder( OutputStream.class, "outputStream", FINAL )
            .build();
        final var arg1 = getComposer().parameterBuilder( CharSequence.class, "command", FINAL )
            .build();
        final var method = getComposer().methodBuilder( "printUsage" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .addParameter( arg1 )
            .returns( VOID )
            .addException( IOException.class )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "$T.printUsage( $N, $N(), $N, $N )", ConfigUtil.class, arg0, getMethod( STD_METHOD_GetRessourceBundle ), arg1, registry )
            .build();
        addMethod( method );
    }   //  createPrintUsage()

    /**
     *  Creates the implementation for the method
     *  {@link org.tquadrat.foundation.config.CLIBeanSpec#retrieveParseErrorMessage()}.
     *
     *  @param  errorMsgHolder  The field for the parse errors.
     */
    private final void createRetrieveParseErrorMessage( final FieldSpec errorMsgHolder )
    {
        final var typeName = ParameterizedTypeName.from( Optional.class, String.class );
        final var method = getComposer().methodBuilder( "retrieveParseErrorMessage" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( typeName )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "return $T.ofNullable( $N )", Optional.class, errorMsgHolder )
            .build();
        addMethod( method );
    }   //  createRetrieveParseErrorMessage()

    /**
     *  Is called by
     *  {@link #build()}
     *  to do the work – only if there is work to do …
     */
    private final void doBuild()
    {
        //---* Create the registry for the CLI definitions *-------------------
        final var registryType = ParameterizedTypeName.from( ClassName.from( List.class ), TypeName.from( CLIDefinition.class ) );
        final var registry = getComposer().fieldBuilder( registryType, STD_FIELD_CLIDefinitions.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The registry for the CLI definitions
                """ )
            .initializer( "new $T<>()", ArrayList.class )
            .build();
        addField( STD_FIELD_CLIDefinitions, registry );

        //---* Create the field for the CLI parsing errors *-------------------
        final var errorMsgHolder = getComposer().fieldBuilder( String.class, STD_FIELD_CLIError.toString(), PRIVATE )
            .addJavadoc(
                """
                The last error message from a call to
                {@link #parseCommandLine(String[])}.

                @see #retrieveParseErrorMessage()
                """ )
            .initializer( "null" )
            .build();
        addField( STD_FIELD_CLIError, errorMsgHolder );

        //---* Add the methods from CLIBeanSpec *------------------------------
        createDumpParamFileTemplate();
        createParseCommandLine( registry, errorMsgHolder );
        createPrintUsage( registry );
        createRetrieveParseErrorMessage( errorMsgHolder );

        /*
         * The names of previously encountered options, collected to avoid
         * collisions.
         */
        final Collection<String> alreadyUsedOptions = new HashSet<>();

        //---* Add the code to the constructor *-------------------------------
        final var objectType = WildcardTypeName.subtypeOf( Object.class );
        final var handlerName = "valueHandler";
        final var handlerType = ParameterizedTypeName.from( ClassName.from( CmdLineValueHandler.class ), objectType );

        final var definitionName = "cliDefinition";

        final var builder = getComposer().codeBlockBuilder().add(
                """
    
                /*
                 * Initialise the CLI definitions.
                 */
                """ )
            .addStatement( "$T $L", handlerType, handlerName )
            .addStatement( "$T $L", CLIDefinition.class, definitionName );

        CLIPropertiesLoop:
        for( final var iterator = getProperties(); iterator.hasNext(); )
        {
            final var property = iterator.next();
            if( !property.isOnCLI() ) continue CLIPropertiesLoop;

            //---* Create the value handler *----------------------------------
            builder.add(
                    """
                    
                    /*
                     * CLI definition for Property &quot;$L&quot;.
                     */
                    """, property.getPropertyName()
                )
                .addStatement( "$L = $L()", handlerName, composeValueHandlerCreation( property ) );

            //---* Create the CLI definition *---------------------------------
            final var usage = property.getCLIUsage().orElse( null );
            final var usageKey = property.getCLIUsageKey().orElse( null );
            final var metaVar = property.getCLIMetaVar().orElse( null );
            final var required = Boolean.valueOf( property.hasFlag( PROPERTY_CLI_MANDATORY ) );
            final var multiValued = Boolean.valueOf( property.hasFlag( PROPERTY_CLI_MULTIVALUED ) );
            final var format = property.getCLIFormat().orElse( null );
            if( property.hasFlag( PROPERTY_IS_ARGUMENT ) )
            {
                builder.addStatement( "$1L = new $2T( $3S, $4L, $5S, $6S, $7S, $8L, $9L, $10L, $11S )",
                    definitionName,
                    CLIArgumentDefinition.class,
                    property.getPropertyName(),
                    Integer.valueOf( property.getCLIArgumentIndex().orElseThrow( () -> new IllegalAnnotationError( format( MSG_NoArgumentIndex, property.getPropertyName() ) ) ) ),
                    usage,
                    usageKey,
                    metaVar,
                    required,
                    handlerName,
                    multiValued,
                    format );
            }
            else if( property.hasFlag( PROPERTY_IS_OPTION ) )
            {
                final var optionNames = property.getCLIOptionNames().orElseThrow( () -> new IllegalAnnotationError( format( MSG_NoOptionName, property.getPropertyName() ) ) );
                for( final var optionName : optionNames )
                {
                    if( !alreadyUsedOptions.add( optionName ) )
                    {
                        throw new IllegalAnnotationError( format( MSG_DuplicateOptionName, optionName, property.getPropertyName() ) );
                    }
                }
                final var names = optionNames.stream()
                    .map( s -> format( "\"%s\"", s ) )
                    .collect( joining( ", ", "List.of( ", " )" ) );
                builder.addStatement( "$1L = new $2T( $3S, $4L, $5S, $6S, $7S, $8L, $9L, $10L, $11S )",
                    definitionName,
                    CLIOptionDefinition.class,
                    property.getPropertyName(),
                    names,
                    usage,
                    usageKey,
                    metaVar,
                    required,
                    handlerName,
                    multiValued,
                    format );
            }
            else
            {
                throw new IllegalAnnotationError( format( MSG_InvalidCLIType, property.getPropertyName() ) );
            }
            builder.addStatement( "$N.add( $L )", registry, definitionName );
        }
        addConstructorCode( builder.build() );
    }   //  doBuild()

    /**
     *  <p>{@summary Retrieves the class for the value handler for the given
     *  property.} If returning
     *  {@link Optional#empty()} empty},
     *  an instance of
     *  {@link SimpleCmdLineValueHandler}
     *  must be used that will be instantiated with the
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  retrieved by a call to
     *  {@link PropertySpec#getStringConverterClass()}.</p>
     *
     *  @param  property    The property.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the respective type name.
     */
    private final Optional<TypeName> retrieveValueHandlerClass( final PropertySpec property )
    {
        var retValue = requireNonNullArgument( property, "property" ).getCLIValueHandlerClass();
        if( retValue.isEmpty() )
        {
            final var handlerClass = m_HandlerClasses.get( property.getPropertyType() );
            if( nonNull( handlerClass ) )
            {
                retValue = Optional.of( handlerClass );
            }
        }
        ifDebug( a ->
        {
            final var propertyName = ((PropertySpec) a [0]).getPropertyName();
            final var propertyType = ((PropertySpec) a [0]).getPropertyType().toString();
            //noinspection unchecked
            final var handlerClass = Objects.toString( ((Optional<TypeName>) a [1]).orElse( null ) );
            return format( "property: %1$s%n\tpropertyType: %2$s%n\thandlerClass: %3$s", propertyName, propertyType, handlerClass );
        }, property, retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveValueHandlerClass()
}
//  class CLIBeanBuilder

/*
 *  End of File
 */
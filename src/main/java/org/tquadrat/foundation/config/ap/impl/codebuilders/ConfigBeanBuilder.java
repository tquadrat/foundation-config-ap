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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RESOURCEBUNDLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ENVIRONMENT_VARIABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.EXEMPT_FROM_TOSTRING;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_IS_DEFAULT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_SPECIAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PREFERENCE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PROPERTY;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ListenerSupport;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_AddListener;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_GetRessourceBundle;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_RemoveListener;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_ToString;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.INSTANCE_VARIABLE_OF_CONCRETE_CLASS;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.THROW_CAUGHT_LOCALLY;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.UNCHECKED;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.StringUtils.repeat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ConfigurationChangeListener;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.config.spi.ConfigChangeListenerSupport;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.AutoLock;
import org.tquadrat.foundation.lang.CommonConstants;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.util.StringUtils;

/**
 *  The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  for the basic stuff, as defined in
 *  {@link org.tquadrat.foundation.config.ConfigBeanSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ConfigBeanBuilder.java 1010 2022-02-05 19:28:36Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: ConfigBeanBuilder.java 1010 2022-02-05 19:28:36Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class ConfigBeanBuilder extends CodeBuilderBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ConfigBeanBuilder}.
     *
     *  @param  context The code generator context.
     */
    public ConfigBeanBuilder( final CodeGeneratorContext context )
    {
        super( context );
    }   //  ConfigBeanBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds the listener support to the new class.
     */
    private final void addListenerSupport()
    {
        //---* Add the listener support *-------------------------------------
        final var field = getComposer().fieldBuilder( ConfigChangeListenerSupport.class, STD_FIELD_ListenerSupport.toString() , PRIVATE, FINAL )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), INSTANCE_VARIABLE_OF_CONCRETE_CLASS ) )
            .addJavadoc(
                """
                The support for the configuration change listener.
                """ )
            .build();
        addField( STD_FIELD_ListenerSupport, field );

        //---* Initialise the listener support *-------------------------------
        var code = getComposer().codeBlockBuilder()
            .add(
                """
                //---* Initialise the listener support *-------------------------------
                """ )
            .addStatement( "$N = new $T( this )", getField( STD_FIELD_ListenerSupport ), ConfigChangeListenerSupport.class )
            .build();
        addConstructorCode( code );

        //---* Add the listener management methods *---------------------------
        final var param1 = getComposer().parameterBuilder( ConfigurationChangeListener.class, "listener", FINAL )
            .build();
        code = getComposer().codeBlockBuilder()
            .addStatement( "$N.addListener( $N )", getField( STD_FIELD_ListenerSupport ), param1 )
            .build();
        var method = getComposer().methodBuilder( STD_METHOD_AddListener.toString() )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addParameter( param1 )
            .returns( VOID )
            .addCode( code )
            .build();
        addMethod( STD_METHOD_AddListener, method );

        code = getComposer().codeBlockBuilder()
            .addStatement( "$N.removeListener( $N )", getField( STD_FIELD_ListenerSupport ), param1 )
            .build();
        method = getComposer().methodBuilder( STD_METHOD_RemoveListener.toString() )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addParameter( param1 )
            .returns( VOID )
            .addCode( code )
            .build();
        addMethod( STD_METHOD_RemoveListener, method );
    }   //  addListenerSupport()

    /**
     *  Adds locking support to the new class.
     */
    private final void addLockSupport()
    {
        //---* Add the locks *-------------------------------------------------
        var field = getComposer().fieldBuilder( AutoLock.class, STD_FIELD_ReadLock.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The &quot;read&quot; lock.
                """ )
            .build();
        addField( STD_FIELD_ReadLock, field );

        field = getComposer().fieldBuilder( AutoLock.class, STD_FIELD_WriteLock.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The &quot;write&quot; lock.
                """ )
            .build();
        addField( STD_FIELD_WriteLock, field );

        //---* Initialise the locks *------------------------------------------
        final var code = getComposer().codeBlockBuilder()
            .add(
                """
                
                //---* Create the locks and initialise them *--------------------------
                """ )
            .addStatement( "final var lock = new $T()", ReentrantReadWriteLock.class )
            .addStatement( "$N = $T.of( lock.readLock() )", getField( STD_FIELD_ReadLock ), AutoLock.class )
            .addStatement( "$N = $T.of( lock.writeLock() )", getField( STD_FIELD_WriteLock ), AutoLock.class )
            .build();
        addConstructorCode( code );
    }   //  addLockSupport()

    /**
     *  Adds &quot;unchecked&quot; to the
     *  {@link SuppressWarnings}
     *  annotation for the constructor if the given type is a
     *  {@link java.util.List},
     *  {@link java.util.Set},
     *  {@link java.util.Map}
     *  or an otherwise parameterised type.
     *
     *  @param  typeName    The type to check.
     *
     *  @see ParameterizedTypeName
     */
    private final void addUnchecked( final TypeName typeName )
    {
        if( typeName instanceof ParameterizedTypeName ) addConstructorSuppressedWarning( UNCHECKED );
    }   //  addUnchecked()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void build()
    {
        //---* Generate the default stuff *------------------------------------
        addListenerSupport();
        if( isSynchronized() ) addLockSupport();

        //---* Generate the properties *---------------------------------------
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            generateProperty( i.next() );
        }

        //---* Create the initialisation code in the constructor *-------------
        getConfiguration().getInitDataMethod()
            .ifPresent( this::composeInitializationCodeFromMethod );
        getConfiguration().getInitDataResource()
            .ifPresent( this::composeInitializationCodeFromResource );

        //---* Create 'toString()' *-------------------------------------------
        createToString();
    }   //  build()

    /**
     *  Composes the constructor code that initialises the properties from the
     *  result of a call to the {@code initData()} method.
     *
     *  @param  method  The {@code initData()} method.
     */
    private final void composeInitializationCodeFromMethod( final MethodSpec method )
    {
        final var builder = getComposer().codeBlockBuilder()
            .add(
                """
                
                /*
                 * Initialise the properties from '$1N()'.
                 */
                """, method
            )
            .beginControlFlow(
                """
                try
                """
            );

        if( method.hasModifier( STATIC ) )
        {
            builder.addStatement( "final var initData = $1T.initData()", getConfiguration().getSpecification() );
        }
        else
        {
            builder.addStatement( "final var initData = initData()" );
        }

        builder.beginControlFlow(
            """
            if( isNull( initData ) )
            """ )
            .addStaticImport( Objects.class, "isNull" )
            .addStatement(
                """
                throw new $T( "initData() returns null" )""", ValidationException.class )
            .endControlFlow();

        PropertyLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next().merge();
            if( propertySpec.hasFlag( GETTER_IS_DEFAULT ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( PROPERTY_IS_SPECIAL )
                && propertySpec.getSpecialPropertyType().filter( spt -> spt == CONFIG_PROPERTY_RESOURCEBUNDLE ).isEmpty() )
            {
                continue PropertyLoop;
            }
            if( propertySpec.hasFlag( SYSTEM_PROPERTY ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( ENVIRONMENT_VARIABLE ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( SYSTEM_PREFERENCE ) ) continue PropertyLoop;

            final var propertyType = propertySpec.getPropertyType();
            final var field = propertySpec.getFieldName();
            final var propertyName = propertySpec.getPropertyName();
            addUnchecked( propertyType );

            builder.beginControlFlow(
                """
                if( initData.containsKey( $1S ) )
                """, propertyName )
                .addStatement( "$1N = ($2T) initData.get( $3S )", field, propertyType.box(), propertyName )
                .endControlFlow();
        }   //  PropertyLoop:

        builder.nextControlFlow(
            """

            catch( final $1T t )
            """, Throwable.class )
            .addStatement(
                """
                final var eiie = new $1T( "initData() failed" )""", ExceptionInInitializerError.class )
            .addStatement( "eiie.addSuppressed( t )" )
            .addStatement( "throw eiie" )
            .endControlFlow();

        //---* Add the code to the constructor *-------------------------------
        addConstructorCode( builder.build() );
    }   //  composeInitializationCodeFromMethod()

    /**
     *  Composes the constructor code that initialises the properties from the
     *  provided resource.
     *
     *  @param  resourceName    The name of the resource.
     */
    private final void composeInitializationCodeFromResource( final String resourceName )
    {
        //---* The code that loads the properties from the resource *----------
        final var builder = getComposer().codeBlockBuilder()
            .add(
                """

                /*
                 * Load initialisation data from resource "$1L".
                 */
                """, resourceName )
            .beginControlFlow( EMPTY_STRING )
            .addStatement( "final var resource = $1T.class.getResource( $2S )", getConfiguration().getSpecification(), resourceName )
            .beginControlFlow(
                """
                if( isNull( resource ) )
                """ )
            .addStaticImport( Objects.class, "isNull" )
            .addStatement(
                """
                final var fnfe = new $1T( "Resource '$2L'" )""", FileNotFoundException.class, resourceName )
            .addStatement( """
                final var eiie = new $1T( "Cannot find resource '$2L'" )""", ExceptionInInitializerError.class, resourceName )
            .addStatement( "eiie.addSuppressed( fnfe )" )
            .addStatement( "throw eiie" )
            .endControlFlow()
            .add( "\n" )
            .addStatement( "final var initData = new $1T()", Properties.class )
            .beginControlFlow(
                """
                try( final var inputStream = resource.openStream() )
                """ )
            .addStatement( "initData.load( inputStream )" )
            .nextControlFlow(
                """

                catch( final $1T e )
                """, IOException.class )
            .addStatement(
                """
                final var eiie = new $1T( format( "Cannot load resource '%s'", resource.toExternalForm() ) )""", ExceptionInInitializerError.class )
            .addStaticImport( StringUtils.class, "format" )
            .addStatement( "eiie.addSuppressed( e )" )
            .addStatement( "throw eiie" )
            .endControlFlow()
            .add(
                """

                /*
                 * Initialise the properties.
                 */
                """ )
            .addStatement( "$1T value", String.class );

        PropertyLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next().merge();
            if( propertySpec.hasFlag( GETTER_IS_DEFAULT ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( PROPERTY_IS_SPECIAL ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( SYSTEM_PROPERTY ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( ENVIRONMENT_VARIABLE ) ) continue PropertyLoop;
            if( propertySpec.hasFlag( SYSTEM_PREFERENCE ) ) continue PropertyLoop;

            /*
             * Without a StringConverter we cannot initialise the property from
             * the resource.
             */
            if( propertySpec.getStringConverterClass().isEmpty() ) continue PropertyLoop;
            final var stringConverter = propertySpec.getStringConverterClass().get();

            final var field = propertySpec.getFieldName();
            final var propertyName = propertySpec.getPropertyName();

            builder.add( "\n" )
                .addStatement( "value = initData.getProperty( $1S )", propertyName )
                .beginControlFlow(
                    """
                    if( nonNull( value ) )
                    """ )
                .addStaticImport( Objects.class, "nonNull" );
            switch( determineStringConverterInstantiation( stringConverter, propertySpec.isEnum() ) )
            {
                case BY_INSTANCE -> builder.addStatement( "final var stringConverter = $1T.INSTANCE", stringConverter );
                case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var stringConverter = new $1T()", stringConverter );
                case AS_ENUM -> builder.addStatement( "final var stringConverter = new $1T( $2T.class )", stringConverter, propertySpec.getPropertyType() );
            }
            builder.addStatement( "$N = stringConverter.fromString( value )", field )
                .endControlFlow();
        }   //  PropertyLoop:
        builder.endControlFlow();

        addConstructorSuppressedWarning( THROW_CAUGHT_LOCALLY );

        //---* Add the code block *--------------------------------------------
        addConstructorCode( builder.build() );
    }   //  composeInitializationCodeFromResource()

    /**
     *  <p>{@summary Creates the implementation of the method
     *  {@link Object#toString()}
     *  for the configuration bean.} The output of that method will be like
     *  this:</p>
     *  <pre><code>&lt;<i>ClassName</i>&gt; <b>[</b>&lt;<i>PropertyName</i>&gt; <b>= &quot;</b>&lt;<i>PropertyValue</i>&gt;<b>&quot;</b>[<b>,</b> …]<b>]</b></code></pre>
     */
    private final void createToString()
    {
        //---* Create the 'toString()' method *--------------------------------
        final var builder = getComposer().createToStringBuilder()
            .addStatement( "final var prefix = format ( $1S, getClass().getName() )", "%s [" )
            .addStaticImport( StringUtils.class, "format" )
            .addStatement( "final var joiner = new $1T( $2S, prefix, $3S )", StringJoiner.class, ", ", "]" )
            .addCode( "\n" );

        //---* Add the locking *-----------------------------------------------
        final var lock = getConfiguration().getSynchronizationRequired()
             ? getField( STD_FIELD_ReadLock )
             : null;
        if( nonNull( lock) ) builder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );
        final var commentLen = nonNull( lock ) ? 67 : 71;

        //---* Add the code *--------------------------------------------------
        var addEmptyLine = false;
        PropertyLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next().merge();
            if( propertySpec.hasFlag( EXEMPT_FROM_TOSTRING ) ) continue PropertyLoop;

            if( addEmptyLine ) builder.addCode( "\n" );
            addEmptyLine = true;
            final var propertyName = propertySpec.getPropertyName();
            final var comment = format( "//---* Property \"%1$s\" *%2$s", propertyName, repeat( '-', 80 ) ).substring( 0, commentLen );
            builder.addCode(
                """
                $L
                """, comment )
                .beginControlFlow( EMPTY_STRING );

            if( !propertySpec.hasFlag( GETTER_IS_DEFAULT ) )
            {
                //---* We have a field … *-------------------------------------
                final var field = propertySpec.getFieldName();

                if( propertySpec.getStringConverterClass().isPresent() )
                {
                    final var stringConverter = propertySpec.getStringConverterClass().get();
                    switch( determineStringConverterInstantiation( stringConverter, propertySpec.isEnum() ) )
                    {
                        case BY_INSTANCE -> builder.addStatement( "final var stringConverter = $1T.INSTANCE", stringConverter );
                        case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var stringConverter = new $1T()", stringConverter );
                        case AS_ENUM -> builder.addStatement( "final var stringConverter = new $1T( $2T.class )", stringConverter, propertySpec.getPropertyType() );
                    }
                    builder.addStatement( "final var value = stringConverter.toString( $1L )", field )
                        .addStatement(
                            """
                            joiner.add( format( "$1N = \\\"%1$$s\\\"", nonNull( value ) ? value : NULL_STRING ) )""", propertyName )
                        .addStaticImport( Objects.class, "nonNull" )
                        .addStaticImport( CommonConstants.class, "NULL_STRING" )
                        .addStaticImport( StringUtils.class, "format" );
                }
                else
                {
                    builder.addStatement(
                        """
                        joiner.add( format( "$1N = \\\"%1$$S\\\"", $2T.toString( $3L ) ) )""", propertyName, Objects.class, field )
                        .addStaticImport( StringUtils.class, "format" );
                }
            }
            else if( propertySpec.getGetterMethodName().isPresent())
            {
                //---* We just have a getter … *-------------------------------
                final var getterMethod = propertySpec.getGetterMethodName().get();
                if( propertySpec.getStringConverterClass().isPresent() )
                {
                    final var stringConverter = propertySpec.getStringConverterClass().get();
                    switch( determineStringConverterInstantiation( stringConverter, propertySpec.isEnum() ) )
                    {
                        case BY_INSTANCE -> builder.addStatement( "final var stringConverter = $1T.INSTANCE", stringConverter );
                        case THROUGH_CONSTRUCTOR -> builder.addStatement( "final var stringConverter = new $1T()", stringConverter );
                        case AS_ENUM -> builder.addStatement( "final var stringConverter = new $1T( $2T.class )", stringConverter, propertySpec.getPropertyType() );
                    }
                    builder.addStatement( "final var value = stringConverter.toString( $1L() )", getterMethod )
                        .addStatement(
                            """
                            joiner.add( format( "$1N = \\\"%1$$s\\\"", nonNull( value ) ? value : NULL_STRING ) )""", propertyName )
                        .addStaticImport( Objects.class, "nonNull" )
                        .addStaticImport( CommonConstants.class, "NULL_STRING" )
                        .addStaticImport( StringUtils.class, "format" );
                }
                else
                {
                    builder.addStatement(
                        """
                        joiner.add( format( "$1N = \\\"%1$$s\\\"", $2T.toString( $3L() ) ) )""", propertyName, Objects.class, getterMethod )
                        .addStaticImport( StringUtils.class, "format" );
                }
            }
            builder.endControlFlow();
        }   //  PropertiesLoop:

        //---* Cleanup *-------------------------------------------------------
        if( nonNull( lock) ) builder.endControlFlow();

        builder.addCode(
            """
            
            //---* Create the return value *---------------------------------------
            """
            )
            .addStatement( "final var retValue = joiner.toString()" )
            .addCode( getComposer().createReturnStatement() );

        addMethod( STD_METHOD_ToString, builder.build() );
    }   //  createToString()

    /**
     *  Generates the methods, fields and other code for the given property.
     *
     *  @param  rawProperty    The property specification.
     */
    private final void generateProperty( final PropertySpec rawProperty )
    {
        final var property = rawProperty.merge();

        //---* Create the field *----------------------------------------------
        property.createField( this ).ifPresent( this::addField );

        /*
         * Create the constructor code for the initialisation of the property.
         */
        property.createConstructorFragment( this ).ifPresent( this::addConstructorCode );

        //---* Create the getter *---------------------------------------------
        if( property.getPropertyName().equals( CONFIG_PROPERTY_RESOURCEBUNDLE.getPropertyName() ) )
        {
            property.createGetter( this ).ifPresent( p -> addMethod( STD_METHOD_GetRessourceBundle, p ) );
        }
        else
        {
            property.createGetter( this ).ifPresent( this::addMethod );
        }

        //---* Create the getter *---------------------------------------------
        property.createSetter( this ).ifPresent( this::addMethod );

        //---* Create the 'add' method *---------------------------------------
        property.createAddMethod( this ).ifPresent( this::addMethod );
    }   //  generateProperty()
}
//  class ConfigBeanBuilder

/*
 *  End of File
 */
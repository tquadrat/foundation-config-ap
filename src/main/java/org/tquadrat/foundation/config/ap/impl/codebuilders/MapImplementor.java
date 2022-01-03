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
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_Registry;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.SIMPLIFY_STREAM_API_CALL_CHAIN;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.UNLIKELY_ARG_TYPE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.Objects.nonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.lang.Objects;

/**
 *  <p>{@summary The
 *  {@linkplain CodeBuilder code builder implementation}
 *  for the generation of the code that let the configuration bean
 *  implement the interface
 *  {@link java.util.Map}.}</p>
 *  <p>More precisely, the configuration bean implements
 *  <code>Map&lt;String,Object&gt;</code>, although this is not checked by the
 *  annotation processor.</p>
 *
 *  @version $Id: MapImplementor.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: MapImplementor.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class MapImplementor extends CodeBuilderBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code MapImplementor}.
     *
     *  @param  context The code generator context.
     */
    public MapImplementor( final CodeGeneratorContext context )
    {
        super( context );
    }   //  MapImplementor()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final void build()
    {
        //---* Create the field *----------------------------------------------
        final var objectType = WildcardTypeName.subtypeOf( Object.class );
        final var supplierType = ParameterizedTypeName.from( ClassName.from( Supplier.class ), objectType );
        final var registryType = ParameterizedTypeName.from( ClassName.from( Map.class ), TypeName.from( String.class ), supplierType );
        final var registry = getComposer().fieldBuilder( registryType, STD_FIELD_Registry.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The shadow map for the properties, used for the implementation of the
                {@link Map}
                interface.
                """ )
            .initializer( "new $1T<>()", TreeMap.class )
            .build();
        addField( STD_FIELD_Registry, registry );

        //---* Create the code for the constructor *---------------------------
        final var builder = getComposer().codeBlockBuilder()
            .add( """

                /*
                 * Initialising the shadow map.
                 */
                """ );
        PropertyLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next().merge();


            //---* Create the supplier and add it to the registry *------------
            if( !propertySpec.hasFlag( PropertyFlag.GETTER_IS_DEFAULT ) )
            {
                final var field = propertySpec.getFieldName();
                final var supplier = getComposer().lambdaBuilder()
                    .addCode( "$1N", field )
                    .build();
                builder.addStatement( "$1N.put( $2S, $3L )", registry, propertySpec.getPropertyName(), supplier );
            }
            else
            {
                propertySpec.getGetterMethodName()
                    .ifPresent( method -> builder.addStatement( "$1N.put( $2S, this::$3L )", registry, propertySpec.getPropertyName(), method  ) );
            }
        }
        addConstructorCode( builder.build() );

        //---* Add the methods from Map *--------------------------------------
        final var throwException = getComposer().statementOf( "throw new $1T()", UnsupportedOperationException.class );
        final var inheritDocComment = getComposer().createInheritDocComment();
        final var lock = isSynchronized()
             ? getField( STD_FIELD_ReadLock )
             : null;

        var method = getComposer().methodBuilder( "clear" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( VOID )
            .addJavadoc( inheritDocComment )
            .addCode( throwException )
            .build();
        addMethod( method );

        var arg0 = getComposer().parameterBuilder( Object.class, "key", FINAL )
            .build();
        method = getComposer().methodBuilder( "containsKey" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), UNLIKELY_ARG_TYPE ) )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .returns( BOOLEAN )
            .addJavadoc( inheritDocComment )
            .addStatement( "return $1N.containsKey( $2N )", registry, arg0 )
            .build();
        addMethod( method );

        arg0 = getComposer().parameterBuilder( Object.class, "value", FINAL )
            .build();
        method = getComposer().methodBuilder( "containsValue" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .returns( BOOLEAN )
            .addJavadoc( inheritDocComment )
            .addStatement( "return values().stream().anyMatch( v -> $1T.equals( v, $2N ) )", Objects.class, arg0 )
            .build();
        addMethod( method );

        TypeName returnType = ParameterizedTypeName.from( ClassName.from( Set.class ), ParameterizedTypeName.from( Map.Entry.class, String.class, Object.class ) );
        var methodBuilder = getComposer().methodBuilder( "entrySet" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( returnType )
            .addJavadoc( inheritDocComment )
            .addStatement( "final $1T retValue = new $2T<>()", returnType, HashSet.class );
        if( nonNull( lock) ) methodBuilder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );
        methodBuilder.beginControlFlow(
            """
            for( final var entry : $1N.entrySet() )
            """, registry )
            .addStatement( "final var key = entry.getKey()" )
            .addStatement( "final var value = entry.getValue().get()" )
            .addStatement( "retValue.add( $1T.entry( key, value ) )", Map.class )
            .endControlFlow();
        if( nonNull( lock ) ) methodBuilder.endControlFlow();
        method = methodBuilder.addCode( getComposer().createReturnStatement() )
            .build();
        addMethod( method );

        arg0 = getComposer().parameterBuilder( Object.class, "o", FINAL )
            .build();
        method = getComposer().methodBuilder( "equals" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .returns( BOOLEAN )
            .addJavadoc( inheritDocComment )
            .addStatement( "return this == $1N", arg0 )
            .build();
        addMethod( method );

        arg0 = getComposer().parameterBuilder( Object.class, "key", FINAL )
            .build();
        methodBuilder = getComposer().methodBuilder( "get" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), UNLIKELY_ARG_TYPE ) )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .returns( Object.class )
            .addJavadoc( inheritDocComment )
            .addStatement( "$1T retValue = null", Object.class )
            .addStatement( "final var supplier = $1N.get( $2N )", registry, arg0 )
            .beginControlFlow(
                """
                if( nonNull( supplier) )
                """ )
            .addStaticImport( Objects.class, "nonNull" );
        if( nonNull( lock) ) methodBuilder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );
        methodBuilder.addStatement( "retValue = supplier.get()" );
        if( nonNull( lock ) ) methodBuilder.endControlFlow();
        method = methodBuilder.endControlFlow()
            .addCode( getComposer().createReturnStatement() )
            .build();
        addMethod( method );

        method = getComposer().methodBuilder( "hashCode" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( INT )
            .addJavadoc( inheritDocComment )
            .addStatement( "return $1N.hashCode()", registry )
            .build();
        addMethod( method );

        method = getComposer().methodBuilder( "isEmpty" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( BOOLEAN )
            .addJavadoc( inheritDocComment )
            .addStatement( "return $1N.isEmpty()", registry )
            .build();
        addMethod( method );

        returnType = ParameterizedTypeName.from( Set.class, String.class );
        method = getComposer().methodBuilder( "keySet" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( returnType )
            .addJavadoc( inheritDocComment )
            .addStatement( "return $N.keySet()", registry )
            .build();
        addMethod( method );

        arg0 = getComposer().parameterBuilder( String.class, "key", FINAL )
            .build();
        method = getComposer().methodBuilder( "put" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .addParameter( Object.class, "value", FINAL )
            .returns( Object.class )
            .addJavadoc( inheritDocComment )
            .addCode( throwException )
            .build();
        addMethod( method );

        final TypeName argType = ParameterizedTypeName.from( ClassName.from( Map.class ), WildcardTypeName.subtypeOf( String.class ), WildcardTypeName.subtypeOf( Object.class ) );
        arg0 = getComposer().parameterBuilder( argType, "m", FINAL )
            .build();
        method = getComposer().methodBuilder( "putAll" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .addJavadoc( inheritDocComment )
            .addCode( throwException )
            .build();
        addMethod( method );

        arg0 = getComposer().parameterBuilder( Object.class, "key", FINAL )
            .build();
        method = getComposer().methodBuilder( "remove" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addParameter( arg0 )
            .returns( Object.class )
            .addJavadoc( inheritDocComment )
            .addCode( throwException )
            .build();
        addMethod( method );

        method = getComposer().methodBuilder( "size" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( INT )
            .addJavadoc( inheritDocComment )
            .addStatement( "return $1N.size()", registry )
            .build();
        addMethod( method );

        returnType = ParameterizedTypeName.from( Collection.class, Object.class );
        methodBuilder = getComposer().methodBuilder( "values" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), SIMPLIFY_STREAM_API_CALL_CHAIN ) )
            .returns( returnType )
            .addJavadoc( inheritDocComment )
            .addStatement( "final $1T retValue", returnType );
        if( nonNull( lock) ) methodBuilder.beginControlFlow(
            """
            try( final var ignored = $N.lock() )
            """, lock );
        methodBuilder.addStatement( """
            retValue = $1N.values()
                .stream()
                .map( $2T::get )
                .collect( toUnmodifiableList() )\
            """, registry, Supplier.class )
            .addStaticImport( Collectors.class, "toUnmodifiableList" );
        if( nonNull( lock ) ) methodBuilder.endControlFlow();
        method = methodBuilder.addCode( getComposer().createReturnStatement() )
            .build();
        addMethod( method );
    }   //  build()
}
//  class MapImplementor

/*
 *  End of File
 */
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
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.DEFAULT_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.ENUM_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.LIST_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MAP_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverter;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverterWithType;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_PreferencesNotConfigured;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.SET_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_Accessors;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_PreferenceChangeListener;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_PreferencesRoot;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_UserPreferences;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.USE_OF_CONCRETE_CLASS;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.spi.prefs.PreferenceAccessor;
import org.tquadrat.foundation.config.spi.prefs.PreferencesException;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  that connects the configuration bean to
 *  {@link java.util.prefs.Preferences},
 *  as defined in
 *  {@link org.tquadrat.foundation.config.PreferencesBeanSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: PreferencesBeanBuilder.java 1010 2022-02-05 19:28:36Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: PreferencesBeanBuilder.java 1010 2022-02-05 19:28:36Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class PreferencesBeanBuilder extends CodeBuilderBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code PreferencesBeanBuilder}.
     *
     *  @param  context The code generator context.
     */
    public PreferencesBeanBuilder( final CodeGeneratorContext context )
    {
        super( context );
    }   //  PreferencesBeanBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "IfStatementWithTooManyBranches" )
    @Override
    public final void build()
    {
        //---* Add the field for the name of the preferences root *------------
        final var preferencesRoot = getComposer().fieldBuilder( String.class, STD_FIELD_PreferencesRoot.toString(), PUBLIC, FINAL, STATIC )
            .addJavadoc(
                """
                The name for the Preferences instance: {@value}.
                """ )
            .initializer( "$S", getConfiguration().getPreferencesRoot() )
            .build();
        addField( STD_FIELD_PreferencesRoot, preferencesRoot );

        //---* Create the field for the preferences root node *----------------
        final var userPreference = getComposer().fieldBuilder( Preferences.class, STD_FIELD_UserPreferences.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The Preferences root node.
                """ )
            .build();
        addField( STD_FIELD_UserPreferences, userPreference );

        //---* Add the registry for the accessor instances *-------------------
        final var preferenceAccessorType = ParameterizedTypeName.from( ClassName.from( PreferenceAccessor.class ), WildcardTypeName.subtypeOf( Object.class ) );
        final var registryType = ParameterizedTypeName.from( ClassName.from( Map.class ), TypeName.from( String.class ), preferenceAccessorType );
        final var accessorRegistry = getComposer().fieldBuilder( registryType, STD_FIELD_Accessors.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The registry for the preferences accessors.
                """ )
            .initializer( "new $T<>()", HashMap.class )
            .build();
        addField( STD_FIELD_Accessors, accessorRegistry );

        final var writeLock = getField( STD_FIELD_WriteLock );

        //---* Initialise the field for the preferences root node *------------
        addConstructorCode( getComposer().codeBlockBuilder()
            .add(
                """

                /*
                 * Retrieve the USER Preferences.
                 */
                """ )
            .addStatement( "$N = userRoot().node( $N )", userPreference, preferencesRoot )
            .addStaticImport( Preferences.class, "userRoot" )
            .build()
        );

        //---* Create the method that returns the preference *-----------------
        final var returnType = ParameterizedTypeName.from( Optional.class, Preferences.class );
        final var method = getComposer().methodBuilder( "obtainPreferencesNode" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( returnType )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "return $T.of( $N )", Optional.class, userPreference )
            .build();
        addMethod( method );

        //---* The builder for the code of the loadPreferences() method *------
        final var loadPrefsCodeBuilder = getComposer().codeBlockBuilder()
            .beginControlFlow( """
                try( final var ignore = $N.lock() )
                """, writeLock );

        //---* Add the preference change listener support *--------------------
        final var prefsChangeListener = getConfiguration().getPreferenceChangeListenerClass();
        if( prefsChangeListener.isPresent() )
        {
            //---* Create the field *------------------------------------------
            final var changeListener = getComposer().fieldBuilder( prefsChangeListener.get(), STD_FIELD_PreferenceChangeListener.toString(), PRIVATE )
                .addJavadoc(
                    """
                    The listener for preference changes.
                    """ )
                .addAnnotation( createSuppressWarningsAnnotation( getComposer(), USE_OF_CONCRETE_CLASS ) )
                .initializer( "$L", "null" )
                .build();
            addField( STD_FIELD_PreferenceChangeListener, changeListener );

            /*
             * The listener itself will be instantiated only when
             * loadPreferences() is called the first time.
             */
            loadPrefsCodeBuilder.add(
                    """
                    /*
                     * Create the preference change listener.
                     */
                    """ )
                .beginControlFlow(
                    """
                    if( isNull( $N ) )
                    """, changeListener
                )
                .addStaticImport( Objects.class, "isNull" )
                .addStatement( "$N = new $T( $N, $N )", changeListener, prefsChangeListener.get(), accessorRegistry, writeLock )
                .addStatement( "$N.addPreferenceChangeListener( $N )", userPreference, changeListener )
                .endControlFlow();
        }
        loadPrefsCodeBuilder.add(
                """
                /*
                 * Synchronise the preferences backing store with the memory.
                 */
                """
            )
            .addStatement( "$N.sync()", userPreference )
            .add(
                """
                
                /*
                 * Load the data.
                 */
                """
            );

        //---* The builder for the code of the updatePreferences() method *----
        final var updatePrefsCodeBuilder = getComposer().codeBlockBuilder()
            .beginControlFlow( """
                try( final var ignore = $N.lock() )
                """, writeLock );

        addConstructorCode( getComposer().codeBlockOf( """

            /*
             * Initialise the registry for the preference accessor instances.
             */
            """ ) );

        //---* Process the properties *----------------------------------------
        PropertiesLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next();
            /*
             * Skip the properties that do not have a tie to the preferences.
             */
            if( !propertySpec.hasFlag( ALLOWS_PREFERENCES ) ) continue PropertiesLoop;

            final var name = propertySpec.getPropertyName();
            final var key = propertySpec.getPrefsKey().orElseThrow( () -> new CodeGenerationError( format( MSG_PreferencesNotConfigured, name ) ) );
            final var accessorClass = propertySpec.getPrefsAccessorClass().orElseThrow( () -> new CodeGenerationError( format( MSG_PreferencesNotConfigured, name ) ) );
            final var field = propertySpec.getFieldName();

            //---* Add the code for the Constructor *--------------------------
            final var getter = getComposer().lambdaBuilder()
                .addCode( "$N", field )
                .build();
            final var setter = getComposer().lambdaBuilder()
                .addParameter( "p" )
                .addCode( "$N = p", field )
                .build();
            final var codeBlockBuilder = getComposer().codeBlockBuilder();

            if( accessorClass.equals( ENUM_ACCESSOR_TYPE ) )
            {
                final var propertyType = propertySpec.getPropertyType();
                codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, $4T.class, $5L, $6L ) )", accessorRegistry, key, accessorClass, propertyType, getter, setter );
            }
            else if( accessorClass.equals( LIST_ACCESSOR_TYPE ) || accessorClass.equals( SET_ACCESSOR_TYPE ) )
            {
                final var propertyType = (ParameterizedTypeName) propertySpec.getPropertyType();
                final var argumentType = propertyType.typeArguments().get( 0 );
                final var stringConverterType = getStringConverter( argumentType )
                    .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, name, argumentType.toString() ) ) );
                switch( determineStringConverterInstantiation( stringConverterType, false ) )
                {
                    case BY_INSTANCE -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, $4T.INSTANCE, $5L, $6L ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter );
                    case THROUGH_CONSTRUCTOR -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, new $4T(), $5L, $6L ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter );
                    case AS_ENUM -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, new $4T( $7T), $5L, $6L ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter, propertyType );
                }
            }
            else if( accessorClass.equals( MAP_ACCESSOR_TYPE ) )
            {
                final var propertyType = (ParameterizedTypeName) propertySpec.getPropertyType();
                final var argumentTypes = propertyType.typeArguments();
                final var keyStringConverterType = getStringConverter( argumentTypes.get( 0 ) )
                    .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, name, argumentTypes.get( 0 ).toString() ) ) );
                final var keySnippet =
                    switch( determineStringConverterInstantiation( keyStringConverterType, false ) )
                    {
                        case BY_INSTANCE -> "$4T.INSTANCE";
                        case THROUGH_CONSTRUCTOR -> "new $4T()";
                        case AS_ENUM -> EMPTY_STRING;
                    };
                final var valueStringConverterType = getStringConverter( argumentTypes.get( 1 ) )
                    .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverterWithType, name, argumentTypes.get( 1 ).toString() ) ) );
                final var valueSnippet =
                    switch( determineStringConverterInstantiation( valueStringConverterType, false ) )
                    {
                        case BY_INSTANCE -> "$5T.INSTANCE";
                        case THROUGH_CONSTRUCTOR -> "new $5T()";
                        case AS_ENUM -> EMPTY_STRING;
                    };
                codeBlockBuilder.addStatement( format( "$1N.put( $2S, new $3T<>( $2S, %1$s, %2$s, $6L, $7L ) )", keySnippet, valueSnippet ), accessorRegistry, key, accessorClass, keyStringConverterType, valueStringConverterType, getter, setter );
            }
            else if( accessorClass.equals( DEFAULT_ACCESSOR_TYPE ) )
            {
                final var stringConverterType = propertySpec.getStringConverterClass()
                    .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverter, name ) ) );
                switch( determineStringConverterInstantiation( stringConverterType, propertySpec.isEnum() ) )
                {
                    case BY_INSTANCE -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, $5L, $6L, $4T.INSTANCE ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter );
                    case THROUGH_CONSTRUCTOR -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, $5L, $6L, new $4T() ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter );
                    case AS_ENUM -> codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T<>( $2S, $5L, $6L, new $4T( $7T ) ) )", accessorRegistry, key, accessorClass, stringConverterType, getter, setter, propertySpec.getPropertyType() );
                }
            }
            else
            {
                codeBlockBuilder.addStatement( "$1N.put( $2S, new $3T( $2S, $4L, $5L ) )", accessorRegistry, key, accessorClass, getter, setter );
            }
            addConstructorCode( codeBlockBuilder.build() );

            //---* Add the code for loadPreferences() *------------------------
            loadPrefsCodeBuilder.addStatement( "$N.get( $S ).readPreference( $N )", accessorRegistry, key, userPreference );

            //---* Add the code for updatePreferences() *----------------------
            updatePrefsCodeBuilder.addStatement( "$N.get( $S ).writePreference( $N )", accessorRegistry, key, userPreference );
        }   //  PropertiesLoop:

        //---* Create the loadPreferences() method *---------------------------
        loadPrefsCodeBuilder.nextControlFlow(
            """

            catch( final $T e )
            """, BackingStoreException.class )
            .addStatement( "throw new $T( e )", PreferencesException.class )
            .endControlFlow();
        final var loadPrefsMethod = getComposer().methodBuilder( "loadPreferences" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( VOID )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addCode( loadPrefsCodeBuilder.build() )
            .build();
        addMethod( loadPrefsMethod );

        //---* Create the updatePreferences() method *-------------------------
        updatePrefsCodeBuilder.add( "\n" )
            .addStatement( "$N.flush()", userPreference )
            .nextControlFlow(
                """

                catch( final $T e )
                """, BackingStoreException.class )
            .addStatement( "throw new $T( e )", PreferencesException.class )
            .endControlFlow();
        final var updatePrefsMethod = getComposer().methodBuilder( "updatePreferences" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( VOID )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addCode( updatePrefsCodeBuilder.build() )
            .build();
        addMethod( updatePrefsMethod );
    }   //  build()
}
//  class PreferencesBeanBuilder

/*
 *  End of File
 */
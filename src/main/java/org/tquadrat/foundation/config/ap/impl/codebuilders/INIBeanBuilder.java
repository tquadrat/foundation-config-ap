/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

import static java.lang.String.format;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIGroupMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIKeyMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIPathMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverter;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_INIFILE;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_INIFile;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.THROW_CAUGHT_LOCALLY;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.JavaUtils.composeGetterName;
import static org.tquadrat.foundation.util.Template.hasVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.spi.prefs.PreferencesException;
import org.tquadrat.foundation.inifile.INIFile;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.lang.CommonConstants;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.util.Template;
import org.tquadrat.foundation.util.stringconverter.PathStringConverter;

/**
 *  The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  that connects the configuration bean to
 *  {@link INIFile},
 *  as defined in
 *  {@link org.tquadrat.foundation.config.INIBeanSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: INIBeanBuilder.java 1051 2023-02-26 19:14:46Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: INIBeanBuilder.java 1051 2023-02-26 19:14:46Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class INIBeanBuilder extends CodeBuilderBase
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The various types for the initialisation of the field holding the
     *  backing
     *  {@link Path}
     *  for the
     *  {@link INIFile}
     *  instance.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: INIBeanBuilder.java 1051 2023-02-26 19:14:46Z tquadrat $
     *  @UMLGraph.link
     *  @since 0.1.0
     */
    @ClassVersion( sourceVersion = "$Id: INIBeanBuilder.java 1051 2023-02-26 19:14:46Z tquadrat $" )
    @API( status = INTERNAL, since = "0.1.0" )
    private static enum InitType
    {
        /**
         *  The given filename denotes an absolute path.
         */
        INIT_ABSOLUTE,

        /**
         *  The given filename starts  with {@code $USER} and stands for a path
         *  that is to be resolved against the user's home folder.
         */
        INIT_HOME,

        /**
         *  The given filename starts with <code>${…}</code> and stands for a
         *  path that will be resolved against a property.
         */
        INIT_PROPERTY,

        /**
         *  The given filename will be resolved against the current working
         *  directory.
         */
        INIT_CWD
    }
    //  enum InitType

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code INIBeanBuilder}.
     *
     *  @param  context The code generator context.
     */
    public INIBeanBuilder( final CodeGeneratorContext context )
    {
        super( context );
    }   //  INIBeanBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"OverlyCoupledMethod", "OverlyLongMethod", "OverlyComplexMethod"} )
    @Override
    public final void build()
    {
        //---* Parse the filename for the INIFile *----------------------------
        final InitType initType;
        final Path iniFilePath;
        String propertyName = null;
        final var rawINIFilePath = getConfiguration().getINIFilePath()
            .orElseThrow( () -> new CodeGenerationError( MSG_INIPathMissing ) );

        if( rawINIFilePath.startsWith( "$USER" ) )
        {
            initType = InitType.INIT_HOME;
            iniFilePath = PathStringConverter.INSTANCE.fromString( rawINIFilePath.substring( 5 ) );
        }
        else if( rawINIFilePath.startsWith( "${" ) && hasVariables( rawINIFilePath ) )
        {
            initType = InitType.INIT_PROPERTY;
            final var template = new Template( rawINIFilePath );
            final var variables = new LinkedList<>( template.findVariables() );
            propertyName = variables.get( 0 );
            iniFilePath = PathStringConverter.INSTANCE.fromString( template.replaceVariable( Map.of( propertyName, EMPTY_STRING ) ) );
        }
        else
        {
            iniFilePath = PathStringConverter.INSTANCE.fromString( rawINIFilePath );
            if( iniFilePath.isAbsolute() )
            {
                initType = InitType.INIT_ABSOLUTE;
            }
            else
            {
                initType = InitType.INIT_CWD;
            }
        }

        /*
         * Add the method that retrieves the path for the file that backs the
         * INIFile.
         */
        final var retrievePathBuilder = getComposer().methodBuilder( "retrieveINIFilePath" )
            .addModifiers( PRIVATE, FINAL )
            .addJavadoc(
                """
                Returns the path for the INIFile backing file.
                """, ExceptionInInitializerError.class
            )
            .returns( Path.class,
                """
                The path for the file that backs the {@code INIFile} instance.\
                """ )
            .addCode( switch( initType )
                {
                    case INIT_CWD -> getComposer().codeBlockBuilder()
                        .addStatement( "final var retValue = $1T.of( $2S, $3S ).toAbsolutePath()", Path.class, ".", PathStringConverter.INSTANCE.toString( iniFilePath ) )
                        .build();
                    case INIT_HOME -> getComposer().codeBlockBuilder()
                        .addStatement( "final var retValue = $1T.of( getProperty( PROPERTY_USER_HOME ), $2S ).toAbsolutePath()", Path.class, PathStringConverter.INSTANCE.toString( iniFilePath ) )
                        .addStaticImport( System.class, "getProperty" )
                        .addStaticImport( CommonConstants.class, "PROPERTY_USER_HOME" )
                        .build();
                    case INIT_ABSOLUTE -> getComposer().codeBlockBuilder()
                        .addStatement( "final var retValue = $1T.of( $2S ).toAbsolutePath()", Path.class, PathStringConverter.INSTANCE.toString( iniFilePath ) )
                        .build();
                    case INIT_PROPERTY -> getComposer().codeBlockBuilder()
                        .addStatement( "final var basePath = $1L().toAbsolutePath()", composeGetterName( propertyName ) )
                        .addStatement( "final var retValue = basePath.resolve( $1S ).toAbsolutePath()", PathStringConverter.INSTANCE.toString( iniFilePath ) )
                        .build();
                } )
            .addCode( getComposer().createReturnStatement() );
        final var retrievePathMethod = retrievePathBuilder.build();
        addMethod( retrievePathMethod );

        //---* Add the method that creates the INIFile instance *--------------
        final var createINIFileBuilder = getComposer().methodBuilder( "createINIFile" )
            .addModifiers( PRIVATE, FINAL )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), THROW_CAUGHT_LOCALLY ) )
            .addJavadoc(
                """
                Creates the
                {@link INIFile}
                instance that is connected with this configuration bean.
                
                @throws $T Something went wrong on creating/opening the INI file.\
                """, ExceptionInInitializerError.class
            )
            .returns( INIFile.class,
                """
                The {@code INIFile} instance.\
                """ )
            .addException( ExceptionInInitializerError.class )
            .addStatement( "final $T retValue", INIFile.class )
            .addStatement( "final var path = $N()", retrievePathMethod )
            .beginControlFlow(
                """
                    try
                    """
            );
        if( getConfiguration().getINIFileMustExist() )
        {
            createINIFileBuilder.beginControlFlow(
                    """
                    if( !exists( path ) )
                    """
                )
                .addStaticImport( Files.class, "exists" )
                .addStatement( "throw new $1T( path.toString() )", FileNotFoundException.class )
                .endControlFlow()
                .addStatement( "retValue = $1T.open( path )", INIFile.class );
        }
        else
        {
            final var fileComment = getConfiguration().getINIFileComment();
            if( fileComment.isPresent() )
            {
                createINIFileBuilder.addStatement(
                    """
                    final var isNew = !exists( path )\
                    """ )
                    .addStaticImport( Files.class, "exists" )
                    .addStatement( "retValue = $T.open( path )", INIFile.class )
                    .beginControlFlow(
                        """
                        if( isNew )
                        """ )
                    .addStatement( "retValue.addComment( $S )", fileComment.get() )
                    .endControlFlow();
            }
            else
            {
                createINIFileBuilder.addStatement(
                    """
                    retValue = $1T.open( path )\
                    """, INIFile.class );
            }
        }
        createINIFileBuilder.nextControlFlow(
            """
            
            catch( final $T e )
            """, IOException.class )
            .addStatement( "throw new $T( e )", ExceptionInInitializerError.class )
            .endControlFlow()
            .addCode( "\n" )
            .addComment( "Sets the structure of the INIFile" );
        for( final var group : getConfiguration().getINIGroups().entrySet() )
        {
            createINIFileBuilder.beginControlFlow(
                    """
                    if( !retValue.hasGroup( $S ) )
                    """, group.getKey()
                )
                .addStatement( "retValue.addComment( $S, $S )", group.getKey(), group.getValue() )
                .endControlFlow();
        }
        PropertiesLoop:
        for( var iterator = getConfiguration().propertyIterator(); iterator.hasNext(); )
        {
            final var property = iterator.next();

            if( !property.hasFlag( ALLOWS_INIFILE ) ) continue PropertiesLoop;
            if( property.getINIComment().isEmpty() ) continue PropertiesLoop;
            final var group = property.getINIGroup().orElseThrow( () -> new CodeGenerationError( format( MSG_INIGroupMissing, property.getPropertyName() ) ) );
            final var key = property.getINIKey().orElseThrow( () -> new CodeGenerationError( format( MSG_INIKeyMissing, property.getPropertyName() ) ) );
            final var comment = property.getINIComment().orElseThrow();
            createINIFileBuilder.beginControlFlow(
                    """
                     if( !retValue.hasValue( $S, $S ) )
                     """, group, key
                )
                .addStatement( "retValue.addComment( $S, $S, $S )", group, key, comment )
                .endControlFlow();
        }   //  PropertiesLoop:

        final var createINIFile = createINIFileBuilder.addCode( getComposer().createReturnStatement() )
            .build();
        addMethod( createINIFile );

        //---* Add the field for the INI file *--------------------------------
        final var iniFileClass = ParameterizedTypeName.from( Lazy.class, INIFile.class );
        final var iniFile = getComposer().fieldBuilder( iniFileClass, STD_FIELD_INIFile.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The INIFile instance that is used by this configuration bean to
                persist (some of) its properties.
                """
            )
            .build();
        addField( STD_FIELD_INIFile, iniFile );

        //---* Initialise the INI file *---------------------------------------
        final var constructorCode = getComposer().codeBlockBuilder()
            .add(
                """
                    
                //---* Initialise the INI file *----------------------------------------
                """
            )
            .addStatement( "$1N = $2T.use( this::$3N )", iniFile, Lazy.class, createINIFile )
            .build();
        addConstructorCode( constructorCode );

        //---* Create the method that returns the INIFile *--------------------
        final var returnType = ParameterizedTypeName.from( Optional.class, INIFile.class );
        final var method = getComposer().methodBuilder( "obtainINIFile" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( returnType )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "return $T.of( $N.get() )", Optional.class, iniFile )
            .build();
        addMethod( method );

        //---* The builder for the code of the loadINIFile() method *----------
        final var loadCodeBuilder = getComposer().codeBlockBuilder();
        if( isSynchronized() )
        {
            loadCodeBuilder.beginControlFlow(
                """
                try( final var ignore = $N.lock() )
                """, getField( STD_FIELD_WriteLock ) );
        }
        else
        {
            loadCodeBuilder.beginControlFlow(
                """
                try
                """ );
        }
        loadCodeBuilder.addStatement( "final var iniFile = $1N.get()", iniFile )
            .addStatement( "iniFile.refresh()" )
            .add(
                """
                
                /*
                 * Load the data.
                 */
                """
            );

        //---* The builder for the code of the updateINIFile() method *--------
        final var updateCodeBuilder = getComposer().codeBlockBuilder();
        if( isSynchronized() )
        {
            updateCodeBuilder.beginControlFlow(
                """
                try( final var ignore = $N.lock() )
                """, getField( STD_FIELD_ReadLock ) );
        }
        else
        {
            updateCodeBuilder.beginControlFlow(
                """
                try
                """ );
        }
        updateCodeBuilder.addStatement( "final var iniFile = $1N.get()", iniFile )
            .add(
            """
            
            /*
             * Write the data.
             */
            """
        );

        //---* Process the properties *----------------------------------------
        PropertiesLoop:
        for( final var iterator = getProperties(); iterator.hasNext(); )
        {
            final var propertySpec = iterator.next();
            /*
             * Skip the properties that do not have a tie to the INI file.
             */
            if( !propertySpec.hasFlag( ALLOWS_INIFILE ) ) continue PropertiesLoop;

            final var name = propertySpec.getPropertyName();
            final var group = propertySpec.getINIGroup().orElseThrow( () -> new CodeGenerationError( format( MSG_INIGroupMissing, name ) ) );
            final var key = propertySpec.getINIKey().orElseThrow( () -> new CodeGenerationError( format( MSG_INIKeyMissing, name ) ) );
            final var field = propertySpec.getFieldName();
            final var stringConverterType = propertySpec.getStringConverterClass()
                .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingStringConverter, name ) ) );
            final var stringConverterCode =
                switch( determineStringConverterInstantiation( stringConverterType, propertySpec.isEnum() ) )
                {
                    case BY_INSTANCE -> getComposer().statementOf( "final var stringConverter = $T.INSTANCE", stringConverterType );
                    case THROUGH_CONSTRUCTOR -> getComposer().statementOf( "final var stringConverter = new $T()", stringConverterType );
                    case AS_ENUM -> getComposer().statementOf( "final var stringConverter = new $1T( $2T.class )", stringConverterType, propertySpec.getPropertyType() );
                };

            //---* Load the value *--------------------------------------------
            loadCodeBuilder.beginControlFlow( EMPTY_STRING )
                .add( stringConverterCode )
                .addStatement( "$1N = iniFile.getValue( $2S, $3S, stringConverter ).orElse( $1N )", field, group, key )
                .endControlFlow();

            //---* Write the value *-------------------------------------------
            updateCodeBuilder.beginControlFlow( EMPTY_STRING )
                .add( stringConverterCode )
                .addStatement( "iniFile.setValue( $2S, $3S, $1N, stringConverter )", field, group, key )
                .endControlFlow();
        }   //  PropertiesLoop:

        //---* Create the loadINIFile() method *-------------------------------
        loadCodeBuilder.nextControlFlow(
            """

            catch( final $T e )
            """, IOException.class )
            .addStatement( "throw new $T( e )", PreferencesException.class )
            .endControlFlow();
        final var loadMethod = getComposer().methodBuilder( "loadINIFile" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( VOID )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addCode( loadCodeBuilder.build() )
            .build();
        addMethod( loadMethod );

        //---* Create the updateINIFile() method *-----------------------------
        updateCodeBuilder.add( "\n" )
            .addStatement( "iniFile.save()" )
            .nextControlFlow(
                """

                catch( final $T e )
                """, IOException.class )
            .addStatement( "throw new $T( e )", PreferencesException.class )
            .endControlFlow();
        final var updateMethod = getComposer().methodBuilder( "updateINIFile" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( VOID )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addCode( updateCodeBuilder.build() )
            .build();
        addMethod( updateMethod );
    }   //  build()
}
//  class INIBeanBuilder

/*
 *  End of File
 */
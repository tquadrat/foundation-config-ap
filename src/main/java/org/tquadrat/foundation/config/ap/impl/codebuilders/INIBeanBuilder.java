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
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIGroupMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIKeyMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_INIPathMissing;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingStringConverter;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_INIFILE;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_INIFile;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_INIFileName;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.FIELD_CAN_BE_LOCAL;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.THROW_CAUGHT_LOCALLY;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.spi.prefs.PreferencesException;
import org.tquadrat.foundation.inifile.INIFile;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.lang.Objects;
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
 *  @version $Id: INIBeanBuilder.java 947 2021-12-23 21:44:25Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: INIBeanBuilder.java 947 2021-12-23 21:44:25Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class INIBeanBuilder extends CodeBuilderBase
{
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
    @Override
    public final void build()
    {
        //---* Add the method that creates the INIFile instance *--------------
        final var pathParameter = getComposer().parameterBuilder( Path.class, "path", FINAL )
            .addJavadoc(
                """
                The path for the file that backs the {@code INIFile}.\
                """
            )
            .build();
        final var createINIFileBuilder = getComposer().methodBuilder( "createINIFile" )
            .addModifiers( PRIVATE, FINAL, STATIC )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), THROW_CAUGHT_LOCALLY ) )
            .addJavadoc(
                """
                Creates the
                {@link INIFile}
                instance that is connected with this configuration bean.
                
                @throws $T Something went wrong on creating/opening the INI file.\
                """, ExceptionInInitializerError.class
            )
            .addParameter( pathParameter )
            .returns( INIFile.class,
                """
                The {@code INIFile} instance.\
                """ )
            .addException( ExceptionInInitializerError.class )
            .addStatement( "final $T retValue", INIFile.class )
            .beginControlFlow(
                """
                    try
                    """
            );
        if( getConfiguration().getINIFileMustExist() )
        {
            createINIFileBuilder.beginControlFlow(
                    """
                    if( !exists( requireNonNullArgument( $1N, "$1N" ) ) )
                    """, pathParameter
                )
                .addStaticImport( Files.class, "exists" )
                .addStaticImport( Objects.class, "requireNonNullArgument" )
                .addStatement( "throw new $T( $N.toString() )", FileNotFoundException.class, pathParameter )
                .endControlFlow()
                .addStatement( "retValue = $T.open( $N )", INIFile.class, pathParameter );
        }
        else
        {
            final var fileComment = getConfiguration().getINIFileComment();
            if( fileComment.isPresent() )
            {
                createINIFileBuilder.addStatement(
                    """
                    final var isNew = !exists( requireNonNullArgument( $1N, "$1N" ) )\
                    """, pathParameter )
                    .addStaticImport( Files.class, "exists" )
                    .addStaticImport( Objects.class, "requireNonNullArgument" )
                    .addStatement( "retValue = $T.open( $N )", INIFile.class, pathParameter )
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
                    retValue = $1T.open( requireNonNullArgument( $2N, "$2N" ) )\
                    """, INIFile.class, pathParameter )
                    .addStaticImport( Objects.class, "requireNonNullArgument" );
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
        //noinspection ForLoopWithMissingComponent
        for( var i = getConfiguration().propertyIterator(); i.hasNext(); )
        {
            final var property = i.next();

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

        //---* Add the field for the name of the INI configuration file *------
        final var iniFileName = getComposer().fieldBuilder( Path.class, STD_FIELD_INIFileName.toString(), PRIVATE, FINAL )
            .addJavadoc(
                """
                The file that backs the INIFile used by this configuration bean.
                """
            )
            .addAnnotation( createSuppressWarningsAnnotation( getComposer(), FIELD_CAN_BE_LOCAL ) )
            .initializer( "$T.INSTANCE.fromString( $S )", PathStringConverter.class, getConfiguration().getINIFilePath()
                .map( PathStringConverter.INSTANCE::toString )
                .orElseThrow( () -> new CodeGenerationError( MSG_INIPathMissing ) ) )
            .build();
        addField( STD_FIELD_INIFileName, iniFileName );

        //---* Add the field for the INI file *--------------------------------
        final var iniFile = getComposer().fieldBuilder( INIFile.class, STD_FIELD_INIFile.toString(), PRIVATE, FINAL )
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
            .addStatement( "$1N = $3N( $2N )", iniFile, iniFileName, createINIFile )
            .build();
        addConstructorCode( constructorCode );

        //---* Create the method that returns the INIFile *--------------------
        final var returnType = ParameterizedTypeName.from( Optional.class, INIFile.class );
        final var method = getComposer().methodBuilder( "obtainINIFile" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .returns( returnType )
            .addJavadoc( getComposer().createInheritDocComment() )
            .addStatement( "return $T.of( $N )", Optional.class, iniFile )
            .build();
        addMethod( method );

        //---* The builder for the code of the loadINIFile() method *----------
        final var loadCodeBuilder = getComposer().codeBlockBuilder()
            .beginControlFlow( """
                try( final var ignore = $N.lock() )
                """, getField( STD_FIELD_WriteLock ) )
            .addStatement( "$N.refresh()", iniFile )
            .add(
                """
                
                /*
                 * Load the data.
                 */
                """
            );

        //---* The builder for the code of the updateINIFile() method *--------
        final var updateCodeBuilder = getComposer().codeBlockBuilder()
            .beginControlFlow( """
                try( final var ignore = $N.lock() )
                """, getField( STD_FIELD_ReadLock ) )
            .add(
                """
                /*
                 * Write the data.
                 */
                """
            );

        //---* Process the properties *----------------------------------------
        PropertiesLoop:
        //noinspection ForLoopWithMissingComponent
        for( final var i = getProperties(); i.hasNext(); )
        {
            final var propertySpec = i.next();
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
            final var stringConverterCode = determineStringConverterInstantiation( stringConverterType )
                ? getComposer().statementOf( "final var stringConverter = $T.INSTANCE", stringConverterType )
                : getComposer().statementOf( "final var stringConverter = new $T()", stringConverterType );

            //---* Load the value *--------------------------------------------
            loadCodeBuilder.beginControlFlow( EMPTY_STRING )
                .add( stringConverterCode )
                .addStatement( "$1N = $2N.getValue( $3S, $4S, stringConverter ).orElse( $1N )", field, iniFile, group, key )
                .endControlFlow();

            //---* Write the value *-------------------------------------------
            updateCodeBuilder.beginControlFlow( EMPTY_STRING )
                .add( stringConverterCode )
                .addStatement( "$2N.setValue( $3S, $4S, $1N, stringConverter )", field, iniFile, group, key )
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
            .addStatement( "$N.save()", iniFile )
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
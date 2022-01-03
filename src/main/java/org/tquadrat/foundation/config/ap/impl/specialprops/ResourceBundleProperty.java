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

package org.tquadrat.foundation.config.ap.impl.specialprops;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RESOURCEBUNDLE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoBaseBundleName;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_ResourceBundleWrongReturnType;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.EXEMPT_FROM_TOSTRING;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_RETURNS_OPTIONAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_REQUIRES_SYNCHRONIZATION;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ReadLock;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ResourceLocale;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_WriteLock;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.FIELD_MAY_BE_FINAL;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.MountPoint;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.I18nSupport;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The implementation of
 *  {@link SpecialPropertySpecBase}
 *  for
 *  {@link SpecialPropertyType#CONFIG_PROPERTY_RESOURCEBUNDLE}.
 *
 *  @version $Id: ResourceBundleProperty.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: ResourceBundleProperty.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class ResourceBundleProperty extends SpecialPropertySpecBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ResourceBundleProperty}.
     */
    public ResourceBundleProperty()
    {
        super( CONFIG_PROPERTY_RESOURCEBUNDLE, EXEMPT_FROM_TOSTRING, GETTER_RETURNS_OPTIONAL, PROPERTY_IS_MUTABLE );
    }   //  ResourceBundleProperty()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  The implementation of the method that composes a field for the
     *  given property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "StaticMethodOnlyUsedInOneClass", "TypeMayBeWeakened"} )
    private static FieldSpec composeField( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        final var builder = composer.fieldBuilder( property.getPropertyType(), property.getFieldName(), PRIVATE )
            .addJavadoc(
                """
                Special Property: &quot;$L&quot;.
                """, property.getPropertyName() )
            .initializer( "null" );

        if( codeBuilder.getConfiguration().getInitDataMethod().isEmpty() && !codeBuilder.getConfiguration().implementInterface( I18nSupport.class ) && property.getSetterMethodName().isEmpty() )
        {
            builder.addAnnotation( createSuppressWarningsAnnotation( codeBuilder.getComposer(), FIELD_MAY_BE_FINAL ) );
        }

        //---* Create the return value *--------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeField()

    /**
     *  <p>{@summary The implementation of the method that composes a getter for the given
     *  property.}</p>
     *  <p>The implementation details depend from the usage: with
     *  {@link I18nSupport},
     *  it is a bit more complex.</p>
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The method specification.
     */
    @SuppressWarnings( {"OptionalGetWithoutIsPresent", "TypeMayBeWeakened", "UseOfConcreteClass"} )
    private static final MethodSpec composeGetter( final CodeBuilder codeBuilder, final PropertySpecImpl property )
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

        //---* Create the body *-----------------------------------------------
        if( codeBuilder.getConfiguration().implementInterface( I18nSupport.class ) )
        {
            if( !property.hasFlag( GETTER_RETURNS_OPTIONAL ) ) throw new CodeGenerationError( MSG_ResourceBundleWrongReturnType );
            final var baseBundleName = codeBuilder.getConfiguration().getBaseBundleName()
                .orElseThrow( () -> new CodeGenerationError( MSG_NoBaseBundleName ) );

            /*
             * I18nSupport does not allow a setter for the resource bundle, so
             * we do not need the locking for tha access to the resource
             * bundle. But it may be necessary for the update.
             */
            final var lock = property.hasFlag( PROPERTY_REQUIRES_SYNCHRONIZATION )
                 ? codeBuilder.getField( STD_FIELD_WriteLock )
                 : null;
            builder.addStatement( "$1T bundle = null", ResourceBundle.class )
                .addStatement( "final var currentLocale = getLocale()" )
                .beginControlFlow(
                    """
                    if( currentLocale.equals( $1N ) )
                    """, STD_FIELD_ResourceLocale.toString() )
                .addStatement( "bundle = $1N", property.getFieldName() )
                .endControlFlow()
                .beginControlFlow(
                    """
                    if( isNull( bundle ) )
                    """ )
                .addStaticImport( Objects.class, "isNull" );
            if( nonNull( lock) )
            {
                builder.beginControlFlow(
                """
                    try( final var ignored = $N.lock() )
                    """, lock );
            }
            else
            {
                builder.addStatement(
                    """
                    try
                    """ );
            }
            builder.addStatement( "bundle = $1T.getBundle( $2L, currentLocale )", ResourceBundle.class, baseBundleName )
                .addStatement( "$1N = bundle", property.getFieldName() )
                .addStatement( "$1N = currentLocale", STD_FIELD_ResourceLocale.toString() )
                .nextControlFlow(
                    """

                    catch( @$1T( "unused" ) final $2T e )
                    """, SuppressWarnings.class, MissingResourceException.class )
                .addCode( """
                    /* Deliberately ignored */
                    """ )
                .endControlFlow()
                .endControlFlow()
                .addStatement( "final var retValue = $T.ofNullable( bundle )", Optional.class )
                .addCode( codeBuilder.getComposer().createReturnStatement() );
        }
        else
        {
            //---* Add the locking *-------------------------------------------
            final var lock = property.hasFlag( PROPERTY_REQUIRES_SYNCHRONIZATION ) && property.hasFlag( PROPERTY_IS_MUTABLE )
                 ? codeBuilder.getField( STD_FIELD_ReadLock )
                 : null;
            if( nonNull( lock) ) builder.beginControlFlow(
                """
                try( final var ignored = $N.lock() )
                """, lock );

            //---* Return the value *------------------------------------------
            if( property.hasFlag( GETTER_RETURNS_OPTIONAL ) )
            {
                builder.addStatement( "return $1T.ofNullable( $2N )", Optional.class, property.getFieldName() );
            }
            else
            {
                builder.addStatement( "return $1N", property.getFieldName() );
            }

            //---* Cleanup *---------------------------------------------------
            if( nonNull( lock) ) builder.endControlFlow();
        }

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeGetter()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getCLIValueHandlerClass() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getGetterComposer() { return Optional.of( ResourceBundleProperty::composeGetter ); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<BiFunction<CodeBuilder,PropertySpecImpl, FieldSpec>> getFieldComposer()
    {
        return Optional.of( ResourceBundleProperty::composeField );
    }   //  getFieldComposer()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getGetterReturnType()
    {
        final var rawType = ClassName.from( Optional.class );
        final var retValue = ParameterizedTypeName.from( rawType, getPropertyType() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getGetterReturnType()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return ClassName.from( ResourceBundle.class ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.empty(); }
}
//  class ResourceBundleProperty

/*
 *  End of File
 */
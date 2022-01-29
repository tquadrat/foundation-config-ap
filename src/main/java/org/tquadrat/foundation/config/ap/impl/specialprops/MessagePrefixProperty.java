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

package org.tquadrat.foundation.config.ap.impl.specialprops;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_MESSAGEPREFIX;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoMessagePrefix;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.EXEMPT_FROM_TOSTRING;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_ON_MAP;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.Optional;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.MountPoint;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  The implementation of
 *  {@link SpecialPropertySpecBase}
 *  for
 *  {@link SpecialPropertyType#CONFIG_PROPERTY_MESSAGEPREFIX}.
 *
 *  @version $Id: ProcessIdProperty.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: ProcessIdProperty.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class MessagePrefixProperty extends SpecialPropertySpecBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ProcessIdProperty}.
     */
    public MessagePrefixProperty()
    {
        super( CONFIG_PROPERTY_MESSAGEPREFIX, EXEMPT_FROM_TOSTRING, GETTER_ON_MAP );
    }   //  ProcessIdProperty()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary The implementation of the method that composes a getter
     *  for the given property.}</p>
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The method specification.
     */
    @SuppressWarnings( {"OptionalGetWithoutIsPresent", "TypeMayBeWeakened", "UseOfConcreteClass"} )
    private static final MethodSpec composeGetter( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        /*
         * This is the value from the String constant holding the message
         * prefix, not the name of that field!
         */
        final var messagePrefix = codeBuilder.getConfiguration().getMessagePrefix()
            .orElseThrow( () -> new CodeGenerationError( MSG_NoMessagePrefix ) );

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
        builder.addStatement( "return $1S", messagePrefix );

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
    public final Optional<BiFunction<CodeBuilder,PropertySpecImpl, FieldSpec>> getFieldComposer() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getGetterComposer() { return Optional.of( MessagePrefixProperty::composeGetter ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getGetterReturnType() { return getPropertyType(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return TypeName.from( String.class ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.empty(); }
}
//  class MessagePrefixProperty

/*
 *  End of File
 */
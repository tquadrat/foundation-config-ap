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
import static javax.lang.model.element.Modifier.PRIVATE;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_PID;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.util.Optional;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.util.SystemUtils;

/**
 *  The implementation of
 *  {@link SpecialPropertySpecBase}
 *  for
 *  {@link SpecialPropertyType#CONFIG_PROPERTY_PID}.
 *
 *  @version $Id: ProcessIdProperty.java 1001 2022-01-29 16:42:15Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: ProcessIdProperty.java 1001 2022-01-29 16:42:15Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class ProcessIdProperty extends SpecialPropertySpecBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code ProcessIdProperty}.
     */
    public ProcessIdProperty()
    {
        super( CONFIG_PROPERTY_PID );
    }   //  ProcessIdProperty()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Composes the constructor fragment for the initialisation of this
     *  property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"TypeMayBeWeakened", "UseOfConcreteClass"} )
    private static final CodeBlock composeConstructorFragment( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var builder = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer()
            .codeBlockBuilder()
            .add(
                """
                
                /*
                 * Initialise the property '$N'.
                 */
                """, property.getPropertyName()
            )
            .addStatement( "$1N = getPID()", property.getFieldName() )
            .addStaticImport( SystemUtils.class, "getPID" );

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeConstructorFragment()

    /**
     *  The method that composes a field for the 'processId' property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @param  property    The property.
     *  @return The field specification.
     */
    @SuppressWarnings( {"TypeMayBeWeakened", "UseOfConcreteClass"} )
    private static FieldSpec composeField( final CodeBuilder codeBuilder, final PropertySpecImpl property )
    {
        final var composer = requireNonNullArgument( codeBuilder, "codeBuilder" ).getComposer();

        final var builder = composer.fieldBuilder( property.getPropertyType(), property.getFieldName(), PRIVATE )
            .addJavadoc(
                """
                Special Property: &quot;$L&quot;.
                """, property.getPropertyName() )
            .addModifiers( FINAL );

        //---* Create the return value *--------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeField()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getCLIValueHandlerClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<BiFunction<CodeBuilder, PropertySpecImpl, CodeBlock>> getConstructorFragmentComposer() { return Optional.of( ProcessIdProperty::composeConstructorFragment );}

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<BiFunction<CodeBuilder,PropertySpecImpl, FieldSpec>> getFieldComposer() { return Optional.of( ProcessIdProperty::composeField ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return TypeName.from( long.class ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.empty(); }
}
//  class ProcessIdProperty

/*
 *  End of File
 */
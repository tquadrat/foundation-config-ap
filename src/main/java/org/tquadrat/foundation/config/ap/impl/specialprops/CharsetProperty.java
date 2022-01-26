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

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CHARSET;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ALLOWS_PREFERENCES;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SETTER_CHECK_NULL;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.config.cli.SimpleCmdLineValueHandler;
import org.tquadrat.foundation.config.spi.prefs.SimplePreferenceAccessor;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.util.stringconverter.CharsetStringConverter;

/**
 *  The implementation of
 *  {@link SpecialPropertySpecBase}
 *  for
 *  {@link SpecialPropertyType#CONFIG_PROPERTY_CHARSET}.
 *
 *  @version $Id: CharsetProperty.java 997 2022-01-26 14:55:05Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: CharsetProperty.java 997 2022-01-26 14:55:05Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class CharsetProperty extends SpecialPropertySpecBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code CharsetProperty}.
     */
    public CharsetProperty()
    {
        super( CONFIG_PROPERTY_CHARSET, ALLOWS_PREFERENCES, PROPERTY_IS_MUTABLE, SETTER_CHECK_NULL );
    }   //  CharsetProperty()

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
    @SuppressWarnings( "TypeMayBeWeakened" )
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
            .addStatement( "$1N = defaultCharset()", property.getFieldName() )
            .addStaticImport( Charset.class, "defaultCharset" );

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  composeConstructorFragment()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getCLIValueHandlerClass() { return Optional.of( ClassName.from( SimpleCmdLineValueHandler.class ) ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<BiFunction<CodeBuilder, PropertySpecImpl, CodeBlock>> getConstructorFragmentComposer() { return Optional.of( CharsetProperty::composeConstructorFragment );}

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.of( ClassName.from( SimplePreferenceAccessor.class ) ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return ClassName.from( Charset.class ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.of( ClassName.from( CharsetStringConverter.class ) ); }
}
//  class CharsetProperty

/*
 *  End of File
 */
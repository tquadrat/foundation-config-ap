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
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_SESSION;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_SessionPropertyMissing;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.lang.Objects;

/**
 *  <p>{@summary The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  for the generation of the code that let the configuration bean
 *  implement the interface
 *  {@link org.tquadrat.foundation.config.SessionBeanSpec}.}</p>
 *
 *  @version $Id: SessionBeanBuilder.java 933 2021-07-03 13:32:17Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SessionBeanBuilder.java 933 2021-07-03 13:32:17Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class SessionBeanBuilder extends CodeBuilderBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code SessionBeanBuilder}.
     *
     *  @param  context The code generator context.
     */
    public SessionBeanBuilder( final CodeGeneratorContext context )
    {
        super( context );
    }   //  SessionBeanBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final void build()
    {
        //---* Get the property *----------------------------------------------
        final var propertySpec = getConfiguration().getProperty( CONFIG_PROPERTY_SESSION.getPropertyName() )
            .map( PropertySpec::merge )
            .orElseThrow( () -> new CodeGenerationError( MSG_SessionPropertyMissing ) );
        final var field = propertySpec.getFieldName();

        //---* Add the constructor argument *----------------------------------
        final var argument = getComposer().parameterBuilder( propertySpec.getPropertyType(), "sessionKey", FINAL )
            .addJavadoc( "The session key." )
            .build();
        addConstructorArgument( argument );

        //---* Create the code for the constructor *---------------------------
        final var code = getComposer().codeBlockBuilder()
            .add( """

                /*
                 * Set the session key.
                 */
                """ )
            .addStatement( "$1N = requireNotEmptyArgument( $2N, $2S )", field, argument.name() )
            .addStaticImport( Objects.class, "requireNotEmptyArgument" )
            .build();
        addConstructorCode( code );
    }   //  build()
}
//  class SessionBeanBuilder

/*
 *  End of File
 */
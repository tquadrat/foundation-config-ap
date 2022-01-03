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
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_GetterMustBeDEFAULT;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingGetter;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_NoMessagePrefix;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardField.STD_FIELD_ResourceLocale;
import static org.tquadrat.foundation.config.ap.impl.CodeBuilder.StandardMethod.STD_METHOD_GetMessagePrefix;
import static org.tquadrat.foundation.util.StringUtils.decapitalize;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.Locale;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag;

/**
 *  <p>{@summary The
 *  {@linkplain org.tquadrat.foundation.config.ap.impl.CodeBuilder code builder implementation}
 *  for the generation of the code that let the configuration bean
 *  implement the interface
 *  {@link org.tquadrat.foundation.config.I18nSupport}.}</p>
 *
 *  @version $Id: I18nSupportBuilder.java 933 2021-07-03 13:32:17Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: I18nSupportBuilder.java 933 2021-07-03 13:32:17Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class I18nSupportBuilder extends CodeBuilderBase
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code I18nSupportBuilder}.
     *
     *  @param  context The code generator context.
     */
    public I18nSupportBuilder( final CodeGeneratorContext context )
    {
        super( context );
    }   //  I18nSupportBuilder()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final void build()
    {
        final var configuration = getConfiguration();
        final var messagePrefix = configuration.getMessagePrefix().orElseThrow( () -> new CodeGenerationError( MSG_NoMessagePrefix ) );

        /*
         * Create the field that tracks the locale for the current resource
         * bundle.
         */
        final var resourceBundleLocaleField = getComposer().fieldBuilder( Locale.class, STD_FIELD_ResourceLocale.toString(), PRIVATE )
            .addJavadoc( """
                The
                {@link Locale}
                for the currently loaded
                {@link ResourceBundle}.

                @see #getResourceBundle()
                """ )
            .initializer( "$1L", "null" )
            .build();
        addField( STD_FIELD_ResourceLocale, resourceBundleLocaleField );

        //---* Overwrite getMessagePrefix() *----------------------------------
        final var propertyName = decapitalize( STD_METHOD_GetMessagePrefix.toString().substring( 3 ) );
        final var propertySpec = configuration.getProperty( propertyName )
            .orElseThrow( () -> new CodeGenerationError( format( MSG_MissingGetter, propertyName ) ) );
        if( !propertySpec.hasFlag( PropertyFlag.GETTER_IS_DEFAULT ) )
        {
            throw new CodeGenerationError( MSG_GetterMustBeDEFAULT );
        }

        final var method = getComposer().methodBuilder( STD_METHOD_GetMessagePrefix.toString() )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( Override.class )
            .addJavadoc( getComposer().createInheritDocComment() )
            .returns( String.class )
            .addStatement( "return $1N", messagePrefix )
            .build();
        addMethod( STD_METHOD_GetMessagePrefix, method );
    }   //  build()
}
//  class I18nSupportBuilder

/*
 *  End of File
 */
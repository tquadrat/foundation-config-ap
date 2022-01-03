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

package org.tquadrat.foundation.config.ap;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.Serial;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  This error will be thrown in cases a configuration bean property has an
 *  inappropriate type, like
 *  {@link java.io.InputStream}
 *  or
 *  {@link java.util.stream.Stream}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: InappropriateTypeError.java 918 2021-05-15 15:35:03Z tquadrat $
 *  @since 0.0.1
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: InappropriateTypeError.java 918 2021-05-15 15:35:03Z tquadrat $" )
@API( status = STABLE, since = "0.0.1" )
public final class InappropriateTypeError extends Error
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The error message: {@value}.
     */
    public static final String MSG_InappropriateType = "The type '%s' is not appropriate for a ConfigBean property.";

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The serial version UID for objects of this class: {@value}.
     *
     *  @hidden
     */
    @Serial
    private static final long serialVersionUID = 1L;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code InappropriateTypeError} instance.
     *
     *  @param  message The message for the error.
     */
    public InappropriateTypeError( final String message )
    {
        super( message );
    }   //  InappropriateTypeError()

    /**
     *  Creates a new {@code InappropriateTypeError} instance.
     *
     *  @param  type    The inappropriate type.
     */
    public InappropriateTypeError( final Class<?> type )
    {
        this( format( MSG_InappropriateType, requireNonNullArgument( type, "type" ).getName() ) );
    }   //  InappropriateTypeError()

    /**
     *  Creates a new {@code InappropriateTypeError} instance.
     *
     *  @param  type    The inappropriate type.
     */
    public InappropriateTypeError( final TypeName type )
    {
        this( format( MSG_InappropriateType, requireNonNullArgument( type, "type" ).toString() ) );
    }   //  InappropriateTypeError()
}
//  class InappropriateTypeError

/*
 *  End of File
 */
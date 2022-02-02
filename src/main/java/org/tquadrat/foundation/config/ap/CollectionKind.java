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

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.LIST_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MAP_ACCESSOR_TYPE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.SET_ACCESSOR_TYPE;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 * The kind of collection for a property type that is a collection.
 *
 * @version $Id: CollectionKind.java 1002 2022-02-01 21:33:00Z tquadrat $
 * @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 * @UMLGraph.link
 * @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: CollectionKind.java 1002 2022-02-01 21:33:00Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public enum CollectionKind
{
        /*------------------*\
    ====** Enum Declaration **=================================================
        \*------------------*/
    /**
     * The type is a List.
     */
    LIST( LIST_ACCESSOR_TYPE ),

    /**
     * The type is a Map.
     */
    MAP( MAP_ACCESSOR_TYPE ),

    /**
     * The type is a Set.
     */
    SET( SET_ACCESSOR_TYPE ),

    /**
     * The type is not a collection at all.
     */
    NO_COLLECTION( null );

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     * The type name for the accessor.
     */
    private final TypeName m_AccessorType;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     * Creates a new {@code CollectionKind} instance.
     *
     * @param accessor The type name for the accessor.
     */
    private CollectionKind( final TypeName accessor )
    {
        m_AccessorType = accessor;
    }   //  CollectionKind()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * Returns the accessor type for the collection kind.
     *
     * @return The accessor type.
     */
    public final TypeName getAccessorType() { return m_AccessorType; }
}
//  enum CollectionKind

/*
 *  End of File
 */
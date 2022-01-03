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
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.JavaUtils.loadClass;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 * The kind of collection for a property type that is a collection.
 *
 * @version $Id: CollectionKind.java 919 2021-05-22 19:40:36Z tquadrat $
 * @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 * @UMLGraph.link
 * @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: CollectionKind.java 919 2021-05-22 19:40:36Z tquadrat $" )
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
     * Determines the type of collection for the given type.
     *
     * @param type The type.
     * @return The collection kind.
     */
    public static final CollectionKind determine( final TypeName type )
    {
        final var retValue =
            isList( requireNonNullArgument( type, "type" ) ) ? LIST
            : isMap( type ) ? MAP
            : isSet( type ) ? SET
            : NO_COLLECTION;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  determine()

    /**
     * Returns the accessor type for the collection kind.
     *
     * @return The accessor type.
     */
    public final TypeName getAccessorType() { return m_AccessorType; }

    /**
     *  Determines whether the given instance of
     *  {@link TypeName}
     *  is for an implementation of
     *  {@link List}.
     *
     *  @param  typeName    The type name to check.
     *  @return {@code true} if the type name is for a {@code List}
     *      implementation, {@code false} otherwise.
     */
    public static final boolean isList( final TypeName typeName )
    {
        var retValue = !(requireNonNullArgument( typeName, "typeName" ).isPrimitive() || typeName.isBoxedPrimitive() || (typeName instanceof ArrayTypeName));
        if( retValue )
        {
            final ClassName className;
            if( typeName instanceof ParameterizedTypeName )
            {
                className = ((ParameterizedTypeName) typeName).rawType();
            }
            else
            {
                className = (ClassName) typeName;
            }
            retValue = loadClass( className.canonicalName() )
                .filter( List.class::isAssignableFrom )
                .isPresent();
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isList()

    /**
     *  Determines whether the given instance of
     *  {@link TypeName}
     *  is for an implementation of
     *  {@link Map}.
     *
     *  @param  typeName    The type name to check.
     *  @return {@code true} if the type name is for a {@code Map}
     *      implementation, {@code false} otherwise.
     */
    public static final boolean isMap( final TypeName typeName )
    {
        var retValue = !(requireNonNullArgument( typeName, "typeName" ).isPrimitive() || typeName.isBoxedPrimitive() || (typeName instanceof ArrayTypeName));
        if( retValue )
        {
            final ClassName className;
            if( typeName instanceof ParameterizedTypeName )
            {
                className = ((ParameterizedTypeName) typeName).rawType();
            }
            else
            {
                className = (ClassName) typeName;
            }
            retValue = loadClass( className.canonicalName() )
                .filter( Map.class::isAssignableFrom )
                .isPresent();
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isMap()

    /**
     *  Determines whether the given instance of
     *  {@link TypeName}
     *  is for an implementation of
     *  {@link Set}.
     *
     *  @param  typeName    The type name to check.
     *  @return {@code true} if the type name is for a {@code Set}
     *      implementation, {@code false} otherwise.
     */
    public static final boolean isSet( final TypeName typeName )
    {
        var retValue = !(requireNonNullArgument( typeName, "typeName" ).isPrimitive() || typeName.isBoxedPrimitive() || (typeName instanceof ArrayTypeName));
        if( retValue )
        {
            final ClassName className;
            if( typeName instanceof ParameterizedTypeName )
            {
                className = ((ParameterizedTypeName) typeName).rawType();
            }
            else
            {
                className = (ClassName) typeName;
            }
            retValue = loadClass( className.canonicalName() )
                .filter( Set.class::isAssignableFrom )
                .isPresent();
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isSet()
}
//  enum CollectionKind

/*
 *  End of File
 */
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

package org.tquadrat.foundation.test;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import javax.lang.model.element.Name;
import java.util.stream.IntStream;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  An implementation of
 *  {@link Name}
 *  that is used by the tests.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: NameImpl.java 938 2021-12-15 14:42:53Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: NameImpl.java 938 2021-12-15 14:42:53Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public class NameImpl implements Name
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The name.
     */
    private final String m_Name;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code name}.
     *
     *  @param  name    The name.
     */
    public NameImpl( final CharSequence name )
    {
        m_Name = requireNotEmptyArgument( requireNonNullArgument( name, "name" ).toString().trim(), "name" );
    }   //  NameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final char charAt( final int index ) { return m_Name.charAt( index ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final IntStream chars() { return m_Name.chars(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final IntStream codePoints() { return m_Name.codePoints(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean contentEquals( final CharSequence other )
    {
        final var retValue = nonNull( other ) && m_Name.equals( other.toString() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   // contentEquals()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && o instanceof NameImpl other )
        {
            retValue = m_Name.equals( other.m_Name );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( m_Name ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() { return m_Name.isEmpty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int length() { return m_Name.length(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final CharSequence subSequence( final int start, final int end )
    {
        final var retValue = m_Name.subSequence( start, end );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  subSequence()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_Name; }
}
//  class NameImpl

/*
 *  End of File
 */
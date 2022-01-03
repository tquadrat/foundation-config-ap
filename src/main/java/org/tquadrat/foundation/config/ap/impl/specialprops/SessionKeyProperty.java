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
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_SESSION;

import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  The implementation of
 *  {@link SpecialPropertySpecBase}
 *  for
 *  {@link SpecialPropertyType#CONFIG_PROPERTY_SESSION}.
 *
 *  @version $Id: SessionKeyProperty.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SessionKeyProperty.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class SessionKeyProperty extends SpecialPropertySpecBase
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code SessionKeyProperty}.
     */
    public SessionKeyProperty()
    {
        super( CONFIG_PROPERTY_SESSION );
    }   //  SessionKeyProperty()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getCLIValueHandlerClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return ClassName.from( String.class ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.empty(); }
}
//  class SessionKeyProperty

/*
 *  End of File
 */
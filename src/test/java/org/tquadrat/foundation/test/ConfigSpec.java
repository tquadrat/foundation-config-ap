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

import java.util.HashMap;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.config.SystemPreference;
import org.tquadrat.foundation.config.spi.prefs.StringAccessor;

/**
 *  A configuration bean specification.
 *
 *  @version $Id: ConfigSpec.java 942 2021-12-20 02:04:04Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "InterfaceNeverImplemented" )
@ClassVersion( sourceVersion = "$Id: ConfigSpec.java 942 2021-12-20 02:04:04Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public interface ConfigSpec extends ConfigBeanSpec
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * Returns the property 'SystemPrefsString'.
     *
     * @return The property.
     */
    @SystemPreference( key = "system_preference", path = "/org/tquadrat/foundation/test", accessor = StringAccessor.class )
    public String getSystemPrefsString();

    /**
     * Provides the initialisation data for the properties.
     *
     * @return The initialisation data.
     */
    public default Map<String, Object> initData()
    {
        final Map<String, Object> values = new HashMap<>();

        final var retValue = Map.copyOf( values );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initData()
}
//  interface ConfigSpec

/*
 *  End of File
 */
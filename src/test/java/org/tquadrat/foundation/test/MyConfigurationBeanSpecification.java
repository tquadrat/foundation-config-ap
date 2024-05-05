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
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CLOCK;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_PID;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RANDOM;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.I18nSupport;
import org.tquadrat.foundation.config.SpecialProperty;

/**
 *  A configuration bean specification.
 *
 *  @version $Id: MyConfigurationBeanSpecification.java 938 2021-12-15 14:42:53Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "InterfaceNeverImplemented" )
@ClassVersion( sourceVersion = "$Id: MyConfigurationBeanSpecification.java 938 2021-12-15 14:42:53Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public interface MyConfigurationBeanSpecification extends I18nSupport, Map<String,Object>
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * Returns the clock that is used by this program.
     *
     * @return The clock.
     */
    @SpecialProperty( CONFIG_PROPERTY_CLOCK )
    public Clock getClock();

    /**
     * Returns the property 'date1'.
     *
     * @return The property.
     */
    public Optional<Instant> getDate1();

    /**
     * Returns the property 'int1'.
     *
     * @return The property.
     */
    public int getInt1();

    /**
     * Returns the property 'int2'.
     *
     * @return The property.
     */
    public Integer getInt2();

    /**
     *  Returns a new object.
     *
     *  @return The new object.
     */
    public default Object getObject1() { return new Object(); }

    /**
     * Returns the current process id.
     *
     * @return The process id.
     * @see org.tquadrat.foundation.util.SystemUtils#getPID()
     */
    @SpecialProperty( CONFIG_PROPERTY_PID )
    public long getProcessId();

    /**
     * Returns the random number generator.
     *
     * @return The random number generator.
     */
    @SpecialProperty( CONFIG_PROPERTY_RANDOM )
    public Random getRandom();

    /**
     * Returns the property 'string1'.
     *
     * @return The property.
     */
    public String getString1();

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

    /**
     * Sets the clock that should be used by this program.
     *
     * @param clock The clock.
     */
    public void setClock( final Clock clock );

    /**
     * Sets the property 'date2' that has no getter.
     *
     * @param value The new value for the property.
     */
    public void setDate2( final Instant value );

    /**
     * Sets the property 'int3' that has no getter.
     *
     * @param value The new value for the property.
     */
    public void setInt3( final int value );

    /**
     * Sets the property 'int4' that has no getter.
     *
     * @param value The new value for the property.
     */
    public void setInt4( final Integer value );

    /**
     * Sets the property 'string2' that has no getter.
     *
     * @param value The new value for the property.
     */
    public void setString2( final String value );
}
//  interface MyConfigurationBeanSpecification

/*
 *  End of File
 */
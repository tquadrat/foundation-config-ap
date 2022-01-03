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

package org.tquadrat.foundation.test.config;

import static org.apiguardian.api.API.Status.STABLE;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.i18n.BaseBundleName;
import org.tquadrat.foundation.i18n.MessagePrefix;

/**
 *  The base class for the configuration bean.
 *
 *  @version $Id: BaseClass.java 944 2021-12-21 21:56:24Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( {"UtilityClass", "AbstractClassWithoutAbstractMethods"} )
@ClassVersion( sourceVersion = "$Id: BaseClass.java 944 2021-12-21 21:56:24Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public abstract class BaseClass
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name of the base bundle: {@value}.
     */
    @BaseBundleName
    public static final String BASE_BUNDLE_NAME = "TextsAndMessageForTest";

    /**
     *  The message prefix: {@value}.
     */
    @MessagePrefix
    public static final String MESSAGE_PREFIX = "TST";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
}
//  class BaseClass

/*
 *  End of File
 */
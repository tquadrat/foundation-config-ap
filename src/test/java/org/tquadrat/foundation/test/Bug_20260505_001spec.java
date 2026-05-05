/*
 * ============================================================================
 *  Copyright © 2002-2026 by Thomas Thrien.
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

import java.nio.file.PathMatcher;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.CLIBeanSpec;
import org.tquadrat.foundation.config.Option;
import org.tquadrat.foundation.config.cli.PathMatcherValueHandler;

/**
 *  <p>{@summary @TODO Comment for Bug_20260505_001spec.}</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Bug_20260505_001spec.java 1231 2026-05-05 14:28:23Z tquadrat $
 *  @since 0.25.0
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "NewClassNamingConvention" )
@ClassVersion( sourceVersion = "$Id: Bug_20260505_001spec.java 1231 2026-05-05 14:28:23Z tquadrat $" )
@API( status = STABLE, since = "0.25.0" )
public interface Bug_20260505_001spec extends CLIBeanSpec
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  PathMatchers.
     */
    @Option( name = "-p", multiValued = true, handler = PathMatcherValueHandler.class )
    public List<PathMatcher> getPathMatchers();

}
//  interface Bug_20260505_001spec

/*
 *  End of File
 */
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

package org.tquadrat.foundation.config.ap.impl;

import static org.apiguardian.api.API.Status.STABLE;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec;

/**
 *  The specification for special properties.
 *
 *  @version $Id: SpecialPropertySpec.java 927 2021-06-08 20:57:27Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SpecialPropertySpec.java 927 2021-06-08 20:57:27Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public interface SpecialPropertySpec extends PropertySpec
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the method that composes the 'add' method for the property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the 'add' method composer.
     */
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getAddMethodComposer();

    /**
     *  Returns all the flags that were set for this special property
     *  specification.
     *
     *  @return The flags.
     */
    public EnumSet<PropertyFlag> getAllFlags();

    /**
     *  Returns the method that composes the constructor fragment for the
     *  initialisation of the property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the composer for the constructor fragment.
     */
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl, CodeBlock>> getConstructorFragmentComposer();

    /**
     *  Returns the method that composes the field for the property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the field composer.
     */
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,FieldSpec>> getFieldComposer();

    /**
     *  Returns the method that composes the getter for the property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the getter composer.
     */
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getGetterComposer();

    /**
     *  Returns the method that composes the setter for the property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the setter composer.
     */
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getSetterComposer();
}
//  interface SpecialPropertySpec

/*
 *  End of File
 */
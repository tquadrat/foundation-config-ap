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

package org.tquadrat.foundation.config.ap.impl.codebuilders;

import static org.apiguardian.api.API.Status.MAINTAINED;

import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.CodeGenerator;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.config.ap.impl.SpecialPropertySpec;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  The API that provides access to the code generator context, including the
 *  already generated code.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeGeneratorContext.java 946 2021-12-23 14:48:19Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: CodeGeneratorContext.java 946 2021-12-23 14:48:19Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public interface CodeGeneratorContext
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a warning to the
     *  {@link java.lang.SuppressWarnings &#64;SuppressWarnings}
     *  annotation for the constructor of the new configuration bean.
     *
     *  @param  warning The warning to suppress.
     */
    public void addConstructorSuppressedWarning( final SuppressableWarnings warning );

    /**
     *  Provides access to the
     *  {@linkplain JavaComposer#classBuilder(CharSequence) class builder}
     *  for the configuration bean.
     *
     *  @return The reference to the class builder.
     */
    public TypeSpec.Builder getClassBuilder();

    /**
     *  Provides access to the
     *  {@link JavaComposer}
     *  instance that is used for the code generation.
     *
     *  @return The reference for the composer.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public JavaComposer getComposer();

    /**
     *  Provides the default implementation of the method that composes an
     *  'add' method for a given property.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,MethodSpec> getAddMethodComposer() { return CodeBuilderBase::composeAddMethod; }

    /**
     *  Provides the default implementation of the method that composes a
     *  constructor fragment for the initialisation of a given property in case
     *  it is annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,CodeBlock> getConstructorFragment4EnvironmentComposer() { return CodeBuilderBase::composeConstructorFragment4Environment; }

    /**
     *  Provides the default implementation of the method that composes a
     *  constructor fragment for the initialisation of a given property in case
     *  it is annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,CodeBlock> getConstructorFragment4SystemPreferenceComposer() { return CodeBuilderBase::composeConstructorFragment4SystemPreference; }

    /**
     *  Provides the default implementation of the method that composes a
     *  constructor fragment for the initialisation of a given property in case
     *  it is annotated with
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,CodeBlock> getConstructorFragment4SystemPropComposer() { return CodeBuilderBase::composeConstructorFragment4SystemProp; }

    /**
     *  Provides the default implementation of the method that composes a field
     *  for a given property.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,FieldSpec> getFieldComposer() { return CodeBuilderBase::composeField; }

    /**
     *  Provides the default implementation of the method that composes a
     *  getter for a given property.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,MethodSpec> getGetterComposer() { return CodeBuilderBase::composeGetter; }

    /**
     *  Provides the default implementation of the method that composes a
     *  setter for a given property.
     *
     *  @return The composer method.
     */
    public static BiFunction<CodeBuilder, PropertySpecImpl,MethodSpec> getSetterComposer() { return CodeBuilderBase::composeSetter; }

    /**
     *  Provides access to the configuration.
     *
     *  @return The configuration.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeGenerationConfiguration getConfiguration();

    /**
     *  Provides access to the builder for the constructor.
     *
     *  @return The reference to the constructor builder.
     */
    public MethodSpec.Builder getConstructorBuilder();

    /**
     *  Provides access to the code builder for the constructor body.
     *
     *  @return The reference to constructor body code builder.
     */
    public CodeBlock.Builder getConstructorCodeBuilder();

    /**
     *  Returns the definition for the special property type.
     *
     *  @param  type    The special property type.
     *  @return The special property specification.
     */
    public default SpecialPropertySpec retrieveSpecialPropertySpecification( final SpecialPropertyType type )
    {
        final var retValue = CodeGenerator.getSpecialPropertySpecification( type );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  retrieveSpecialPropertySpecification()
}
//  interface CodeGeneratorContext

/*
 *  End of File
 */
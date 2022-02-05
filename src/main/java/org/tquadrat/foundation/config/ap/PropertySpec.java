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

import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.ap.CollectionKind.NO_COLLECTION;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.Name;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  The specification for a property of a configuration bean.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: PropertySpec.java 1006 2022-02-03 23:03:04Z tquadrat $
 *  @since 0.1.0
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "ClassWithTooManyMethods" )
@ClassVersion( sourceVersion = "$Id: PropertySpec.java 1006 2022-02-03 23:03:04Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public interface PropertySpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The flags for a property.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: PropertySpec.java 1006 2022-02-03 23:03:04Z tquadrat $
     *  @since 0.1.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: PropertySpec.java 1006 2022-02-03 23:03:04Z tquadrat $" )
    @API( status = MAINTAINED, since = "0.1.0" )
    public static enum PropertyFlag
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         * Indicates that this property can be initialised from and persisted
         * to an
         * {@link org.tquadrat.foundation.inifile.INIFile}.
         * This also means that the methods
         * {@link #getINIGroup()}
         * and
         * {@link #getINIKey()}
         * may not return
         * {@linkplain Optional#empty() empty}.
         */
        ALLOWS_INIFILE,

        /**
         * Indicates that this property can be initialised from and persisted
         * to a preference. This also means that the methods
         * {@link #getPrefsAccessorClass()}
         * and
         * {@link #getPrefsKey()}
         * may not return
         * {@linkplain Optional#empty() empty}.
         */
        ALLOWS_PREFERENCES,

        /**
         * Indicates that this property is initialised from the value of an
         * environment variable. The method
         * {@link #getEnvironmentVariableName()}
         * may not return
         * {@linkplain Optional#empty() empty}.
         *
         * @see System#getenv()
         */
        ENVIRONMENT_VARIABLE,

        /**
         *  <p>{@summary Indicates that this property will not be part of the
         *  map, in case the configuration bean implements
         *  {@link java.util.Map}.}</p>
         *  <p>There is no related annotation for this flag, it is used mainly
         *  for
         *  {@linkplain org.tquadrat.foundation.config.SpecialProperty special properties}.</p>
         */
        EXEMPT_FROM_MAP,

        /**
         *  Indicates that this property will not appear in the return value of
         *  {@link #toString()}.
         */
        EXEMPT_FROM_TOSTRING,

        /**
         *  <p>{@summary Indicates that the getter method for this property is
         *  default.} This means that the method will not be implemented, and
         *  that there is no field for the property.</p>
         *  <p>This flag implies
         *  {@link #EXEMPT_FROM_MAP}.</p>
         */
        GETTER_IS_DEFAULT,

        /**
         *  <p>{@summary Indicates that the implementation of
         *  {@link java.util.Map}
         *  will refer to the getter for this property, instead of the
         *  field.}</p>
         *  <p>There is no related annotation for this flag, it is used mainly
         *  for
         *  {@linkplain org.tquadrat.foundation.config.SpecialProperty special properties}.</p>
         */
        GETTER_ON_MAP,

        /**
         * Indicates that the getter method returns an instance of
         * {@link Optional}
         * holding the configuration value.
         */
        GETTER_RETURNS_OPTIONAL,

        /**
         *  Indicates that a value for this property is required on the
         *  command line.
         */
        PROPERTY_CLI_MANDATORY,

        /**
         *  Indicates that it is possible to provide multiple values for this
         *  property on the command line.
         */
        PROPERTY_CLI_MULTIVALUED,

        /**
         *  Indicates that the property can be initialised with a command line
         *  argument.
         *
         *  @see    #getCLIArgumentIndex()
         *  @see    #getCLIValueHandlerClass()
         *  @see    #getCLIFormat()
         *  @see    #getCLIMetaVar()
         */
        PROPERTY_IS_ARGUMENT,

        /**
         *  Indicates that the property value can be modified. Basically, it
         *  means that there is a setter and/or an 'add' method for that
         *  property.
         */
        PROPERTY_IS_MUTABLE,

        /**
         *  Indicates that the property can be initialised with a command line
         *  option.
         *
         *  @see    #getCLIOptionNames()
         *  @see    #getCLIValueHandlerClass()
         *  @see    #getCLIFormat()
         *  @see    #getCLIMetaVar()
         */
        PROPERTY_IS_OPTION,

        /**
         * Indicates that the property is a 'special' property. This also
         * means that
         * {@link PropertySpec#getSpecialPropertyType()}
         * will not return an
         * {@linkplain Optional#empty() empty}
         * value.
         */
        PROPERTY_IS_SPECIAL,

        /**
         *  Indicates that the access to the property requires synchronisation.
         */
        PROPERTY_REQUIRES_SYNCHRONIZATION,

        /**
         * Indicates that the setter method for this property should check for
         * an empty argument.
         */
        SETTER_CHECK_EMPTY,

        /**
         * Indicates that the setter method for this property should check for
         * a null argument.
         */
        SETTER_CHECK_NULL,

        /**
         * Indicates that the setter method for this property is default; this
         * means that the method will not be implemented, and that there is no
         * field for the property.
         */
        SETTER_IS_DEFAULT,

        /**
         * Indicates that this property is initialised from the value of a
         * system preference. The methods
         * {@link #getSystemPrefsPath()},
         * {@link #getPrefsKey()}
         * and
         * {@link #getPrefsAccessorClass()}
         * may not return
         * {@linkplain Optional#empty() empty}.
         */
        SYSTEM_PREFERENCE,

        /**
         * Indicates that this property is initialised from the value of a
         * system property. The method
         * {@link #getSystemPropertyName()}
         * may not return
         * {@linkplain Optional#empty() empty}.
         *
         * @see System#getProperties()
         */
        SYSTEM_PROPERTY
    }
    //  enum PropertyFlag

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates the specification of the 'add' method for this property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the method specification.
     */
    public Optional<MethodSpec> createAddMethod( final CodeBuilder codeBuilder );

    /**
     *  Creates a code block that is a fragment for the constructor of the new
     *  configuration bean and that initialises this property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the code block.
     */
    public Optional<CodeBlock> createConstructorFragment( final CodeBuilder codeBuilder );

    /**
     *  Creates the field specification for this property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the field specification.
     */
    public Optional<FieldSpec> createField( final CodeBuilder codeBuilder );

    /**
     *  Creates the specification of the getter for this property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the method specification.
     */
    public Optional<MethodSpec> createGetter( final CodeBuilder codeBuilder );

    /**
     *  Creates the specification of the setter for this property.
     *
     *  @param  codeBuilder The factory for the code generation.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the method specification.
     */
    public Optional<MethodSpec> createSetter( final CodeBuilder codeBuilder );

    /**
     *  Returns the name of the 'add' method's argument.
     *
     *  @return The argument name; is probable {@code null} when
     *      {@link #getAddMethodName()}
     *      returns
     *      {@link Optional#empty()}.
     */
    public Name getAddMethodArgumentName();

    /**
     *  Returns the name of the 'add' method for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name of the add method.
     */
    public Optional<Name> getAddMethodName();

    /**
     *  <p>{@summary Returns the index for an argument on the command
     *  line.}</p>
     *  <p>The return value will be
     *  {@linkplain OptionalInt#empty() empty}
     *  if the property is not a CLI argument.</p>
     *
     *  @return An instance of
     *      {@link OptionalInt}
     *      that holds the index.
     *
     *  @see org.tquadrat.foundation.config.Argument#index()
     */
    public OptionalInt getCLIArgumentIndex();

    /**
     *  Returns the special CLI format.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the format.
     *
     *  @see org.tquadrat.foundation.config.Argument#format()
     *  @see org.tquadrat.foundation.config.Option#format()
     */
    public Optional<String> getCLIFormat();

    /**
     *  Returns the name of the CLI meta variable for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name of the meta variable.
     *
     *  @see org.tquadrat.foundation.config.Argument#metaVar()
     *  @see org.tquadrat.foundation.config.Option#metaVar()
     */
    public Optional<String> getCLIMetaVar();

    /**
     *  <p>{@summary Returns the CLI option names.} The mandatory first name in
     *  the list is the primary name, the optional others are the aliases.</p>
     *  <p>The return value will be
     *  {@linkplain Optional#empty() empty}
     *  if the property is not a CLI option.</p>
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the option names.
     *
     *  @see org.tquadrat.foundation.config.Option#name()
     *  @see org.tquadrat.foundation.config.Option#aliases()
     */
    @SuppressWarnings( "OptionalContainsCollection" )
    public Optional<List<String>> getCLIOptionNames();

    /**
     *  Returns the CLI usage text for this property. This text will not be
     *  localised.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the usage text.
     *
     *  @see org.tquadrat.foundation.config.Argument#usage()
     *  @see org.tquadrat.foundation.config.Option#usage()
     */
    public Optional<String> getCLIUsage();

    /**
     *  Returns the CLI usage key for this property. This key is used to
     *  retrieve a localised usage text.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the usage key.
     *
     *  @see org.tquadrat.foundation.config.Argument#usageKey()
     *  @see org.tquadrat.foundation.config.Option#usageKey()
     */
    public Optional<String> getCLIUsageKey();

    /**
     *  Returns the CLI value handler class for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the
     *      {@link TypeName}
     *      for the handler class.
     *
     *  @see org.tquadrat.foundation.config.Argument#handler()
     *  @see org.tquadrat.foundation.config.Option#handler()
     */
    public Optional<TypeName> getCLIValueHandlerClass();

    /**
     *  Returns the kind of collection for this property.
     *
     *  @return The collection kind.
     */
    public CollectionKind getCollectionKind();

    /**
     *  Returns the name of the environment variable that is used to initialise
     *  this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the environment variable name.
     *
     * @see org.tquadrat.foundation.config.EnvironmentVariable
     */
    public Optional<String> getEnvironmentVariableName();

    /**
     *  Returns the name of the field for the property.
     *
     *  @return The field name.
     */
    public default String getFieldName() { return format( "m_%s", capitalize( getPropertyName() ) ); }

    /**
     *  Returns a builder for the getter for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the builder.
     */
    public Optional<MethodSpec.Builder> getGetterBuilder();

    /**
     *  Returns the name of the getter method name. If there is no name for the
     *  method, it will not be generated.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name of the getter method.
     */
    public Optional<Name> getGetterMethodName();

    /**
     *  Returns the return type of the getter. This is not necessarily the same
     *  as the
     *  {@linkplain #getPropertyType() property type}.
     *
     *  @return The getter's return type.
     */
    public TypeName getGetterReturnType();

    /**
     *  Returns the comment for this property in the {@code INI} file.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the comment.
     */
    public Optional<String> getINIComment();

    /**
     *  Returns the group for this property in the {@code INI} file.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the group name.
     */
    public Optional<String> getINIGroup();

    /**
     *  Returns the key for this property in the {@code INI} file.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the key.
     */
    public Optional<String> getINIKey();

    /**
     *  <p>{@summary Returns the {@code Preferences} accessor class.}</p>
     *  <p>This is used when this property is linked to a preference, but also
     *  to initialise it from a SYSTEM preference.</p>
     *
     *  @see org.tquadrat.foundation.config.PreferencesBeanSpec
     *  @see org.tquadrat.foundation.config.Preference#accessor()
     *  @see org.tquadrat.foundation.config.SystemPreference#accessor()
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the
     *      {@link TypeName}
     *      for the accessor class.
     */
    public Optional<TypeName> getPrefsAccessorClass();

    /**
     *  <p>{@summary Returns the {@code Preferences} key for this
     *  property.}</p>
     *  <p>This is used when this property is linked to a preference, but also
     *  to initialise it from a SYSTEM preference. In first case, the name is
     *  defaulted to the property name, while it is mandatory otherwise.</p>
     *
     *  @see org.tquadrat.foundation.config.PreferencesBeanSpec
     *  @see org.tquadrat.foundation.config.Preference#key()
     *  @see org.tquadrat.foundation.config.SystemPreference#key()
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the preferences key for this property.
     */
    public Optional<String> getPrefsKey();

    /**
     *  Returns the name of the configuration property.
     *
     *  @return The name.
     */
    public String getPropertyName();

    /**
     *  Returns the property type.
     *
     *  @return The property type.
     */
    public TypeName getPropertyType();

    /**
     *  Returns the name of the setter's argument.
     *
     *  @return The argument name; is probably {@code null} when
     *      {@link #getSetterMethodName()}
     *      returns
     *      {@link Optional#empty()}.
     */
    public Name getSetterArgumentName();

    /**
     *  Returns a builder for the setter for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the builder.
     */
    public Optional<MethodSpec.Builder> getSetterBuilder();

    /**
     *  Returns the name of the setter method name. If there is no name for the
     *  method, it will not be generated.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name of the setter method.
     */
    public Optional<Name> getSetterMethodName();

    /**
     *  Return the 'speciality' type for this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the speciality type.
     */
    public Optional<SpecialPropertyType> getSpecialPropertyType();

    /**
     *  Returns the class that implements the String converter for the type of
     *  this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the implementation class for
     *      {@link org.tquadrat.foundation.lang.StringConverter}.
     */
    public Optional<TypeName> getStringConverterClass();

    /**
     *  Returns the path to the SYSTEM {@code Preferences} node that holds the
     *  data for the initialisation of this property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the path.
     *
     *  @see org.tquadrat.foundation.config.SystemPreference#path()
     */
    public Optional<String> getSystemPrefsPath();

    /**
     *  Returns the name of the system property that is used to initialise this
     *  property.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the system property name.
     *
     * @see org.tquadrat.foundation.config.SystemProperty
     */
    public Optional<String> getSystemPropertyName();

    /**
     *  Checks whether the given flag is set for this property.
     *
     *  @param  flag    The flag to test for.
     *  @return {@code true} if the flag is set, {@code false} otherwise.
     */
    public boolean hasFlag( final PropertyFlag flag );

    /**
     *  Checks whether this property is a collection of some kind.
     *
     *  @return {@code true} if this property is a collection, {@code false}
     *      otherwise.
     */
    public default boolean isCollection() { return getCollectionKind() != NO_COLLECTION; }

    /**
     *  Returns the flag that indicates whether the property is an
     *  {@link Enum enum}
     *  type.
     *
     *  @return {@code true} if the property type is an {@code enum},
     *      {@code false} otherwise.
     */
    public boolean isEnum();

    /**
     *  {@summary Checks whether this property is exposed to the CLI.} This
     *  means that it has either the flag
     *  {@link PropertyFlag#PROPERTY_IS_ARGUMENT}
     *  or the
     *  {@link PropertyFlag#PROPERTY_IS_OPTION}
     *  set to it.
     *
     *  @return {@code true} if the property is exposed to the CLI,
     *      {@code false} otherwise.
     */
    public default boolean isOnCLI() { return hasFlag( PropertyFlag.PROPERTY_IS_ARGUMENT ) || hasFlag( PropertyFlag.PROPERTY_IS_OPTION ); }

    /**
     *  <p>{@summary 'Merges' the attributes from a
     *  {@linkplain SpecialPropertyType special property}
     *  with the attributes retrieved from the configuration bean
     *  specification and returns a new instance of {@code PropertySpec}.} The
     *  original instance remains unchanged.</p>
     *  <p>If the property is not a special property (the flag
     *  {@link PropertyFlag#PROPERTY_IS_SPECIAL}
     *  is not set), this instance will be returned.</p>
     *
     *  @return The effective property specification.
     */
    public PropertySpec merge();
}
//  interface PropertySpec

/*
 *  End of File
 */
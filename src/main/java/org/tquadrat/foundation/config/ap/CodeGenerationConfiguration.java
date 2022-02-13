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

import static java.util.Collections.unmodifiableMap;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_DuplicateProperty;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_IllegalImplementation;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.Comparators.caseInsensitiveComparator;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.StringUtils.isEmptyOrBlank;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.lang.model.element.Name;
import javax.lang.model.util.Elements;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.APHelper;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.INIGroup;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  An instance of this class provides the configuration for the code
 *  generation, and it collects the results from the different code generators.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeGenerationConfiguration.java 1015 2022-02-09 08:25:36Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: CodeGenerationConfiguration.java 1015 2022-02-09 08:25:36Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
public final class CodeGenerationConfiguration
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The base bundle name for the resource bundle if i18n support is
     *  required.
     *
     *  @note   This is <i>not</i> the value but the fully qualified field name
     *      where to find that value!
     */
    private String m_BaseBundleName = null;

    /**
     *  The optional base class for the new configuration bean.
     */
    private final TypeName m_BaseClass;

    /**
     *  The build time; this is provided by the configuration as this allows
     *  easier testing.
     */
    private final Instant m_BuildTime = Instant.now();

    /**
     * The class name for the configuration bean class, without the package.
     */
    private final Name m_ClassName;

    /**
     * The
     * {@link JavaComposer}
     * instance that is used for the code generation.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The processing environment.
     */
    private final APHelper m_Environment;

    /**
     *  The comment for the configuration file.
     */
    private String m_INIFileComment = null;

    /**
     *  The flag that indicates whether the configuration must exist prior to
     *  the first open attempt.
     */
    private boolean m_INIFileMustExist;

    /**
     *  The name for the configuration file.
     */
    private String m_INIFilePath = null;

    /**
     *  The {@code INI} file groups. The key is the name of the group, the
     *  value is the respective comment for the group.
     */
    private final Map<String,String> m_INIGroups = new TreeMap<>();

    /**
     *  The method that is provided as a source for the initialisation of the
     *  properties of the configuration bean.
     */
    private MethodSpec m_InitDataMethod = null;

    /**
     * The name of the resource that is used to initialise the properties of
     * the configuration bean.
     */
    private String m_InitDataResource = null;

    /**
     *  The interfaces that are extended by the configuration bean
     *  specification.
     */
    private final Collection<TypeName> m_InterfacesToImplement = new HashSet<>();

    /**
     *  The message prefix for the i18n support.
     *
     *  @note   This is <i>not</i> the value but the fully qualified field name
     *      where to find that value!
     */
    private String m_MessagePrefix = null;

    /**
     * The name of the package for the configuration bean.
     */
    private final Name m_PackageName;

    /**
     *  The change listener class for the {@code Preferences}.
     */
    private TypeName m_PreferenceChangeListenerClass;

    /**
     *  <p>{@summary The properties for the configuration bean.} The name of
     *  the property is the key.</p>
     */
    private final SortedMap<String, PropertySpecImpl> m_Properties = new TreeMap<>( caseInsensitiveComparator() );

    /**
     *  The configuration bean specification.
     */
    private final ClassName m_Specification;

    /**
     *  The name of the preferences root node.
     */
    private String m_PreferencesRoot = null;

    /**
     *  This flag indicates whether the access to the configuration bean
     *  properties must be thread-safe.
     */
    private final boolean m_SynchronizeAccess;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates an instance of {@code CodeGenerationConfiguration}.
     *
     *  @param  environment The access to the processing environment.
     *  @param  composer    The
     *      {@link JavaComposer}
     *      instance that is used for the code generation.
     *  @param  specification   The configuration specification, the interface
     *      that defines the configuration bean.
     *  @param  className   The name for the configuration bean class, without
     *      the package name.
     *  @param  packageName The name of the package for the configuration bean
     *      class.
     *  @param  baseClass   The optional base class for the new configuration
     *      bean class.
     *  @param  synchronizeAccess   {@code true} if the access to the
     *      configuration bean properties should be thread safe, {@code false}
     *      if a synchronisation/locking is not required.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeGenerationConfiguration( final APHelper environment, final JavaComposer composer, final ClassName specification, final Name className, final Name packageName, final TypeName baseClass, final boolean synchronizeAccess )
    {
        m_Environment = requireNonNullArgument( environment, "environment" );
        m_Composer = requireNonNullArgument( composer, "composer" );
        m_ClassName = requireNotEmptyArgument( className, "className" );
        m_PackageName = requireNonNullArgument( packageName, "packageName" );
        m_Specification = requireNonNullArgument( specification, "specification" );
        m_BaseClass = baseClass;
        m_SynchronizeAccess = synchronizeAccess;
    }   //  CodeGenerationConfiguration()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds an {@code INI} file group.
     *
     *  @param  name    The name for the group.
     *  @param  comment The comment for the group.
     */
    public final void addINIGroup( final String name, final String comment )
    {
        if( isNotEmptyOrBlank( name ) )
        {
            if( isEmptyOrBlank( comment ) )
            {
                m_INIGroups.remove( name );
            }
            else
            {
                m_INIGroups.put( name, comment );
            }
        }
    }   //  addINIGroup()

    /**
     *  Adds an {@code INI} file group.
     *
     *  @param  group   The group definition.
     */
    public final void addINIGroup( final INIGroup group )
    {
        addINIGroup( requireNonNullArgument( group, "group" ).name(), group.comment() );
    }   //  addINIGroup()

    /**
     *  Adds some interfaces that have to be implemented by the new
     *  configuration beam.
     *
     *  @param  interfacesToImplement   The classes for the interfaces to
     *      implement.
     */
    public final void addInterfacesToImplement( final Collection<? extends TypeName> interfacesToImplement )
    {
        m_InterfacesToImplement.addAll( requireNotEmptyArgument( interfacesToImplement, "interfacesToImplement" ) );
    }   //  addInterfaceToImplement()

    /**
     *  <p>{@summary Adds a property to the new configuration bean.}</p>
     *  <p>If a property with the same name as the new one already exists, a
     *  {@link CodeGenerationError}
     *  will be thrown.</p>
     *
     *  @param  property    The property
     *  @throws CodeGenerationError There is a duplicate property.
     */
    public final void addProperty( final PropertySpec property ) throws CodeGenerationError
    {
        if( requireNonNullArgument( property, "property" ) instanceof PropertySpecImpl propertyImpl )
        {
            final var propertyName = propertyImpl.getPropertyName();
            if( nonNull( m_Properties.put( propertyName, propertyImpl ) ) )
            {
                throw new CodeGenerationError( format( MSG_DuplicateProperty, propertyName ) );
            }
        }
        else
        {
            throw new CodeGenerationError( format( MSG_IllegalImplementation, PropertySpec.class.getName(), property.getClass().getName() ) );
        }
    }   //  addProperty()

    /**
     *  Returns the name of the field that holds the base bundle name for the
     *  resource bundle, in case i18n support is configured.
     *
     *  @return An instance of
     *      {@link Optional}
     *      holding the fully qualified field name.
     */
    public final Optional<String> getBaseBundleName() { return Optional.ofNullable( m_BaseBundleName ); }

    /**
     *  Returns the base class for the new configuration bean.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the base class.
     */
    public final Optional<TypeName> getBaseClass() { return Optional.ofNullable( m_BaseClass ); }

    /**
     *  Returns the build time for the new configuration bean.
     *
     *  @return The build time.
     */
    public final Instant getBuildTime() { return m_BuildTime; }

    /**
     *  Returns the name of the class for the new configuration bean.
     *
     *  @return The class name.
     */
    public final Name getClassName() { return m_ClassName; }

    /**
     *  Returns the
     *  {@link JavaComposer}
     *  instance that is used for the code generation.
     *
     *  @return The composer instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public final JavaComposer getComposer() { return m_Composer; }

    /**
     *  Returns an implementation of some utility methods for operating on
     *  elements.
     *
     *  @return The element utilities.
     */
    public final Elements getElementUtils() { return getEnvironment().getElementUtils(); }

    /**
     *  Returns the processing environment.
     *
     *  @return The processing environment.
     */
    public final APHelper getEnvironment() { return m_Environment; }

    /**
     *  Returns the comment for the {@code INI} file.
     *
     *  @return The comment for the configuration file.
     */
    public final Optional<String> getINIFileComment() { return Optional.ofNullable( m_INIFileComment ); }

    /**
     *  Returns the flag that indicates whether the configuration file must
     *  exist before the program starts.
     *
     *  @return {@code true} if the file must exist already, {@code false} if
     *      it will be created on startup.
     *
     * @see org.tquadrat.foundation.config.INIFileConfig#mustExist()
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public final boolean getINIFileMustExist() { return m_INIFileMustExist; }

    /**
     *  Returns the name for the file that backs the
     *  {@link org.tquadrat.foundation.inifile.INIFile}
     *  instance used by the generated configuration bean.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the filename.
     */
    public final Optional<String> getINIFilePath() { return Optional.ofNullable( m_INIFilePath ); }

    /**
     *  Returns the {@code INI} file groups.
     *
     *  @return The group names and the related comments.
     */
    public final Map<String,String> getINIGroups() { return unmodifiableMap( m_INIGroups ); }

    /**
     *  <p>{@summary Returns the method that is provided as a source for the
     *  initialisation of the properties of the configuration bean.}</p>
     *  <p>It it is {@code default} or {@code static}, the implementation of
     *  the method has to be part of the configuration bean specification
     *  interface itself, otherwise it has to be implemented in base class.</p>
     *  <p>It is an error if there is an {@code initData()} method that is
     *  neither {@code default} nor {@code static}, and no base class is
     *  defined with the
     *  {@link org.tquadrat.foundation.config.ConfigurationBeanSpecification &#64;ConfigurationBeanSpecificatgion}
     *  annotation.</p>
     *  <p>The signature for the method has to be</p>
     *  <pre><code>public Map&lt;String,Object&gt; initData() throws Exception</code></pre>
     *  <p>and the return value is a map with the initialisation values, where
     *  the property names are the keys.</p>
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the method.
     *
     *  @see org.tquadrat.foundation.config.ConfigurationBeanSpecification#baseClass()
     */
    public final Optional<MethodSpec> getInitDataMethod() { return Optional.ofNullable( m_InitDataMethod ); }

    /**
     *  Returns the name of the resource that is used to initialise the
     *  properties of the configuration bean.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the resource name.
     */
    public final Optional<String> getInitDataResource() { return Optional.ofNullable( m_InitDataResource ); }

    /**
     *  Returns the interfaces that have to be implemented by the new
     *  configuration bean.
     *
     *  @return The interfaces to implement.
     */
    public final Collection<TypeName> getInterfacesToImplement() { return List.copyOf( m_InterfacesToImplement ); }

    /**
     *  Returns the name of the field that holds the message prefix, in case
     *  i18n support is configured.
     *
     *  @return An instance of
     *      {@link Optional}
     *      holding the fully qualified field name.
     */
    public final Optional<String> getMessagePrefix() { return Optional.ofNullable( m_MessagePrefix ); }

    /**
     *  Returns the name of the package for the new configuration bean.
     *
     *  @return The package name.
     */
    public final Name getPackageName() { return m_PackageName; }

    /**
     *  <p>{@summary Returns the class for the {@code Preferences} change
     *  listener.} If no listener class is defined, the change listener support
     *  for the {@code Preferences} will be omitted.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the listener class.
     *
     *  @see org.tquadrat.foundation.config.PreferencesRoot#changeListenerClass()
     */
    public final Optional<TypeName> getPreferenceChangeListenerClass()
    {
        final var retValue = Optional.ofNullable( m_PreferenceChangeListenerClass );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getPreferenceChangeListenerClass()

    /**
     *  Returns the name for the {@code Preferences} root node.
     *
     *  @return The name for the {@code Preferences} root node.
     *
     *  @see org.tquadrat.foundation.config.PreferencesRoot#nodeName()
     */
    public final String getPreferencesRoot()
    {
        final var retValue = isNotEmptyOrBlank( m_PreferencesRoot) ? m_PreferencesRoot : m_Specification.canonicalName();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getPreferencesRoot()
    /**
     *  Returns the property with the given name.
     *
     *  @param  propertyName    The name of the property.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the retrieved property.
     */
    public final Optional<PropertySpec> getProperty( final String propertyName )
    {
        final Optional<PropertySpec> retValue = Optional.ofNullable( m_Properties.get( requireNotEmptyArgument( propertyName, "propertyName" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getProperty()

    /**
     *  Returns the configuration bean specification.
     *
     *  @return The configuration bean specification.
     */
    public final ClassName getSpecification() { return m_Specification;}

    /**
     *  Returns the flag that controls whether the generated code for the
     *  access to the configuration bean properties has to be thread-safe.
     *
     *  @return {@code true} if synchronisation/locking is required,
     *      {@code false} if not.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public final boolean getSynchronizationRequired() { return m_SynchronizeAccess; }

    /**
     *  Checks whether a property with the given name does already exist.
     *
     *  @param  propertyName    The name of the property.
     *  @return {@code true} if the property with the given name already
     *      exists, {@code false} otherwise.
     */
    public final boolean hasProperty( final String propertyName ) { return m_Properties.containsKey( propertyName ); }

    /**
     *  Checks whether the given interface must be implemented by the new
     *  configuration bean.
     *
     *  @param  interfaceToImplement    The class for the interface that has to
     *      be implemented.
     *  @return {@code true} if the given interface must be implemented,
     *      {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public final boolean implementInterface( final TypeName interfaceToImplement )
    {
        final var retValue = m_InterfacesToImplement.contains( requireNonNullArgument( interfaceToImplement, "interfaceToImplement" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  implementInterface()

    /**
     *  Checks whether the given interface must be implemented by the new
     *  configuration bean.
     *
     *  @param  interfaceToImplement    The class for the interface that has to
     *      be implemented.
     *  @return {@code true} if the given interface must be implemented,
     *      {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public final boolean implementInterface( final Type interfaceToImplement )
    {
        final var element = TypeName.from( interfaceToImplement );
        final var retValue = implementInterface( element );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  implementInterface()

    /**
     *  Returns an
     *  {@link java.util.Iterator}
     *  over the defined properties.
     *
     *  @return The iterator.
     */
    public final Iterator<PropertySpec> propertyIterator()
    {
        final var iterator = m_Properties.values().iterator();
        final Iterator<PropertySpec> retValue = new Iterator<>()
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final boolean hasNext() { return iterator.hasNext(); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final PropertySpec next() { return iterator.next(); }
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  propertyIterator()

    /**
     *  Sets the i18n parameters.
     *
     *  @param  messagePrefix   The value for the message prefix.
     *  @param  baseBundleName  The name of the base bundle.
     */
    public final void setI18NParameters( final String messagePrefix, final String baseBundleName )
    {
        m_MessagePrefix = messagePrefix;
        m_BaseBundleName = baseBundleName;
    }   //  setI18NParameters()

    /**
     *  Sets the configuration for the {@code INI} file.
     *
     *  @param  filename    The path; can be {@code null}.
     *  @param  flag    The flag that indicates whether the configuration file
     *      must exist before the program starts.{@code true} if the file must
     *      exist, {@code false} if it will be created on startup.
     *  @param  comment The comment; can be {@code null}.
     */
    public final void setINIFileConfig( final String filename, final boolean flag,  final String comment )
    {
        m_INIFilePath = filename;
        m_INIFileComment = isNull( m_INIFilePath ) ? null : comment;
        m_INIFileMustExist = flag;
    }   //  setINIFileConfig()

    /**
     *  Set the method that is provided as a source for the initialisation of
     *  the properties of the configuration bean.
     *
     *  @param  method  The method; can be {@code null}.
     */
    public final void setInitDataMethod( final MethodSpec method ) { m_InitDataMethod = method; }

    /**
     *  Set the name of the resource that is used to initialise the
     *  properties of the configuration bean.
     *
     *  @param  initDataResource    The resource name; can be {@code null}.
     */
    public final void setInitDataResource( final String initDataResource ) { m_InitDataResource = initDataResource; }

    /**
     *  Sets the class for the {@code Preferences} change listener.
     *
     *  @param  listenerClass   The listener class; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.PreferencesRoot#changeListenerClass()
     */
    public final void setPreferenceChangeListenerClass( final TypeName listenerClass )
    {
        m_PreferenceChangeListenerClass = listenerClass;
    }   //  setPreferenceChangeListenerClass()

    /**
     *  Sets the name for the preferences root node.
     *
     *  @param  name    The name for the preferences root node.
     */
    public final void setPreferencesRoot( final String name )
    {
        m_PreferencesRoot = requireNotEmptyArgument( name, "name" );
    }   //  setPreferencesRoot()
}
//  class CodeGenerationConfiguration

/*
 *  End of File
 */
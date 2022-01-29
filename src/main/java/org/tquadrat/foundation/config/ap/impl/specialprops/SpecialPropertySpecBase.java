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

import static java.util.Arrays.asList;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.config.ap.CollectionKind.NO_COLLECTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_SPECIAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.Name;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.MountPoint;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.CollectionKind;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.config.ap.impl.CodeBuilder;
import org.tquadrat.foundation.config.ap.impl.PropertySpecImpl;
import org.tquadrat.foundation.config.ap.impl.SpecialPropertySpec;
import org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext;
import org.tquadrat.foundation.exception.IllegalOperationException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.MethodSpec.Builder;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  The base class for the special property specifications.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: SpecialPropertySpecBase.java 947 2021-12-23 21:44:25Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: SpecialPropertySpecBase.java 947 2021-12-23 21:44:25Z tquadrat $" )
@API( status = MAINTAINED, since = "0.1.0" )
abstract sealed class SpecialPropertySpecBase implements SpecialPropertySpec
    permits CharsetProperty, ClockProperty, LocaleProperty, MessagePrefixProperty, ProcessIdProperty, RandomProperty, ResourceBundleProperty, SessionKeyProperty, TimeZoneProperty
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The property flags.
     */
    private final Set<PropertyFlag> m_PropertyFlags = EnumSet.noneOf( PropertyFlag.class );

    /**
     *  The type of the special property.
     */
    private final SpecialPropertyType m_Type;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code SpecialPropertySpecBase}.
     *
     *  @param  type    The type of the special property.
     *  @param  flags   The flags for this special property.
     */
    protected SpecialPropertySpecBase( final SpecialPropertyType type, final PropertyFlag... flags )
    {
        m_Type = requireNonNullArgument( type, "type" );
        m_PropertyFlags.addAll( asList( requireNonNullArgument( flags, "flags" ) ) );
        m_PropertyFlags.add( PROPERTY_IS_SPECIAL );
    }   //  SpecialPropertySpecBase()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec> createAddMethod( final CodeBuilder codeBuilder ) { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<CodeBlock> createConstructorFragment( final CodeBuilder codeBuilder ) { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<FieldSpec> createField( final CodeBuilder codeBuilder ) { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec> createGetter( final CodeBuilder codeBuilder ) { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec> createSetter( final CodeBuilder codeBuilder ) { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getAddMethodArgumentName() { return null; }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getAddMethodComposer() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<Name> getAddMethodName() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final EnumSet<PropertyFlag> getAllFlags() { return EnumSet.copyOf( m_PropertyFlags ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final OptionalInt getCLIArgumentIndex() { return OptionalInt.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIFormat() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIMetaVar() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "OptionalContainsCollection" )
    @Override
    public final Optional<List<String>> getCLIOptionNames() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIUsage() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIUsageKey() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    @MountPoint
    public CollectionKind getCollectionKind() { return NO_COLLECTION; }

    /**
     *  {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder, PropertySpecImpl, CodeBlock>> getConstructorFragmentComposer() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getEnvironmentVariableName() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,FieldSpec>> getFieldComposer() { return Optional.of( CodeGeneratorContext.getFieldComposer() ); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<Builder> getGetterBuilder() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getGetterComposer() { return Optional.of( CodeGeneratorContext.getGetterComposer() ); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<Name> getGetterMethodName() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    @MountPoint
    public TypeName getGetterReturnType() { return getPropertyType(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getINIComment() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getINIGroup() { return Optional.empty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getINIKey() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getPrefsKey() { return Optional.of( m_Type.getPropertyName() ); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getPropertyName() { return m_Type.getPropertyName(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getSetterArgumentName() { return null; }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<Builder> getSetterBuilder() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    @MountPoint
    public Optional<BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec>> getSetterComposer() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<Name> getSetterMethodName() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<SpecialPropertyType> getSpecialPropertyType() { return Optional.of( m_Type ); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getSystemPrefsPath() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Optional<String> getSystemPropertyName() { return Optional.empty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasFlag( final PropertyFlag flag ) { return m_PropertyFlags.contains( requireNonNullArgument( flag, "flag" ) ); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PropertySpec merge() { throw new IllegalOperationException( "Not allowed for a SpecialProperty" ); }
}
//  class SpecialPropertySpecBase

/*
 *  End of File
 */
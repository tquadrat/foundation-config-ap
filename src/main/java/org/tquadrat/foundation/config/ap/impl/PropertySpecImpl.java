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

import static java.util.Arrays.copyOfRange;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.config.ap.CollectionKind.NO_COLLECTION;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.ENVIRONMENT_VARIABLE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.GETTER_IS_DEFAULT;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_SPECIAL;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PREFERENCE;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.SYSTEM_PROPERTY;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getAddMethodComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getConstructorFragment4EnvironmentComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getConstructorFragment4SystemPreferenceComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getConstructorFragment4SystemPropComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getFieldComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getGetterComposer;
import static org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext.getSetterComposer;
import static org.tquadrat.foundation.lang.CommonConstants.NUL;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.isEmptyOrBlank;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.INIValue;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.CollectionKind;
import org.tquadrat.foundation.config.ap.PropertySpec;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.TypeName;

/**
 *  The implementation for
 *  {@link PropertySpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: PropertySpecImpl.java 1011 2022-02-05 19:31:34Z tquadrat $
 *  @since 0.1.0
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "OverlyComplexClass"} )
@ClassVersion( sourceVersion = "$Id: PropertySpecImpl.java 1011 2022-02-05 19:31:34Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class PropertySpecImpl implements PropertySpec
{
       /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The name for the 'add' method's argument.
     */
    private Name m_AddMethodArgumentName = null;

    /**
     *  The builder for the 'add' method of this property.
     */
    private MethodSpec.Builder m_AddMethodBuilder = null;

    /**
     *  The method that creates the 'add' method for this property.
     */
    private BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec> m_AddMethodComposer;

    /**
     *  The name for the 'add'' method.
     */
    private Name m_AddMethodName;

    /**
     *  The argument index.
     *
     *  @see org.tquadrat.foundation.config.Argument#index()
     */
    private int m_CLIArgumentIndex = -1; // -1 means that the value was not set

    /**
     *  The special CLI format.
     *
     *  @see org.tquadrat.foundation.config.Argument#format()
     *  @see org.tquadrat.foundation.config.Option#format()
     */
    private String m_CLIFormat = null;

    /**
     *  The CLI meta variable.
     *
     *  @see org.tquadrat.foundation.config.Argument#metaVar()
     *  @see org.tquadrat.foundation.config.Option#metaVar()
     */
    private String m_CLIMetaVar = null;

    /**
     *  The names for a CLI option.
     *
     *  @see org.tquadrat.foundation.config.Option#name()
     *  @see org.tquadrat.foundation.config.Option#aliases()
     */
    private List<String> m_CLIOptionNames = null;

    /**
     *  The CLI value handler class.
     *
     *  @see org.tquadrat.foundation.config.Argument#handler()
     *  @see org.tquadrat.foundation.config.Option#handler()
     */
    private TypeName m_CLIValueHandlerClass = null;

    /**
     *  The CLI usage text.
     *
     *  @see org.tquadrat.foundation.config.Argument#usage()
     *  @see org.tquadrat.foundation.config.Option#usage()
     */
    private String m_CLIUsage = null;

    /**
     *  The CLI usage key.
     *
     *  @see org.tquadrat.foundation.config.Argument#usageKey()
     *  @see org.tquadrat.foundation.config.Option#usageKey()
     */
    private String m_CLIUsageKey = null;

    /**
     *  The kind of collection for this property.
     */
    private CollectionKind m_CollectionKind = NO_COLLECTION;

    /**
     *  The method that creates the constructor fragment for the initialisation
     *  of this property.
     */
    private BiFunction<CodeBuilder,PropertySpecImpl, CodeBlock> m_ConstructorFragmentComposer;

    /**
     *  The default value for environment variables or system properties.
     */
    private String m_EnvironmentDefaultValue = null;

    /**
     *  The name of the environment variable that is used to initialise this
     *  property.
     */
    private String m_EnvironmentVariableName = null;

    /**
     *  The method that creates the field for this property.
     */
    private BiFunction<CodeBuilder,PropertySpecImpl,FieldSpec> m_FieldComposer;

    /**
     *  The name of the field for the property.
     */
    private String m_FieldName = null;

    /**
     *  The builder for the getter of this property.
     */
    private MethodSpec.Builder m_GetterBuilder = null;

    /**
     *  The method that creates the getter for this property.
     */
    private BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec> m_GetterComposer;

    /**
     *  The name for the getter method.
     */
    private Name m_GetterMethodName = null;

    /**
     *  The return type for the getter method.
     */
    private TypeName m_GetterReturnType;

    /**
     *  The comment for this property when stored in an {@code INI} file.
     */
    private String m_INIComment = null;

    /**
     *  The group for this property when stored in an {@code INI} file.
     */
    private String m_INIGroup = null;

    /**
     *  The key for this property when stored in an {@code INI} file.
     */
    private String m_INIKey = null;

    /**
     *  The flag that indicates whether the property type is an {@code enum}
     *  type.
     */
    private boolean m_IsEnum = false;

    /**
     *  <p>{@summary The {@code Preferences} accessor class.}</p>
     *  <p>This is used when this property is linked to a preference, but also
     *  to initialise it from a SYSTEM preference.</p>
     *
     *  @see org.tquadrat.foundation.config.Preference#accessor()
     *  @see org.tquadrat.foundation.config.SystemPreference#accessor()
     */
    private TypeName m_PrefsAccessorClass = null;

    /**
     *  <p>{@summary The {@code Preferences} key.}</p>
     *  <p>This is used when this property is linked to a preference, but also
     *  to initialise it from a SYSTEM preference. In first case, the name is
     *  defaulted to the property name, while it is mandatory otherwise.</p>
     *
     *  @see org.tquadrat.foundation.config.Preference#key()
     *  @see org.tquadrat.foundation.config.SystemPreference#key()
     */
    private String m_PrefsKey = null;

    /**
     *  The property flags.
     */
    private final EnumSet<PropertyFlag> m_PropertyFlags = EnumSet.noneOf( PropertyFlag.class );

    /**
     *  The name of the property.
     */
    private final String m_PropertyName;

    /**
     *  The type of the property.
     */
    private TypeName m_PropertyType;

    /**
     *  The name for the setter's argument.
     */
    private Name m_SetterArgumentName = null;

    /**
     *  The builder for the setter of this property.
     */
    private MethodSpec.Builder m_SetterBuilder = null;

    /**
     *  The method that creates the setter for this property.
     */
    private BiFunction<CodeBuilder,PropertySpecImpl,MethodSpec> m_SetterComposer;

    /**
     *  The name for the setter method.
     */
    private Name m_SetterMethodName;

    /**
     *  The speciality type for this property; usually, this is {@code null}.
     */
    private SpecialPropertyType m_SpecialPropertyType = null;

    /**
     *  The class that implements the String converter for the type of this
     *  property.
     */
    private TypeName m_StringConverterClass = null;

    /**
     *  The path for the SYSTEM preferences node that holds the initialisation
     *  data for this property.
     *
     *  @see org.tquadrat.foundation.config.SystemPreference#path()
     */
    private String m_SystemPrefsPath = null;

    /**
     *  The name of the system property that is used to initialise this
     *  property.
     */
    private String m_SystemPropertyName = null;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code PropertySpecImpl} instance.
     *
     *  @param  propertyName    The name of the property.
     */
    public PropertySpecImpl( final String propertyName )
    {
        m_PropertyName = requireNotEmptyArgument( propertyName, "propertyName" );
    }   //  PropertySpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec> createAddMethod( final CodeBuilder codeBuilder )
    {
        final Optional<MethodSpec> retValue = isNull( m_AddMethodComposer ) || hasFlag( GETTER_IS_DEFAULT )
            ? Optional.empty()
            : Optional.of( m_AddMethodComposer.apply( requireNonNullArgument( codeBuilder, "codeBuilder" ), this ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createAddMethod()

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<CodeBlock> createConstructorFragment( final CodeBuilder codeBuilder )
    {
        final Optional<CodeBlock> retValue = isNull( m_ConstructorFragmentComposer ) || hasFlag( GETTER_IS_DEFAULT )
            ? Optional.empty()
            : Optional.of( m_ConstructorFragmentComposer.apply( requireNonNullArgument( codeBuilder, "codeBuilder" ), this ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstructorFragment()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<FieldSpec> createField( final CodeBuilder codeBuilder )
    {
        final Optional<FieldSpec> retValue = isNull( m_FieldComposer ) || hasFlag( GETTER_IS_DEFAULT )
            ? Optional.empty()
            : Optional.of( m_FieldComposer.apply( requireNonNullArgument( codeBuilder, "codeBuilder" ), this ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createField()

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<MethodSpec> createGetter( final CodeBuilder codeBuilder )
    {
        final Optional<MethodSpec> retValue = isNull( m_GetterComposer ) || hasFlag( GETTER_IS_DEFAULT )
            ? Optional.empty()
            : Optional.of( m_GetterComposer.apply( requireNonNullArgument( codeBuilder, "codeBuilder" ), this ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createGetter()

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<MethodSpec> createSetter( final CodeBuilder codeBuilder )
    {
        final Optional<MethodSpec> retValue = isNull( m_SetterComposer ) || hasFlag( GETTER_IS_DEFAULT )
            ? Optional.empty()
            : Optional.of( m_SetterComposer.apply( requireNonNullArgument( codeBuilder, "codeBuilder" ), this ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSetter()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Name getAddMethodArgumentName() { return m_AddMethodArgumentName; }

    /**
     *  <p>{@summary Returns a builder for the 'add' method for this
     *  property.}</p>
     *  <p>This is a convenience method that allows to benefit from
     *  {@link JavaComposer#createMethod(ExecutableElement)}.</p>
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the builder.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Optional<MethodSpec.Builder> getAddMethodBuilder() { return Optional.ofNullable( m_AddMethodBuilder ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<Name> getAddMethodName() { return Optional.ofNullable( m_AddMethodName ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final OptionalInt getCLIArgumentIndex() { return m_CLIArgumentIndex < 0 ? OptionalInt.empty() : OptionalInt.of( m_CLIArgumentIndex ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIFormat() { return Optional.ofNullable( m_CLIFormat ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIMetaVar() { return Optional.ofNullable( m_CLIMetaVar ); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "OptionalContainsCollection" )
    @Override
    public final Optional<List<String>> getCLIOptionNames() { return Optional.ofNullable( m_CLIOptionNames ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIUsage() { return Optional.ofNullable( m_CLIUsage ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getCLIUsageKey() { return Optional.ofNullable( m_CLIUsageKey ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getCLIValueHandlerClass() { return Optional.ofNullable( m_CLIValueHandlerClass ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final CollectionKind getCollectionKind() { return m_CollectionKind; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getEnvironmentDefaultValue() { return Optional.ofNullable( m_EnvironmentDefaultValue ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getEnvironmentVariableName() { return Optional.ofNullable( m_EnvironmentVariableName ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getFieldName()
    {
        final var retValue = isEmptyOrBlank( m_FieldName ) ? PropertySpec.super.getFieldName() : m_FieldName;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getFieldName()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec.Builder> getGetterBuilder() { return Optional.ofNullable( m_GetterBuilder ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<Name> getGetterMethodName() { return Optional.ofNullable( m_GetterMethodName ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getGetterReturnType() { return m_GetterReturnType; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<String> getINIComment() { return Optional.ofNullable( m_INIComment ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<String> getINIGroup() { return Optional.ofNullable( m_INIGroup ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<String> getINIKey() { return Optional.ofNullable( m_INIKey ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getPrefsAccessorClass() { return Optional.ofNullable( m_PrefsAccessorClass ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getPrefsKey() { return Optional.ofNullable( m_PrefsKey ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getPropertyName() { return m_PropertyName; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName getPropertyType() { return m_PropertyType; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Name getSetterArgumentName() { return m_SetterArgumentName; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<MethodSpec.Builder> getSetterBuilder() { return Optional.ofNullable( m_SetterBuilder ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<Name> getSetterMethodName() { return Optional.ofNullable( m_SetterMethodName ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<SpecialPropertyType> getSpecialPropertyType() { return Optional.ofNullable( m_SpecialPropertyType ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<TypeName> getStringConverterClass() { return Optional.ofNullable( m_StringConverterClass ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public Optional<String> getSystemPrefsPath()
    {
        return Optional.ofNullable( m_SystemPrefsPath );
    }   //  getSystemPrefsPath()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getSystemPropertyName() { return Optional.ofNullable( m_SystemPropertyName ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isEnum() { return m_IsEnum; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasFlag( final PropertyFlag flag ) { return m_PropertyFlags.contains( requireNonNullArgument( flag, "flag" ) ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final PropertySpec merge()
    {
        final PropertySpecImpl retValue;
        if( hasFlag( PROPERTY_IS_SPECIAL ) )
        {
            retValue = new PropertySpecImpl( m_SpecialPropertyType.getPropertyName() );
            final var otherSpec = CodeGenerator.getSpecialPropertySpecification( m_SpecialPropertyType );

            final var flags = EnumSet.copyOf( m_PropertyFlags );
            flags.addAll( otherSpec.getAllFlags() );
            retValue.setFlag( flags.toArray( PropertyFlag []::new ) );

            retValue.m_AddMethodArgumentName = m_AddMethodArgumentName;
            getAddMethodBuilder().ifPresent( retValue::setAddMethodBuilder );
            retValue.m_AddMethodComposer = otherSpec.getAddMethodComposer().orElse( m_AddMethodComposer );
            getAddMethodName().ifPresent( retValue::setAddMethodName );
            getCLIArgumentIndex().ifPresent( retValue::setCLIArgumentIndex );
            getCLIFormat().ifPresent( retValue::setCLIFormat );
            getCLIMetaVar().ifPresent( retValue::setCLIMetaVar );
            getCLIOptionNames().ifPresent( retValue::setCLIOptionNames );
            getCLIUsage().ifPresent( retValue::setCLIUsage );
            getCLIUsageKey().ifPresent( retValue::setCLIUsageKey );
            getCLIValueHandlerClass().ifPresent( retValue::setCLIValueHandlerClass );
            retValue.setCollectionKind( otherSpec.getCollectionKind() );
            retValue.m_ConstructorFragmentComposer = otherSpec.getConstructorFragmentComposer().orElse( m_ConstructorFragmentComposer );
            otherSpec.getEnvironmentDefaultValue().ifPresent( retValue::setEnvironmentDefaultValue );
            otherSpec.getEnvironmentVariableName().ifPresentOrElse( retValue::setEnvironmentVariableName, () -> retValue.m_EnvironmentVariableName = null );
            retValue.setFieldName( otherSpec.getFieldName() ); // Has a side effect in setting retValue.m_FieldComposer …
            retValue.m_FieldComposer = otherSpec.getFieldComposer().orElse( m_FieldComposer );
            getGetterBuilder().ifPresent( retValue::setGetterBuilder );
            getGetterMethodName().ifPresent( retValue::setGetterMethodName );
            retValue.m_GetterComposer = otherSpec.getGetterComposer().orElse( m_GetterComposer );
            retValue.setGetterReturnType( otherSpec.getGetterReturnType() );
            otherSpec.getINIComment().ifPresent( c -> retValue.m_INIComment = c );
            otherSpec.getINIGroup().ifPresent( g -> retValue.m_INIGroup = g );
            otherSpec.getINIKey().ifPresent( k -> retValue.m_INIKey = k );
            retValue.setIsEnum( otherSpec.isEnum() );
            otherSpec.getPrefsAccessorClass().ifPresent( retValue::setPrefsAccessorClass );
            otherSpec.getPrefsKey().ifPresent( retValue::setPrefsKey );
            retValue.setPropertyType( otherSpec.getPropertyType() );
            retValue.m_SetterArgumentName = m_SetterArgumentName;
            otherSpec.getSystemPrefsPath().ifPresent( retValue::setSystemPrefsPath );
            getSetterBuilder().ifPresent( retValue::setSetterBuilder );
            retValue.m_SetterComposer = otherSpec.getSetterComposer().orElse( m_SetterComposer );
            getSetterMethodName().ifPresent( retValue::setSetterMethodName );
            retValue.setSpecialPropertyType( m_SpecialPropertyType );
            otherSpec.getStringConverterClass().ifPresent( retValue::setStringConverterClass );
            otherSpec.getSystemPropertyName().ifPresentOrElse( retValue::setSystemPropertyName, () -> retValue.m_SystemPropertyName = null );
        }
        else
        {
            retValue = this;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  merge()

    /**
     *  Sets the name for the 'add' method's argument.
     *
     *  @param  name    The name of the argument.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setAddMethodArgumentName( final Name name )
    {
        m_AddMethodArgumentName = requireNonNullArgument( name, "name" );
    }   //  setAddMethodArgumentName()

    /**
     *  Sets the builder for the 'add' method of this property.
     *
     *  @param  builder The builder; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setAddMethodBuilder( final MethodSpec.Builder builder )
    {
        m_AddMethodBuilder = builder;
    }   //  setAddMethodBuilder()

    /**
     *  Sets the name of the 'add'' method for this property.
     *
     *  @param  name    The name of the setter method.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setAddMethodName( final Name name )
    {
        m_AddMethodName = requireNotEmptyArgument( name, "name" );
        m_AddMethodComposer = getAddMethodComposer();
    }   //  setAddMethodName()

    /**
     *  Sets the index for an argument on the command line.
     *
     *  @param  index   The index, starting by 0. A negative value indicates,
     *      that the value was not set.
     *
     *  @see org.tquadrat.foundation.config.Argument#index()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIArgumentIndex( final int index ) { m_CLIArgumentIndex = index; }

    /**
     *  Sets the special CLI format.
     *
     *  @param  format  The format String; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.Argument#format()
     *  @see org.tquadrat.foundation.config.Option#format()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIFormat( final String format ) { m_CLIFormat = format; }

    /**
     *  Sets the CLI meta variable.
     *
     *  @param  metaVar The meta variable; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.Argument#metaVar()
     *  @see org.tquadrat.foundation.config.Option#metaVar()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIMetaVar( final String metaVar ) { m_CLIMetaVar = metaVar; }

    /**
     *  <p>{@summary Sets the CLI option names.}</p>
     *  <p>The first entry of the list is the option name, the others are the
     *  aliases.</p>
     *
     *  @param  names   The name and the aliases for the CLI option for this
     *      property; can be {@code null}, but may not be empty.
     *
     *  @see org.tquadrat.foundation.config.Option#name()
     *  @see org.tquadrat.foundation.config.Option#aliases()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIOptionNames( final List<String> names )
    {
        m_CLIOptionNames = nonNull( names) ? List.copyOf( requireNotEmptyArgument( names, "names" ) ) : null;
    }   //  setCLIOptionNames()

    /**
     *  Sets the CLI usage text.
     *
     *  @param  text    The usage text; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.Argument#usage()
     *  @see org.tquadrat.foundation.config.Option#usage()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIUsage( final String text )
    {
        m_CLIUsage = text;
    }   //  setCLIUsage()

    /**
     *  Sets the CLI usage key.
     *
     *  @param  key The usage key; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.Argument#usageKey()
     *  @see org.tquadrat.foundation.config.Option#usageKey()
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIUsageKey( final String key )
    {
        m_CLIUsageKey = key;
    }   //  setCLIUsageKey()

    /**
     *  Sets the CLI value handler class.
     *
     *  @param  handlerClass    The
     *      {@link TypeName}
     *      for the value handler class; can be {@code null}.
     *
     *  @see org.tquadrat.foundation.config.Argument#handler()
     *  @see org.tquadrat.foundation.config.Option#handler()
     *  @see org.tquadrat.foundation.config.cli.CmdLineValueHandler
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCLIValueHandlerClass( final TypeName handlerClass )
    {
        m_CLIValueHandlerClass = handlerClass;
    }   //  setCLIValueHandlerClass()

    /**
     *  Sets the kind of collection for this property.
     *
     *  @param  collectionKind  The kind of collection.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setCollectionKind( final CollectionKind collectionKind )
    {
        m_CollectionKind = requireNonNullArgument( collectionKind, "collectionKind" );
    }   //  setCollectionKind()

    /**
     *  <p>{@summary Sets the default value for an environment variable or a
     *  system property.} This is used to initialise this property when it has
     *  the annotation
     *  {@link org.tquadrat.foundation.config.EnvironmentVariable &#64;EnvironmentVariable}
     *  or
     *  {@link org.tquadrat.foundation.config.SystemProperty &#64;EnvironmentVariable},
     *  but no value is provided.</p>
     *  <p>A default value is mandatory when the annotated property has a
     *  primitive type.</p>
     *  <p>A String with the only the {@code NUL} character is treated as
     *  {@code null}.</p>
     *
     *  @param  value   The default value.
     */
    public final void setEnvironmentDefaultValue( final String value )
    {
        m_EnvironmentDefaultValue = NUL.equals( value ) ? null : value;
    }   //  setEnvironmentDefaultValue()

    /**
     *  Sets the name of the environment variable that provides the (initial)
     *  value for this property.
     *
     *  @param  name    The name of the environment variable.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setEnvironmentVariableName( final String name )
    {
        m_EnvironmentVariableName = name;
        if( isNotEmptyOrBlank( name ) )
        {
            m_PropertyFlags.add( ENVIRONMENT_VARIABLE );
            m_ConstructorFragmentComposer = getConstructorFragment4EnvironmentComposer();
        }
        else
        {
            m_PropertyFlags.remove( ENVIRONMENT_VARIABLE );
            m_ConstructorFragmentComposer = null;
        }
    }   //  setEnvironmentVariableName()

    /**
     *  Sets the name of the field for the property.
     *
     *  @param  name    The field name.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setFieldName( final String name )
    {
        m_FieldName = requireNotEmptyArgument( name, "name" );
        m_FieldComposer = getFieldComposer();
    }   //  setFieldName()

    /**
     *  Sets the given flags to the property.
     *
     *  @param  flag    The flags to set.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setFlag( final PropertyFlag... flag )
    {
        final var length = requireNonNullArgument( flag, "flag" ).length;
        switch( length )
        {
            case 0: break /* Do nothing */;
            case 1: m_PropertyFlags.add( flag [0] ); break;
            case 2: m_PropertyFlags.addAll( EnumSet.of( flag [0], flag [1] ) ); break;
            default: m_PropertyFlags.addAll( EnumSet.of( flag [0], copyOfRange( flag, 1, length ) ) ); break;
        }
    }   //  setFlag()

    /**
     *  Sets the builder for the getter of this property.
     *
     *  @param  builder The builder; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setGetterBuilder( final MethodSpec.Builder builder )
    {
        m_GetterBuilder = builder;
    }   //  setGetterBuilder()

    /**
     *  Sets the method name for the getter.
     *
     *  @param  name    The method name.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setGetterMethodName( final Name name )
    {
        m_GetterMethodName = requireNotEmptyArgument( name, "name" );
        m_GetterComposer = getGetterComposer();
    }   //  setGetterMethodName()

    /**
     *  Sets the return type for the getter.
     *
     *  @param  type    The getter's return type.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setGetterReturnType( final TypeName type )
    {
        m_GetterReturnType = requireNonNullArgument( type, "type" );
    }   //  setGetterReturnType()

    /**
     *  Sets the {@code INI} file configuration for this property.
     *
     *  @param  group   The group.
     *  @param  key The key.
     *  @param  comment The comment; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setINIConfiguration( final String group, final String key, final String comment )
    {
        m_INIComment = comment;
        m_INIGroup = requireNotEmptyArgument( group, "group" );
        m_INIKey = requireNotEmptyArgument( key, "key" );
    }   //  setINIConfiguration()

    /**
     *  Sets the {@code INI} file configuration for this property.
     *
     *  @param  configuration   The configuration annotation.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public void setINIConfiguration( final INIValue configuration )
    {
        setINIConfiguration( requireNonNullArgument( configuration, "configuration" ).group(), configuration.key(), configuration.comment() );
    }   //  setINIConfiguration()

    /**
     *  Sets the flag that indicates whether the property is an
     *  {@link Enum enum}
     *  type.
     *
     *  @param  flag    {@code true} if the property type is an {@code enum}
     *      type, {@code false} otherwise.
     */
    public final void setIsEnum( final boolean flag ) { m_IsEnum = flag; }

    /**
     *  Sets the preferences accessor class for this property.
     *
     *  @param  accessorClass   The accessor class; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setPrefsAccessorClass( final TypeName accessorClass )
    {
        m_PrefsAccessorClass = accessorClass;
    }   //  setPrefsAccessorClass()

    /**
     *  Sets the preferences key for this property.
     *
     *  @param  preferenceKey   The key.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setPrefsKey( final String preferenceKey )
    {
        m_PrefsKey = preferenceKey;
    }   //  setPrefsKey

    /**
     *  Sets the property type.
     *
     *  @param  type    The type of the property.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setPropertyType( final TypeName type )
    {
        m_PropertyType = requireNonNullArgument( type, "type" );
    }   //  setPropertyType()

    /**
     *  Sets the name for the setter's argument.
     *
     *  @param  name    The name of the argument.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setSetterArgumentName( final Name name )
    {
        m_SetterArgumentName = requireNonNullArgument( name, "name" );
    }   //  setSetterArgumentName()

    /**
     *  Sets the builder for the setter of this property.
     *
     *  @param  builder The builder; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setSetterBuilder( final MethodSpec.Builder builder )
    {
        m_SetterBuilder = builder;
    }   //  setGetterBuilder()

    /**
     *  Sets the name of the setter method for this property.
     *
     *  @param  name    The name of the setter method.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public void setSetterMethodName( final Name name )
    {
        m_SetterMethodName = requireNotEmptyArgument( name, "name" );
        m_SetterComposer = getSetterComposer();
    }   //  setSetterMethodName()

    /**
     *  Sets the speciality type for this property.
     *
     *  @param  type    The speciality type.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setSpecialPropertyType( final SpecialPropertyType type )
    {
        m_SpecialPropertyType = type;
        if( nonNull( m_SpecialPropertyType ) )
        {
            m_PropertyFlags.add( PROPERTY_IS_SPECIAL );
        }
        else
        {
            m_PropertyFlags.remove( PROPERTY_IS_SPECIAL );
        }
    }   //  setSpecialPropertyType()

    /**
     *  Sets the name for the class that implements
     *  {@link org.tquadrat.foundation.lang.StringConverter}
     *  for the type of this property.
     *
     *  @param  typeName    The String converter class; can be {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setStringConverterClass( final TypeName typeName )
    {
        m_StringConverterClass = typeName;
    }   //  setStringConverterClass()

    /**
     *  Sets the path for the SYSTEM {@code Preferences} node that holds the
     *  initialisation data for this property.
     *
     *  @param  path    The path.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setSystemPrefsPath( final String path )
    {
        m_SystemPrefsPath = path;
        if( isNotEmptyOrBlank( path ) )
        {
            m_PropertyFlags.add( SYSTEM_PREFERENCE );
            m_ConstructorFragmentComposer = getConstructorFragment4SystemPreferenceComposer();
        }
        else
        {
            m_PropertyFlags.remove( SYSTEM_PREFERENCE );
            m_ConstructorFragmentComposer = null;
        }
    }   //  setSystemPrefsPath()

    /**
     *  Sets the name of the system property that provides the (initial) value
     *  for this property.
     *
     *  @param  name    The name of the system property.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setSystemPropertyName( final String name )
    {
        m_SystemPropertyName = name;
        if( isNotEmptyOrBlank( name ) )
        {
            m_PropertyFlags.add( SYSTEM_PROPERTY );
            m_ConstructorFragmentComposer = getConstructorFragment4SystemPropComposer();
        }
        else
        {
            m_PropertyFlags.remove( SYSTEM_PROPERTY );
            m_ConstructorFragmentComposer = null;
        }
    }   //  setSystemPropertyName()
}
//  class PropertySpecImpl

/*
 *  End of File
 */
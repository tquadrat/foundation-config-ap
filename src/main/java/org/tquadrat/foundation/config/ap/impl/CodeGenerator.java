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

import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CHARSET;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_CLOCK;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_LOCALE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_MESSAGEPREFIX;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_PID;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RANDOM;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_RESOURCEBUNDLE;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_SESSION;
import static org.tquadrat.foundation.config.SpecialPropertyType.CONFIG_PROPERTY_TIMEZONE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.MSG_MissingInterface;
import static org.tquadrat.foundation.config.ap.PropertySpec.PropertyFlag.PROPERTY_IS_MUTABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.OVERLY_COMPLEX_CLASS;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.OVERLY_COUPLED_CLASS;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.ap.CodeGenerationError;
import org.tquadrat.foundation.config.CLIBeanSpec;
import org.tquadrat.foundation.config.ConfigBeanSpec;
import org.tquadrat.foundation.config.I18nSupport;
import org.tquadrat.foundation.config.INIBeanSpec;
import org.tquadrat.foundation.config.PreferencesBeanSpec;
import org.tquadrat.foundation.config.SessionBeanSpec;
import org.tquadrat.foundation.config.SpecialPropertyType;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor;
import org.tquadrat.foundation.config.ap.impl.codebuilders.CLIBeanBuilder;
import org.tquadrat.foundation.config.ap.impl.codebuilders.CodeGeneratorContext;
import org.tquadrat.foundation.config.ap.impl.codebuilders.ConfigBeanBuilder;
import org.tquadrat.foundation.config.ap.impl.codebuilders.I18nSupportBuilder;
import org.tquadrat.foundation.config.ap.impl.codebuilders.INIBeanBuilder;
import org.tquadrat.foundation.config.ap.impl.codebuilders.MapImplementor;
import org.tquadrat.foundation.config.ap.impl.codebuilders.PreferencesBeanBuilder;
import org.tquadrat.foundation.config.ap.impl.codebuilders.SessionBeanBuilder;
import org.tquadrat.foundation.config.ap.impl.specialprops.CharsetProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.ClockProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.LocaleProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.MessagePrefixProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.ProcessIdProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.RandomProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.ResourceBundleProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.SessionKeyProperty;
import org.tquadrat.foundation.config.ap.impl.specialprops.TimeZoneProperty;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  Generates the code for the new configuration bean.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeGenerator.java 1001 2022-01-29 16:42:15Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "OverlyCoupledClass" )
@ClassVersion( sourceVersion = "$Id: CodeGenerator.java 1001 2022-01-29 16:42:15Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class CodeGenerator implements CodeGeneratorContext
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The class builder.
     */
    private final TypeSpec.Builder m_ClassBuilder;

    /**
     *  The instance of
     *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}
     *  that is used for the code generation.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The configuration for the generation process.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final CodeGenerationConfiguration m_Configuration;

    /**
     *  The builder for the constructor.
     */
    private final MethodSpec.Builder m_Constructor;

    /**
     *  The builder for body of the constructor.
     */
    private final CodeBlock.Builder m_ConstructorCode;

    /**
     *  The list of the suppressed warnings for the constructor of the new
     *  configuration bean.
     */
    private final Set<SuppressableWarnings> m_SuppressedWarningsForConstructor = EnumSet.noneOf( SuppressableWarnings.class );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The special properties.
     */
    private static final Map<SpecialPropertyType,SpecialPropertySpec> m_SpecialProperties;

    static
    {
        final Map<SpecialPropertyType,SpecialPropertySpec> map = new EnumMap<>( SpecialPropertyType.class );
        map.put( CONFIG_PROPERTY_CHARSET, new CharsetProperty() );
        map.put( CONFIG_PROPERTY_CLOCK, new ClockProperty() );
        map.put( CONFIG_PROPERTY_LOCALE, new LocaleProperty() );
        map.put( CONFIG_PROPERTY_MESSAGEPREFIX, new MessagePrefixProperty() );
        map.put( CONFIG_PROPERTY_PID, new ProcessIdProperty() );
        map.put( CONFIG_PROPERTY_RANDOM, new RandomProperty() );
        map.put( CONFIG_PROPERTY_RESOURCEBUNDLE, new ResourceBundleProperty() );
        map.put( CONFIG_PROPERTY_SESSION, new SessionKeyProperty() );
        map.put( CONFIG_PROPERTY_TIMEZONE, new TimeZoneProperty() );
        m_SpecialProperties = unmodifiableMap( map );
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code CodeGenerator}.
     *
     *  @param  configuration   The configuration for the generation process.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeGenerator( final CodeGenerationConfiguration configuration )
    {
        m_Configuration = requireNonNullArgument( configuration, "configuration" );
        m_Composer = m_Configuration.getComposer();

        m_ClassBuilder = m_Composer.classBuilder( m_Configuration.getClassName() )
            .addSuppressableWarning( OVERLY_COUPLED_CLASS )
            .addSuppressableWarning( OVERLY_COMPLEX_CLASS );

        m_Constructor = m_Composer.constructorBuilder();
        m_ConstructorCode = m_Composer.codeBlockBuilder();
    }   //  CodeGenerator()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    public final void addConstructorSuppressedWarning( final SuppressableWarnings warning )
    {
        if( nonNull( warning ) ) m_SuppressedWarningsForConstructor.add( warning );
    }   //  addConstructorSuppressedWarning()

    /**
     *  Generates the code from the configuration provided in the constructor.
     *
     *  @return The generated code.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final JavaFile createCode()
    {
        /*
         * If the configuration bean specification implements I18nSupport, no
         * setter for the resource bundle is allowed, but the getter modifies
         * the attribute for the resource bundle if the locale changes;
         * therefore we need to ensure that this attribute is not final.
         */
        if( m_Configuration.implementInterface( I18nSupport.class ) )
        {
           m_Configuration.getProperty( CONFIG_PROPERTY_RESOURCEBUNDLE.getPropertyName() ).ifPresent( p -> ((PropertySpecImpl) p).setFlag( PROPERTY_IS_MUTABLE )  );
        }

        /*
         *  The interface ConfigBeanSpec is mandatory; if this is not extended
         *  by the current configuration bean specification, we are done …
         */
        if( !m_Configuration.implementInterface( ConfigBeanSpec.class ) )
        {
            throw new CodeGenerationError( format( MSG_MissingInterface, m_Configuration.getSpecification().canonicalName(), ConfigBeanSpec.class.getName() ) );
        }
        new ConfigBeanBuilder( this ).build();

        /*
         *  The configuration bean specification can extend the interface
         *  I18nSupport.
         */
        if( m_Configuration.implementInterface( I18nSupport.class ) )
        {
            new I18nSupportBuilder( this ).build();
        }

        /*
         *  The configuration bean specification can extend the interface
         *  SessionBeanSpec.
         */
        if( m_Configuration.implementInterface( SessionBeanSpec.class ) )
        {
            new SessionBeanBuilder( this ).build();
        }

        /*
         *  The configuration bean specification can extend the interface
         *  Map<String,Object>.
         */
        if( m_Configuration.implementInterface( Map.class ) )
        {
            new MapImplementor( this ).build();
        }

        /*
         *  The configuration bean specification can extend the interface
         *  CLIBeanSpec.
         */
        if( m_Configuration.implementInterface( CLIBeanSpec.class ) )
        {
            new CLIBeanBuilder( this ).build();
        }

        /*
         *  The configuration bean specification extends the interface
         *  PreferencesBeanSpec.
         */
        if( m_Configuration.implementInterface( PreferencesBeanSpec.class ) )
        {
            new PreferencesBeanBuilder( this ).build();
        }

        /*
         *  The configuration bean specification extends the interface
         *  INIBeanSpec.
         */
        if( m_Configuration.implementInterface( INIBeanSpec.class ) )
        {
            new INIBeanBuilder( this ).build();
        }

        //---* Build the constructor *-----------------------------------------
        var constructorBody = m_ConstructorCode.build();
        if( constructorBody.isEmpty() )
        {
            constructorBody = m_Composer.codeBlockOf(
                """
                /* Just exists */
                """ );
        }

        //---* Create the @SuppressWarnings annotation *-----------------------
        m_Composer.createSuppressWarningsAnnotation( m_SuppressedWarningsForConstructor )
            .ifPresent( m_Constructor::addAnnotation );

        //---* Finish the constructor *----------------------------------------
        final var constructor = m_Constructor
            .addJavadoc(
                """
                Creates a new {@code $L} instance.
                """, m_Configuration.getClassName() )
            .addModifiers( PUBLIC )
            .addCode( constructorBody )
            .build();

        //---* Finish the class *----------------------------------------------
        final var sourceVersion = format( "Generated through %1$s at %2$s", ConfigAnnotationProcessor.class.getName(), m_Configuration.getBuildTime().toString() );
        m_Configuration.getBaseClass().ifPresent( m_ClassBuilder::superclass );
        final var configurationBean = m_ClassBuilder.addSuperinterface( m_Configuration.getSpecification() )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( m_Composer.createClassVersionAnnotation( sourceVersion ) )
            .addJavadoc(
                """
                The configuration bean that implements
                {@link $T}.
                """,
                m_Configuration.getSpecification() )
            .addMethod( constructor )
            .build();

        //---* Create the return value *---------------------------------------
        final var retValue = m_Composer.javaFileBuilder( m_Configuration.getPackageName(), configurationBean )
            .addFileComment(
                """
                ============================================================================
                This file inherits the copyright and license(s) from the interface that is
                implemented by the class
                
                    $1L.$2N
                  
                Refer to
                
                    $3L
                    
                and the file comment there for the details.
                ============================================================================""",
                m_Configuration.getPackageName(), configurationBean, m_Configuration.getSpecification().canonicalName()
            )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createCode()

    /**
     * {@inheritDoc}
     */
    @Override
    public final TypeSpec.Builder getClassBuilder() { return m_ClassBuilder; }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final JavaComposer getComposer() { return m_Composer; }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final CodeGenerationConfiguration getConfiguration() { return m_Configuration; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MethodSpec.Builder getConstructorBuilder() { return m_Constructor; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CodeBlock.Builder getConstructorCodeBuilder() { return m_ConstructorCode; }

    /**
     *  Returns the definition for the special property type.
     *
     *  @param  type    The special property type.
     *  @return The special property specification.
     */
    public static final SpecialPropertySpec getSpecialPropertySpecification( final SpecialPropertyType type )
    {
        final var retValue = m_SpecialProperties.get( requireNonNullArgument( type, "type" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getSpecialPropertySpecification
}
//  class CodeGenerator

/*
 *  End of File
 */
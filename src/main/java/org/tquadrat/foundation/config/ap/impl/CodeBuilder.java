/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.METHODNAME_ConfigBeanSpec_AddListener;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.METHODNAME_ConfigBeanSpec_GetResourceBundle;
import static org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor.METHODNAME_ConfigBeanSpec_InitData;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.config.ap.CodeGenerationConfiguration;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;

/**
 *  An API to the internals of the code builders.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public interface CodeBuilder
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The standard fields.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $" )
    @API( status = MAINTAINED, since = "0.2.0" )
    public enum StandardField
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  The registry for the preferences accessors.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_Accessors( "m_AccessorsRegistry" ),

        /**
         *  The CLI definitions.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_CLIDefinitions( "m_CLIDefinitions" ),

        /**
         *  The CLI error.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_CLIError( "m_CLIErrorMessage" ),

        /**
         *  The INIFile instance.
         */
        @API( status = MAINTAINED, since = "0.1.0" )
        STD_FIELD_INIFile( "m_INIFile" ),

        /**
         *  The file name for the INIFile.
         */
        @API( status = MAINTAINED, since = "0.1.0" )
        STD_FIELD_INIFileName( "m_INIFilePath" ),

        /**
         *  The listener support.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_ListenerSupport( "m_ListenerSupport" ),

        /**
         *  The listener for preference changes.
         */
        @API( status = MAINTAINED, since = "0.1.0" )
        STD_FIELD_PreferenceChangeListener( "m_PreferenceChangeListener" ),

        /**
         *  The preferences root.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_PreferencesRoot( "m_PreferencesRoot" ),

        /**
         *  The read lock.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_ReadLock( "m_ReadLock" ),

        /**
         *  The registry for the properties when the Map interface needs to be
         *  implemented.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_Registry( "m_ShadowMap" ),

        /**
         *  The
         *  {@link java.util.Locale}
         *  for the current resource bundle.
         */
        @API( status = MAINTAINED, since = "0.0.2" )
        STD_FIELD_ResourceLocale( "m_CurrentResourceBundleLocale" ),

        /**
         *  The current
         *  {@link java.util.ResourceBundle}.
         */
        @API( status = MAINTAINED, since = "0.0.2" )
        STD_FIELD_ResourceBundle( "m_ResourceBundle" ),

        /**
         *  The system preferences.
         *
         *  @deprecated Obsolete now.
         */
        @Deprecated( since = "0.1.0", forRemoval = true )
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_SystemPreferences( "m_SystemPreferences" ),

        /**
         *  The user preferences.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_UserPreferences( "m_UserPreferences" ),

        /**
         *  The "write" lock.
         */
        @API( status = MAINTAINED, since = "0.0.1" )
        STD_FIELD_WriteLock( "m_WriteLock" );

            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The field name.
         */
        private final String m_FieldName;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code StandardField} instance.
         *
         *  @param  fieldName   The field name.
         */
        private StandardField( final String fieldName )
        {
            m_FieldName = fieldName;
        }   //  StandardField()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Returns the field name for this standard field, as it is used in the
         *  generated code.
         *
         *  @return The field name.
         */
        @Override
        public final String toString() { return m_FieldName; }
    }
    //  enum StandardField

    /**
     *  The standard methods.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: CodeBuilder.java 946 2021-12-23 14:48:19Z tquadrat $" )
    @API( status = MAINTAINED, since = "0.2.0" )
    public enum StandardMethod
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  The method to add listeners.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_AddListener( METHODNAME_ConfigBeanSpec_AddListener ),

        /**
         *  The method that returns the message prefix.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_GetMessagePrefix( "getMessagePrefix" ),

        /**
         *  The method that returns the ressource bundle for the messages.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_GetRessourceBundle( METHODNAME_ConfigBeanSpec_GetResourceBundle ),

        /**
         *  The method that provides initialisation data.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_InitData( METHODNAME_ConfigBeanSpec_InitData ),

        /**
         *  The method to remove listeners.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_RemoveListener( "removeListener" ),

        /**
         *  The
         *  {@link Object#toString()}
         *  method.
         */
        @API( status = MAINTAINED, since = "0.2.0" )
        STD_METHOD_ToString( "toString" );

            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The method name.
         */
        private final String m_MethodName;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code StandardMethod} instance.
         *
         *  @param  methodName  The method name.
         */
        private StandardMethod( final String methodName )
        {
            m_MethodName = methodName;
        }   //  StandardMethod()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Returns the method name for this standard method, as it is used in the
         *  generated code.
         *
         *  @return The method name.
         */
        @Override
        public final String toString() { return m_MethodName; }
    }
    //  enum StandardMethod

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Generates the relevant code.
     */
    public void build();

    /**
     *  Provides access to the composer.
     *
     *  @return The composer.
     */
    public JavaComposer getComposer();

    /**
     *  Provides access to the code builder configuration.
     *
     *  @return The configuration.
     */
    public CodeGenerationConfiguration getConfiguration();

    /**
     *  Returns the specification for a standard field.
     *
     *  @param  reference   The identifier for the standard field.
     *  @return The field.
     */
    public FieldSpec getField( final StandardField reference );

    /**
     *  Returns the specification for a standard method.
     *
     *  @param  reference   The identifier for the standard method.
     *  @return The method.
     */
    public MethodSpec getMethod( final StandardMethod reference );
}
//  interface CodeBuilder

/*
 *  End of File
 */
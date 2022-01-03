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

/**
 *  <p>{@summary This is the Annotation Processor for the module
 *  {@code org.tquadrat.foundation.config}.} That module provides a facility
 *  for the runtime configuration of a program. The core component is a class,
 *  the {@code Configuration}, that holds the configuration values. Basically,
 *  this class is a POJO (or, more precisely, a JavaBean) that this Annotation
 *  Processor generates during compilation from an annotated interface, the
 *  <i>configuration bean specification</i>. For the details, refer to the
 *  {@code org.tquadrat.foundation.config} module itself.</p>
 *
 *  <p>The Annotation Processor can be used with with plain {@code javac} as
 *  well as with Maven, Gradle or ant.</p>
 *  <p>That {@code javac} can find the Annotation Processor requires to add the
 *  {@code *.jar} file with this code (it should be something like
 *  {@code org.tquadrat.foundation.config.ap-<version>.jar}) to the CLASSPATH;
 *  from there <code>javac</code> will detect it automatically. Alternatively,
 *  the class name for the Annotation Processor,
 *  {@link org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor},
 *  can be provided explicitly with the {@code processor} option.</p>
 *  <p>Finally, the destination for the generated sources has to be set with
 *  the {@code -s} option.</p>
 *
 *  <div style="border-style: solid; border-radius: 8px; margin-left: 10px; padding-left:5px; padding-right:5px;">
 *  <p>The {@href https://docs.oracle.com/javase/10/tools/javac.htm documentation for <code>javac</code>}
 *  says &quot;<cite>[&hellip;]Unless annotation processing is disabled with
 *  the {@code -proc:none} option, the compiler searches for any annotation
 *  processors that are available. The search path can be specified with the
 *  {@code -processorpath} option. If no path is specified, then the user class
 *  path is used. Processors are located by means of service
 *  provider-configuration files named
 *  {@code META-INF/services/javax.annotation.processing.Processor}
 *  on the search path. Such files should contain the names of any annotation
 *  processors to be used, listed one per line. Alternatively, processors can
 *  be specified explicitly, using the {@code -processor} option.
 *  [&hellip;]</cite>&quot;</p>
 *  <p>Tests so far are showing inconsistent results with the auto-detection
 *  mechanism, so the recommendation is to use the {@code -processor} option
 *  always instead of relying on the auto-detection mechanism.</p></div>
 *
 *  <p>To use the Annotation Processor with Maven, the following lines must be
 *  added to the {@code pom.xml} of the project to build:</p>
 *  <blockquote><pre><code>&hellip;
 *  &hellip;
 *  &lt;build&gt;
 *      &hellip;
 *      &lt;plugins&gt;
 *          &hellip;
 *          &lt;plugin&gt;
 *              &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *              &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
 *              &hellip;
 *              &lt;configuration&gt;
 *                  &hellip;
 *                  &lt;annotationProcessorPaths&gt;
 *                      &lt;annotationProcessorPath&gt;
 *                          &lt;groupId&gt;org.tquadrat.tool&lt;/groupId&gt;
 *                          &lt;artifactId&gt;org.tquadrat.foundation.config.ap&lt;/artifactId&gt;
 *                          &lt;version&gt;<i>proper_version</i>&lt;/version&gt;
 *                      &lt;/annotationProcessorPath&gt;
 *                      &hellip;
 *                  &lt;/annotationProcessorPaths&gt;
 *                  &hellip;
 *              &lt;/configuration&gt;
 *              &hellip;
 *              &lt;executions&gt;
 *                  &hellip;
 *                  &lt;execution&gt;
 *                      &lt;id&gt;default-compile&lt;/id&gt;
 *                      &lt;phase&gt;compile&lt;/phase&gt;
 *                      &lt;goals&gt;
 *                          &lt;goal&gt;compile&lt;/goal&gt;
 *                      &lt;/goals&gt;
 *                      &lt;configuration&gt;
 *                          &hellip;
 *                          &lt;annotationProcessors&gt;
 *                              &lt;annotationProcessor&gt;org.tquadrat.foundation.config.ap.ConfigAnnotationProcessor&lt;/annotationProcessor&gt;
 *                              &hellip;
 *                          &lt;/annotationProcessors&gt;
 *                          &hellip;
 *                          &lt;compilerArgs&gt;
 *                              &hellip;
 *                              &lt;arg&gt;-Aorg.tquadrat.foundation.ap.maven.goal=compile&lt;/arg&gt;
 *                              &hellip;
 *                          &lt;/compilerArgs&gt;
 *                          &hellip;
 *                      &lt;/configuration&gt;
 *                      &hellip;
 *                  &lt;/execution&gt;
 *                  &hellip;
 *              &lt;/executions&gt;
 *              &hellip;
 *          &lt;/plugin&gt;
 *          &hellip;
 *      &lt;/plugins&gt;
 *      &hellip;
 *  &lt;/build&gt;</code></pre></blockquote>
 *
 *  TODO Add the configuration for Gradle and for ant.
 *
 *  <p>Unless otherwise stated, {@code null} argument values will cause
 *  methods and constructors of all classes in this package to throw an
 *  {@link java.lang.Exception Exception},
 *  usually a
 *  {@link org.tquadrat.foundation.exception.NullArgumentException},
 *  but in some rare cases, it could be also a
 *  {@link java.lang.NullPointerException}.</p>
 */

package org.tquadrat.foundation.config.ap;

/*
 *  End of File
 */
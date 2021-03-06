/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import groovy.util.GroovyTestCase
import org.jasig.portal.persondir.AttributeDuplicatingPersonAttributesScript
import org.jasig.services.persondir.IPersonAttributeScriptDao

class AttributeDuplicatingPersonAttributesScriptTest extends GroovyTestCase {

    /**
     * Test that an AttributeDuplicatingPersonAttributesScript configured to duplicate the username attribute to the
     * uid and the user.login.id attribute , when presented with a user with a username attribute,
     * does that duplication.
     */
    void testUsernameAttributeDuplicatesToAdditionalAttributes() {
        IPersonAttributeScriptDao dao = new AttributeDuplicatingPersonAttributesScript(
                "username", new HashSet<String>(["uid", "user.login.id"]))
        Map<String, List<Object>> actual = dao.getPersonAttributesFromMultivaluedAttributes(
                [username: ['tomThumb'].asList()])
        assertEquals("username should have duplicated to uid and user.login.id attributes for three total attributes.",
                3, actual.size());
        Map<String, List<Object>> expected =
          [username: ['tomThumb'].asList(), uid: ['tomThumb'].asList(), "user.login.id": ['tomThumb'].asList() ]
        assertEquals("username value should have duplicated to uid and user.login.id values.", expected, actual);
    }

    /**
     * Test that an AttributeDuplicatingPersonAttributesScript configured to duplicate the attitude attribute does
     * not duplicate the attribute when presented with a user without that attribute.
     */
    void testNonMatchingUserAttributesDoNotDuplicate() {

        IPersonAttributeScriptDao duplicator =
                new AttributeDuplicatingPersonAttributesScript("attitude", new HashSet(["bearing", "demeanor"]));

        Map expected = [username: ['gazda']]

        Map actual = duplicator.getPersonAttributesFromMultivaluedAttributes(expected);

        assertEquals "Duplicator should have been a no-op", expected , actual


    }
}

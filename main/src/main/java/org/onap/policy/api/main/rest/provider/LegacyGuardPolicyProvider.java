/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy API
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.api.main.rest.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;
import org.onap.policy.models.base.PfConceptKey;
import org.onap.policy.models.base.PfModelException;
import org.onap.policy.models.pdp.concepts.PdpGroup;
import org.onap.policy.models.pdp.concepts.PdpGroupFilter;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;
import org.onap.policy.models.tosca.legacy.concepts.LegacyGuardPolicyInput;
import org.onap.policy.models.tosca.legacy.concepts.LegacyGuardPolicyOutput;

/**
 * Class to provide all kinds of legacy guard policy operations.
 *
 * @author Chenfei Gao (cgao@research.att.com)
 */
public class LegacyGuardPolicyProvider extends CommonModelProvider {

    private static final String INVALID_POLICY_VERSION = "legacy policy version is not an integer";
    private static final String LEGACY_MINOR_PATCH_SUFFIX = ".0.0";
    private static final Map<String, PfConceptKey> GUARD_POLICY_TYPE_MAP = new LinkedHashMap<>();

    static {
        GUARD_POLICY_TYPE_MAP.put("guard.frequency.",
                new PfConceptKey("onap.policies.controlloop.guard.FrequencyLimiter:1.0.0"));
        GUARD_POLICY_TYPE_MAP.put("guard.minmax.",
                new PfConceptKey("onap.policies.controlloop.guard.MinMax:1.0.0"));
        GUARD_POLICY_TYPE_MAP.put("guard.blacklist.",
                new PfConceptKey("onap.policies.controlloop.guard.Blacklist:1.0.0"));
    }

    /**
     * Default constructor.
     */
    public LegacyGuardPolicyProvider() throws PfModelException {
        super();
    }

    /**
     * Retrieves a list of guard policies matching specified ID and version.
     *
     * @param policyId the ID of policy
     * @param policyVersion the version of policy
     *
     * @return the map of LegacyGuardPolicyOutput objects
     */
    public Map<String, LegacyGuardPolicyOutput> fetchGuardPolicy(String policyId, String policyVersion)
            throws PfModelException {

        if (policyVersion != null) {
            validNumber(policyVersion, INVALID_POLICY_VERSION);
        }
        return modelsProvider.getGuardPolicy(policyId, policyVersion);
    }

    /**
     * Retrieves a list of deployed guard policies in each pdp group.
     *
     * @param policyId the ID of the policy
     *
     * @return a list of deployed policies in each pdp group
     *
     * @throws PfModelException the PfModel parsing exception
     */
    public Map<Pair<String, String>, Map<String, LegacyGuardPolicyOutput>> fetchDeployedGuardPolicies(String policyId)
            throws PfModelException {

        return collectDeployedPolicies(
                policyId, getGuardPolicyType(policyId), modelsProvider::getGuardPolicy, Map::putAll, new HashMap<>());
    }

    /**
     * Creates a new guard policy.
     *
     * @param body the entity body of policy
     *
     * @return the map of LegacyGuardPolicyOutput objectst
     */
    public Map<String, LegacyGuardPolicyOutput> createGuardPolicy(LegacyGuardPolicyInput body)
            throws PfModelException {

        return modelsProvider.createGuardPolicy(body);
    }

    /**
     * Deletes the guard policies matching specified ID and version.
     *
     * @param policyId the ID of policy
     * @param policyVersion the version of policy
     *
     * @return the map of LegacyGuardPolicyOutput objects
     */
    public Map<String, LegacyGuardPolicyOutput> deleteGuardPolicy(String policyId, String policyVersion)
            throws PfModelException {

        validNumber(policyVersion, INVALID_POLICY_VERSION);
        validateDeleteEligibility(policyId, policyVersion);

        return modelsProvider.deleteGuardPolicy(policyId, policyVersion);
    }

    /**
     * Validates whether specified policy can be deleted based on the rule that deployed policy cannot be deleted.
     *
     * @param policyId the ID of policy
     * @param policyVersion the version of policy
     *
     * @throws PfModelException the PfModel parsing exception
     */
    private void validateDeleteEligibility(String policyId, String policyVersion) throws PfModelException {

        List<ToscaPolicyIdentifier> policies = new ArrayList<>();
        policies.add(new ToscaPolicyIdentifier(policyId, policyVersion + LEGACY_MINOR_PATCH_SUFFIX));
        PdpGroupFilter pdpGroupFilter = PdpGroupFilter.builder().policyList(policies).build();

        List<PdpGroup> pdpGroups = modelsProvider.getFilteredPdpGroups(pdpGroupFilter);

        if (!pdpGroups.isEmpty()) {
            throw new PfModelException(Response.Status.CONFLICT,
                    constructDeletePolicyViolationMessage(policyId, policyVersion, pdpGroups));
        }
    }

    /**
     * Retrieves guard policy type given guard policy ID.
     *
     * @param policyId the ID of guard policy
     *
     * @return the concept key of guard policy type
     *
     * @throws PfModelException the PfModel parsing exception
     */
    private PfConceptKey getGuardPolicyType(String policyId) throws PfModelException {

        for (Entry<String, PfConceptKey> guardPolicyTypeEntry : GUARD_POLICY_TYPE_MAP.entrySet()) {
            if (policyId.startsWith(guardPolicyTypeEntry.getKey())) {
                return guardPolicyTypeEntry.getValue();
            }
        }
        throw new PfModelException(Response.Status.BAD_REQUEST, "No policy type defined for " + policyId);
    }
}
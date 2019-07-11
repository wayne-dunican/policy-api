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
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.onap.policy.api.main.parameters.ApiParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.models.base.PfModelException;
import org.onap.policy.models.pdp.concepts.PdpGroup;
import org.onap.policy.models.provider.PolicyModelsProvider;
import org.onap.policy.models.provider.PolicyModelsProviderFactory;
import org.onap.policy.models.provider.PolicyModelsProviderParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Super class for providers that use a model provider.
 */
public class CommonModelProvider implements AutoCloseable {

    protected final PolicyModelsProvider modelsProvider;

    /**
     * Constructs the object, populating {@link #modelsProvider}.
     *
     * @throws PfModelException if an error occurs
     */
    public CommonModelProvider() throws PfModelException {

        ApiParameterGroup parameterGroup = ParameterService.get("ApiGroup");
        PolicyModelsProviderParameters providerParameters = parameterGroup.getDatabaseProviderParameters();
        modelsProvider = new PolicyModelsProviderFactory().createPolicyModelsProvider(providerParameters);
    }

    /**
     * Closes the connection to database.
     *
     * @throws PfModelException the PfModel parsing exception
     */
    @Override
    public void close() throws PfModelException {

        modelsProvider.close();
    }

    /**
     * Checks if service template contains any policy.
     *
     * @param serviceTemplate the service template to check against
     *
     * @return boolean whether service template contains any policy
     */
    protected boolean hasPolicy(ToscaServiceTemplate serviceTemplate) {

        return hasData(serviceTemplate.getToscaTopologyTemplate().getPolicies());
    }

    /**
     * Checks if service template contains any policy type.
     *
     * @param serviceTemplate the service template to check against
     *
     * @return boolean whether service template contains any policy type
     */
    protected boolean hasPolicyType(ToscaServiceTemplate serviceTemplate) {

        return hasData(serviceTemplate.getPolicyTypes());
    }

    /**
     * Checks if the first element of a list contains data.
     *
     * @param list list to be examined
     * @return {@code true} if the list contains data, {@code false} otherwise
     */
    protected <T> boolean hasData(List<Map<String, T>> list) {

        return (list != null && !list.isEmpty() && !list.get(0).isEmpty());
    }

    /**
     * Validates that some text represents a number.
     *
     * @param text text to be validated
     * @param errorMsg error message included in the exception, if the text is not a valid
     *        number
     * @throws PfModelException if the text is not a valid number
     */
    protected void validNumber(String text, String errorMsg) throws PfModelException {
        try {
            Integer.parseInt(text);

        } catch (NumberFormatException exc) {
            throw new PfModelException(Response.Status.BAD_REQUEST, errorMsg, exc);
        }
    }

    /**
     * Constructs returned message for policy delete rule violation.
     *
     * @param policyId the ID of policy
     * @param policyVersion the version of policy
     * @param pdpGroups the list of pdp groups
     *
     * @return the constructed message
     */
    protected String constructDeletePolicyViolationMessage(String policyId, String policyVersion,
                    List<PdpGroup> pdpGroups) {

        List<String> pdpGroupNameVersionList = new ArrayList<>(pdpGroups.size());
        for (PdpGroup pdpGroup : pdpGroups) {
            pdpGroupNameVersionList.add(pdpGroup.getName() + ":" + pdpGroup.getVersion());
        }
        String deployedPdpGroups = String.join(",", pdpGroupNameVersionList);
        return "policy with ID " + policyId + ":" + policyVersion
                        + " cannot be deleted as it is deployed in pdp groups " + deployedPdpGroups;
    }

    /**
     * Constructs returned message for policy type delete rule violation.
     *
     * @param policyTypeId the ID of policy type
     * @param policyTypeVersion the version of policy type
     * @param policies the list of policies that parameterizes specified policy type
     *
     * @return the constructed message
     */
    protected String constructDeletePolicyTypeViolationMessage(String policyTypeId, String policyTypeVersion,
                    List<ToscaPolicy> policies) {

        List<String> policyNameVersionList = new ArrayList<>(policies.size());
        for (ToscaPolicy policy : policies) {
            policyNameVersionList.add(policy.getName() + ":" + policy.getVersion());
        }
        String parameterizedPolicies = String.join(",", policyNameVersionList);
        return "policy type with ID " + policyTypeId + ":" + policyTypeVersion
                        + " cannot be deleted as it is parameterized by policies " + parameterizedPolicies;
    }
}

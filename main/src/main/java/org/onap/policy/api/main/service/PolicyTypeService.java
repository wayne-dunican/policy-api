/*
 *  ============LICENSE_START=======================================================
 *   Copyright (C) 2022 Bell Canada. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================;
 */

package org.onap.policy.api.main.service;

import lombok.RequiredArgsConstructor;
import org.onap.policy.api.main.repository.PolicyTypeRepository;
import org.onap.policy.models.base.PfConceptKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PolicyTypeService {

    private final PolicyTypeRepository policyTypeRepository;

    /**
     * Delete the specified policyType.
     *
     * @param policyTypeKey the policy type key containing name and version
     */
    public void deletePolicyType(final PfConceptKey policyTypeKey) {
        policyTypeRepository.deleteById(policyTypeKey);
    }
}
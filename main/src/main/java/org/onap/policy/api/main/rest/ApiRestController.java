/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy API
 * ================================================================================
 * Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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

package org.onap.policy.api.main.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.BasicAuthDefinition;
import io.swagger.annotations.Info;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.policy.api.main.rest.provider.HealthCheckProvider;
import org.onap.policy.api.main.rest.provider.PolicyProvider;
import org.onap.policy.api.main.rest.provider.PolicyTypeProvider;
import org.onap.policy.api.main.rest.provider.StatisticsProvider;
import org.onap.policy.common.endpoints.report.HealthCheckReport;
import org.onap.policy.model.tosca.ToscaPolicy;
import org.onap.policy.model.tosca.ToscaPolicyList;
import org.onap.policy.model.tosca.ToscaPolicyType;
import org.onap.policy.model.tosca.ToscaPolicyTypeList;

/**
 * Class to provide REST API services.
 */
@Path("/policy/api/v1")
@Api(value = "Policy Design API")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SwaggerDefinition(info = @Info(
        description = "Policy Design API is publicly exposed for clients to Create/Read/Update/Delete"
                    + "policy types, policy type implementation and policies which can be recognized"
                    + "and executable by incorporated policy engines XACML, Drools and APEX. It is a"
                    + "standalone component running rest service that takes all policy design API calls"
                    + "from clients and then assign them to different API working functions. Besides"
                    + "that, API is also exposed for clients to retrieve healthcheck status of this API"
                    + "rest service and the statistics report including the counters of API invocation.",
        version = "v1.0", title = "Policy Design"),
        consumes = { MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON },
        schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS },
        tags = { @Tag(name = "policy-api", description = "Policy API Service Operations") },
        securityDefinition = @SecurityDefinition(basicAuthDefinitions = { @BasicAuthDefinition(key = "basicAuth") }))
public class ApiRestController {

    /**
     * Retrieves the healthcheck status of the API component.
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/healthcheck")
    @ApiOperation(value = "Perform a system healthcheck",
            notes = "Returns healthy status of the Policy API component",
            response = HealthCheckReport.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "HealthCheck", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getHealthCheck() {
        return Response.status(Response.Status.OK).entity(new HealthCheckProvider().performHealthCheck()).build();
    }

    /**
     * Retrieves the statistics report of the API component.
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/statistics")
    @ApiOperation(value = "Retrieve current statistics",
            notes = "Returns current statistics including the counters of API invocation",
            response = StatisticsReport.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Statistics", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getStatistics() {
        return Response.status(Response.Status.OK).entity(new StatisticsProvider().fetchCurrentStatistics()).build();
    }

    /**
     * Retrieves all available policy types.
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes")
    @ApiOperation(value = "Retrieve existing policy types",
            notes = "Returns a list of existing policy types stored in Policy Framework",
            response = ToscaPolicyTypeList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getAllPolicyTypes() {
        return Response.status(Response.Status.OK)
            .entity(new PolicyTypeProvider().fetchPolicyTypes(null, null)).build();
    }

    /**
     * Retrieves all versions of a particular policy type.
     *
     * @param policyTypeId the ID of specified policy type
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}")
    @ApiOperation(value = "Retrieve all available versions of a policy type",
            notes = "Returns a list of all available versions for the specified policy type",
            response = ToscaPolicyTypeList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getAllVersionsOfPolicyType(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyTypeProvider().fetchPolicyTypes(policyTypeId, null)).build();
    }

    /**
     * Retrieves specified version of a particular policy type.
     *
     * @param policyTypeId the ID of specified policy type
     * @param versionId the version of specified policy type
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}/versions/{versionId}")
    @ApiOperation(value = "Retrieve one particular version of a policy type",
            notes = "Returns a particular version for the specified policy type",
            response = ToscaPolicyTypeList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getSpecificVersionOfPolicyType(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("versionId") @ApiParam("ID of version") String versionId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyTypeProvider().fetchPolicyTypes(policyTypeId, versionId)).build();
    }

    /**
     * Creates a new policy type.
     *
     * @param body the body of policy type following TOSCA definition
     *
     * @return the Response object containing the results of the API operation
     */
    @POST
    @Path("/policytypes")
    @ApiOperation(value = "Create a new policy type",
            notes = "Client should provide TOSCA body of the new policy type",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid Body"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response createPolicyType(ToscaPolicyType body) {
        return Response.status(Response.Status.OK).entity(new PolicyTypeProvider().createPolicyType(body)).build();
    }

    /**
     * Deletes all versions of a particular policy type.
     *
     * @param policyTypeId the ID of specified policy type
     *
     * @return the Response object containing the results of the API operation
     */
    @DELETE
    @Path("/policytypes/{policyTypeId}")
    @ApiOperation(value = "Delete all versions of a policy type",
            notes = "Rule 1: pre-defined policy types cannot be deleted;"
                  + "Rule 2: policy types that are in use (parameterized by a TOSCA policy) cannot be deleted."
                  + "The parameterizing TOSCA policies must be deleted first;",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 409, message = "Delete Conflict, Rule Violation"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response deleteAllVersionsOfPolicyType(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyTypeProvider().deletePolicyTypes(policyTypeId, null)).build();
    }

    /**
     * Deletes specified version of a particular policy type.
     *
     * @param policyTypeId the ID of specified policy type
     * @param versionId the version of specified policy type
     *
     * @return the Response object containing the results of the API operation
     */
    @DELETE
    @Path("/policytypes/{policyTypeId}/versions/{versionId}")
    @ApiOperation(value = "Delete one version of a policy type",
            notes = "Rule 1: pre-defined policy types cannot be deleted;"
                  + "Rule 2: policy types that are in use (parameterized by a TOSCA policy) cannot be deleted."
                  + "The parameterizing TOSCA policies must be deleted first;",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "PolicyType", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 409, message = "Delete Conflict, Rule Violation"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response deleteSpecificVersionOfPolicyType(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("versionId") @ApiParam("ID of version") String versionId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyTypeProvider().deletePolicyTypes(policyTypeId, versionId)).build();
    }

    /**
     * Retrieves all versions of a particular policy.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies")
    @ApiOperation(value = "Retrieve all versions of a policy created for a particular policy type version",
            notes = "Returns a list of all versions of specified policy created for the specified policy type version",
            response = ToscaPolicyList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getAllPolicies(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().fetchPolicies(policyTypeId, policyTypeVersion, null, null)).build();
    }

    /**
     * Retrieves all versions of a particular policy.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param policyId the ID of specified policy
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies/{policyId}")
    @ApiOperation(value = "Retrieve all version details of a policy created for a particular policy type version",
            notes = "Returns a list of all version details of the specified policy",
            response = ToscaPolicyList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getAllVersionsOfPolicy(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            @PathParam("policyId") @ApiParam("ID of policy") String policyId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().fetchPolicies(policyTypeId, policyTypeVersion, policyId, null)).build();
    }

    /**
     * Retrieves the specified version of a particular policy.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param policyId the ID of specified policy
     * @param policyVersion the version of specified policy
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies/{policyId}/versions/{policyVersion}")
    @ApiOperation(value = "Retrieve one version of a policy created for a particular policy type version",
            notes = "Returns a particular version of specified policy created for the specified policy type version",
            response = ToscaPolicyList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getSpecificVersionOfPolicy(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            @PathParam("policyId") @ApiParam("ID of policy") String policyId,
            @PathParam("policyVersion") @ApiParam("ID of policy version") String policyVersion) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().fetchPolicies(policyTypeId, policyTypeVersion,
                                                       policyId, policyVersion)).build();
    }

    /**
     * Retrieves either latest or deployed version of a particular policy depending on query parameter.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param policyId the ID of specified policy
     *
     * @return the Response object containing the results of the API operation
     */
    @GET
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies/{policyId}/versions")
    @ApiOperation(value = "Retrieve either latest or deployed version of a particular policy depending on query param",
            notes = "Returns either latest or deployed version of specified policy depending on query param",
            response = ToscaPolicyList.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response getEitherLatestOrDeployedVersionOfPolicy(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            @PathParam("policyId") @ApiParam("ID of policy") String policyId,
            @QueryParam("type") @ApiParam("Type of version that can only be 'latest' or 'deployed'") String type) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().fetchPolicies(policyTypeId, policyTypeVersion, policyId, type)).build();
    }

    /**
     * Creates a new policy for a particular policy type and version.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param body the body of policy following TOSCA definition
     *
     * @return the Response object containing the results of the API operation
     */
    @POST
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies")
    @ApiOperation(value = "Create a new policy for a policy type version",
            notes = "Client should provide TOSCA body of the new policy",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid Body"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response createPolicy(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            ToscaPolicy body) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().createPolicy(policyTypeId, policyTypeVersion, body)).build();
    }

    /**
     * Deletes all versions of a particular policy.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param policyId the ID of specified policy
     *
     * @return the Response object containing the results of the API operation
     */
    @DELETE
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies/{policyId}")
    @ApiOperation(value = "Delete all versions of a policy",
            notes = "Rule: the version that has been deployed in PDP group(s) cannot be deleted",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 409, message = "Delete Conflict, Rule Violation"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response deleteAllVersionsOfPolicy(
            @PathParam("policyTypeId") @ApiParam("ID of policy type") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            @PathParam("policyId") @ApiParam("ID of policy") String policyId) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().deletePolicies(policyTypeId, policyTypeVersion, policyId, null)).build();
    }

    /**
     * Deletes the specified version of a particular policy.
     *
     * @param policyTypeId the ID of specified policy type
     * @param policyTypeVersion the version of specified policy type
     * @param policyId the ID of specified policy
     * @param policyVersion the version of specified policy
     *
     * @return the Response object containing the results of the API operation
     */
    @DELETE
    @Path("/policytypes/{policyTypeId}/versions/{policyTypeVersion}/policies/{policyId}/versions/{policyVersion}")
    @ApiOperation(value = "Delete a particular version of a policy",
            notes = "Rule: the version that has been deployed in PDP group(s) cannot be deleted",
            authorizations = @Authorization(value = "basicAuth"),
            tags = { "Policy", })
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Authorization Error"),
            @ApiResponse(code = 404, message = "Resource Not Found"),
            @ApiResponse(code = 409, message = "Delete Conflict, Rule Violation"),
            @ApiResponse(code = 500, message = "Internal Server Error")
        })
    public Response deleteSpecificVersionOfPolicy(
            @PathParam("policyTypeId") @ApiParam("PolicyType ID") String policyTypeId,
            @PathParam("policyTypeVersion") @ApiParam("ID of policy type version") String policyTypeVersion,
            @PathParam("policyId") @ApiParam("ID of policy") String policyId,
            @PathParam("policyVersion") @ApiParam("ID of policy version") String policyVersion) {
        return Response.status(Response.Status.OK)
            .entity(new PolicyProvider().deletePolicies(policyTypeId, policyTypeVersion,
                                                        policyId, policyVersion)).build();
    }
}

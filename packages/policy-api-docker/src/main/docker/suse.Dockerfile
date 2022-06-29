#-------------------------------------------------------------------------------
# Dockerfile
# ============LICENSE_START=======================================================
#  Copyright (C) 2022 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#-------------------------------------------------------------------------------

FROM busybox AS tarball
RUN mkdir /packages /extracted
COPY /maven/lib/policy-api.tar.gz /packages/
RUN tar xvzf /packages/policy-api.tar.gz --directory /extracted/

FROM opensuse/leap:15.4

LABEL maintainer="Policy Team"
LABEL org.opencontainers.image.title="Policy API"
LABEL org.opencontainers.image.description="Policy API image based on OpenSuse"
LABEL org.opencontainers.image.url="https://github.com/onap/policy-api"
LABEL org.opencontainers.image.vendor="ONAP Policy Team"
LABEL org.opencontainers.image.licenses="Apache-2.0"
LABEL org.opencontainers.image.created="${git.build.time}"
LABEL org.opencontainers.image.version="${git.build.version}"
LABEL org.opencontainers.image.revision="${git.commit.id.abbrev}"

ARG POLICY_LOGS=/var/log/onap/policy/api

ENV POLICY_LOGS=$POLICY_LOGS
ENV POLICY_HOME=/opt/app/policy/api
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
ENV JAVA_HOME=/usr/lib64/jvm/java-11-openjdk-11

RUN zypper -n -q install --no-recommends java-11-openjdk-headless netcat-openbsd && \
    zypper -n -q update && zypper -n -q clean --all && \
    groupadd --system policy && \
    useradd --system --shell /bin/sh -G policy policy && \
    mkdir -p $POLICY_HOME $POLICY_LOGS && \
    chown -R policy:policy $POLICY_HOME $POLICY_LOGS

COPY --chown=policy:policy --from=tarball /extracted/ $POLICY_HOME/

WORKDIR $POLICY_HOME
COPY --chown=policy:policy policy-api.sh bin/
COPY --chown=policy:policy /maven/api.jar /app/

RUN chmod 755 bin/*.sh

USER policy
WORKDIR $POLICY_HOME/bin
ENTRYPOINT [ "./policy-api.sh" ]
